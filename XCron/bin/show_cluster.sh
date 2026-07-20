#!/bin/sh

EXEC_FILE="$0"
BASE_NAME=`basename "$EXEC_FILE"`
if [ "$EXEC_FILE" = "./$BASE_NAME" ] || [ "$EXEC_FILE" = "$BASE_NAME" ]; then
        FULL_PATH=`pwd`
else
        FULL_PATH=`echo "$EXEC_FILE" | sed 's/'"${BASE_NAME}"'$//'`
        cd "$FULL_PATH"                 > /dev/null 2>&1
        FULL_PATH=`pwd`
fi

. "$FULL_PATH/setDomainEnv.sh"
. "$FULL_PATH/setEnv.sh"

${JAVA_HOME}java -Dxcron.home=${XCRON_HOME} com.inticube.xcron.core.XCronClusterClient $1 $2
