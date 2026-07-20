//========================================== JDBC - ODBC Bridge ==========================================

If working with iODBC ODBC Driver manager, then you must copy the libiodbc.so file 
to libodbc.so.1 and libodbcinst.so.1.
This is because the iODBC driver manager ships with one library 
that exposes all the required functions.
You can copy these files under {oa_root}\lib\{platform} folder

//=========================================================================================================

//========================================== JCrontab 사용방법  ============================================

#
# Tasks planification configuration file.
# IMPORTANT: All the index begin in 0, except day of month and
#       month that begin in 1.
#              minute         0-59
#              hour           0-23
#              day of month   1-31
#              month          1-12
#              day of week    0-6 (0 is Sunday)

# IMPORTANT: The first day of the week is sunday

# Each minute
* * * * * org.jcrontab.tests.TaskTest2 eachMinute
* * * * * org.jcrontab.tests.TaskTest2#testMethod testing params passed to TaskTest2#testMethod
# Each 5 minutes
*/5 * * * * org.jcrontab.tests.TaskTest2 each5Minutes1

# 0,5,10,15,20,25,30,35,40,45,50,55 * * * * org.jcrontab.tests.TaskTest2 each5Minutes

# Each 10 minutes in even hours
*/10 */2 * * * org.jcrontab.tests.TaskTest2 each10MinutesInEvenHours

# At half of odd hours
30 1,3,5,7,9,11,13,15,17,19,21,23 * * * org.jcrontab.tests.TaskTest2 halfOddHours

# Odd hours of odd months
0 */2 * */2 * org.jcrontab.tests.TaskTest2 oddHoursOddMonths

# Mondays at 20:00h
0 20 * * 1 org.jcrontab.tests.TaskTest2 MondaysAt20
#
# Sundays at 20:00h
0 20 * * 0 org.jcrontab.tests.TaskTest2 SundaysAt20
#
# 5th days of each month at 00:00h
0 0 5 * * org.jcrontab.tests.TaskTest2 5thDayAt00
#
# 	The objective of this test is to 
#	test  how to call a main method not explicitly
#
*/2 * * * * org.jcrontab.tests.TaskTest
#
# 	The objective of this test is to 
#	how to call a static method (main) passing 
#	parameters
#
*/10 * * * * org.jcrontab.tests.TaskTest2#main testing params passed to TaskTest2

#
# 	The objective of this test is to 
#	call a Default constructor in a Thread
#
* */2 * * * org.jcrontab.tests.TaskTest3
#
# 	The objective of this test is to 
#	call a Default constructor of Runnable class passing parameters 
#
* * * * * org.jcrontab.tests.TaskTest4 testing params passed to TaskTest4

#
#	The objective of this line is to 
#	call a native program and test NativeExec 
#
* * * * * org.jcrontab.NativeExec dir 

//=========================================================================================================

# NativeExec 사용 방법
	<crontabentry id="2">
		<seconds>0,15,30,45</seconds> 
		<minutes>*</minutes> 
		<hours>*</hours> 
		<daysofmonth>*</daysofmonth> 
		<months>*</months> 
		<daysofweek>*</daysofweek> 
		<years>*</years> 
		<bussinesdays>true</bussinesdays> 
		<startDate></startDate> 
		<endDate></endDate> 
		<class>org.jcrontab.NativeExec</class> 
		<method>main</method>
		<parameters>del c:\download\a.txt</parameters>
		<description>HSplit.log를 처리하는 스케줄링</description>
	</crontabentry>
# 


================================================================== XCRON_SERVICE_LOG TABLE 정의 ================================================================
CREATE TABLE XCRON_SERVICE_LOG
(
  SERVICE_NAME    VARCHAR2(100 BYTE)            NOT NULL,
  START_DATETIME  VARCHAR2(17 BYTE)             NOT NULL,
  END_DATETIME    VARCHAR2(17 BYTE),
  EXECUTE_TIME    NUMBER,
  START_DATE      VARCHAR2(8 BYTE),
  START_TIME      VARCHAR2(6 BYTE),
  PROC_RSLT       VARCHAR2(10 BYTE),
  ERR_MSG         VARCHAR2(4000 BYTE),
  PROC_FILECNT    NUMBER                        DEFAULT 0
)
TABLESPACE WFM_DATA
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          72K
            MINEXTENTS       1
            MAXEXTENTS       2147483645
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
LOGGING 
NOCACHE
NOPARALLEL;


CREATE UNIQUE INDEX PK_XCRON_LOG ON XCRON_SERVICE_LOG
(SERVICE_NAME, START_DATETIME)
NOLOGGING
TABLESPACE WFM_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          72K
            MINEXTENTS       1
            MAXEXTENTS       2147483645
            PCTINCREASE      0
            FREELISTS        1
            FREELIST GROUPS  1
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;


CREATE INDEX IDX_XCRON_LOG_01 ON XCRON_SERVICE_LOG
(SERVICE_NAME, START_DATE, START_TIME)
NOLOGGING
TABLESPACE WFM_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       2147483645
            PCTINCREASE      0
            FREELISTS        1
            FREELIST GROUPS  1
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;


CREATE INDEX IDX_XCRON_LOG_02 ON XCRON_SERVICE_LOG
(START_DATE, START_TIME)
NOLOGGING
TABLESPACE WFM_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       2147483645
            PCTINCREASE      0
            FREELISTS        1
            FREELIST GROUPS  1
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;


ALTER TABLE XCRON_SERVICE_LOG ADD (
  CONSTRAINT PK_XCRON_LOG PRIMARY KEY (SERVICE_NAME, START_DATETIME)
    USING INDEX 
    TABLESPACE WFM_INDEX
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          72K
                MINEXTENTS       1
                MAXEXTENTS       2147483645
                PCTINCREASE      0
                FREELISTS        1
                FREELIST GROUPS  1
               ));
================================================================== XCRON_FILE_LOG TABLE 정의 ================================================================
CREATE TABLE XCRON_FILE_LOG
(
  SERVICE_NAME    VARCHAR2(100 BYTE)            NOT NULL,
  START_DATETIME  VARCHAR2(17 BYTE)             NOT NULL,
  FILE_NAME       VARCHAR2(200 BYTE)            NOT NULL,
  FILE_PATH       VARCHAR2(500 BYTE),
  FILE_SIZE       NUMBER,
  REMOVE          CHAR(1 BYTE)
)
TABLESPACE WFM_DATA
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          72K
            MINEXTENTS       1
            MAXEXTENTS       2147483645
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
LOGGING 
NOCACHE
NOPARALLEL;


CREATE UNIQUE INDEX PK_XCRON_FILE_LOG ON XCRON_FILE_LOG
(SERVICE_NAME, START_DATETIME, FILE_NAME)
NOLOGGING
TABLESPACE WFM_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          72K
            MINEXTENTS       1
            MAXEXTENTS       2147483645
            PCTINCREASE      0
            FREELISTS        1
            FREELIST GROUPS  1
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;


ALTER TABLE XCRON_FILE_LOG ADD (
  CONSTRAINT PK_XCRON_FILE_LOG PRIMARY KEY (SERVICE_NAME, START_DATETIME, FILE_NAME)
    USING INDEX 
    TABLESPACE WFM_INDEX
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          72K
                MINEXTENTS       1
                MAXEXTENTS       2147483645
                PCTINCREASE      0
                FREELISTS        1
                FREELIST GROUPS  1
               ));
================================================================== XCRON_SERVICE_LOG 조회 쿼리 ================================================================
	<sql-service name='wfm.com.xcronservicelog' datasource='wfm' type='simple'>
		<description>XCron 서비스 로그 조회</description>
		<result-name>servicelog</result-name>
		<query><![CDATA[
SELECT
	SERVICE_NAME,
	START_DATETIME,
	END_DATETIME,
	EXECUTE_TIME,
	START_DATE,
	SUBSTR(START_TIME, 1, 4) START_TIME,
	PROC_RSLT,
	ERR_MSG,
	PROC_FILECNT
FROM XCRON_SERVICE_LOG
WHERE SERVICE_NAME LIKE '?id%'
AND START_DATETIME BETWEEN '?sdate'||'00000' AND '?edate'||'00000'
AND PROC_RSLT LIKE '?procresult%'
ORDER BY SERVICE_NAME, START_DATETIME, END_DATETIME
		]]></query>
	</sql-service>

	<sql-service name='wfm.com.xcronfilelog' datasource='wfm' type='simple'>
		<description>XCron 파일 로그 조회</description>
		<result-name>filelog</result-name>
		<query><![CDATA[
SELECT
	FILE_NAME,
	FILE_PATH,
	FILE_SIZE,
	REMOVE
FROM XCRON_FILE_LOG
WHERE SERVICE_NAME = '?service_name'
AND START_DATETIME = '?start_datetime'
		]]></query>
	</sql-service>