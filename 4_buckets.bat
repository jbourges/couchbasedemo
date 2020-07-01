@echo off

REM Couchbase demo bucket script

call 1_login.bat

SET COUCHBASE_HOME=C:\Progra~1\Couchbase\Server\bin

REM CREATE BUCKETS
%COUCHBASE_HOME%\couchbase-cli bucket-create -c 127.0.0.1:8091 --username %USER% --password %PASSWORD% --bucket searchdata --bucket-type couchbase --bucket-ramsize 3072
%COUCHBASE_HOME%\couchbase-cli bucket-create -c 127.0.0.1:8091 --username %USER% --password %PASSWORD% --bucket emaildata --bucket-type couchbase --bucket-ramsize 3072

REM CREATE INDEX
%COUCHBASE_HOME%\cbq -u %USER% -p %PASSWORD% -script "CREATE PRIMARY INDEX ON searchdata"
%COUCHBASE_HOME%\cbq -u %USER% -p %PASSWORD% -script "CREATE PRIMARY INDEX ON emaildata"

