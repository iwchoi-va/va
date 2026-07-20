@echo off
setlocal

set JEDI_HOME=C:\MSENS_AIG\MSENS_OB
set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.7.0_79\
set ANT_HOME=%JEDI_HOME%\buildxml\ant-1.8.2
set WAS_LIB=%JEDI_HOME%\servers\tomcat_7.0.42\lib
set ANT_FILE=build.xml

if "%JEDI_HOME%"=="" goto jedi_err
if "%JAVA_HOME%"=="" goto javaHome_err
if "%ANT_HOME%"==""  goto ant_err
if "%WAS_LIB%"=="" goto wasLib_err
if "%ANT_FILE%"=="" goto antfile-err


:runANT
	%ANT_HOME%\bin\ant -Dantfile=%ANT_FILE% -buildfile %JEDI_HOME%\buildxml\tool.xml %1
	goto finish


:jedi_err
	echo "JEDI_HOME을 설정하세요(예: set JEDI_HOME=c:\jedi20)"
	goto finish

:ant_err
	echo ANT_HOME을 설정하세요(예: set ANT_HOME=C:\ant)
	goto finish

:javaHome_err
	echo JAVA_HOME을 설정해세요(예: set JAVA_HOME=C:\jdk)
	goto finish


:antfile_err
	echo ANT_FILE을 설정해세요(예: set ANT_FILE=build-weblogic.xml)
	goto finish

:wasLib_err
	echo WAS_LIB를 설정하세요(예: set WAS_LIB=c:\bea\weblogic8.1\server\lib)"
	goto finish

:finish
	endlocal
