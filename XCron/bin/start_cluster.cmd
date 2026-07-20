@ECHO OFF
SETLOCAL

call setDomainEnv.cmd
call setEnv.cmd

set SYSTEM_PROPERTIES=-Dxcron.home=%XCRON_HOME% -Djava.library.path=%XCRON_HOME%/lib/sigar
set MEM_ARGS=-XX:NewSize=%MEM_MIN_SIZE% -XX:MaxNewSize=%MEM_MIN_SIZE% -XX:SurvivorRatio=8 -Xms%MEM_MAX_SIZE% -Xmx%MEM_MAX_SIZE%
set JAVA_VM=-server

%JAVA_HOME%java -Dxcron.home=%XCRON_HOME% com.inticube.xcron.daemon.XCronClusterLoader %1 %2 %3

ENDLOCAL