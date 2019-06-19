CREATE SCHEMA IF NOT EXISTS vos_data
    AUTHORIZATION postgres;

COMMENT ON SCHEMA vos_data
    IS 'schema hosting data objects';

CREATE OR REPLACE PROCEDURAL LANGUAGE plpython3u
    HANDLER plpython3_call_handler
    INLINE plpython3_inline_handler
    VALIDATOR plpython3_validator;

CREATE OR REPLACE FUNCTION vos_data.update_ftp_files_download_completed(p_ftp_file_id integer, p_status integer, p_file_path text)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

BEGIN
    	update ftp_files set ftp_file_dwl_phase_status = p_status, ftp_file_local_path = p_file_path, ftp_file_last_phase_change_timestamp = now() where ftp_file_id = p_ftp_file_id;
END; 

$function$;

CREATE OR REPLACE FUNCTION vos_data.update_ftp_files_upload_completed(p_ftp_file_id integer, p_status integer, p_file_path text)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

BEGIN
    	update ftp_files set ftp_file_upload_phase_status = p_status, ftp_file_destination_path = p_file_path, ftp_file_last_phase_change_timestamp = now() where ftp_file_id = p_ftp_file_id;
END; 

$function$;

CREATE OR REPLACE FUNCTION vos_data.commit_ftp_files_insert(p_temp_table_name character varying)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_ftp_files refcursor;
BEGIN
	OPEN c_ftp_files FOR
		execute ('insert into ftp_files (ftp_file_name,ftp_file_size,ftp_file_type,ftp_file_row_quantity,ftp_file_server_path,ftp_file_local_path,ftp_file_destination_path,ftp_file_archive_folder,ftp_file_reatempt_count,ftp_file_reatempt_bulk_count,ftp_file_current_phase,ftp_file_last_phase_change_timestamp,ftp_file_dir_phase_status,ftp_file_dwl_phase_status,ftp_file_upload_phase_status,ftp_file_bulk_phase_status,ftp_file_archive_phase_status,ftp_file_clean_phase_status,ftp_file_error_code,site_id,service_id) select ftp_file_name,ftp_file_size,ftp_file_type,ftp_file_row_quantity,ftp_file_server_path,ftp_file_local_path,ftp_file_destination_path,ftp_file_archive_folder,ftp_file_reatempt_count,ftp_file_reatempt_bulk_count,ftp_file_current_phase,ftp_file_last_phase_change_timestamp,ftp_file_dir_phase_status,ftp_file_dwl_phase_status,ftp_file_upload_phase_status,ftp_file_bulk_phase_status,ftp_file_archive_phase_status,ftp_file_clean_phase_status,ftp_file_error_code,site_id,service_id from ' || p_temp_table_name || ' where ' || p_temp_table_name || '.ftp_file_name not in (select ftp_file_name from ftp_files) returning *');
	RETURN NEXT c_ftp_files;

END; 
$function$;

CREATE OR REPLACE FUNCTION vos_data.get_ftp_files_for_bulk(p_service_id integer, p_count integer, p_state integer, p_status integer, p_new_state integer, p_new_status integer, p_concurrent_id integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_ftp_files refcursor;
BEGIN

	OPEN c_ftp_files FOR
    update ftp_files set 
        ftp_file_current_phase = p_new_state, 
        ftp_file_bulk_phase_status = p_new_status,
        ftp_file_last_phase_change_timestamp = now()
    where ftp_file_id in 
        (
            select ftp_file_id from ftp_files where service_id = p_service_id and ftp_file_current_phase = p_state and ftp_file_upload_phase_status = p_status limit p_count)
    -- returning ftp_file_id, ftp_file_destination_path;
    returning *;

	RETURN NEXT c_ftp_files;

END;  
$function$;

CREATE OR REPLACE FUNCTION vos_data.get_ftp_files_for_download(p_service_id integer, p_count integer, p_state integer, p_status integer, p_new_state integer, p_new_status integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_ftp_files refcursor;
BEGIN

	OPEN c_ftp_files FOR
    update ftp_files set 
        ftp_file_current_phase = p_new_state, 
        ftp_file_dwl_phase_status = p_new_status,
        ftp_file_last_phase_change_timestamp = now()
    where ftp_file_id in 
        (
            select ftp_file_id from ftp_files where service_id = p_service_id and ftp_file_current_phase = p_state and ftp_file_dir_phase_status = p_status limit p_count)
    returning *;

	RETURN NEXT c_ftp_files;

END; 
$function$;

CREATE OR REPLACE FUNCTION vos_data.get_ftp_files_for_upload(p_service_id integer, p_count integer, p_state integer, p_status integer, p_new_state integer, p_new_status integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE c_ftp_files refcursor;
BEGIN

	OPEN c_ftp_files FOR
    update ftp_files set 
        ftp_file_current_phase = p_new_state, 
        ftp_file_upload_phase_status = p_new_status,
        ftp_file_last_phase_change_timestamp = now()
    where ftp_file_id in 
        (
            select ftp_file_id from ftp_files where service_id = p_service_id and ftp_file_current_phase = p_state and ftp_file_dwl_phase_status = p_status limit p_count)
    returning *;

	RETURN NEXT c_ftp_files;

END;  
$function$;

CREATE OR REPLACE FUNCTION vos_data.prepare_ftp_files_insert(p_temp_table_name character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

BEGIN
	execute ('CREATE TEMPORARY TABLE IF NOT EXISTS ' || p_temp_table_name || ' as  SELECT * FROM vos_data.ftp_files LIMIT 0');
	execute ('CREATE INDEX if not exists temp_ftp_files_index_name ON ' || p_temp_table_name || ' (ftp_file_name)');
    execute ('TRUNCATE TABLE ' || p_temp_table_name);

END; 
$function$;

CREATE OR REPLACE FUNCTION vos_data.update_ftp_files_bulk_completed(p_ftp_file_id integer, p_status integer, p_error_desc text)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

BEGIN
		update ftp_files set ftp_file_bulk_phase_status = p_status, ftp_file_error_description = p_error_desc, ftp_file_last_phase_change_timestamp = now() where ftp_file_id = p_ftp_file_id;
END; 
$function$;

CREATE OR REPLACE FUNCTION vos_data.do_bulk_insert_save(p_ftp_file_id integer, p_ftp_file_name character varying, zip_file_full_path character varying, sql_template character varying, success_status integer, fail_status integer)
 RETURNS character varying
 LANGUAGE plpython3u
AS $function$

        import os
        import zipfile
 
        result_status = success_status
        sql_string = ''
        the_remove_file_result = ''
        the_zip_result = ''
        the_bulk_result = ''
        # the_path, the_zip_file_name = os.path.split(zip_file_full_path)
        the_path = zip_file_full_path
        the_zip_file_name = p_ftp_file_name
        the_name_list = []        
        # if not the_path.endswith('/') and not the_path.endswith('\\\\'):
        #    the_path = the_path + "/"
        try:
            t_zip_file_full_path = os.path.join(zip_file_full_path, p_ftp_file_name)
            zip_ref = zipfile.ZipFile(t_zip_file_full_path)
            the_name_list = zip_ref.namelist()
        except Exception as e0:
            result_status = fail_status
            the_zip_result = 'failed to open zip file - ' + str(e0)

        if(result_status==success_status):
            if(len(the_name_list)==1):
                the_file_name=the_name_list[0]
                try:
                    os.remove(the_path + the_file_name)
                except OSError:
                    pass
                
                try:
                    extracted_file_name = zip_ref.extract(the_file_name, the_path)
                    the_zip_result = 'succeeded to extract file'
                except Exception as e1:
                    result_status = fail_status
                    the_zip_result = 'failed to extract zip file - ' + str(e1)
                try:
                    zip_ref.close()
                except Exception as e2:
                    the_zip_result = 'failed to close zip file object ' + str(e2)
                    pass

            else:
                result_status = fail_status
                the_zip_result = 'failed to extract zip file - unexpected number of files in archive: ' + str(len(the_name_list))

            
        if(result_status==success_status):
            # sql_string = 'COPY metersdata(meterid, sampletime, energydata, taoz, temptype, isautomatic) FROM \\'' + extracted_file_name + '\\''
            sql_string = sql_template.format(extracted_file_name)
            try:
                plpy.execute(sql_string)
                result_status = success_status
                the_bulk_result = 'succeeded to bulk'
            except plpy.SPIError as e3:
                result_status = fail_status
                the_bulk_result = 'failed to bulk - ' + str(e3)
            try:
                os.remove(extracted_file_name)
                the_remove_file_result = 'succeeded to remove file'
            except OSError as e4:
                the_remove_file_result = 'failed to remove file - ' + str(e4)
                pass

        try:
            plan = plpy.prepare("select * from update_ftp_files_bulk_completed($1, $2, $3)", ["integer", "integer", "text"])
            plpy.execute(plan, [p_ftp_file_id, result_status, sql_string + ' - ' + the_zip_result + ' - ' + the_bulk_result + ' - ' + the_remove_file_result])
            update_result = 'suceeded to update'
        except plpy.SPIError as e5:
            update_result = 'failed to update - ' + str(e5)

        return sql_string + ' - ' + the_zip_result + ' - '+ the_bulk_result + ' - ' + the_remove_file_result + ' - ' + update_result

$function$;

CREATE OR REPLACE FUNCTION vos_data.do_bulk_insert_new(p_ftp_file_id integer, p_ftp_file_name character varying, zip_file_full_path character varying, sql_template character varying, success_status integer, fail_status integer)
 RETURNS character varying
 LANGUAGE plpython3u
AS $function$

        import os
        import zipfile
 
        result_status = success_status
        sql_string = ''
        the_remove_file_result = ''
        the_zip_result = ''
        the_bulk_result = ''
        # the_path, the_zip_file_name = os.path.split(zip_file_full_path)
        the_path = zip_file_full_path
        the_zip_file_name = p_ftp_file_name
        the_name_list = []        
        # if not the_path.endswith('/') and not the_path.endswith('\\\\'):
        #    the_path = the_path + "/"
        try:
            t_zip_file_full_path = os.path.join(zip_file_full_path, p_ftp_file_name)
            zip_ref = zipfile.ZipFile(t_zip_file_full_path)
            the_name_list = zip_ref.namelist()
        except Exception as e0:
            result_status = fail_status
            the_zip_result = 'failed to open zip file - ' + str(e0)

        if(result_status==success_status):
            if(len(the_name_list)==1):
                the_file_name=the_name_list[0]
                try:
                    os.remove(the_path + the_file_name)
                except OSError:
                    pass
                
                try:
                    extracted_file_name = zip_ref.extract(the_file_name, the_path)
                    the_zip_result = 'succeeded to extract file'
                except Exception as e1:
                    result_status = fail_status
                    the_zip_result = 'failed to extract zip file - ' + str(e1)
                try:
                    zip_ref.close()
                except Exception as e2:
                    the_zip_result = 'failed to close zip file object ' + str(e2)
                    pass

            else:
                result_status = fail_status
                the_zip_result = 'failed to extract zip file - unexpected number of files in archive: ' + str(len(the_name_list))
                
        if(result_status==success_status):
            sql_template_parts = sql_template.split(')')
            if(len(sql_template_parts)==2):
                new_sql_template = sql_template_parts[0] + ', ftp_file_id) ' + sql_template_parts[1]
            else:
                result_status = fail_status
                the_bulk_result = 'failed to split template string: ' + sql_template

        if(result_status==success_status):
            modified_extracted_file_name = extracted_file_name + '.out'
            try:
                with open(extracted_file_name, 'r') as ins:
                    with open(modified_extracted_file_name, 'w') as csvoutput:
                        for line in ins:
                            csvoutput.write(line[:-1] + str(p_ftp_file_id) + '\
')
        

            except Exception as e6:
                result_status = fail_status
                the_bulk_result = 'failed to modify file with file id - ' + str(e6)
            
            
            
            
        if(result_status==success_status):
            # sql_string = 'COPY metersdata(meterid, sampletime, energydata, taoz, temptype, isautomatic) FROM \\'' + extracted_file_name + '\\''
            sql_string = new_sql_template.format(modified_extracted_file_name)
            try:
                plpy.execute(sql_string)
                result_status = success_status
                the_bulk_result = 'succeeded to bulk'
            except plpy.SPIError as e3:
                result_status = fail_status
                the_bulk_result = 'failed to bulk - ' + str(e3)
            try:
                os.remove(extracted_file_name)
                the_remove_file_result = 'succeeded to remove file'
            except OSError as e4:
                the_remove_file_result = 'failed to remove file - ' + str(e4)
                pass

        try:
            plan = plpy.prepare("select * from update_ftp_files_bulk_completed($1, $2, $3)", ["integer", "integer", "text"])
            plpy.execute(plan, [p_ftp_file_id, result_status, sql_string + ' - ' + the_zip_result + ' - ' + the_bulk_result + ' - ' + the_remove_file_result])
            update_result = 'suceeded to update'
        except plpy.SPIError as e5:
            update_result = 'failed to update - ' + str(e5)

        return sql_string + ' - ' + the_zip_result + ' - '+ the_bulk_result + ' - ' + the_remove_file_result + ' - ' + update_result

$function$;

CREATE OR REPLACE FUNCTION vos_data.do_bulk_insert(p_ftp_file_id integer, p_ftp_file_name character varying, zip_file_full_path character varying, sql_template character varying, success_status integer, fail_status integer)
 RETURNS character varying
 LANGUAGE plpython3u
AS $function$

        import os
        import zipfile
 
        result_status = success_status
        sql_string = ''
        the_remove_file_result = ''
        the_zip_result = ''
        the_bulk_result = ''
        # the_path, the_zip_file_name = os.path.split(zip_file_full_path)
        the_path = zip_file_full_path
        the_zip_file_name = p_ftp_file_name
        the_name_list = []        
        # if not the_path.endswith('/') and not the_path.endswith('\\\\'):
        #    the_path = the_path + "/"
        try:
            t_zip_file_full_path = os.path.join(zip_file_full_path, p_ftp_file_name)
            zip_ref = zipfile.ZipFile(t_zip_file_full_path)
            the_name_list = zip_ref.namelist()
        except Exception as e0:
            result_status = fail_status
            the_zip_result = 'failed to open zip file - ' + str(e0)

        if(result_status==success_status):
            if(len(the_name_list)==1):
                the_file_name=the_name_list[0]
                try:
                    os.remove(the_path + the_file_name)
                except OSError:
                    pass
                
                try:
                    extracted_file_name = zip_ref.extract(the_file_name, the_path)
                    the_zip_result = 'succeeded to extract file'
                except Exception as e1:
                    result_status = fail_status
                    the_zip_result = 'failed to extract zip file - ' + str(e1)
                try:
                    zip_ref.close()
                except Exception as e2:
                    the_zip_result = 'failed to close zip file object ' + str(e2)
                    pass

            else:
                result_status = fail_status
                the_zip_result = 'failed to extract zip file - unexpected number of files in archive: ' + str(len(the_name_list))
                
            
            
            
            
        if(result_status==success_status):
            # sql_string = 'COPY metersdata(meterid, sampletime, energydata, taoz, temptype, isautomatic) FROM \\'' + extracted_file_name + '\\''
            # sql_string = 'select * from do_bulk_insert_operands_data(\\'{}\\',{});'.format(extracted_file_name, str(p_ftp_file_id))
            # sql_string = new_sql_template.format(modified_extracted_file_name)
            sql_string = sql_template.format(extracted_file_name, str(p_ftp_file_id))

            try:
                plpy.execute(sql_string)
                result_status = success_status
                the_bulk_result = 'succeeded to bulk'
            except plpy.SPIError as e3:
                result_status = fail_status
                the_bulk_result = 'failed to bulk - ' + str(e3)
            try:
                os.remove(extracted_file_name)
                # os.remove(modified_extracted_file_name)
                the_remove_file_result = 'succeeded to remove file'
            except OSError as e4:
                the_remove_file_result = 'failed to remove file - ' + str(e4)
                pass

        try:
            plan = plpy.prepare("select * from update_ftp_files_bulk_completed($1, $2, $3)", ["integer", "integer", "text"])
            plpy.execute(plan, [p_ftp_file_id, result_status, sql_string + ' - ' + the_zip_result + ' - ' + the_bulk_result + ' - ' + the_remove_file_result])
            update_result = 'suceeded to update'
        except plpy.SPIError as e5:
            update_result = 'failed to update - ' + str(e5)

        return sql_string + ' - ' + the_zip_result + ' - '+ the_bulk_result + ' - ' + the_remove_file_result + ' - ' + update_result

$function$;

CREATE OR REPLACE FUNCTION vos_data.update_orphand_ftp_files(p_service_id integer, p_orphand_ftp_files_timeout integer, p_orphand_ftp_files_download_phase integer, p_orphand_ftp_files_download_new_phase integer, p_orphand_ftp_files_upload_phase integer, p_orphand_ftp_files_upload_new_phase integer, p_orphand_ftp_files_status integer)
 RETURNS SETOF refcursor
 LANGUAGE plpgsql
AS $function$

DECLARE 
c_ftp_files refcursor;

BEGIN

    OPEN c_ftp_files FOR
        with t as (
            UPDATE vos_data.ftp_files
                SET 
                ftp_file_last_phase_change_timestamp = now()::timestamp,
                ftp_file_current_phase = 
                CASE 
            		WHEN ftp_file_current_phase = p_orphand_ftp_files_download_phase
                	THEN p_orphand_ftp_files_download_new_phase
                	ELSE p_orphand_ftp_files_upload_new_phase
                END
            WHERE 
               (((ftp_file_current_phase = p_orphand_ftp_files_download_phase and ftp_file_dwl_phase_status = p_orphand_ftp_files_status)
                or 
                (ftp_file_current_phase = p_orphand_ftp_files_upload_phase and ftp_file_upload_phase_status = p_orphand_ftp_files_status))
                and (EXTRACT(EPOCH FROM (now()::timestamp-ftp_file_last_phase_change_timestamp)) >= p_orphand_ftp_files_timeout))
            returning ftp_file_id
        )
        select count(*) as number_of_affected_rows from t;

    RETURN NEXT c_ftp_files; 
END; 

$function$;

CREATE OR REPLACE FUNCTION vos_data.extract_meters_data_by_site_id(_site_id integer, _from timestamp without time zone, _to timestamp without time zone)
 RETURNS SETOF void
 LANGUAGE plpgsql
AS $function$

    declare _FromUTC timestamp;
    declare _ToUTC timestamp;

BEGIN
   -- select CAST(((_from AT TIME ZONE 'IDT') AT TIME ZONE 'UTC') into _FromUTC;
   
	select vos_data.f_to_utc(_from) into _FromUTC;
    select vos_data.f_to_utc(_to) into _ToUTC;
	
    perform vos_data.get_meters_data(_site_id,_FromUTC, _ToUTC);


END;

 

$function$;

CREATE OR REPLACE FUNCTION vos_data.f_is_dst(_time timestamp without time zone)
 RETURNS timestamp without time zone
 LANGUAGE plpgsql
AS $function$

DECLARE _sample_time timestamp without time zone;
DECLARE _is_dst integer;
DECLARE _diff float;
declare _TimeUTC timestamp;

BEGIN

select max(d.sample_time) from vos_data.dst d into _sample_time where d.sample_time <= _time;
	select d.is_dst from vos_data.dst d into _is_dst where d.sample_time = _sample_time;
    
    select extract(epoch from _time - _sample_time)/3600 into _diff;
    
    if (_diff < 2) 
    then
    	_is_dst := 1 - _is_dst;
    elsif (_diff < 3)
    then
    	_is_dst := 1;
    end if;

if (_is_dst = 1)
    then
    	SELECT _time AT TIME ZONE 'IDT' AT TIME ZONE 'UTC' into _TimeUTC;
    else
        SELECT _time AT TIME ZONE 'IST' AT TIME ZONE 'UTC' into _TimeUTC;
    end if;
    
RETURN _TimeUTC;
END
$function$;

CREATE OR REPLACE FUNCTION vos_data.f_to_utc(_time timestamp without time zone)
 RETURNS timestamp without time zone
 LANGUAGE plpgsql
AS $function$

	DECLARE _sample_time timestamp without time zone;
	DECLARE _is_dst integer;
	DECLARE _diff float;
	DECLARE _TimeUTC timestamp;

BEGIN

	select max(d.sample_time) from vos_data.dst d into _sample_time where d.sample_time <= _time;
	select d.is_dst from vos_data.dst d into _is_dst where d.sample_time = _sample_time;
    
    select extract(epoch from _time - _sample_time)/3600 into _diff;
    
    if (_diff < 2) 
    then
    	_is_dst := 1 - _is_dst;
    elsif (_diff < 3)
    then
    	_is_dst := 1;
    end if;

	if (_is_dst = 1)
    then
    	SELECT _time AT TIME ZONE 'IDT' AT TIME ZONE 'UTC' into _TimeUTC;
    else
        SELECT _time AT TIME ZONE 'IST' AT TIME ZONE 'UTC' into _TimeUTC;
    end if;
    
RETURN _TimeUTC;
END

$function$;

CREATE OR REPLACE FUNCTION vos_data.get_data(_extract_id integer, _from timestamp without time zone, _to timestamp without time zone)
 RETURNS SETOF void
 LANGUAGE plpgsql
AS $function$

	declare _column_ordinal integer;
	declare _column_name text;
	declare _operand_id integer;
	declare _rnum integer;

	declare arow record;

 

BEGIN

    
	drop table if exists tmp_operands_data_extract;

	CREATE temporary TABLE tmp_operands_data_extract
	(
		row_number SERIAL,
	    utc_sample_time timestamp without time zone NOT NULL,
	    sample_time timestamp without time zone  NULL,

		CONSTRAINT tmp_operands_data_extract_data_pkey PRIMARY KEY (row_number)
	)  on commit drop;

-- add first column
	select operand_id, column_name, column_ordinal into _operand_id, _column_name, _column_ordinal
	from vos_data.operands_data_extracts_members where operands_data_extract_id = _extract_id
	order by column_ordinal limit 1;

	execute 'alter table tmp_operands_data_extract add column "' || _column_name::text || '" text' ;

	execute 'insert into tmp_operands_data_extract (utc_sample_time, "' || _column_name::text || '") 
	select sample_time, sample_value from vos_data.operands_data
	where operand_id = ' || _operand_id::text || ' and sample_time between ''' || _from || ''' AND ''' || _to || ''' order by sample_time limit 30001;';

	CREATE INDEX IX_tmp_Sample_Time
    	ON tmp_operands_data_extract USING btree
    	(utc_sample_time);

-- add rest of columns
	for arow in
  		select * , row_number() OVER () as rnum from vos_data.operands_data_extracts_members 
  		where operands_data_extract_id = _extract_id and column_ordinal > _column_ordinal
  		order by column_ordinal
	loop
		_operand_id := arow.operand_id;
		_column_name := arow.column_name;
		_column_ordinal := arow.column_ordinal;
	
        execute 'alter table tmp_operands_data_extract add column "' || _column_name::text || '" text' ;

		execute 'update tmp_operands_data_extract as tmpo set "' || _column_name::text || '" = o.sample_value
		from vos_data.operands_data o where o.operand_id = ' || _operand_id::text || ' and
		tmpo.utc_sample_time = o.sample_time and tmpo.utc_sample_time between ''' || _from || ''' AND ''' || _to || ''';';
	end loop;

-- update utc time to local time
	update tmp_operands_data_extract as tmpo set sample_time = case d.is_dst when 0 then tmpo.utc_sample_time AT TIME ZONE 'UTC' AT TIME ZONE 'IST' when 1 then tmpo.utc_sample_time AT TIME ZONE 'UTC' AT TIME ZONE 'IDT' end
	from
	(select tmpo.row_number,  (select max(d.sample_time) from vos_data.dst d where d.sample_time <= tmpo.utc_sample_time) as dstc 
	from tmp_operands_data_extract tmpo) ooo inner join vos_data.dst d on ooo.dstc =  d.sample_time
	where ooo.row_number = tmpo.row_number;

-- drop utc column
	alter table tmp_operands_data_extract drop column utc_sample_time;

END;

 

$function$;

CREATE OR REPLACE FUNCTION vos_data.get_alarms(_extract_id integer, _from timestamp without time zone, _to timestamp without time zone)
 RETURNS SETOF void
 LANGUAGE plpgsql
AS $function$

BEGIN

	drop table if exists tmp_operands_data_extract;

	CREATE temporary TABLE tmp_operands_data_extract
	(
		row_number SERIAL,
        alarm_name text not null,
	    utc_sample_time timestamp without time zone NOT NULL,
	    sample_time timestamp without time zone  NULL,
		state text not null,
		CONSTRAINT tmp_operands_data_extract_data_pkey PRIMARY KEY (row_number)
	) on commit drop;

	insert into tmp_operands_data_extract (alarm_name, utc_sample_time, state)
    select em.column_name, o.sample_time, left(o.sample_value, 1) from vos_data.operands_data o
    inner join vos_data.operands_data_extracts_members em on o.operand_id = em.operand_id
    
    where em.operands_data_extract_id = _extract_id and o.sample_time between _from and _to
    order by o.sample_time, em.column_name limit 30001;

	CREATE INDEX IX_tmp_Sample_Time
    	ON tmp_operands_data_extract USING btree
    	(utc_sample_time);

-- update utc time to local timesample_time
	update tmp_operands_data_extract as tmpo set sample_time = case d.is_dst when 0 then tmpo.utc_sample_time AT TIME ZONE 'UTC' AT TIME ZONE 'IST' when 1 then tmpo.utc_sample_time AT TIME ZONE 'UTC' AT TIME ZONE 'IDT' end
	from
	(select tmpo.row_number,  (select max(d.sample_time) from vos_data.dst d where d.sample_time <= tmpo.utc_sample_time) as dstc 
	from tmp_operands_data_extract tmpo) ooo inner join vos_data.dst d on ooo.dstc =  d.sample_time
	where ooo.row_number = tmpo.row_number;

-- drop utc column
	alter table tmp_operands_data_extract drop column utc_sample_time;

    
END;

 

$function$;

CREATE OR REPLACE FUNCTION vos_data.extract_operands_data_by_extract_id(_extract_id integer, _from timestamp without time zone, _to timestamp without time zone)
 RETURNS SETOF void
 LANGUAGE plpgsql
AS $function$

	declare _extract_type integer;
    declare _FromUTC timestamp;
    declare _ToUTC timestamp;

BEGIN
   -- select CAST(((_from AT TIME ZONE 'IDT') AT TIME ZONE 'UTC') into _FromUTC;
   
	select vos_data.f_to_utc(_from) into _FromUTC;
    select vos_data.f_to_utc(_to) into _ToUTC;
	select operands_data_extract_type 
    FROM vos_data.operands_data_extracts 
    into _extract_type 
    where operands_data_extract_id = _extract_id;
    
    if (_extract_type = 1)
    then
    	perform vos_data.get_data(_extract_id,_FromUTC, _ToUTC);
    elseif (_extract_type = 2)
    then
    	perform vos_data.get_alarms(_extract_id,_FromUTC, _ToUTC);
    end if;

END;

 

$function$;

CREATE OR REPLACE FUNCTION vos_data.do_bulk_insert_operands_data(p_file_path text, p_file_id integer)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

-- select *, to_char(sample_time, 'YYYYMM') from operands_data_tmp;

    declare arow record;
    declare _tsql text;
    declare _this_month text;
    declare _next_month text;
    declare _this_month_date text;
    declare _next_month_date text;
    declare _table_name text;
begin    
--    CREATE temporary TABLE if not exists operands_data_tmp on commit drop
--    as select * from operands_data limit 0;
    CREATE temporary TABLE if not exists operands_data_tmp 
    (
        operand_id integer NOT NULL,
        sample_time timestamp without time zone NOT NULL,
        sample_value text NOT NULL,
        manual_mode boolean not NULL,
        ftp_file_id integer
    ) on commit drop;

    truncate table operands_data_tmp;
    -- alter table operands_data_tmp drop column ftp_file_id;
    -- select * from operands_data_tmp;

	_tsql := 'COPY operands_data_tmp(operand_id, sample_time, sample_value, manual_mode)  FROM ''' || p_file_path || '''';
--    COPY operands_data_tmp(operand_id, sample_time, sample_value, manual_mode)  FROM p_file_path;

    execute _tsql;
    update operands_data_tmp set ftp_file_id = p_file_id;

    for arow in
      select date_trunc('month', sample_time) as current_month from operands_data_tmp group by date_trunc('month', sample_time)
      order by date_trunc('month', sample_time)
    loop
    	select to_char(arow.current_month, 'YYYYMM') into _this_month;
        select to_char(arow.current_month  + interval '1 month', 'YYYYMM') into _next_month;
    	select to_char(arow.current_month, 'YYYY-MM-DD') into _this_month_date;
        select to_char(arow.current_month  + interval '1 month', 'YYYY-MM-DD') into _next_month_date;
		_table_name := 'operands_data_' || _this_month;
        
    	_tsql := 'CREATE TABLE IF NOT EXISTS ' || _table_name || ' 
        (
            CONSTRAINT operands_data_' || _this_month || '_pkey PRIMARY KEY (operands_data_id),
            CHECK(sample_time >= ''' ||  _this_month_date || ''' and sample_time < ''' || _next_month_date || ''')
		) INHERITS (operands_data);';

        _tsql := _tsql || 'CREATE INDEX if not exists ix_' || _table_name || '_operand_id
    ON ' || _table_name || ' USING btree
    (operand_id);';
            
        _tsql := _tsql || 'CREATE INDEX if not exists ix_' || _table_name || '_operand_id_sample_time
    ON ' || _table_name || ' USING btree
    (operand_id, sample_time);';

        _tsql := _tsql || 'CREATE INDEX if not exists ix_' || _table_name || '_sample_time
    ON ' || _table_name || ' USING btree
    (sample_time);';

   
		execute _tsql;                        
        _tsql = 'insert into ' || _table_name || '(operand_id, sample_time, sample_value, manual_mode, ftp_file_id)
            select operand_id, sample_time, sample_value, manual_mode, ftp_file_id from operands_data_tmp where to_char(date_trunc(''month'', sample_time), ''YYYYMM'') = ''' || _this_month || ''';';
		execute _tsql;                        

    end loop;
END; 

$function$;

CREATE OR REPLACE FUNCTION vos_data.get_meters_data(_site_id integer, _from timestamp without time zone, _to timestamp without time zone)
 RETURNS SETOF void
 LANGUAGE plpgsql
AS $function$

	declare _column_ordinal integer;
	declare _column_name text;
	declare _meter_id integer;
	declare _rnum integer;

	declare arow record;

 

BEGIN

    
	drop table if exists tmp_meters_data_extract;

	CREATE temporary TABLE tmp_meters_data_extract
	(
		row_number SERIAL,
	    utc_sample_time timestamp without time zone NOT NULL,
	    sample_time timestamp without time zone  NULL,

		CONSTRAINT tmp_meters_data_extract_data_pkey PRIMARY KEY (row_number)
	)  on commit drop;

-- add first column
	select meter_id, column_name, column_ordinal into _meter_id, _column_name, _column_ordinal
	from vos_data.meters_data_extracts where site_id = _site_id
	order by column_ordinal limit 1;

	execute 'alter table tmp_meters_data_extract add column "' || _column_name::text || '" text' ;

	execute 'insert into tmp_meters_data_extract (utc_sample_time, "' || _column_name::text || '") 
	select sample_time, energy_data from vos_data.meters_data
	where meter_id = ' || _meter_id::text || ' and sample_time between ''' || _from || ''' AND ''' || _to || ''' order by sample_time limit 30001;';

	CREATE INDEX IX_tmp_Sample_Time
    	ON tmp_meters_data_extract USING btree
    	(utc_sample_time);

-- add rest of columns
	for arow in
  		select * , row_number() OVER () as rnum from vos_data.meters_data_extracts
  		where site_id = _site_id and column_ordinal > _column_ordinal
  		order by column_ordinal
	loop
		_meter_id := arow.meter_id;
		_column_name := arow.column_name;
		_column_ordinal := arow.column_ordinal;
	
        execute 'alter table tmp_meters_data_extract add column "' || _column_name::text || '" text' ;

		execute 'update tmp_meters_data_extract as tmpo set "' || _column_name::text || '" = o.energy_data
		from vos_data.meters_data o where o.meter_id = ' || _meter_id::text || ' and
		tmpo.utc_sample_time = o.sample_time;';
	end loop;

-- update utc time to local time
	update tmp_meters_data_extract as tmpo set sample_time = case d.is_dst when 0 then tmpo.utc_sample_time AT TIME ZONE 'UTC' AT TIME ZONE 'IST' when 1 then tmpo.utc_sample_time AT TIME ZONE 'UTC' AT TIME ZONE 'IDT' end
	from
	(select tmpo.row_number,  (select max(d.sample_time) from vos_data.dst d where d.sample_time <= tmpo.utc_sample_time) as dstc 
	from tmp_meters_data_extract tmpo) ooo inner join vos_data.dst d on ooo.dstc =  d.sample_time
	where ooo.row_number = tmpo.row_number;

-- drop utc column
	alter table tmp_meters_data_extract drop column utc_sample_time;

END;

 

$function$;

CREATE OR REPLACE FUNCTION vos_data.do_bulk_insert_meters_data(p_file_path text, p_file_id integer)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

    declare _tsql text;
begin    
    -- CREATE temporary TABLE if not exists meters_data_tmp on commit drop
    -- as select * from meters_data limit 0;
    CREATE temporary TABLE if not exists meters_data_tmp 
    (
        meter_id integer NOT NULL,
        sample_time timestamp without time zone NOT NULL,
        energy_data double precision NOT NULL,
        taoz integer NOT NULL,
        temp_type integer NOT NULL,
        is_automatic integer,
        ftp_file_id integer
    ) on commit drop;

    truncate table meters_data_tmp;
    -- alter table operands_data_tmp drop column ftp_file_id;
    -- select * from operands_data_tmp;

	_tsql := 'COPY meters_data_tmp(meter_id, sample_time, energy_data, taoz, temp_type, is_automatic) FROM ''' || p_file_path || '''';

    execute _tsql;
    update meters_data_tmp set ftp_file_id = p_file_id;
    
	INSERT INTO vos_data.meters_data
    (meter_id, sample_time, energy_data, taoz, temp_type, is_automatic, ftp_file_id)
	select meter_id, sample_time, energy_data, taoz, temp_type, is_automatic, ftp_file_id from meters_data_tmp;

END; 

$function$;

