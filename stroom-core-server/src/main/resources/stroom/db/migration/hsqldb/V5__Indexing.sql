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

----------------------------------------
-- Indexing
----------------------------------------

-- Index
create table IDX (ID INT generated by default as identity (start with 1), VER TINYINT not null, CRT_DT TIMESTAMP not null, CRT_USER varchar(255), UPD_DT TIMESTAMP not null, UPD_USER varchar(255), NAME varchar(255) not null, UUID varchar(255) not null, PART_BY tinyint, DESCRIP clob(255), FLDS clob(2147483647), MAX_DOC integer not null, PART_SZ integer not null, RETEN_DAY_AGE INT, MAX_SHRD integer not null, FK_FOLDER_ID INT, primary key (ID));
alter table IDX add constraint UK_3tphq2pd594ip9tj3gt38mqsm  unique (FK_FOLDER_ID, NAME);
alter table IDX add constraint UK_IDX_UUID unique (UUID);
alter table IDX add constraint FK_b1cr9k5hhcd6341adxdi6oj0y foreign key (FK_FOLDER_ID) references FOLDER;

-- Index Shard
create table IDX_SHRD (ID INT generated by default as identity (start with 1), VER TINYINT not null, CRT_DT TIMESTAMP not null, CRT_USER varchar(255), UPD_DT TIMESTAMP not null, UPD_USER varchar(255), CMT_DOC_CT INT, CMT_DUR_MS BIGINT, CMT_MS BIGINT, DOC_CT INT not null, FILE_SZ BIGINT, IDX_VER varchar(255), PART varchar(255) not null, PART_FROM_DT timestamp, PART_TO_DT timestamp, STAT tinyint not null, FK_IDX_ID INT not null, FK_ND_ID INT not null, FK_VOL_ID INT not null, primary key (ID));
alter table IDX_SHRD add constraint FK_exm5l7g8kr628g5rqeq0wgl3x foreign key (FK_IDX_ID) references IDX;
alter table IDX_SHRD add constraint FK_qm0ery3gc54xmx5h7y4jdy4p4 foreign key (FK_ND_ID) references ND;
alter table IDX_SHRD add constraint FK_kjp2wdnh23lcdo5pkayqafvva foreign key (FK_VOL_ID) references VOL;

-- Index Volume
create table IDX_VOL (FK_IDX_ID INT not null, FK_VOL_ID INT not null, primary key (FK_IDX_ID, FK_VOL_ID));
alter table IDX_VOL add constraint FK_chph4pc0vcwtx0hwjgrd5wgp3 foreign key (FK_VOL_ID) references VOL;
alter table IDX_VOL add constraint FK_7jxcd9orytp9kqfln16nugpg5 foreign key (FK_IDX_ID) references IDX;