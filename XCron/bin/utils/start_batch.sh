#!/bin/sh

. "../setDomainEnv.sh"
. "../setEnv.sh"

nohup ${JAVA_HOME}java -Dxcron.home=${XCRON_HOME} xcron.core.XCronBatchClient $1 $2 > ./xcronbatchservice.log &