-- Drop table

-- DROP TABLE public.devices

CREATE TABLE public.devices (
	id serial NOT NULL,
	"TYPE" varchar(15) NOT NULL,
	"name" varchar(25) NULL,
	active_flag varchar(3) NOT NULL,
	description varchar(50) NULL,
	CONSTRAINT constraint_8 PRIMARY KEY (id)
)
WITH (
	OIDS=FALSE
) ;
CREATE UNIQUE INDEX primary_key_8 ON devices USING btree (id) ;

-- Drop table

-- DROP TABLE public.points

CREATE TABLE public.points (
	id bigserial NOT NULL,
	device_id int2 NOT NULL,
	point varchar(40) NULL,
	"TIMESTAMP" timestamp NULL,
	CONSTRAINT constraint_8c PRIMARY KEY (id)
)
WITH (
	OIDS=FALSE
) ;
CREATE INDEX points_device_id_idx ON points USING btree (device_id) ;
CREATE INDEX points_timestamp_idx ON points USING btree ("TIMESTAMP") ;
CREATE UNIQUE INDEX primary_key_485 ON points USING btree (id) ;

-- Drop table

-- DROP TABLE public.sessions

CREATE TABLE public.sessions (
	session_id varchar(70) NOT NULL,
	user_id int4 NOT NULL,
	exp_date timestamp NULL
)
WITH (
	OIDS=FALSE
) ;
CREATE INDEX sessions_session_id_idx ON sessions USING btree (session_id) ;

-- Drop table

-- DROP TABLE public.users

CREATE TABLE public.users (
	id serial NOT NULL,
	login varchar(15) NOT NULL,
	pass varchar(70) NOT NULL,
	active_flag bpchar(3) NOT NULL,
	user_type varchar(10) NULL,
	last_visit timestamp NULL,
	CONSTRAINT constraint_4 PRIMARY KEY (id)
)
WITH (
	OIDS=FALSE
) ;
CREATE UNIQUE INDEX primary_key_4 ON users USING btree (id) ;
CREATE UNIQUE INDEX primary_key_61 ON users USING btree (id) ;
