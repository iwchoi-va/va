#!/bin/sh

PRG="$0"
PRGDIR=`dirname "$PRG"`

if [ -f $PRGDIR/setEnv.sh ]; then
	. $PRGDIR/setEnv.sh
fi

export CATALINA_HOME=$JEDI_HOME/servers/jakarta-tomcat-5.0.28

$CATALINA_HOME/bin/shutdown.sh
