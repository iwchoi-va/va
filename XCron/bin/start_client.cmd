@ECHO OFF
SETLOCAL

echo ********************************************************
echo *        XCron Client Program Starting                 *
echo *-------------------------------------------------------
echo *  Usage : start_client  serviceid                     *
echo *  Usage : start_client  ip  serviceid                 *
echo *  Usage : start_client  ip  port  serviceid           *
echo *  Usage : start_client  ip  port  timeout  serviceid  *
echo ********************************************************

call setDomainEnv.cmd
call setEnv.cmd

%JAVA_HOME%java -Dxcron.home=%XCRON_HOME% com.inticube.xcron.core.XCronClient %1 %2 %3 %4

ENDLOCAL