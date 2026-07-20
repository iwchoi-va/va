@ECHO OFF
SETLOCAL

REM echo ********************************************************
REM echo *        XCron Show Cluster Program Starting           *
REM echo *-------------------------------------------------------
REM echo *  Usage : show_cluster                                *
REM echo *  Usage : show_cluster  ip                            *
REM echo *  Usage : show_cluster  ip  port                      *
REM echo ********************************************************

call setDomainEnv.cmd
call setEnv.cmd

%JAVA_HOME%java -Dxcron.home=%XCRON_HOME% xcron.core.XCronClusterClient %1 %2

ENDLOCAL