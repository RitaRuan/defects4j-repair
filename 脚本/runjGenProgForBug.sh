#!/bin/bash
. /etc/profile

#jGenprog修复defects4j bug

#输出：变体

#参数:
# 1st param 项目 (ex: Lang, Chart, Closure, Math, Time)
# 2nd param bugID (ex: 1,2,3,4,...)
# 3th param bug2
# 4th param 初始seed.如0
# 5th param 最终seed.

#示例:
#./runjGenProgForBug.sh Math 1 2 0 10  


if [ "$#" -lt 5 ]; then
	echo "This script should be run with 5 parameters:"
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
DIROFJAVA8="/home/rtx/java/jdk1.8.0_271"
DATADIR="/home/rtx//repair/d4j"
GP_HOME="/home/rtx/repair/astor-2"
jar=$GP_HOME/target/astor-1.0.0-SNAPSHOT-jar-with-dependencies.jar

currentDir=$(pwd)

#loop to repair bugs from bug1 to bug2
for (( bug=$bug1; bug<=$bug2; bug++ ))
do

	# bug项目
	BUGWD=$DATADIR/$PROJECT"/""$bug"b
	cd "$BUGWD"

	#compile project
	defects4j compile

	#获取bug源码及测试
	SRCWD=`defects4j export -p dir.src.classes`
	TESTWD=`defects4j export -p dir.src.tests`

	#获取bug及测试class
	binsrc=`defects4j export -p dir.bin.classes`
	bintest=`defects4j export -p dir.bin.tests`

	#获取编译和运行项目的classpath
	COMPILECP=`defects4j export -p cp.compile`
	TESTCP=`defects4j export -p cp.test`

    	#获取失败测试用例
	NEGT=`defects4j export -p tests.trigger`
	#截取出测试类(:)-删除右边，保留左边
	#echo ${NEGT%%:*}

	#补丁输出目录
	mkdir $BUGWD/jgenprog

        #用不同的种子运行
      	for (( seed=$SEED1; seed<$SEED2; seed++ ))
      	do	

		mkdir $BUGWD/jgenprog/variantSeed$seed/

		echo "RUNNING THE BUG: $PROJECT $bug, WITH THE SEED: $seed"

		echo "RUNNING THE BUG: $PROJECT $bug, WITH THE SEED: $seed" > $BUGWD/jgenprog/logSeed$seed.txt   

		#运行jgenprog 超时30m

	        timeout 30m $DIROFJAVA8/bin/java -cp $jar fr.inria.main.evolution.AstorMain -mode jGenProg -location $BUGWD -jvm4testexecution $DIROFJAVA8/bin/ -failing ${NEGT%%:*} -srcjavafolder $SRCWD -srctestfolder $TESTWD -binjavafolder $binsrc -bintestfolder $bintest -seed $seed -flthreshold 0.5 -stopfirst true -dependencies $COMPILECP:$TESTCP:/home/rtx/repair/defects4j/framework/project/Math/lib/commons-discovery-0.5.jar -out $BUGWD/jgenprog/variantSeed$seed | tee $BUGWD/jgenprog/logSeed$seed.txt
			
      done

done

fi #correct number of params
