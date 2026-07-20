@ECHO OFF
SETLOCAL

echo ********************************************************
echo *        XCron Batch Client Program Starting           *
echo *-------------------------------------------------------
echo *  Usage : start_batch  serviceid  propertyfilepath    *
echo ********************************************************

call ../setDomainEnv.cmd
call ../setEnv.cmd

%JAVA_HOME%java -Dxcron.home=%XCRON_HOME% xcron.core.XCronBatchClient %1 %2

ENDLOCAL