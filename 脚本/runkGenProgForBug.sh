#!/bin/bash

#--------------------------------
#功能：运行kGenprog修复defects4j中bug项目中的某些bug（不需要lib）
#输出：输出是在bug目录/kgenprog-out下，其存储了找到的补丁及所有已生成变体的生成过程。
#参数:   1.项目 (ex: Lang) 2.bug1 (ex: 1...) 3.bug2 (ex: 2...) 4.seed1(ex:0) 5.seed2(ex:20)  
#示例：./runkGenProgForBug.sh Math 1 20 0 20（修复bug Math1- Math20 with seed 0-19）
#--------------------------------

#前置条件：1.bug项目已经下载到本地 2.jdk为1.11

#参数检查
if [ "$#" -lt 5 ]; then
	echo "This script should be run with 5 parameters:"
	echo " 1st param is the project in upper case (ex: Lang, Chart, Closure, Math, Time)"
	echo " 2nd param is the bug number (ex: 1,2,3,4,...)"
	echo " 3nd param is the bug number (ex: 1,2,3,4,...)"
	echo " 4nd param is the seed1 (ex: 0)"
	echo " 5nd param is the seed2 (ex: 20)"
	
else

PROJECT="$1"
BUG1="$2"
BUG2="$3"
SEED1="$4"
SEED2="$5"
DIROFJAVA="/usr/lib/jvm/java-11-openjdk-amd64"
DATADIR="/home/rtx/repair/d4j/$PROJECT"
KGP_HOME="/home/rtx/repair/kGenProg"

#当前目录
currentDir=$(pwd)
#切换到当前目录
#cd $currentDir

for (( bug=$BUG1; bug<=$BUG2; bug++ ))
do
	# 当前bug项目所在目录
	BUGWD=$DATADIR/"$bug"b
	cd "$BUGWD"

	mkdir $BUGWD/kGenProg-Jaccard
	
	#获取bug源码及测试
	SRCWD=`defects4j export -p dir.src.classes`
	TESTWD=`defects4j export -p dir.src.tests`
	
	#获取编译和运行项目的classpath
	COMPILECP=`defects4j export -p cp.compile`
	TESTCP=`defects4j export -p cp.test`

	string1=$COMPILECP
        string2=$TESTCP

	#:->space
        COMLIB=${string1//:/ }
        TESTLIB=${string2//:/ }

	echo $TESTLIB

	#获取失败测试用例
	NEGT=`defects4j export -p tests.trigger`
	#截取出测试类(:)-删除右边，保留左边
	#echo ${NEGT%%:*}

	#用不同的种子运行n次
	for (( seed=$SEED1; seed<$SEED2; seed++ ))
     	do
		echo "RUNNING THE BUG: $PROJECT $bug, WITH THE SEED: $seed"
		echo "RUNNING THE BUG: $PROJECT $bug, WITH THE SEED: $seed" > $BUGWD/kGenProg-Jaccard/logseed$seed.txt
    
		#运行k-genprog进行修复$currentDir/hamcrest-core-1.3.jar
		timeout 30m $DIROFJAVA/bin/java -jar "$KGP_HOME"/kGenProg-1.8.0.jar -r ./ -s $SRCWD -t $TESTWD -x ${NEGT%%:*} --random-seed $seed --fault-localization Jaccard --scope package -o $BUGWD/kGenProg-Jaccard/patch$seed -c /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/junit/junit/4.5/junit-4.5.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/commons-lang/commons-lang/2.4/commons-lang-2.4.jar /home/rtx/repair/defects4j/framework/projects/lib/junit-4.11.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/com/google/code/gson/gson/2.7/gson-2.7.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/junit/junit/4.12/junit-4.12.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/com/google/code/gson/gson/2.7/gson-2.7.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/commons-lang/commons-lang/2.4/commons-lang-2.4.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/junit/junit/4.12/junit-4.12.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/junit/junit/4.5/junit-4.5.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-http/9.2.22.v20170606/jetty-http-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-http/9.2.26.v20180806/jetty-http-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-http/9.2.28.v20190418/jetty-http-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-io/9.2.22.v20170606/jetty-io-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-io/9.2.26.v20180806/jetty-io-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-io/9.2.28.v20190418/jetty-io-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-security/9.2.22.v20170606/jetty-security-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-security/9.2.26.v20180806/jetty-security-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-security/9.2.28.v20190418/jetty-security-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-server/9.2.22.v20170606/jetty-server-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-server/9.2.26.v20180806/jetty-server-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-server/9.2.28.v20190418/jetty-server-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-servlet/9.2.22.v20170606/jetty-servlet-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-servlet/9.2.26.v20180806/jetty-servlet-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-servlet/9.2.28.v20190418/jetty-servlet-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-util/9.2.22.v20170606/jetty-util-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-util/9.2.26.v20180806/jetty-util-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/eclipse/jetty/jetty-util/9.2.28.v20190418/jetty-util-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/lib/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-http/9.2.22.v20170606/jetty-http-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-http/9.2.26.v20180806/jetty-http-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-http/9.2.28.v20190418/jetty-http-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-io/9.2.22.v20170606/jetty-io-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-io/9.2.26.v20180806/jetty-io-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-io/9.2.28.v20190418/jetty-io-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-security/9.2.22.v20170606/jetty-security-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-security/9.2.26.v20180806/jetty-security-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-security/9.2.28.v20190418/jetty-security-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-server/9.2.22.v20170606/jetty-server-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-server/9.2.26.v20180806/jetty-server-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-server/9.2.28.v20190418/jetty-server-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-servlet/9.2.22.v20170606/jetty-servlet-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-servlet/9.2.26.v20180806/jetty-servlet-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-servlet/9.2.28.v20190418/jetty-servlet-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-util/9.2.22.v20170606/jetty-util-9.2.22.v20170606.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-util/9.2.26.v20180806/jetty-util-9.2.26.v20180806.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/eclipse/jetty/jetty-util/9.2.28.v20190418/jetty-util-9.2.28.v20190418.jar /home/rtx/repair/defects4j/framework/projects/Jsoup/lib/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar --patch-output | tee -a $BUGWD/kGenProg-Jaccard/logseed$seed.txt

	done

done

fi #correct number of params
