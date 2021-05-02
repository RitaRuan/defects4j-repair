#!/bin/bash

#补丁版本执行自动生成的测试套件

#对于每个工具可修复的每个bug：
	#1.下载源项目
	#2.将补丁应用到源项目中，编译
	#3.执行自动生成的测试套件，执行过程保存到log.txt

#参数：
#1.repair tool: Q1-kGenProg,GenProg-A,GenProg4Java,jGenProg,kGenProg-Jaccard,kGenProg-Tarantula,kGenProg-file
#2.project:Chart
#3.bugId:1...
#4.seed1:0-9
#5.seed2:1-10

# Example usage:
#./PatchRunTestcase.sh Q1-kGenProg Chart 18 0 10

if [ "$#" -ne 5 ]; then
    echo "This script should be run with 5 parameters:"
	echo "1st param: repair tool"
    	echo "2nd param: project name, sentence case (ex: Lang, Chart, Closure, Math, Time)"
	echo "3rd param: bug number (ex: 1,2,3,4,...)"
	echo "4th param: seed1 (0-9)"
	echo "5th param: seed2 (1-10)"	
    exit 0
fi

tool="$1"
project="$2"
bug="$3"
seed1="$4"
seed2="$5"
DATADIR="/home/rtx/repair/d4j/patch"

#脚本路径
ScriptDir=$(pwd)

mkdir -p $DATADIR/$tool/$project

#保存测试套件执行结果的文件
touch $DATADIR/$tool/logRunTestCase-$project.txt

#循环处理不同seed产生的补丁
for (( seed=$seed1; seed<$seed2; seed++ ))
do
	echo "running $tool $project $bug seed: $seed" >> $DATADIR/$tool/logRunTestCase-$project.txt
	
	#***1.下载源项目*****************
	echo "********download $project '$bug'b-$seed********" >> $DATADIR/$tool/logRunTestCase-$project.txt


	#创建目录，存储项目
	mkdir -p $DATADIR/$tool/$project/"$bug"b-$seed
        defects4j checkout -p $project -v "$bug"b -w $DATADIR/$tool/$project/"$bug"b-$seed
	
	cd $DATADIR/$tool/$project/"$bug"b-$seed
	src=`defects4j export -p dir.src.classes`
	
	#***2.将补丁应用到bug项目中*****************调用python脚本
	echo "********apply patch using python Script********" >> $DATADIR/$tool/logRunTestCase-$project.txt

	cd $ScriptDir
	python3 applyPatch.py $tool $project $bug $seed $src | tee -a $DATADIR/$tool/logRunTestCase-$project.txt

	
	#***3.补丁版本运行自动生成的5个测试套件*****************
	cd $ScriptDir
	echo "current directory:  $(pwd)"
	echo "********running test cases********** "
	./runTestcase.sh $tool $project $bug $seed | tee -a $DATADIR/$tool/logRunTestCase-$project.txt

done
