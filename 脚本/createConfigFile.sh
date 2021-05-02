#!/bin/bash
. /etc/profile

#这个脚本被prepareBug.sh调用
#目标： 创建配置文件

#输出：一个配置文件rungenprog4java.config

# 使用示例:
#./createConfigFile.sh Lang 1 $SRCFOLDER $libs $WD $TESTCP $COMPILECP


#参数：7个

if [ "$#" -ne 7 ]; then
	echo "This script(createConfigFile.sh) should be run with 7 parameters:"
	echo "1st param: PACKAGE, sentence case (ex: Lang, Chart)"
	echo "2nd param: BUGNUMBER (ex: 1,2,3,4,...)"
	echo "3th param: SRCFOLDER"
	echo "4th param: CONFIGLIBS"
	echo "5th param: WD"
	echo "6th param: TESTCP"
	echo "7th param: COMPILECP"
	exit 0
fi

PROJECT="$1"
BUGNUMBER="$2"
SRCFOLDER="$3"
CONFIGLIBS="$4"
WD="$5"
TESTCP="$6"
COMPILECP="$7"

DATADIR="/home/rtx/repair/d4j"
DIROFJAVA8="/home/rtx/java/jdk1.8.0_271"
BUGWD=$DATADIR/$PROJECT/"$BUGNUMBER"b

#创建配置文件
file=$BUGWD/rungenprog4java.config
/bin/cat <<EOM >$file
seed = 0
outputDir = $BUGWD/genprog4java
sanity = yes
popsize = 40
sourceVersion=1.8
javaVM = $DIROFJAVA8/jre/bin/java
workingDir = $BUGWD/
classSourceFolder = $BUGWD/$SRCFOLDER
libs = $CONFIGLIBS:$TESTCP:$COMPILECP
sourceDir = $WD
positiveTests = $BUGWD/pos.tests
negativeTests = $BUGWD/neg.tests
jacocoPath = $GP4J_HOME/lib/jacocoagent.jar
testClassPath=$TESTCP
srcClassPath=$COMPILECP
targetClassName = $BUGWD/bugfiles.txt
#class or method
testGranularity=class

# 0.1 for GenProg and 1.0 for TrpAutoRepair and PAR
sample=0.1 

# edits for PAR, GenProg, TrpAutoRepair
edits=APPEND;DELETE;REPLACE

# use 1.0,0.1 for TrpAutoRepair and PAR. Use 0.35 and 0.65 for GenProg
negativePathWeight=0.35
positivePathWeight=0.65

# trp for TrpAutoRepair, gp for GenProg and PAR 
search=gp

EOM


#这里，我们基本上将genprog的参数维护为默认参数，并添加所有可能的突变操作
