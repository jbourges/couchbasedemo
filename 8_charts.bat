@echo off

@REM Couchbase demo java script

call 1_login.bat

@REM first check for JAVA_HOME 

@REM Example JAVA_HOME = C:\Progra~1\Java\jdk-11.0.7 
if "%JAVA_HOME%"=="" goto JERROR

SET MYPATH=%cd%

SET CLASSPATH=.;%MYPATH%\Couchbase-java-Client-3.0.5\*;%MYPATH%\*

"%JAVA_HOME%"\bin\java coveotest.QueryCountryDeviceToHighCharts %USER% %PASSWORD% 

start chrome %MYPATH%/highcharts/country_device.html

goto END

:JERROR
echo JAVA_HOME environment variable is not defined correctly >&2
echo This environment variable is needed to run this program >&2
echo NB: JAVA_HOME should point to a JDK not a JRE >&2

:END

