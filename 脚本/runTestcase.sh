#!/bin/bash

#run 5 evosuite test cases for 1 patch

#参数：
#1.repair tool: kGenProg,GenProg-A,GenProg4Java,jGenProg
#2.project:Chart
#3.bugId:
#4.seed:0-9

# Example usage:
#./runTestcase.sh Q1-kGenProg Chart 18 0

if [ "$#" -ne 4 ]; then
    echo "This script should be run with 4 parameters:"
	echo "1st param: repair tool"
    	echo "2nd param: project name, sentence case (ex: Lang, Chart, Closure, Math, Time)"
	echo "3rd param: bug number (ex: 1,2,3,4,...)"
	echo "4th param: seed (0-9)"
    exit 0
fi

tool="$1"
PROJECT="$2"
bug="$3"
seed="$4"
DATADIR="/home/rtx/repair/d4j/patch"
testcase="/home/rtx/repair/d4j/generatedTestSuites"


#deal with 1 patch
#echo "running $tool $PROJECT $bug seed: $seed"

#保存测试套件执行结果的文件
#touch $DATADIR/$tool/logRunTestCase-$PROJECT.txt

# patch所在的目录
BUGWD=$DATADIR/$tool/$PROJECT/"$bug"b-$seed

cd $BUGWD

echo "This is the working directory: $BUGWD "

#编译Patch项目
echo "compile patch project"
defects4j compile

#run 5  test cases
for (( i=1; i<=5; i++ ))
do
	echo "running $tool $PROJECT $bug seed: $seed the testcase: $i" >> $DATADIR/$tool/logRunTestCase-$PROJECT.txt

	defects4j test -s $testcase/$PROJECT-$bug-$i/$PROJECT/evosuite/1/$PROJECT-"$bug"f-evosuite.1.tar.bz2 | tee -a $DATADIR/$tool/logRunTestCase-$PROJECT.txt


done

