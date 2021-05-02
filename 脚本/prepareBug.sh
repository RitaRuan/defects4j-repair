#!/bin/bash
. /etc/profile

#这个脚本被runGenProgForBug.sh调用
#该脚本的目的是设置运行特定defects4j bug的Genprog环境：获取通过和失败的测试用例，调用脚本创建配置文件

#前置条件：
#D4J_HOME 指定到defects4安装的目录
#GP4J_HOME指定到genprog4java安装的目录

#输出是一个txt文件：在每个指定的文件夹上运行测试套件的覆盖分析。

# 1st param: project name, sentence case (ex: Lang, Chart, Closure, Math, Time)
# 2nd param: bug number (ex: 1,2,3,4,...)

# Example usage, VM:
#./prepareBug.sh Math 2   

if [ "$#" -ne 2 ]; then
	echo "This script(prepareBug.sh) should be run with 2 parameters:"
	echo "1st param: project name, sentence case (ex: Lang, Chart, Closure, Math, Time)"
	echo "2nd param: bug number (ex: 1,2,3,4,...)"

    exit 0
fi

PROJECT="$1"
bug="$2"
DATADIR="/home/rtx/repair/d4j"

currentDir=$(pwd)

# 下载的项目所在的目录
BUGWD=$DATADIR/$PROJECT/"$bug"b


# genprog libs: junit测试运行器等
libs=$GP4J_HOME"/lib/junittestrunner.jar":"$GP4J_HOME"/lib/commons-cli-20040117.000000.jar:"$GP4J_HOME"/lib/junit-4.12.jar:"$GP4J_HOME"/lib/hamcrest-core-1.3.jar:"$GP4J_HOME"/lib/log4j-1.2.17.jar

#进入bug项目，
cd $BUGWD
TESTWD=`defects4j export -p dir.src.tests`
SRCFOLDER=`defects4j export -p dir.bin.classes`
COMPILECP=`defects4j export -p cp.compile`
TESTCP=`defects4j export -p cp.test`
WD=`defects4j export -p dir.src.classes`

cd $currentDir

#调用createConfigFile.sh，创建配置文件
echo "run createConfigFile.sh"
./createConfigFile.sh $PROJECT $bug $SRCFOLDER $libs $WD $TESTCP $COMPILECP

cd $BUGWD

# 获取通过和失败的测试以及文件
#bug信息

defects4j export -p tests.trigger > $BUGWD/neg.tests


currentpid="$currentpid $!"
wait $currentpid

#test: "onlyRelevant"  
##获取所有通过的相关的测试
#defects4j export -p tests.all > $BUGWD/pos.tests
defects4j export -p tests.relevant > $BUGWD/pos.tests

currentpid="$currentpid $!"
wait $currentpid

#删除测试套件中通过的测试，按TESTSUITESAMPLE的值，百分比。如果是10，就是0.1
cd $BUGWD

# 获取要修复的类名

defects4j export -p classes.modified > $BUGWD/bugfiles.txt

echo "This is the working directory: "
echo $BUGWD
