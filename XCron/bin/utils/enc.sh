#!/bin/sh

. "../setDomainEnv.sh"
. "../setEnv.sh"

${JAVA_HOME}java com.inticube.xcron.utils.CyperUtil $1
