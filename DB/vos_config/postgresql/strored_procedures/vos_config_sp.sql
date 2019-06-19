CREATE SCHEMA IF NOT EXISTS vos_config
    AUTHORIZATION postgres;

COMMENT ON SCHEMA vos_config
    IS 'schema hosting configuraration objects';
	
CREATE OR REPLACE FUNCTION vos_config.get_parameters(p_service_id integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$
DECLARE c_parameters refcursor;
DECLARE i_site_id int;
BEGIN
	SELECT site_id into i_site_id from services where service_id = p_service_id;
	OPEN c_parameters FOR
		select * from parameters where service_id = p_service_id
		UNION ALL
		select * from parameters where service_id is NULL and site_id = i_site_id
		and parameter_name not in (select parameter_name from parameters where service_id = p_service_id);
	RETURN NEXT c_parameters;

END; $function$;

CREATE OR REPLACE FUNCTION vos_config.get_plc(p_service_id integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_plcs refcursor;
BEGIN

    OPEN c_plcs FOR
		select * from plcs inner join npl_plc on plcs.plc_id = npl_plc.plc_id
		where npl_plc.service_id = p_service_id;

    RETURN NEXT c_plcs;

END;  $function$;

CREATE OR REPLACE FUNCTION vos_config.get_plc_modbus_chunks(p_plc_id integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_plc_modbus_chunks refcursor;
BEGIN

    OPEN c_plc_modbus_chunks FOR
		select * from modbus_chunks where plc_id = p_plc_id;

    RETURN NEXT c_plc_modbus_chunks;

END;  $function$;

CREATE OR REPLACE FUNCTION vos_config.get_service(p_service_name character varying)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$
DECLARE c_service refcursor;
BEGIN
    OPEN c_service FOR
        SELECT * from services
	WHERE services.service_name = p_service_name;
    RETURN NEXT c_service;

END; $function$;

CREATE OR REPLACE FUNCTION vos_config.get_ftp_place_holders(p_service_id integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$
DECLARE c_ftp_place_holders refcursor;
BEGIN
	OPEN c_ftp_place_holders FOR
		select * from ftp_place_holders where site_id in (select site_id from ftp_servers where service_id = p_service_id);
	RETURN NEXT c_ftp_place_holders;

END; $function$;

CREATE OR REPLACE FUNCTION vos_config.upsert_ftp_place_holders(p_ftp_file_type integer, p_site_id integer, p_ftp_place_holder_folder text)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$ 
    DECLARE c_identity integer; 
    DECLARE c_ftp_place_holders refcursor;
    BEGIN 
        UPDATE ftp_place_holders SET ftp_place_holder_folder = p_ftp_place_holder_folder WHERE site_id = p_site_id and ftp_file_type = p_ftp_file_type
        returning ftp_place_holder_id into c_identity;
        IF NOT FOUND THEN 
	        INSERT INTO ftp_place_holders (ftp_file_type, site_id, ftp_place_holder_folder) values (p_ftp_file_type, p_site_id, p_ftp_place_holder_folder)
    	    returning ftp_place_holder_id into c_identity; 
        END IF; 
		OPEN c_ftp_place_holders FOR
		select * from ftp_place_holders where ftp_place_holder_id = c_identity;
	RETURN NEXT c_ftp_place_holders;
    
    END; 
    $function$;

CREATE OR REPLACE FUNCTION vos_config.get_ftp_servers(p_service_id integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_ftp_servers refcursor;
BEGIN
	OPEN c_ftp_servers FOR
		select  ftp.ftp_server_id,
 ftp.ftp_server_name,
 ftp.ftp_server_description,
 ftp.ftp_server_type,
 ftp.ftp_server_address_ip,
 ftp.ftp_server_address_port,
 ftp.ftp_server_user_name,
 ftp.ftp_server_user_password,
 ftp.ftp_server_key_store_path,
 ftp.ftp_server_max_parallel_actions,
 ftp.ftp_server_site_type,
 ftp.ftp_server_root_path,
 ftp.service_id,
 s.site_id,
 s.site_name,
 s.site_description from ftp_servers ftp inner join sites s on ftp.site_id = s.site_id  where service_id = p_service_id;
	RETURN NEXT c_ftp_servers;

END; 
$function$;

CREATE OR REPLACE FUNCTION vos_config.get_services(p_service_type integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_services refcursor;
BEGIN
    OPEN c_services FOR
        SELECT * from services
	WHERE (p_service_type = -1) OR (services.service_type = p_service_type);
    RETURN NEXT c_services;

END; 
$function$;

CREATE OR REPLACE FUNCTION vos_config.f_operand_name_unique(_name text, _register_id integer, _calc_operand_id integer)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$

DECLARE _site_id integer; 
DECLARE _result bool;
BEGIN
    if(_register_id is not null)
    then
        SELECT s.site_id into _site_id from vos_config.sites s inner join vos_config.services srvs on s.site_id = srvs.site_id inner join vos_config.registers r on srvs.service_id = r.service_id where r.register_id = _register_id;    
    ELSIF (_calc_operand_id is not null)
    then
        SELECT s.site_id into _site_id from vos_config.sites s inner join vos_config.services srvs on s.site_id = srvs.site_id inner join  vos_config.calc_operands c on srvs.service_id = c.service_id where c.calc_operand_id = _calc_operand_id;    
    end if; 

    drop table if exists _x;
     create temporary table _x as

        select o.operand_name from vos_config.operands o inner join vos_config.calc_operands c on o.calc_operand_id = c.calc_operand_id
        inner join vos_config.services s on c.service_id = s.service_id inner join vos_config.sites sts on s.site_id = sts.site_id
        where sts.site_id = _site_id
        union all 
        select o.operand_name from vos_config.operands o inner join vos_config.registers r on o.register_id = r.register_id
        inner join vos_config.services s on r.service_id = s.service_id inner join vos_config.sites sts on s.site_id = sts.site_id
        where sts.site_id = _site_id;

	select not exists (select 1 from _x where operand_name = _name) into _result;
	return _result;
END

$function$;

CREATE OR REPLACE FUNCTION vos_config.f_operand_name_unique(_operand_id integer, _name text, _register_id integer, _calc_operand_id integer)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$

DECLARE _site_id integer; 
DECLARE _result bool;
BEGIN
	select exists (select 1 from vos_config.operands where operand_name = _name and operand_id = _operand_id) into _result;
	if(_result = false)
    then
        if(_register_id is not null)
        then
            SELECT s.site_id into _site_id from vos_config.sites s inner join vos_config.services srvs on s.site_id = srvs.site_id inner join vos_config.registers r on srvs.service_id = r.service_id where r.register_id = _register_id;    
        ELSIF (_calc_operand_id is not null)
        then
            SELECT s.site_id into _site_id from vos_config.sites s inner join vos_config.services srvs on s.site_id = srvs.site_id inner join  vos_config.calc_operands c on srvs.service_id = c.service_id where c.calc_operand_id = _calc_operand_id;    
        end if; 

        drop table if exists _x;
         create temporary table _x as

            select o.operand_name from vos_config.operands o inner join vos_config.calc_operands c on o.calc_operand_id = c.calc_operand_id
            inner join vos_config.services s on c.service_id = s.service_id inner join vos_config.sites sts on s.site_id = sts.site_id
            where sts.site_id = _site_id
            union all 
            select o.operand_name from vos_config.operands o inner join vos_config.registers r on o.register_id = r.register_id
            inner join vos_config.services s on r.service_id = s.service_id inner join vos_config.sites sts on s.site_id = sts.site_id
            where sts.site_id = _site_id;

        select not exists (select 1 from _x where operand_name = _name) into _result;
    end if;
	return _result;
END

$function$;

CREATE OR REPLACE FUNCTION vos_config.get_all_operands(p_service_id integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_operands refcursor;
BEGIN

    OPEN c_operands FOR
	select 
		operands.operand_id , 
		operands.equipment_id , 
		operands.operand_type , 
		operands.operand_name , 
		operands.operand_description , 
		operands.operand_format , 
		operands.operand_log_by_percentage_value , 
		operands.operand_log_by_interval_value , 
		operands.physical_unit_id , 
		operands.operand_flags , 
		operands.operand_data_type , 
		registers.register_id ,
		registers.register_name , 
		registers.register_description , 
		registers.register_reference , 
		registers.register_size , 
		registers.register_type ,
		calc_operands.calc_operand_id , 
		calc_operands.calc_operand_name , 
		calc_operands.calc_operand_description , 
		calc_operands.calc_operand_formula_prefix , 
		calc_operands.calc_operand_formula,
		meters.meter_id , 
		meters.meter_name , 
		meters.meter_description , 
		meters.meter_type , 
		meters.meter_temp_type , 
		meters.max_consumption
	from 
		operands left outer join registers on operands.register_id = registers.register_id 
		left outer join calc_operands on operands.calc_operand_id = calc_operands.calc_operand_id 
		left outer join meters on operands.operand_id = meters.operand_id
	where registers.service_id = p_service_id or calc_operands.service_id = p_service_id
    order by operands.operand_id;

    RETURN NEXT c_operands;

END;  
$function$;

CREATE OR REPLACE FUNCTION vos_config.get_sites(
	)
    RETURNS refcursor
    LANGUAGE 'plpgsql'

AS $function$

DECLARE c_sites refcursor;
BEGIN
    OPEN c_sites FOR
        SELECT * from sites;
    RETURN c_sites;

END; 

$function$;

CREATE OR REPLACE FUNCTION vos_config.get_services(
	p_service_type integer)
    RETURNS SETOF refcursor 
    LANGUAGE 'plpgsql'

AS $function$

DECLARE c_services refcursor;
BEGIN
    OPEN c_services FOR
        SELECT * from services
	WHERE (p_service_type = -1) OR (services.service_type = p_service_type)
    ORDER BY services.site_id,services.service_type,services.service_name;
    RETURN NEXT c_services;

END; 

$function$;

