CREATE TABLE IAMUSER.IDENTITY_QUEST_GRP (
       IDENTITY_QUEST_GRP_ID VARCHAR2(32) NOT NULL,
       NAME                 VARCHAR2(60) NULL,
       STATUS               VARCHAR2(20) NULL,
       COMPANY_OWNER_ID     VARCHAR2(32) NULL,
       CREATE_DATE          DATE NULL,
       CREATED_BY           VARCHAR2(20) NULL,
       LAST_UPDATE          DATE NULL,
       LAST_UPDATED_BY      VARCHAR2(20) NULL,
       PRIMARY KEY (IDENTITY_QUEST_GRP_ID)
); 


CREATE TABLE IAMUSER.IDENTITY_QUESTION (
       IDENTITY_QUESTION_ID 	VARCHAR2(32) NOT NULL,
       IDENTITY_QUEST_GRP_ID 	VARCHAR2(32) NULL,
       QUESTION_TEXT        	VARCHAR2(255) NULL,
       REQUIRED             	INTEGER NULL,
       ACTIVE             		INTEGER NULL,
 				USER_ID			VARCHAR2(32),	
       PRIMARY KEY (IDENTITY_QUESTION_ID),
       CONSTRAINT FK_ID_QST_IDENTITY_QUEST_GRP
              FOREIGN KEY (IDENTITY_QUEST_GRP_ID) REFERENCES IAMUSER.IDENTITY_QUEST_GRP(IDENTITY_QUEST_GRP_ID)
);


CREATE TABLE IAMUSER.USER_IDENTITY_ANS (
       IDENTITY_ANS_ID      VARCHAR2(32) NOT NULL,
       IDENTITY_QUESTION_ID VARCHAR2(32) NULL,
	   QUESTION_TEXT		VARCHAR2(255) null,
       USER_ID              VARCHAR2(32) NULL,
       QUESTION_ANSWER      VARCHAR2(255) NULL,
       PRIMARY KEY (IDENTITY_ANS_ID), 
       CONSTRAINT FK_USR_ID_ANS_ID_QUESTION
       		FOREIGN KEY (IDENTITY_QUESTION_ID)  REFERENCES IAMUSER.IDENTITY_QUESTION(IDENTITY_QUESTION_ID), 
       CONSTRAINT FK_USR_ID_ANS_USERS
       		FOREIGN KEY (USER_ID) REFERENCES IAMUSER.USERS(USER_ID)
); 


CREATE TABLE IAMUSER.POLICY_DEF (
       POLICY_DEF_ID        VARCHAR2(32) NOT NULL,
       NAME                 VARCHAR2(60) NULL,
       DESCRIPTION          VARCHAR2(255) NULL,
       POLICY_TYPE          VARCHAR2(20) NULL,
       LOCATION_TYPE        VARCHAR2(20) NULL,
       POLICY_RULE          VARCHAR2(500) NULL,
       POLICY_HANDLER       VARCHAR2(255) NULL,
	POLICY_ADVISE_HANDLER	VARCHAR2(255) NULL,
       PRIMARY KEY (POLICY_DEF_ID)
); 


CREATE TABLE IAMUSER.POLICY (
       POLICY_ID            VARCHAR2(32) NOT NULL,
       POLICY_DEF_ID        VARCHAR2(32) NULL,
       NAME                 VARCHAR2(60) NULL,
       DESCRIPTION          VARCHAR2(255) NULL,
       STATUS           	INTEGER NULL,
       CREATE_DATE          DATE NULL,
       CREATED_BY           VARCHAR2(20) NULL,
       LAST_UPDATE          DATE NULL,
       LAST_UPDATED_BY      VARCHAR2(20) NULL,
	   RULE_SRC_URL         VARCHAR2(80) NULL,
	   RULE_TEXT  		    CLOB NULL,
       PRIMARY KEY (POLICY_ID), 
       CONSTRAINT FK_POLICY_POLICY_DEF
       	FOREIGN KEY (POLICY_DEF_ID) REFERENCES IAMUSER.POLICY_DEF(POLICY_DEF_ID)
); 



CREATE TABLE IAMUSER.POLICY_DEF_PARAM (
       DEF_PARAM_ID         VARCHAR2(32) NOT NULL,
       POLICY_DEF_ID        VARCHAR2(32) NULL,
       NAME                 VARCHAR2(60) NULL,
       DESCRIPTION          VARCHAR2(255) NULL,
       OPERATION            VARCHAR2(20) NULL,
       VALUE1               VARCHAR2(255) NULL,
       VALUE2               VARCHAR2(255) NULL,
       REPEATS               INTEGER NULL,
	   PARAM_GROUP			VARCHAR2(20) NULL,
	   HANDLER_LANGUAGE		VARCHAR2(20)  NULL,
	POLICY_PARAM_HANDLER VARCHAR2(255) NULL,
       PRIMARY KEY (DEF_PARAM_ID), 
       CONSTRAINT FK_POLICY_DEF_PARAM_POLICY_DEF
       		FOREIGN KEY (POLICY_DEF_ID) REFERENCES IAMUSER.POLICY_DEF(POLICY_DEF_ID)
);

CREATE TABLE IAMUSER.POLICY_ATTRIBUTE (
       POLICY_ATTR_ID       VARCHAR2(32) NOT NULL,
       DEF_PARAM_ID         VARCHAR2(32) NULL,
       POLICY_ID            VARCHAR2(32) NULL,
       NAME                 VARCHAR2(60) NULL,
       OPERATION            VARCHAR2(20) NULL,
       VALUE1               VARCHAR2(255) NULL,
       VALUE2               VARCHAR2(255) NULL,
	   RULE_TEXT			CLOB NULL,
       PRIMARY KEY (POLICY_ATTR_ID),
       CONSTRAINT FK_PLCY_ATTR_POLICY_DEF_PARAM
       		FOREIGN KEY (DEF_PARAM_ID) REFERENCES IAMUSER.POLICY_DEF_PARAM(DEF_PARAM_ID), 
       CONSTRAINT FK_PLC_ATTR_POLICY
       		FOREIGN KEY (POLICY_ID) REFERENCES IAMUSER.POLICY(POLICY_ID)
); 


CREATE TABLE IAMUSER.POLICY_OBJECT_ASSOC (
   POLICY_OBJECT_ID VARCHAR2(32) NOT NULL,
   POLICY_ID VARCHAR2(32) NULL,
   ASSOCIATION_LEVEL VARCHAR2(20) NULL,
   ASSOCIATION_VALUE VARCHAR2(255) NULL,
   OBJECT_TYPE VARCHAR2(100) NULL,
   OBJECT_ID VARCHAR2(32) NULL,
   PARENT_ASSOC_ID VARCHAR2(32) NULL,
   PRIMARY KEY (POLICY_OBJECT_ID)
);

CREATE TABLE IAMUSER.ENTITLEMENT (
       ENTITLEMENT_ID       VARCHAR2(32) NOT NULL,
       ENTITLEMENT_NAME     VARCHAR2(40) NULL,
       ENTITLEMENT_VALUE    VARCHAR2(80) NULL,
       DESCRIPTION          VARCHAR2(255) NULL,
       CREATE_DATE          DATE NULL,
       CREATED_BY           VARCHAR2(20) NULL,
       PRIMARY KEY (ENTITLEMENT_ID)
); 

CREATE TABLE IAMUSER.ROLE_ENTITLEMENT (
       ENTITLEMENT_ID       VARCHAR2(32) NOT NULL,
       ROLE_ID              VARCHAR2(32) NOT NULL,
       SERVICE_ID           VARCHAR2(20) NOT NULL,
       PRIMARY KEY (ENTITLEMENT_ID, ROLE_ID),
       CONSTRAINT RL_EN_RL
       	FOREIGN KEY (ROLE_ID,SERVICE_ID) REFERENCES IAMUSER.ROLE (ROLE_ID,SERVICE_ID),
	   CONSTRAINT RL_EN_EN
       	FOREIGN KEY (ENTITLEMENT_ID) REFERENCES IAMUSER.ENTITLEMENT(ENTITLEMENT_ID)
); 



CREATE TABLE IAMUSER.RESOURCE_POLICY (
       RESOURCE_POLICY_ID   VARCHAR2(32) NOT NULL,
       SERVICE_ID           VARCHAR2(20) NOT NULL,
       ROLE_ID              VARCHAR2(32) NULL,
       RESOURCE_ID          VARCHAR2(32) NULL,
       POLICY_START         DATE NULL,
       POLICY_END           DATE NULL,
       APPLY_TO_CHILDREN    INTEGER NULL,
       PRIMARY KEY (RESOURCE_POLICY_ID), 
       CONSTRAINT RS_PL_RL_RLID 
       		FOREIGN KEY (ROLE_ID, SERVICE_ID)
                             REFERENCES IAMUSER.ROLE (ROLE_ID, SERVICE_ID), 
       CONSTRAINT RS_PL_RS_RSID 
       		FOREIGN KEY (RESOURCE_ID)
                             REFERENCES IAMUSER.RES(RESOURCE_ID)
); 


CREATE TABLE IAMUSER.PROVISION_CONNECTOR (
	CONNECTOR_ID 			VARCHAR2(32) NOT NULL,
	NAME		 			VARCHAR2(40) NULL,
	METADATA_TYPE_ID		VARCHAR2(20) NULL,
	STD_COMPLIANCE_LEVEL	VARCHAR2(20) NULL,
	CLIENT_COMM_PROTOCOL	VARCHAR2(20) NULL,
	SERVICE_URL				VARCHAR2(100) NULL,
	SERVICE_NAMESPACE		VARCHAR2(100) NULL,
	SERVICE_PORT			VARCHAR2(100) NULL,
	SERVICE_WSDL			VARCHAR2(100) NULL,
	CLASS_NAME				VARCHAR2(60) NULL,
	HOST							VARCHAR2(60) NULL,
  PORT							VARCHAR2(10) NULL,
  CONNECTOR_INTERFACE 			VARCHAR2(20) NULL,
	PRIMARY KEY(CONNECTOR_ID)
);


CREATE TABLE IAMUSER.MANAGED_SYS (
       MANAGED_SYS_ID           VARCHAR2(32) NOT NULL,
       NAME         						VARCHAR2(40) NULL,
 	   DESCRIPTION							VARCHAR2(80) NULL,
       STATUS								VARCHAR2(20) NULL,
	   CONNECTOR_ID							VARCHAR2(32) NULL,
	   DOMAIN_ID							VARCHAR2(20) NULL,
	   HOST_URL								VARCHAR2(80) NULL,
	   APPL_URL								VARCHAR2(80) NULL,
	   PORT									INTEGER NULL,
	   COMM_PROTOCOL						VARCHAR2(20) NULL,
	   /* Need to accomodate fully qualified ldap names */
	   USER_ID								VARCHAR2(150) NULL,
	   PSWD									VARCHAR2(100) NULL,
	   START_DATE							DATE NULL,
	   END_DATE								DATE NULL,
	   RESOURCE_ID							VARCHAR2(32) NULL,
	   PRIMARY_REPOSITORY 				INTEGER NULL,
	   SECONDARY_REPOSITORY_ID 		VARCHAR2(32) NULL,
	   ALWAYS_UPDATE_SECONDARY 		INTEGER NULL,
	   RES_DEPENDENCY						VARCHAR2(32) NULL,
	   ADD_HNDLR							VARCHAR2(100) NULL,
	   MODIFY_HNDLR							VARCHAR2(100) NULL,
	   DELETE_HNDLR							VARCHAR2(100) NULL,
	   SETPASS_HNDLR						VARCHAR2(100) NULL,
	   SUSPEND_HNDLR						VARCHAR2(100) NULL,	   
	   SEARCH_HNDLR							VARCHAR2(100) NULL,
	   LOOKUP_HNDLR							VARCHAR2(100) NULL,
 	   TEST_CONNECTION_HNDLR				VARCHAR2(100) NULL,
 	   RECONCILE_RESOURCE_HNDLR				VARCHAR2(100) NULL,
	   HNDLR_5									VARCHAR2(100) NULL,
       DRIVER_URL                             VARCHAR2(100) NULL,
       CONNECTION_STRING                      VARCHAR2(100) NULL,
       PRIMARY KEY (MANAGED_SYS_ID),
			 CONSTRAINT FK_MNG_SYS_PROV_CON
			 	FOREIGN KEY (CONNECTOR_ID) REFERENCES IAMUSER.PROVISION_CONNECTOR(CONNECTOR_ID)
);


CREATE TABLE IAMUSER.MNG_SYS_OBJECT_MATCH (
		OBJECT_SEARCH_ID	VARCHAR2(32) NOT NULL,
		MANAGED_SYS_ID      VARCHAR2(32) NOT NULL,
		/* USER, GROUP, ROLE, COMPUTER, RESOURCE, ETC */
		OBJECT_TYPE			VARCHAR2(20) DEFAULT ('USER') NULL,
		/* BASE_DN, SEARCH */ 
		MATCH_METHOD			VARCHAR2(20) DEFAULT ('BASE_DN') NULL,
		BASE_DN						VARCHAR2(200) NULL,
		SEARCH_FILTER			VARCHAR2(1000) NULL,
		SEARCH_BASE_DN		VARCHAR2(200) NULL,
		KEY_FIELD					VARCHAR2(40) NULL,
       PRIMARY KEY (OBJECT_SEARCH_ID),
			 CONSTRAINT FK_MNG_SYS_OBJ_MATC
			 	FOREIGN KEY (MANAGED_SYS_ID) REFERENCES IAMUSER.MANAGED_SYS(MANAGED_SYS_ID)
);


CREATE TABLE IAMUSER.MANAGED_SYS_ATTRIBUTE (
       MNG_SYS_ATTR_ID        varchar2(32) NOT NULL,
       NAME                   varchar2(60) NULL,
       VALUE1                 varchar2(255) NULL,
       VALUE2                 varchar2(255) NULL,
       MANAGED_SYS_ID         VARCHAR2(32) NULL,
       PRIMARY KEY (MNG_SYS_ATTR_ID),
       CONSTRAINT FK_MNG_MNG_ATTR
       		FOREIGN KEY (MANAGED_SYS_ID) REFERENCES IAMUSER.MANAGED_SYS(MANAGED_SYS_ID)
);

CREATE TABLE IAMUSER.APPROVER_ASSOC(
	APPROVER_ASSOC_ID           VARCHAR2(32)    NOT NULL,
	REQUEST_TYPE	            VARCHAR2(32) NULL,			/* Each type of request can have a different approval process. */
	APPROVER_LEVEL              INTEGER DEFAULT (1) NULL,   /* approval level */
	ASSOCIATION_TYPE            VARCHAR2(20)	   NULL,	/* GROUP, ROLE, SUPERVISOR, INDIVIDUAL, RESOURCE  */
	ASSOCIATION_OBJ_ID          VARCHAR2(32)	   NULL,	/* ID OF GROUP, ROLE, SUPERVISOR,  OR RESOURCE   */
	APPROVER_USER_ID            VARCHAR2(32)	   NULL,    /* ID OF THE APPROVER - WE CAN HAVE MANY APPROVERS  */
	ON_APPROVE_NOTIFY_USER_ID   VARCHAR2(32)   NULL,
	ON_REJECT_NOTIFY_USER_ID    VARCHAR2(32)   NULL,
	APPROVE_NOTIFY_USER_TYPE    VARCHAR2(20)   NULL,  /* USER, SUPERVISOR, TARGET_USER*/
	REJECT_NOTIFY_USER_TYPE     VARCHAR2(20)   NULL,
	ACTN    		    VARCHAR2(32) DEFAULT ('START') NULL,   /* START, ACCEPT, REJECT - FUTURE USE  */
	APPROVER_ROLE_ID 	    VARCHAR2(32)  NULL,
	APPROVER_ROLE_DOMAIN 	    VARCHAR2(20)  NULL,
	APPLY_DELEGATION_FILTER     INTEGER  NULL,
PRIMARY KEY (APPROVER_ASSOC_ID) 
);


CREATE TABLE IAMUSER.MNG_SYS_GROUP(
    MNG_SYS_GROUP_ID    VARCHAR2(32)    NOT NULL,
    MANAGED_SYS_ID      VARCHAR2(32)    NOT NULL,
    PRIMARY KEY (MANAGED_SYS_ID, MNG_SYS_GROUP_ID), 
    CONSTRAINT Refmanaged_sys831 FOREIGN KEY (MANAGED_SYS_ID)
    REFERENCES IAMUSER.MANAGED_SYS(MANAGED_SYS_ID)
);





CREATE TABLE IAMUSER.PROV_REQUEST(
    REQUEST_ID            VARCHAR2(32)     NOT NULL,
    REQUESTOR_ID          VARCHAR2(32),    /* CAN BE NULL IN SELF-REGISTRATION */
    REQUESTOR_FIRSTNAME   VARCHAR2(50),   /* AVOIDS A LOOKUP ON USERS*/
    REQUESTOR_LASTNAME    VARCHAR2(50),    /* AVOIDS A LOOKUP ON USERS*/
    REQUEST_DATE        DATE,
    STATUS              VARCHAR2(20),
    STATUS_DATE         DATE,
    REQUEST_REASON      VARCHAR2(1000),
	REQUEST_TYPE        VARCHAR2(50),
	WORKFLOW_NAME       VARCHAR2(50),
	CHANGE_ACCESS_BY	VARCHAR2(32),
	REQUEST_XML 		CLOB,
	REQUEST_TITLE       varchar2(200) NULL,
    NEW_ROLE_ID              varchar2(32) NULL,
    NEW_SERVICE_ID           varchar2(20) NULL,
	MANAGED_RESOURCE_ID	VARCHAR2(32) NULL,
    REQUEST_FOR_ORG_ID  VARCHAR2(32) NULL,
    PRIMARY KEY (REQUEST_ID)
) ENGINE=InnoDB;



CREATE TABLE IAMUSER.REQ_APPROVER(
    REQ_APPROVER_ID     VARCHAR2(32)    NOT NULL,
    APPROVER_ID         VARCHAR2(32)	   NULL,
    APPROVER_TYPE		VARCHAR2(20)    NULL,   /* MANAGER, APP_APPROVER, FORM_APPROVER */
    APPROVER_LEVEL      INTEGER	   NULL,
    REQUEST_ID          VARCHAR2(32)    NULL,
    ACTION_DATE         DATE       NULL,
    STATUS              VARCHAR2(20)    NULL,  /* PENDING, COMPLETE */
    ACTION              VARCHAR2(20)    NULL,  /* APPROVE, REJECT, DELEGATE */
    CMT             VARCHAR2(1000)   NULL,      /* COMMENT */
    MANAGED_SYS_ID      VARCHAR2(32)    NULL,
       ROLE_DOMAIN	        VARCHAR2(20)	NULL,
    MNG_SYS_GROUP_ID	VARCHAR2(32)	NULL,
    PRIMARY KEY (REQ_APPROVER_ID)
);


CREATE TABLE IAMUSER.MNG_SYS_LIST(
    MANAGED_SYS_ID    VARCHAR2(32)    NOT NULL,
    REQUEST_ID        VARCHAR2(32)    NOT NULL,
    PRIMARY KEY (MANAGED_SYS_ID, REQUEST_ID), 
    CONSTRAINT Refmanaged_sys941 FOREIGN KEY (MANAGED_SYS_ID)
    REFERENCES IAMUSER.MANAGED_SYS(MANAGED_SYS_ID),
    CONSTRAINT RefPROV_REQUEST951 FOREIGN KEY (REQUEST_ID)
    REFERENCES IAMUSER.PROV_REQUEST(REQUEST_ID)
); 

CREATE TABLE IAMUSER.REQUEST_ATTACHMENT(
    REQUEST_ATTACHMENT_ID    VARCHAR2(32)    NOT NULL,
    NAME                     VARCHAR2(20),
    ATTACHMENT               VARCHAR2(20),
    USER_ID                  VARCHAR2(32),
    ATTACHMENT_DATE          VARCHAR2(20),
    REQUEST_ID               VARCHAR2(32)    NOT NULL,
    PRIMARY KEY (REQUEST_ATTACHMENT_ID), 
    CONSTRAINT RefPROV_REQUEST1001 FOREIGN KEY (REQUEST_ID)
    REFERENCES IAMUSER.PROV_REQUEST(REQUEST_ID)
) ; 


CREATE TABLE IAMUSER.REQUEST_ATTRIBUTE(
    REQUEST_ATTR_ID    VARCHAR2(32)    NOT NULL,
    NAME               VARCHAR2(20)	NULL,
    VALUE              VARCHAR2(20)	NULL,
    METADATA_ID        VARCHAR2(20)	NULL,
    ATTR_GROUP         VARCHAR2(20)	NULL,
    REQUEST_ID         VARCHAR2(32)   NULL,
    PRIMARY KEY (REQUEST_ATTR_ID), 
    CONSTRAINT RefPROV_REQUEST1011 FOREIGN KEY (REQUEST_ID)
    REFERENCES IAMUSER.PROV_REQUEST(REQUEST_ID)
); 

CREATE TABLE IAMUSER.REQUEST_USER(
    REQ_USER_ID         VARCHAR2(32)    NOT NULL,
    USER_ID             VARCHAR2(32),
    FIRST_NAME          VARCHAR2(50),
    LAST_NAME           VARCHAR2(50),
    MIDDLE_INIT         VARCHAR2(20),
    DEPT_CD             VARCHAR2(32),
    DIVISION            VARCHAR2(32),
    LOCATION_CD         VARCHAR2(32),
    AFFILIATION         VARCHAR2(32),
    REQUEST_ID          VARCHAR2(32)   NULL,
    PRIMARY KEY (REQ_USER_ID), 
    CONSTRAINT RefPROV_REQUEST931 FOREIGN KEY (REQUEST_ID)
    REFERENCES IAMUSER.PROV_REQUEST(REQUEST_ID)
); 


CREATE TABLE IAMUSER.ATTRIBUTE_MAP (
  ATTRIBUTE_MAP_ID        varchar2(32) NOT NULL,
  MANAGED_SYS_ID          varchar2(32) NOT NULL,
  RESOURCE_ID             VARCHAR2(32) NULL,
/* User, Group, OTHER */
  MAP_FOR_OBJECT_TYPE     VARCHAR2(20) DEFAULT ('USER'),
  ATTRIBUTE_NAME	      VARCHAR2(50) NULL,
/* CN, DN, UID, ETC   */
  TARGET_ATTRIBUTE_NAME   varchar2(50) NULL,
/* IS AUTHORITATIVE SRC. 0-FALSE, 1-TRUE */
  AUTHORITATIVE_SRC       INTEGER default (0),
  ATTRIBUTE_POLICY_ID     VARCHAR2(32) NULL,
  RULE_TEXT CLOB,
  STATUS                  VARCHAR2(20) default ('ACTIVE'),
  START_DATE 	          DATE,
  END_DATE 		          DATE,
  STORE_IN_IAMDB          INTEGER DEFAULT (0),
  DATA_TYPE               VARCHAR2(20) NULL,
  DEFAULT_VALUE           varchar2(32) NULL,
  PRIMARY KEY  (ATTRIBUTE_MAP_ID)
 ); 

CREATE TABLE IAMUSER.RECONCILIATION_CONFIG (
  RECON_CONFIG_ID	varchar2(32) NOT NULL,
  RESOURCE_ID 		VARCHAR2(32) NULL,
  FREQUENCY       VARCHAR2(20) NULL,
  STATUS          VARCHAR2(20) NULL,
  PRIMARY KEY (RECON_CONFIG_ID)
);


CREATE TABLE IAMUSER.RECONCILIATION_SITUATION (
  RECON_SITUATION_ID	 	varchar2(32) NOT NULL,
  RECON_CONFIG_ID 			VARCHAR2(32) NULL,
  SITUATION					VARCHAR2(40) NULL,
  SITUATION_RESP		    VARCHAR2(40) NULL,
  SCRIPT				    VARCHAR2(80) NULL,
  PRIMARY KEY  (RECON_SITUATION_ID),
CONSTRAINT RECON_SITUATION FOREIGN KEY (RECON_CONFIG_ID)
    REFERENCES IAMUSER.RECONCILIATION_CONFIG(RECON_CONFIG_ID)
);


CREATE TABLE IAMUSER.SYNCH_CONFIG (
      SYNCH_CONFIG_ID        	varchar2(32) NOT NULL,
      NAME       		varchar2(60) 	NULL,
      STATUS       		VARCHAR2(20) 	NULL,
      SYNCH_SRC			VARCHAR2(20) 	NULL, /* CSV FILE, MANAGED_RESOURCE */ 
      FILE_NAME			VARCHAR2(80) 	NULL,
      SRC_LOGIN_ID		VARCHAR2(100) 	NULL,
      SRC_PASSWORD		VARCHAR2(100) 	NULL,
      SRC_HOST			VARCHAR2(100) 	NULL,
      DRIVER			VARCHAR2(100)	NULL,
      CONNECTION_URL		VARCHAR2(100)   NULL,
      QUERY			VARCHAR2(1000) 	NULL,
      QUERY_TIME_FIELD	        VARCHAR2(50)	NULL,   /* FIELD IN THE TABLE THAT SHOULD BE USED WHEN DOING INCREMENTAL SEARCH */
      BASE_DN                   VARCHAR2(50)    NULL,
      LAST_EXEC_TIME	        DATE		NULL,
      LAST_REC_PROCESSED        VARCHAR2(32)	NULL,
      MANAGED_SYS_ID		VARCHAR2(32) 	NULL, 
      LOAD_MATCH_ONLY		INTEGER	DEFAULT (0) NULL,
      UPDATE_ATTRIBUTE     	INTEGER	DEFAULT (1) NULL,
      SYNCH_FREQUENCY		VARCHAR2(20) 	NULL,
      SYNCH_TYPE		VARCHAR2(20) 	NULL,
      DELETE_RULE		VARCHAR2(80) 	NULL,
      PROCESS_RULE		VARCHAR2(80)	NULL,
      VALIDATION_RULE		VARCHAR2(80) 	NULL,
      TRANSFORMATION_RULE	VARCHAR2(80) 	NULL,
      MATCH_FIELD_NAME		VARCHAR2(40) 	NULL,
      MATCH_MANAGED_SYS_ID 	VARCHAR2(32) 	NULL,
      MATCH_SRC_FIELD_NAME 	VARCHAR2(40) 	NULL,
      CUSTOM_MATCH_RULE		VARCHAR2(100) 	NULL,
      CUSTOM_ADAPTER_SCRIPT	VARCHAR2(100) 	NULL,	
      CUSTOM_MATCH_ATTR		VARCHAR2(40) 	NULL,
      WS_URL	                VARCHAR2(100) 	NULL,
      WS_SCRIPT		        VARCHAR2(100) 	NULL,
       WS_ENDPOINT              VARCHAR2(100)    NULL,
PRIMARY KEY (SYNCH_CONFIG_ID) 
);


CREATE TABLE IAMUSER.SYNCH_CONFIG_DATA_MAPPING(
    MAPPING_ID    			VARCHAR2(32)    NOT NULL,
    SYNCH_CONFIG_ID     VARCHAR2(32) NULL,
    IDM_FIELD_NAME      VARCHAR2(40),
		SRC_FIELD_NAME			VARCHAR2(40),
		MULTIVALUED         INTEGER NULL,
    PRIMARY KEY (MAPPING_ID), 
    CONSTRAINT SYNCH_DATA_MAP FOREIGN KEY (SYNCH_CONFIG_ID)
    REFERENCES IAMUSER.SYNCH_CONFIG(SYNCH_CONFIG_ID)
);

CREATE TABLE IAMUSER.WEB_RESOURCE_ATTRIBUTE(
	ATTRIBUTE_MAP_ID      varchar2(32) NOT NULL,
	RESOURCE_ID           varchar2(32) NOT NULL,
	TARGET_ATTRIBUTE_NAME varchar2(100) NOT NULL,
	AM_ATTRIBUTE_NAME     varchar2(100) NULL,
	AM_POLICY_URL         varchar2(100) NULL,
	PRIMARY KEY(ATTRIBUTE_MAP_ID)
);

CREATE TABLE IAMUSER.MAIL_TEMPLATE(
	TMPL_ID       VARCHAR2(32) NOT NULL,
	TMPL_NAME     VARCHAR2(64) NOT NULL ,
    TMPL_SUBJECT  VARCHAR2(64) NOT NULL,
    BODY_TYPE     VARCHAR2(16) NOT NULL,
    ATTACHMENT_FILE_PATH  VARCHAR2(64) NULL,
    BODY          CLOB NULL,
    PRIMARY KEY (TMPL_ID)
);

ALTER TABLE IAMUSER.MAIL_TEMPLATE
add CONSTRAINT MAIL_TEMPLATE_NAME UNIQUE (TMPL_NAME);

CREATE TABLE IAMUSER.NOTIFICATION(
	MSG_ID          VARCHAR2(32) NOT NULL,
	MSG_NAME        VARCHAR2(64) NOT NULL,
    PROVIDER_SCRIPT VARCHAR2(64),
    TMPL_ID         VARCHAR2(32),
    START_DATE      DATE NULL,
    END_DATE        DATE NULL,
    MSG_TYPE        VARCHAR2(32),
    PRIMARY KEY (MSG_ID),
    CONSTRAINT FK_MESSAGE_MAIL_TEMPLATE
       FOREIGN KEY (TMPL_ID)
       REFERENCES IAMUSER.MAIL_TEMPLATE(TMPL_ID)
);

ALTER TABLE IAMUSER.NOTIFICATION
add CONSTRAINT NOTIFICATION_MSG_NAME UNIQUE (MSG_NAME);


/* views */

CREATE VIEW IAMUSER.USER_ROLE_VW AS 
SELECT ug.USER_ID, gr.ROLE_ID,gr.SERVICE_ID 
FROM USER_GRP ug LEFT JOIN GRP_ROLE gr on (ug.GRP_ID = gr.GRP_ID)
		 WHERE gr.ROLE_ID is not null
 UNION
SELECT ur.USER_ID,  ur.ROLE_ID, ur.SERVICE_ID
FROM USER_ROLE ur 
	WHERE ur.ROLE_ID is not null
	ORDER BY USER_ID;	
	
CREATE VIEW IAMUSER.USER_EMAIL_VW AS 
	SELECT *
	FROM EMAIL_ADDRESS
	WHERE NAME = 'EMAIL1' AND PARENT_TYPE='USER';

CREATE VIEW IAMUSER.USER_PHONE_VW AS 
SELECT *
FROM PHONE
WHERE NAME = 'DESK PHONE' AND PARENT_TYPE='USER';


CREATE VIEW IAMUSER.USER_IDENTITY_VW AS
  SELECT u.*, l.auth_fail_count, l.canonical_name,
  l.current_login_host, l.first_time_login, l.grace_period,
  l.identity_type, l.is_default, l.is_locked, l.last_auth_attempt, l.last_login,
  l.last_login_ip, l.login, l.managed_sys_id, l.prev_login, l.prev_login_ip,
  l.pwd_change_count, l.pwd_exp, l.reset_pwd, l.service_id
  FROM LOGIN l, USERS u
  WHERE l.USER_ID = u.USER_ID;


CREATE VIEW IAMUSER.USER_PSWD_EXPIRED_YESTERDAY_VW AS
  select LOGIN,l.SERVICE_ID, l.MANAGED_SYS_ID, l.USER_ID, GRACE_PERIOD AS EXPIRATION_DATE,  u.FIRST_NAME, u.LAST_NAME, u.STATUS
  FROM LOGIN l,  USERS u
  WHERE l.USER_ID = u.USER_ID AND
      MANAGED_SYS_ID = 0 AND
  (    (
      GRACE_PERIOD IS NOT NULL AND
      GRACE_PERIOD BETWEEN (SYSDATE-1) AND (SYSDATE)
      )
     OR
     (
      PWD_EXP IS NOT NULL AND GRACE_PERIOD IS NULL AND
     PWD_EXP BETWEEN (SYSDATE-1) AND (SYSDATE)
      )
  );

commit;