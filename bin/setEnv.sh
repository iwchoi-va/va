#!/bin/sh

PRG="$0"
PRGDIR=`dirname "$PRG"`

export JEDI_HOME=`cd $PRGDIR/.. ; pwd`
export JAVA_HOME=""
export ANT_HOME=""
export WAS_LIB=$JEDI_HOME/servers/jakarta-tomcat-5.0.28/common/lib/

#echo "setEnv.sh JEDI_HOME:$JEDI_HOME"

if [ -z $JAVA_HOME ]; then
	echo "You must set JAVA_HOME(ex:  export JAVA_HOME=/project/jdk)"
	exit
fi

if [ -z $ANT_HOME ]; then
	echo "You must set ANT_HOME(ex:  export ANT_HOME=/project/ant)"
	exit
fi




