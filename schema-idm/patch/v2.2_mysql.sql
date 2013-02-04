ALTER TABLE ATTRIBUTE_MAP ADD COLUMN DEFAULT_VALUE varchar(32) NULL;

insert into METADATA_TYPE(TYPE_ID, DESCRIPTION, SYNC_MANAGED_SYS) values('WORKFLOW','Workflow', 0);
insert into METADATA_ELEMENT(metadata_id, type_id, attribute_name) values ('430','WORKFLOW','LAUNCH_URL');


INSERT INTO RESOURCE_TYPE (RESOURCE_TYPE_ID, DESCRIPTION, METADATA_TYPE_ID, PROVISION_RESOURCE) VALUES('WORKFLOW', 'Workflow', 'WORKFLOW', 0);
insert into RES (RESOURCE_ID, RESOURCE_TYPE_ID,NAME, DISPLAY_ORDER, DESCRIPTION) VALUES ('260', 'WORKFLOW', 'NEW_USER', 1, 'NEW USER');
insert into RES (RESOURCE_ID, RESOURCE_TYPE_ID,NAME, DISPLAY_ORDER, DESCRIPTION) VALUES ('261', 'WORKFLOW', 'TERMINATE_USER', 1, 'TERMINATE USER');
insert into RES (RESOURCE_ID, RESOURCE_TYPE_ID,NAME, DISPLAY_ORDER, DESCRIPTION) VALUES ('262', 'WORKFLOW', 'CHANGE_ACCESS', 1, 'CHANGE ACCESS');

INSERT INTO APPROVER_ASSOC (APPROVER_ASSOC_ID,REQUEST_TYPE,APPROVER_LEVEL,ASSOCIATION_TYPE,ASSOCIATION_OBJ_ID,APPROVER_USER_ID,ON_APPROVE_NOTIFY_USER_ID,ON_REJECT_NOTIFY_USER_ID,APPROVE_NOTIFY_USER_TYPE,REJECT_NOTIFY_USER_TYPE,ACTN,APPROVER_ROLE_ID,APPROVER_ROLE_DOMAIN,APPLY_DELEGATION_FILTER) VALUES ('402881063a00b213013a00e1546c0007','260',1,'USER',NULL,'3000','','','TARGET_USER','REQUESTOR',NULL,NULL,NULL,0);
INSERT INTO APPROVER_ASSOC (APPROVER_ASSOC_ID,REQUEST_TYPE,APPROVER_LEVEL,ASSOCIATION_TYPE,ASSOCIATION_OBJ_ID,APPROVER_USER_ID,ON_APPROVE_NOTIFY_USER_ID,ON_REJECT_NOTIFY_USER_ID,APPROVE_NOTIFY_USER_TYPE,REJECT_NOTIFY_USER_TYPE,ACTN,APPROVER_ROLE_ID,APPROVER_ROLE_DOMAIN,APPLY_DELEGATION_FILTER) VALUES ('402881063a00b213013a00e3e6330009','261',1,'USER',NULL,'3000','','','USER','REQUESTOR',NULL,NULL,NULL,0);
INSERT INTO APPROVER_ASSOC (APPROVER_ASSOC_ID,REQUEST_TYPE,APPROVER_LEVEL,ASSOCIATION_TYPE,ASSOCIATION_OBJ_ID,APPROVER_USER_ID,ON_APPROVE_NOTIFY_USER_ID,ON_REJECT_NOTIFY_USER_ID,APPROVE_NOTIFY_USER_TYPE,REJECT_NOTIFY_USER_TYPE,ACTN,APPROVER_ROLE_ID,APPROVER_ROLE_DOMAIN,APPLY_DELEGATION_FILTER) VALUES ('402881063a00b213013a00e49244000b','262',1,'USER',NULL,'3000','','','TARGET_USER','REQUESTOR',NULL,NULL,NULL,0);


/* oct 3 - changes */
ALTER TABLE PROV_REQUEST
   ADD COLUMN REQUEST_TITLE varchar(200) NULL;

CREATE TABLE REPORT_INFO(
	REPORT_INFO_ID varchar(32) NOT NULL,
	REPORT_NAME VARCHAR(64) NOT NULL UNIQUE,
    DATASOURCE_FILE_PATH VARCHAR(255) NOT NULL,
    REPORT_FILE_PATH VARCHAR(255),
    PRIMARY KEY (REPORT_INFO_ID)
)Engine=InnoDB;

CREATE TABLE REPORT_CRITERIA_PARAMETER (
    RCP_ID VARCHAR(32) NOT NULL,
    REPORT_INFO_ID VARCHAR(32) NOT NULL,
	  PARAM_NAME VARCHAR(64) NOT NULL,
    PARAM_VALUE VARCHAR(64),
    PRIMARY KEY (RCP_ID),
    CONSTRAINT FK_REPORT_CRITERIA_PARAMETER_REPORT_INFO
       FOREIGN KEY (REPORT_INFO_ID)
       REFERENCES REPORT_INFO(REPORT_INFO_ID),
    UNIQUE KEY UK_REPORT_INFO_ID_PARAM_NAME (REPORT_INFO_ID, PARAM_NAME)
) Engine=InnoDB;

create table WEB_RESOURCE_ATTRIBUTE(
	ATTRIBUTE_MAP_ID      varchar(32) NOT NULL,
	RESOURCE_ID           varchar(32) NOT NULL,
	TARGET_ATTRIBUTE_NAME varchar(100) NOT NULL,
	AM_ATTRIBUTE_NAME     varchar(100) NULL,
	AM_POLICY_URL         varchar(100) NULL,
	PRIMARY KEY(ATTRIBUTE_MAP_ID),
	UNIQUE (RESOURCE_ID, TARGET_ATTRIBUTE_NAME)
)Engine=InnoDB;

ALTER TABLE NOTIFICATION_CONFIG
  ADD COLUMN SEARCH_COMPANY_ID   VARCHAR(32) NULL,
  ADD COLUMN SEARCH_LAST_NAME    VARCHAR(50) NULL,
  ADD COLUMN SEARCH_DEPT_ID      VARCHAR(32) NULL,
  ADD COLUMN SEARCH_DIVISION     VARCHAR(32) NULL,
  ADD COLUMN SEARCH_USER_STATUS  VARCHAR(20) NULL,
  ADD COLUMN MSG_TEXT TEXT NULL;

ALTER TABLE NOTIFICATION_CONFIG
  ADD COLUMN TEMPLATE_URL   VARCHAR(80) NULL;


ALTER TABLE LOGIN
  ADD COLUMN LOGIN_ATTR_IN_TARGET_SYS   VARCHAR(40) NULL;

insert into MENU (menu_id, menu_group, menu_name,menu_desc,url,LANGUAGE_CD, DISPLAY_ORDER) values('ROLE_POLICY','SECURITY_ROLE','Policy','Role Policy','rolePolicy.cnt', 'en',3);
INSERT INTO PERMISSIONS(MENU_ID,ROLE_ID,SERVICE_ID) VALUES('ROLE_POLICY','SUPER_SEC_ADMIN','IDM');

insert into CATEGORY (category_id, parent_id, category_name, show_list) values ('GROUP_TYPE', 'ROOT', 'Group Type',0);

insert into METADATA_TYPE(TYPE_ID, DESCRIPTION, SYNC_MANAGED_SYS) values('AD_GROUP_TYPE','AD GROUP TYPE', 0);
insert into METADATA_TYPE(TYPE_ID, DESCRIPTION, SYNC_MANAGED_SYS) values('LDAP_GROUP_TYPE','LDAP GROUP TYPE', 0);
insert into METADATA_TYPE(TYPE_ID, DESCRIPTION, SYNC_MANAGED_SYS) values('DEFAULT_GROUP','DEFAULT GROUP TYPE', 0);

insert into CATEGORY_TYPE (category_id, type_id) values('GROUP_TYPE','AD_GROUP_TYPE');
insert into CATEGORY_TYPE (category_id, type_id) values('GROUP_TYPE','LDAP_GROUP_TYPE');
insert into CATEGORY_TYPE (category_id, type_id) values('GROUP_TYPE','DEFAULT_GROUP');

ALTER TABLE PROV_REQUEST
  ADD COLUMN REQUESTOR_FIRSTNAME VARCHAR(50) NULL,
  ADD COLUMN REQUESTOR_LASTNAME  VARCHAR(50) NULL;

ALTER TABLE PROV_REQUEST
   MODIFY COLUMN REQUEST_TYPE varchar(50) NULL;


ALTER TABLE PROV_REQUEST
  ADD COLUMN WORKFLOW_NAME VARCHAR(50) NULL;


DROP TABLE AUTH_STATE;

CREATE TABLE AUTH_STATE (
       AUTH_STATE_ID        varchar(32) NOT NULL,
       USER_ID              varchar(32) NOT NULL,
       AUTH_STATE           numeric(5,1) NULL,
       TOKEN                varchar(2000) NULL,
       AA                   varchar(20) NULL,
       EXPIRATION           numeric(18,0) NULL,
       LAST_LOGIN	    datetime NULL,
       IP_ADDRESS	    varchar(20) NULL,
       PRIMARY KEY (AUTH_STATE_ID)
) ENGINE=InnoDB;


insert into BATCH_CONFIG(TASK_ID, TASK_NAME, FREQUENCY_UNIT_OF_MEASURE, ENABLED, TASK_URL, EXECUTION_ORDER) VALUES('109','SESSION_CLEANUP', 'MINUTE','0','batch/SessionCleanup.groovy','2');

/* migrate system connections to new table structure */
update MANAGED_SYS
  SET HNDLR_5 = HNDLR_1
  WHERE HNDLR_1 IS NOT NULL;

ALTER TABLE MANAGED_SYS
  DROP COLUMN HNDLR_1;
ALTER TABLE MANAGED_SYS
  DROP COLUMN HNDLR_2;
ALTER TABLE MANAGED_SYS
  DROP COLUMN HNDLR_3;
ALTER TABLE MANAGED_SYS
  DROP COLUMN HNDLR_4;

ALTER TABLE MANAGED_SYS
  ADD COLUMN SEARCH_HNDLR	 VARCHAR(100) NULL,
	ADD COLUMN   LOOKUP_HNDLR							VARCHAR(100) NULL,
 	ADD COLUMN   TEST_CONNECTION_HNDLR				VARCHAR(100) NULL,
 	ADD COLUMN   RECONCILE_RESOURCE_HNDLR				VARCHAR(100) NULL;


/* feb 02 */
UPDATE MENU
 SET url = 'userSearch.cnt'
WHERE MENU_ID = 'QUERYUSER';