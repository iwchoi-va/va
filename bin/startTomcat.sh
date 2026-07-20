#!/bin/sh

PRG="$0"
PRGDIR=`dirname "$PRG"`

if [ -f $PRGDIR/setEnv.sh ]; then
	. $PRGDIR/setEnv.sh
fi

export CATALINA_HOME=/home/stat/web/IS_STAT/servers/tomcat_7.0.42
$CATALINA_HOME/bin/startup.sh
