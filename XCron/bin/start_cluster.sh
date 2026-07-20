#!/bin/sh

. "./setDomainEnv.sh"
. "./setEnv.sh"

SYSTEM_PROPERTIES="-Dxcron.home=${XCRON_HOME} -Djava.library.path=${XCRON_HOME}/lib/sigar"
export SYSTEM_PROPERTIES

MEM_ARGS=""
JAVA_VM=""

if [ "${JAVA_VENDOR}" = "Oracle" ] ; then
	MEM_ARGS="-XX:NewSize=${MEM_MIN_SIZE} -XX:MaxNewSize=${MEM_MIN_SIZE} -Xms${MEM_MAX_SIZE} -Xmx${MEM_MAX_SIZE} -XX:SurvivorRatio=8 -XX:-UseParallelGC"
	export MEM_ARGS
	JAVA_VM=-server
	export JAVA_VM
	LANG=ko_KR.eucKR
	export LANG
fi

if [ "${JAVA_VENDOR}" = "IBM" ] ; then
	MEM_ARGS="-Xms${MEM_MIN_SIZE} -Xmx${MEM_MAX_SIZE}"
	export MEM_ARGS
	JAVA_VM=
	export JAVA_VM
	LANG=ko_KR.IBM-eucKR
	export LANG
fi

${JAVA_HOME}java -Dxcron.home=${XCRON_HOME} com.inticube.xcron.daemon.XCronClusterLoader $1 $2 $3
