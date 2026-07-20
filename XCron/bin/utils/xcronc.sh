#!/bin/sh

. "../setDomainEnv.sh"

ANT_HOME=/usr/local/apache-ant-1.6.5
export ANT_HOME

JAVA_HOME=/usr/local/jdk1.6.0_19
export JAVA_HOME

ANT_FILE=build.xml
export ANT_FILE

export PATH=${JAVA_HOME}/bin:${PATH}:${ANT_HOME}/bin

ant -Dantfile=${ANT_FILE} -buildfile ${XCRON_HOME}/buildxml/tool.xml
