#!/bin/sh

. "./setDomainEnv.sh"
. "./setEnv.sh"

${JAVA_HOME}java -Dxcron.home=${XCRON_HOME} com.inticube.xcron.core.XCronClient $1 $2 $3 $4
