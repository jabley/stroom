/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

-- Global Property
RENAME TABLE GLOB_PROP TO OLD_GLOB_PROP;
CREATE TABLE GLOB_PROP (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  VAL 				longtext NOT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY        NAME (NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO GLOB_PROP (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, VAL) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, VAL FROM OLD_GLOB_PROP;
DROP TABLE OLD_GLOB_PROP;

-- Text Converter
ALTER TABLE TXT_CONV DROP INDEX UUID;
ALTER TABLE TXT_CONV DROP FOREIGN KEY TXT_CONV_FK_FOLDER_ID;
RENAME TABLE TXT_CONV TO OLD_TXT_CONV;
CREATE TABLE TXT_CONV (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  UUID              varchar(255) NOT NULL,
  DESCRIP 			longtext,
  CONV_TP 			tinyint(4) NOT NULL,
  DAT 				longtext,
  FK_FOLDER_ID 	    int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY		NAME (FK_FOLDER_ID, NAME),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT 	    TXT_CONV_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO TXT_CONV (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, CONV_TP, DAT, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, CONV_TP, DAT, FK_FOLDER_ID FROM OLD_TXT_CONV;
DROP TABLE OLD_TXT_CONV;

-- XSLT
ALTER TABLE XSLT DROP INDEX UUID;
ALTER TABLE XSLT DROP FOREIGN KEY XSLT_FK_FOLDER_ID;
RENAME TABLE XSLT TO OLD_XSLT;
CREATE TABLE XSLT (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  UUID              varchar(255) NOT NULL,
  DESCRIP 			longtext,
  DAT 				longtext,
  FK_FOLDER_ID 	    int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY		NAME (FK_FOLDER_ID, NAME),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT 	    XSLT_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO XSLT (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, DAT, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, DAT, FK_FOLDER_ID FROM OLD_XSLT;
DROP TABLE OLD_XSLT;

-- XML Schema
ALTER TABLE XML_SCHEMA DROP INDEX UUID;
ALTER TABLE XML_SCHEMA DROP FOREIGN KEY XML_SCHEMA_FK_FOLDER_ID;
RENAME TABLE XML_SCHEMA TO OLD_XML_SCHEMA;
CREATE TABLE XML_SCHEMA (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  UUID              varchar(255) NOT NULL,
  DESCRIP 			longtext,
  DAT 				longtext,
  DEPRC 			bit(1) NOT NULL,
  SCHEMA_GRP 		varchar(255) DEFAULT NULL,
  NS 				varchar(255) DEFAULT NULL,
  SYSTEM_ID 		varchar(255) DEFAULT NULL,
  FK_FOLDER_ID  	int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY		NAME (FK_FOLDER_ID, NAME),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT 		XML_SCHEMA_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO XML_SCHEMA (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, DAT, DEPRC, SCHEMA_GRP, NS, SYSTEM_ID, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, DAT, DEPRC, SCHEMA_GRP, NS, SYSTEM_ID, FK_FOLDER_ID FROM OLD_XML_SCHEMA;
DROP TABLE OLD_XML_SCHEMA;

-- Pipeline
ALTER TABLE STRM_PROC DROP FOREIGN KEY STRM_PROC_FK_PIPE_ID;

ALTER TABLE PIPE DROP INDEX UUID;
ALTER TABLE PIPE DROP FOREIGN KEY PIPE_FK_FOLDER_ID;
RENAME TABLE PIPE TO OLD_PIPE;
CREATE TABLE PIPE (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  UUID              varchar(255) NOT NULL,
  DESCRIP 			longtext,
  PARNT_PIPE        longtext,
  DAT 				longtext,
  PIPE_TP 			varchar(255) DEFAULT NULL,
  FK_FOLDER_ID  	int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY		NAME (FK_FOLDER_ID, NAME),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT 		PIPE_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO PIPE (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, PARNT_PIPE, DAT, PIPE_TP, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, PARNT_PIPE, DAT, PIPE_TP, FK_FOLDER_ID FROM OLD_PIPE;
DROP TABLE OLD_PIPE;

ALTER TABLE STRM_PROC ADD CONSTRAINT STRM_PROC_FK_PIPE_ID FOREIGN KEY (FK_PIPE_ID) REFERENCES PIPE(ID);

-- Stream Processor Filter
ALTER TABLE STRM_TASK DROP FOREIGN KEY STRM_TASK_FK_STRM_PROC_FILT_ID;

ALTER TABLE STRM_PROC_FILT DROP FOREIGN KEY STRM_PROC_FILT_FK_STRM_PROC_ID;
ALTER TABLE STRM_PROC_FILT DROP FOREIGN KEY STRM_PROC_FILT_FK_STRM_PROC_FILT_TRAC_ID;
RENAME TABLE STRM_PROC_FILT TO OLD_STRM_PROC_FILT;
CREATE TABLE STRM_PROC_FILT (
  ID 				        int(11) NOT NULL AUTO_INCREMENT,
  VER				        tinyint(4) NOT NULL,
  CRT_MS 			        bigint(20) DEFAULT NULL,
  CRT_USER			        varchar(255) DEFAULT NULL,
  UPD_MS 			        bigint(20) DEFAULT NULL,
  UPD_USER 			        varchar(255) DEFAULT NULL,
  DAT 				        longtext NOT NULL,
  PRIOR 			        int(11) NOT NULL,
  FK_STRM_PROC_ID 	        int(11) NOT NULL,
  FK_STRM_PROC_FILT_TRAC_ID	int(11) NOT NULL,
  ENBL				        bit(1) NOT NULL,
  PRIMARY KEY               (ID),
  CONSTRAINT STRM_PROC_FILT_FK_STRM_PROC_ID FOREIGN KEY (FK_STRM_PROC_ID) REFERENCES STRM_PROC (ID),
  CONSTRAINT STRM_PROC_FILT_FK_STRM_PROC_FILT_TRAC_ID FOREIGN KEY (FK_STRM_PROC_FILT_TRAC_ID) REFERENCES STRM_PROC_FILT_TRAC (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO STRM_PROC_FILT (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, DAT, PRIOR, FK_STRM_PROC_ID, FK_STRM_PROC_FILT_TRAC_ID, ENBL) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, DAT, PRIOR, FK_STRM_PROC_ID, FK_STRM_PROC_FILT_TRAC_ID, ENBL FROM OLD_STRM_PROC_FILT;
DROP TABLE OLD_STRM_PROC_FILT;

ALTER TABLE STRM_TASK ADD CONSTRAINT STRM_TASK_FK_STRM_PROC_FILT_ID FOREIGN KEY (FK_STRM_PROC_FILT_ID) REFERENCES STRM_PROC_FILT(ID);

-- Statistic datasource
ALTER TABLE STAT_DAT_SRC DROP INDEX UUID;
ALTER TABLE STAT_DAT_SRC DROP FOREIGN KEY STAT_DAT_SRC_FK_FOLDER_ID;
RENAME TABLE STAT_DAT_SRC TO OLD_STAT_DAT_SRC;
CREATE TABLE STAT_DAT_SRC(
  ID 				    int(11) NOT NULL AUTO_INCREMENT,
  VER				    tinyint(4) NOT NULL,
  CRT_MS 			    bigint(20) DEFAULT NULL,
  CRT_USER			    varchar(255),
  UPD_MS 			    bigint(20) DEFAULT NULL,
  UPD_USER 			    varchar(255),
  NAME 				    varchar(255) NOT NULL,
  UUID                  varchar(255) NOT NULL,
  DESCRIP 			    longtext,
  ENGINE_NAME		    varchar(20) NOT NULL,
  PRES			       	bigint(20) NOT NULL,
  ENBL 			    	bit(1) NOT NULL,
  STAT_TP		    	tinyint(4) NOT NULL,
  ROLLUP_TP		    	tinyint(4) NOT NULL,
  DAT 			    	longtext,
  FK_FOLDER_ID 	        int(11) NOT NULL,
  PRIMARY KEY           (ID),
  UNIQUE KEY		    NAME (FK_FOLDER_ID, NAME, ENGINE_NAME),
  UNIQUE KEY            UUID (UUID),
  CONSTRAINT 		    STAT_DAT_SRC_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO STAT_DAT_SRC (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, ENGINE_NAME, PRES, ENBL, STAT_TP, ROLLUP_TP, DAT, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, ENGINE_NAME, PRES, ENBL, STAT_TP, ROLLUP_TP, DAT, FK_FOLDER_ID FROM OLD_STAT_DAT_SRC;
DROP TABLE OLD_STAT_DAT_SRC;

-- Dashboard
ALTER TABLE QUERY DROP FOREIGN KEY QUERY_FK_DASH_ID;

ALTER TABLE DASH DROP INDEX UUID;
ALTER TABLE DASH DROP FOREIGN KEY DASH_FK_FOLDER_ID;
RENAME TABLE DASH TO OLD_DASH;
CREATE TABLE DASH (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  UUID 				varchar(255) DEFAULT NULL,
  DAT 				longtext,
  FK_FOLDER_ID 	    int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY		NAME (FK_FOLDER_ID, NAME),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT 		DASH_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO DASH (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DAT, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DAT, FK_FOLDER_ID FROM OLD_DASH;
DROP TABLE OLD_DASH;

ALTER TABLE QUERY ADD CONSTRAINT QUERY_FK_DASH_ID FOREIGN KEY (FK_DASH_ID) REFERENCES DASH(ID);

-- Resource
ALTER TABLE SCRIPT DROP FOREIGN KEY SCRIPT_FK_RES_ID;

RENAME TABLE RES TO OLD_RES;
CREATE TABLE RES (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  DAT 				longtext,
  PRIMARY KEY       (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO RES (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, DAT) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, DAT FROM OLD_RES;
DROP TABLE OLD_RES;

ALTER TABLE SCRIPT ADD CONSTRAINT SCRIPT_FK_RES_ID FOREIGN KEY (FK_RES_ID) REFERENCES RES(ID);

-- Script
ALTER TABLE SCRIPT DROP INDEX UUID;
ALTER TABLE SCRIPT DROP FOREIGN KEY SCRIPT_FK_FOLDER_ID;
ALTER TABLE SCRIPT DROP FOREIGN KEY SCRIPT_FK_RES_ID;
RENAME TABLE SCRIPT TO OLD_SCRIPT;
CREATE TABLE SCRIPT (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  UUID 				varchar(255) DEFAULT NULL,
  DESCRIP 			longtext,
  DEP   		    longtext,
  FK_RES_ID			int(11) DEFAULT NULL,
  FK_FOLDER_ID  	int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY		NAME (FK_FOLDER_ID, NAME),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT        SCRIPT_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID),
  CONSTRAINT        SCRIPT_FK_RES_ID FOREIGN KEY (FK_RES_ID) REFERENCES RES (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO SCRIPT (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, DEP, FK_RES_ID, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, DEP, FK_RES_ID, FK_FOLDER_ID FROM OLD_SCRIPT;
DROP TABLE OLD_SCRIPT;

-- Vis
ALTER TABLE VIS DROP INDEX UUID;
ALTER TABLE VIS DROP FOREIGN KEY VIS_FK_FOLDER_ID;
RENAME TABLE VIS TO OLD_VIS;
CREATE TABLE VIS (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  UUID 				varchar(255) DEFAULT NULL,
  DESCRIP 			longtext,
  FUNC_NAME			varchar(255) DEFAULT NULL,
  SCRIPT		    longtext,
  SETTINGS			longtext,
  FK_FOLDER_ID 	    int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY		NAME (FK_FOLDER_ID, NAME),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT        VIS_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO VIS (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, FUNC_NAME, SCRIPT, SETTINGS, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DESCRIP, FUNC_NAME, SCRIPT, SETTINGS, FK_FOLDER_ID FROM OLD_VIS;
DROP TABLE OLD_VIS;

-- Dictionary
ALTER TABLE DICT DROP INDEX UUID;
ALTER TABLE DICT DROP FOREIGN KEY DICT_FK_FOLDER_ID;
RENAME TABLE DICT TO OLD_DICT;
CREATE TABLE DICT (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) NOT NULL,
  UUID 				varchar(255) DEFAULT NULL,
  DAT 				longtext,
  FK_FOLDER_ID 	    int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY		NAME (FK_FOLDER_ID, NAME),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT 		DICT_FK_FOLDER_ID FOREIGN KEY (FK_FOLDER_ID) REFERENCES FOLDER (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO DICT (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DAT, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, DAT, FK_FOLDER_ID FROM OLD_DICT;
DROP TABLE OLD_DICT;

-- Query
ALTER TABLE QUERY DROP INDEX UUID;
ALTER TABLE QUERY DROP FOREIGN KEY QUERY_FK_DASH_ID;
RENAME TABLE QUERY TO OLD_QUERY;
CREATE TABLE QUERY (
  ID 				int(11) NOT NULL AUTO_INCREMENT,
  VER				tinyint(4) NOT NULL,
  CRT_MS 			bigint(20) DEFAULT NULL,
  CRT_USER			varchar(255) DEFAULT NULL,
  UPD_MS 			bigint(20) DEFAULT NULL,
  UPD_USER 			varchar(255) DEFAULT NULL,
  NAME 				varchar(255) DEFAULT NULL,
  UUID 				varchar(255) DEFAULT NULL,
  FK_DASH_ID		int(11) DEFAULT NULL,
  DAT 				longtext,
  FAVOURITE         bit(1) NOT NULL,
  FK_FOLDER_ID 	    int(11) DEFAULT NULL,
  PRIMARY KEY       (ID),
  UNIQUE KEY        UUID (UUID),
  CONSTRAINT		QUERY_FK_DASH_ID FOREIGN KEY (FK_DASH_ID) REFERENCES DASH (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO QUERY (ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, FK_DASH_ID, DAT, FAVOURITE, FK_FOLDER_ID) SELECT ID, VER, CRT_MS, CRT_USER, UPD_MS, UPD_USER, NAME, UUID, FK_DASH_ID, DAT, NAME IS NOT NULL, FK_FOLDER_ID FROM OLD_QUERY;
DROP TABLE OLD_QUERY;
