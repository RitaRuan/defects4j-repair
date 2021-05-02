#/bin/bash

#replace ":" to " " in "defects4j export -p cp.test"
#alg: 1.project(ex:Lang) 2.bugID(ex:1)
#output:replace result 

project=$1
bugID=$2

ProjectPath="/home/rtx/repair/d4j/"$project"/"$bugID"b"
cd $ProjectPath

COMPILECP=`defects4j export -p cp.compile`
TESTCP=`defects4j export -p cp.test`
string=$TESTCP
string2=$COMPILECP

#delele ":" ,put in array
result=${string//:/ }
result2=${string2//:/ }

#output
echo "cp.test:"
echo $result
echo "cp.compile:"
echo $result2

