XCRON_CLASSPATH=${XCRON_HOME}/lib/xml-apis.jar:${XCRON_HOME}/lib/xercesImpl.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/ojdbc14.jar:${XCRON_HOME}/lib/log4j-1.2.15.jar:${XCRON_HOME}/lib/commons-pool-1.6.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/commons-net-3.0.jar:${XCRON_HOME}/lib/commons-dbcp-1.3.jar:${XCRON_HOME}/lib/commons-collections-3.2.1.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/commons-daemon.jar:${XCRON_HOME}/lib/commons-codec-1.4.jar:${XCRON_HOME}/lib/jakarta-oro.jar:${XCRON_HOME}/lib/megathin2.jar:${XCRON_HOME}/lib/opljdbc2.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/jcrontab.jar:${XCRON_HOME}/lib/bsh.jar:${XCRON_HOME}/lib/mssqlserver.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/msbase.jar:${XCRON_HOME}/lib/msutil.jar:${XCRON_HOME}/lib/mysql-connector-5.1.8.jar:${XCRON_HOME}/lib/jconn3.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/junit.jar:${XCRON_HOME}/lib/sigar/sigar.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/mail.jar:${XCRON_HOME}/lib/sigar/activation.jar:${XCRON_HOME}/lib/ejb.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/jgroups.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/db2jcc.jar:${XCRON_HOME}/lib/jsch-0.1.49.jar
export XCRON_CLASSPATH


XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/lib/db2jcc.jar:${XCRON_HOME}/lib/XCron-1.1.1.jar
export XCRON_CLASSPATH

XCRON_CLASSPATH=${XCRON_CLASSPATH}:${XCRON_HOME}/common
export XCRON_CLASSPATH

CLASSPATH=${XCRON_CLASSPATH}:${CLASSPATH}
export CLASSPATH
