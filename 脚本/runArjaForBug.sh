#!/bin/bash
. /etc/profile
#--------------------------------
#功能：运行Genprog-A(Arja)修复特定的defects4j bug
#输出：输出是在d4j/arja-repair/中，其存储了所有的变体，包括找到的补丁。
#参数:   1.项目 (ex: Lang) 2.bug编号 (ex: 1...) 3.bug编号 (ex: 1...) 4. seed1    5.seed2
#示例：./runArjaForBug.sh Lang 1 3 0 10 
#--------------------------------

#前置条件：配置了环境变量D4J_HOME，指到defects4j的安装目录

#参数检查
if [ "$#" -lt 5 ]; then
	echo "This script should be run with 2 parameters:"
	echo " 1st param is the project in upper case (ex: Lang, Chart, Closure, Math, Time)"
	echo " 2nd param is the bug number (ex: 1,2,3,4,...)"
	echo " 3nd param is the last bug number (ex: 1,2,3,4,...)"
       	echo " 4nd param is the init seed (ex:0)"
	echo " 5nd param is the finish seed (ex: 10)"
else

PROJECT="$1"
bug1="$2"
bug2="$3"
SEED1="$4"
SEED2="$5"
#DIROFJAVA8="/home/ubuntu/rtx/java/jdk1.8.0_271"
JAVA8LOCATION=$(which java)    #/usr/bin/java
DATADIR="/home/rtx/repair/d4j"
#ARJA_HOME指定到arja安装的目录
ARJA_HOME="/home/rtx/repair/arja"

#loop to repair bugs from bug1 to bug2
for (( bug=$bug1; bug<=$bug2; bug++ ))
do
	# bug项目所在目录
	BUGWD=$DATADIR/$PROJECT/"$bug"b
	cd "$BUGWD"

	#获取bug源码及测试
	SRCWD=`defects4j export -p dir.src.classes`
	TESTWD=`defects4j export -p dir.src.tests`

	#获取bug及测试class
	binsrc=`defects4j export -p dir.bin.classes`
	bintest=`defects4j export -p dir.bin.tests`
	
	#echo $bintest

	#获取编译和运行项目的classpath
	COMPILECP=`defects4j export -p cp.compile`
	TESTCP=`defects4j export -p cp.test`

	#echo $COMPILECP
	#echo $TESTCP

	#补丁输出目录
	mkdir $BUGWD/genprog-A
	
	#进入arja文件夹
	cd "$ARJA_HOME"

	#用不同的种子运行多次，没有seed参数，就直接运行10次
	for (( seed=$SEED1; seed<$SEED2; seed++ ))
     	do
	
		mkdir $BUGWD/genprog-A/variantSeed$seed/
		echo "RUNNING THE BUG: $PROJECT $bug, WITH THE SEED: $seed"
		echo "RUNNING THE BUG: $PROJECT $bug, WITH THE SEED: $seed" > $BUGWD/genprog-A/logSeed$seed.txt

    
		#运行genprog-a（arja）进行修复，设置超时时间30m；源和class路径要根据项目chart：
		timeout 30m $JAVA8LOCATION -cp lib/*:bin us.msu.cse.repair.Main GenProg -DsrcJavaDir $BUGWD/$SRCWD -DbinJavaDir $BUGWD/$binsrc -DbinTestDir $BUGWD/$bintest -Ddependences $COMPILECP:$TESTCP -DpatchOutputRoot $BUGWD/genprog-A/variantSeed$seed/ | tee -a $BUGWD/genprog-A/logSeed$seed.txt 

	done

done

fi #correct number of params
