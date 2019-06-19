CREATE SCHEMA IF NOT EXISTS vos_data
    AUTHORIZATION postgres;

COMMENT ON SCHEMA vos_data
    IS 'schema hosting data objects';

SET search_path = vos_data;

CREATE TABLE vos_data.ftp_files
(
    ftp_file_id SERIAL,
    ftp_file_name text NOT NULL,
    ftp_file_size integer,
    ftp_file_type integer NOT NULL,
    ftp_file_row_quantity integer,
    ftp_file_server_path text NOT NULL,
    ftp_file_local_path text NOT NULL,
    ftp_file_destination_path text NOT NULL,
    ftp_file_archive_folder text,
    ftp_file_reatempt_count integer NOT NULL,
    ftp_file_reatempt_bulk_count integer NOT NULL,
    ftp_file_current_phase integer NOT NULL,
    ftp_file_last_phase_change_timestamp timestamp without time zone NOT NULL,
    ftp_file_creation_time timestamp without time zone NOT NULL DEFAULT now(),
    ftp_file_dir_phase_status integer NOT NULL,
    ftp_file_dwl_phase_status integer NOT NULL,
    ftp_file_upload_phase_status integer NOT NULL,
    ftp_file_bulk_phase_status integer NOT NULL,
    ftp_file_archive_phase_status integer NOT NULL,
    ftp_file_clean_phase_status integer NOT NULL,
    ftp_file_error_code integer NOT NULL,
    ftp_file_error_description text,
    site_id integer NOT NULL,
    service_id integer NOT NULL
);

-- Index: ftp_file_current_phase_idx

DROP INDEX vos_data.ftp_file_current_phase_idx;

CREATE INDEX ftp_file_current_phase_idx
    ON vos_data.ftp_files USING btree
    (ftp_file_current_phase)
    TABLESPACE pg_default;

-- Index: ftp_file_name_idx

DROP INDEX vos_data.ftp_file_name_idx;

CREATE UNIQUE INDEX ftp_file_name_idx
    ON vos_data.ftp_files USING btree
    (ftp_file_name COLLATE pg_catalog."default")
    TABLESPACE pg_default;

CREATE TABLE vos_data.operands_data
(
    operands_data_id SERIAL,
    operand_id integer NOT NULL,
    sample_time timestamp without time zone NOT NULL,
    sample_value text NOT NULL,
    manual_mode boolean NOT NULL,
    ftp_file_id integer
);

CREATE TABLE vos_data.meters_data
(
    id SERIAL,
    meter_id integer NOT NULL,
    sample_time timestamp without time zone NOT NULL,
    energy_data double precision NOT NULL,
    taoz integer NOT NULL,
    temp_type integer NOT NULL,
    is_automatic integer,
    ftp_file_id integer
);

CREATE TABLE vos_data.meters_data_extracts
(
    meters_data_extracts_id SERIAL,
    site_id integer,
    meter_id integer NOT NULL,
    column_name text NOT NULL,
    column_ordinal integer NOT NULL,
    CONSTRAINT meters_data_extracts_pkey PRIMARY KEY (meters_data_extracts_id)
);

CREATE TABLE vos_data.operands_data_extracts
(
    operands_data_extract_id SERIAL,
    operands_data_extrac_name VARCHAR(50) NOT NULL,
    operands_data_extrac_description VARCHAR(250),
    operands_data_extract_type integer NOT NULL,
    site_id integer NOT NULL,
    CONSTRAINT operands_data_extracts_pkey PRIMARY KEY (operands_data_extract_id),
    CONSTRAINT fk_operands_data_extracts_sites FOREIGN KEY (site_id)
        REFERENCES vos_config.sites (site_id)
);

CREATE TABLE vos_data.operands_data_extracts_members
(
    operands_data_extracts_member_id SERIAL,
    operands_data_extract_id integer,
    operand_id integer NOT NULL,
    column_name text NOT NULL,
    column_ordinal integer NOT NULL,
    CONSTRAINT operands_data_extracts_members_pkey PRIMARY KEY (operands_data_extracts_member_id)
);