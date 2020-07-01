@echo off

REM Couchbase demo bucket script

call 1_login.bat

SET COUCHBASE_HOME=C:\Progra~1\Couchbase\Server\bin

REM Import Data
%COUCHBASE_HOME%\cbimport csv -c localhost -u %USER% -p %PASSWORD% -b searchdata -d file://data/result.csv -g searchdata::#MONO_INCR#
%COUCHBASE_HOME%\cbimport json -c localhost -u %USER% -p %PASSWORD% -b searchdata -f lines -d file://data/result.json -g searchdatajson::#MONO_INCR#

