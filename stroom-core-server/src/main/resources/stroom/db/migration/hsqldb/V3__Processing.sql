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
-- Processing
----------------------------------------

-- Job
create table JB (ID INT generated by default as identity (start with 1), VER TINYINT not null, CRT_DT TIMESTAMP not null, CRT_USER varchar(255), UPD_DT TIMESTAMP not null, UPD_USER varchar(255), NAME varchar(255) not null, ENBL boolean not null, primary key (ID));
alter table JB add constraint UK_6xmeusl3ar71f8aq6l9d646b3  unique (NAME);

-- Job Node
create table JB_ND (ID INT generated by default as identity (start with 1), VER TINYINT not null, CRT_DT TIMESTAMP not null, CRT_USER varchar(255), UPD_DT TIMESTAMP not null, UPD_USER varchar(255), JB_TP tinyint not null, ENBL boolean not null, SCHEDULE varchar(255), TASK_LMT INT not null check (TASK_LMT>=0), FK_JB_ID INT not null, FK_ND_ID INT not null, primary key (ID));
alter table JB_ND add constraint UK_qwea9qiygjb9tohlta8a0pj4g  unique (FK_ND_ID, FK_JB_ID);
alter table JB_ND add constraint FK_1q2xt2egwglqjx0590vu8808u foreign key (FK_JB_ID) references JB;
alter table JB_ND add constraint FK_5ttoa4b1w5ft3j1b9v9u3febs foreign key (FK_ND_ID) references ND;

-- Stream Processor
create table STRM_PROC (ID INT generated by default as identity (start with 1), VER TINYINT not null, CRT_DT TIMESTAMP not null, CRT_USER varchar(255), UPD_DT TIMESTAMP not null, UPD_USER varchar(255), ENBL boolean, TASK_TP varchar(255), FK_PIPE_ID INT, primary key (ID));
alter table STRM_PROC add constraint FK_c4r1w5p2ge7tbxdg1asnsyru8 foreign key (FK_PIPE_ID) references PIPE;

-- Stream Processor Filter Tracker
create table STRM_PROC_FILT_TRAC (ID INT generated by default as identity (start with 1), VER TINYINT not null, EVT_CT bigint, LAST_POLL_MS bigint, LAST_POLL_TASK_CT integer, MAX_STRM_CRT_MS bigint, MIN_EVT_ID bigint not null, MIN_STRM_CRT_MS bigint, MIN_STRM_ID bigint not null, STAT varchar(255), STRM_CT bigint, STRM_CRT_MS bigint, primary key (ID));

-- Stream Processor Filter
create table STRM_PROC_FILT (ID INT generated by default as identity (start with 1), VER TINYINT not null, CRT_DT TIMESTAMP not null, CRT_USER varchar(255), UPD_DT TIMESTAMP not null, UPD_USER varchar(255), DAT clob(2147483647) not null, ENBL boolean, PRIOR integer not null, FK_STRM_PROC_ID INT not null, FK_STRM_PROC_FILT_TRAC_ID INT not null, primary key (ID));
alter table STRM_PROC_FILT add constraint UK_eevicit22vnh1rci67e3bawvm  unique (FK_STRM_PROC_FILT_TRAC_ID);
alter table STRM_PROC_FILT add constraint FK_a72k67j0lty6yxemtwoa3onuq foreign key (FK_STRM_PROC_ID) references STRM_PROC;
alter table STRM_PROC_FILT add constraint FK_eevicit22vnh1rci67e3bawvm foreign key (FK_STRM_PROC_FILT_TRAC_ID) references STRM_PROC_FILT_TRAC;