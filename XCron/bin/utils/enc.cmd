@ECHO OFF
SETLOCAL

echo ********************************************************
echo *        XCron Cyper Program                           *
echo *-------------------------------------------------------
echo *  Usage  : enc plain_text                             *
echo *  Result : plain_text = cyper_text                    *
echo ********************************************************

call ../setDomainEnv.cmd
call ../setEnv.cmd

%JAVA_HOME%java com.inticube.xcron.utils.CyperUtil %1

ENDLOCAL