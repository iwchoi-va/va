@echo off                                                                 
setlocal                                                                  
call "setEnv.bat"                                                         
set CATALINA_HOME=D:\win7_backup\SITE\Dev_Sou\VSENS_AIG\servers\tomcat_7.0.42
call %CATALINA_HOME%\bin\shutdown.bat