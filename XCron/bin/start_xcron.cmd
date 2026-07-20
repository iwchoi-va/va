@ECHO OFF
SETLOCAL

echo ********************************************************
echo *        XCron Cluster Server Controller               *
echo *-------------------------------------------------------
echo *  Usage : start_xcron                                 *
echo *  Usage : start_xcron  ip  port  [restart^|stop]       *
echo ********************************************************

call start_cluster.cmd %1 %2 %3

ENDLOCAL