@echo off
setlocal

call ../setDomainEnv.cmd

set JAVA_HOME=C:/jedi/Java/jdk1.7.0_71
set ANT_HOME=C:/jedi/apache-ant-1.8.2

set ANT_FILE=build.xml
set PATH=%JAVA_HOME%\bin;%PATH%;%ANT_HOME%\bin

if "%XCRON_HOME%"=="" goto xcron_err
if "%JAVA_HOME%"=="" goto javaHome_err
if "%ANT_HOME%"==""  goto ant_err
if "%ANT_FILE%"=="" goto antfile-err


:runANT
	%ANT_HOME%\bin\ant -Dantfile=%ANT_FILE% -buildfile %XCRON_HOME%\buildxml\tool.xml %1
	goto finish


:xcron_err
	echo "XCRON_HOME�� �����ϼ���(��: set XCRON_HOME=c:/XCron)"
	goto finish

:ant_err
	echo ANT_HOME�� �����ϼ���(��: set ANT_HOME=C:\ant)
	goto finish

:javaHome_err
	echo JAVA_HOME�� �����ؼ���(��: set JAVA_HOME=C:\jdk)
	goto finish


:antfile_err
	echo ANT_FILE�� �����ؼ���(��: set ANT_FILE=build-weblogic.xml)
	goto finish

:finish
	endlocal
