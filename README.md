"# couchbasedemo" 

This is a demo of a data streaming application using couchbase, java and highcharts

It's running under windows 10.

Open a cmd console, and follow the steps to run each batch.


##1) Tool Install

Some tools are required:

- a recent JDK, for example jdk-11.0.7
https://www.oracle.com/java/technologies/javase-jdk11-downloads.html

- Couchbase 6.5.1 Enterprise Edition
https://www.couchbase.com/downloads/fr/start-today

When installing couchbase, you will be ask for a user password for the administrator account

please change it into this file:  
1_login.bat

##2) Compilation 

Extract this repo in a directory of your choice

Run: 2_compile.bat

##3) Transform Files

We will decompose the data/search.csv file in two parts, a csv and a json file.

This file is small to facilitate the copy of the repo, it could be replaced by the original big file   

Run: 3_data.bat

##4) Prepare buckets

Open the couchbase buckets interface http://localhost:8091/ui/index.html#!/buckets 
Click on each line to expand selection then delete + confirm delete bucket

We assume that couchbase is installed in this directory:
SET COUCHBASE_HOME=C:\Progra~1\Couchbase\Server\bin
If it's not the case fix it in the scripts (3)+(4)

Run: 4_buckets.bat

##5) Eventing

In this part we create a javascript function in the system, that can react and consume new searchdata events, 
and produce and store it in another bucket of data: emaildata

click on eventing (left bottom)  http://localhost:8091/ui/index.html#!/eventing/summary
click on import (righttop)
choose /scripts/5_new_email.json
click next add code
click on eventing (left bottom)
click on new_email new function
click deploy
click deploy function


##6) import csv & json

Run: 6_import.bat

It will produce data events in the system, and feed the buckets.

##7) Test a query

click on query http://localhost:8091/ui/index.html#!/query/workbench
input the query "select distinct email, domain from  emaildata where email is not null"
click on execute

##8) Test highcharts

In this part we read the buckets to transform the data into a highcharts html document.

Run: 8_charts.bat

Once it's done open \highcharts\country_device.html

You will get a dynamic chart with clickable categories


