CREATE SCHEMA IF NOT EXISTS vos_config
    AUTHORIZATION postgres;

COMMENT ON SCHEMA vos_config
    IS 'schema hosting configuraration objects';

SET search_path = vos_config;

CREATE TABLE vos_config.sites
(
    site_id SERIAL,
    site_name VARCHAR(50) NOT NULL,
    site_description VARCHAR(250),
    CONSTRAINT sites_pkey PRIMARY KEY (site_id),
    CONSTRAINT sites_name_unique UNIQUE (site_name)
);

CREATE TABLE vos_config.departments
(
    department_id SERIAL,
    department_name VARCHAR(50) NOT NULL,
    department_description VARCHAR(250),
    site_id integer NOT NULL,
    CONSTRAINT departments_pkey PRIMARY KEY (department_id),
    CONSTRAINT departments_name_unique UNIQUE (department_name),
    CONSTRAINT fk_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);

CREATE TABLE vos_config.equipments
(
    equipment_id SERIAL,
    equipment_name VARCHAR(50) NOT NULL,
    equipment_description VARCHAR(250),
    department_id integer NOT NULL,
    CONSTRAINT equipments_pkey PRIMARY KEY (equipment_id),
    CONSTRAINT equipments_name_unique UNIQUE (equipment_name),
    CONSTRAINT fk_departments FOREIGN KEY (department_id)
        REFERENCES vos_config.departments (department_id)
);

CREATE TABLE vos_config.services
(
    service_id SERIAL,
    service_type integer NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    service_description VARCHAR(250),
    service_address_ip text NOT NULL,
    service_address_port integer NOT NULL,
    site_id integer NOT NULL,
    CONSTRAINT services_pkey PRIMARY KEY (service_id),
    CONSTRAINT services_address_unique UNIQUE (service_address_ip, service_address_port),
    CONSTRAINT services_name_unique UNIQUE (service_name),
    CONSTRAINT fk_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);

CREATE TABLE vos_config.plcs
(
    plc_id SERIAL,
    plc_name VARCHAR(50) NOT NULL,
    plc_description VARCHAR(250),
    plc_protocol_type integer NOT NULL,
    plc_address_ip text NOT NULL,
    plc_address_port integer NOT NULL,
    plc_address_slave_id integer,
    site_id integer NOT NULL,
    CONSTRAINT plcs_pkey PRIMARY KEY (plc_id),
    CONSTRAINT plcs_address_unique UNIQUE (plc_address_ip, plc_address_port),
    CONSTRAINT plcs_name_unique UNIQUE (plc_name),
    CONSTRAINT fk_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);

CREATE TABLE vos_config.npl_plc
(
    npl_plc_id SERIAL,
    service_id integer NOT NULL,
    plc_id integer NOT NULL,
    CONSTRAINT npl_plc_pkey PRIMARY KEY (npl_plc_id),
    CONSTRAINT fk_npl_service FOREIGN KEY (service_id)
        REFERENCES vos_config.services (service_id),
    CONSTRAINT fk_plc FOREIGN KEY (plc_id)
        REFERENCES vos_config.plcs (plc_id)
);

CREATE TABLE vos_config.parameters
(
    parameter_id SERIAL,
    parameter_name VARCHAR(50) NOT NULL,
    parameter_value text NOT NULL,
    parameter_description VARCHAR(250),
    service_id integer,
    site_id integer,
    CONSTRAINT parameters_pkey PRIMARY KEY (parameter_id),
    CONSTRAINT parameters_name_unique UNIQUE (parameter_name, service_id),
    CONSTRAINT fk_services FOREIGN KEY (service_id)
        REFERENCES vos_config.services (service_id),
    CONSTRAINT fk_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);

CREATE TABLE vos_config.physical_units
(
    physical_unit_id SERIAL,
    physical_unit_name VARCHAR(50) NOT NULL,
    physical_unit_description VARCHAR(250),
    phisical_unit_symbol text,
    site_id integer NOT NULL,
    CONSTRAINT physical_units_pkey PRIMARY KEY (physical_unit_id),
    CONSTRAINT physical_units_name_unique UNIQUE (physical_unit_name, site_id),
    CONSTRAINT fk_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);

CREATE TABLE vos_config.calc_operands
(
    calc_operand_id SERIAL,
    calc_operand_name VARCHAR(50) NOT NULL,
    calc_operand_description VARCHAR(250),
    calc_operand_formula_prefix text,
    calc_operand_formula text,
    service_id integer NOT NULL,
    CONSTRAINT calc_operands_pkey PRIMARY KEY (calc_operand_id),
    CONSTRAINT fk_npl_service FOREIGN KEY (service_id)
        REFERENCES vos_config.services (service_id)
);

CREATE TABLE vos_config.registers
(
    register_id SERIAL,
    register_name VARCHAR(50) NOT NULL,
    register_description VARCHAR(250),
    register_reference text NOT NULL,
    register_size integer NOT NULL,
    register_type integer NOT NULL,
    service_id integer NOT NULL,
    CONSTRAINT registers_pkey PRIMARY KEY (register_id),
    CONSTRAINT fk_plc FOREIGN KEY (service_id)
        REFERENCES vos_config.services (service_id)
);

CREATE TABLE vos_config.operands
(
    operand_id SERIAL,
    equipment_id integer NOT NULL,
    operand_type integer NOT NULL,
    operand_name VARCHAR(50) NOT NULL,
    operand_description VARCHAR(250),
    operand_format text,
    operand_log_by_percentage_value double precision,
    operand_log_by_interval_value double precision,
    physical_unit_id integer,
    operand_flags bytea NOT NULL,
    operand_data_type integer NOT NULL,
    register_id integer,
    calc_operand_id integer,
    CONSTRAINT operands_pkey PRIMARY KEY (operand_id),
    CONSTRAINT fk_calc_operand FOREIGN KEY (calc_operand_id)
        REFERENCES vos_config.calc_operands (calc_operand_id),
    CONSTRAINT fk_equipment FOREIGN KEY (equipment_id)
        REFERENCES vos_config.equipments (equipment_id),
    CONSTRAINT fk_register FOREIGN KEY (register_id)
        REFERENCES vos_config.registers (register_id),
		CONSTRAINT operand_name_unique_in_site CHECK (vos_config.f_operand_name_unique(operand_id, operand_name::text, register_id, calc_operand_id)) NOT VALID
);

CREATE TABLE vos_config.modbus_chunks
(
    chunk_id SERIAL,
    chunk_size integer NOT NULL,
    chunk_start_address text NOT NULL,
    chunk_type integer NOT NULL,
    plc_id integer NOT NULL,
    CONSTRAINT modbus_chunks_pkey PRIMARY KEY (chunk_id),
    CONSTRAINT fk_plc FOREIGN KEY (plc_id)
        REFERENCES vos_config.plcs (plc_id)
);

CREATE TABLE vos_config.meters
(
    meter_id integer NOT NULL,
    meter_name VARCHAR(50) NOT NULL,
    meter_description VARCHAR(250),
    operand_id integer NOT NULL,
    meter_type integer NOT NULL,
    meter_temp_type integer NOT NULL,
    max_consumption integer NOT NULL,
    site_id integer NOT NULL,
    CONSTRAINT meters_pkey PRIMARY KEY (meter_id),
    CONSTRAINT meters_name_unique UNIQUE (meter_name, site_id),
    CONSTRAINT fk_operand FOREIGN KEY (operand_id)
        REFERENCES vos_config.operands (operand_id),
    CONSTRAINT fk_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);

SET search_path = vos_config;

CREATE TABLE vos_config.ftp_servers
(
    ftp_server_id SERIAL,
    ftp_server_name VARCHAR(50) NOT NULL,
    ftp_server_description VARCHAR(250),
    ftp_server_type integer NOT NULL,
    ftp_server_address_ip text NOT NULL,
    ftp_server_address_port integer NOT NULL,
    ftp_server_user_name text NOT NULL,
    ftp_server_user_password text NOT NULL,
    ftp_server_key_store_path text,
    ftp_server_max_parallel_actions integer NOT NULL,
    ftp_server_site_type integer NOT NULL,
    ftp_server_root_path text NOT NULL,
    site_id integer NOT NULL,
    service_id integer NOT NULL,
    CONSTRAINT ftp_servers_pkey PRIMARY KEY (ftp_server_id),
    CONSTRAINT ftp_servers_name_unique UNIQUE (ftp_server_name),
    CONSTRAINT fk_services FOREIGN KEY (service_id)
        REFERENCES vos_config.services (service_id),
    CONSTRAINT fk_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);

CREATE TABLE vos_config.ftp_place_holders
(
    ftp_place_holder_id SERIAL,
    ftp_file_type integer NOT NULL,
    site_id integer NOT NULL,
    ftp_place_holder_folder text NOT NULL,
    CONSTRAINT ftp_place_holders_pkey PRIMARY KEY (ftp_place_holder_id),
    CONSTRAINT ftp_place_holders_type_unique UNIQUE (ftp_file_type, site_id),
    CONSTRAINT fk_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);
