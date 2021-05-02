#!/bin/bash
. /etc/profile

#该脚本的目的是用Genprog修复特定的defects4j bug。

#前置条件：
#D4J_HOME 指定到defects4安装的目录
#GP4J_HOME指定到genprog4java安装的目录


#输出：输出是在 中创建的一个文件夹，其中存储了所有的变体，包括找到的补丁。

#参数:
# 1st param ：项目，首字母大写 (ex: Lang, Chart, Closure, Math, Time)
# 2nd param ：bug编号 (ex: 1,2,3,4,...)
# 3th param ：bug2
# 4th param ：初始seed. 然后它会通过加1来增加种子，直到它达到下一个参数中的数字
# 5th param ：seed的终值.

#使用示例:
#./runGenProg4444ForBug.sh Math 1 2 0 10  


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
DATADIR="/home/rtx/repair/d4j"

if [ -z "$D4J_HOME" ]; then
    echo "Need to set D4J_HOME"
    exit 1
fi  
if [ -z "$GP4J_HOME" ]; then
    echo "Need to set GP4J_HOME"
    exit 1
fi  

#当前目录
currentDir=$(pwd)

#loop to repair bugs from bug1 to bug2
for (( bug=$bug1; bug<=$bug2; bug++ ))
do

	# bug项目的目录
	BUGWD=$DATADIR/$PROJECT"/""$bug"b

	#切换到当前目录

	cd $currentDir

    	#调用./prepareBug.sh ，设定运行环境（获取测试用例、创建配置文件）

	echo "run prepareBug.sh"
     	./prepareBug.sh $PROJECT $bug  

    	#进入工作路径
      	cd $BUGWD

	mkdir $BUGWD/genprog4java

        #用不同的种子运行多次
      	for (( seed=$SEED1; seed<$SEED2; seed++ ))
      	do	

		mkdir $BUGWD/genprog4java/variantSeed$seed/

		echo "RUNNING THE BUG: $PROJECT $bug, WITH THE SEED: $seed"

		echo "RUNNING THE BUG: $PROJECT $bug, WITH THE SEED: $seed" > $BUGWD/genprog4java/logSeed$seed.txt


		#改变seed
		CHANGESEEDCOMMAND="sed -i '1s/.*/seed = $seed/' "$BUGWD/rungenprog4java.config
		eval $CHANGESEEDCOMMAND
    
		#改变dr of variants
                CHANGEOUTCOMMAND="sed -i '2c outputDir = $BUGWD/genprog4java/variantSeed$seed/' "$BUGWD/rungenprog4java.config
                eval $CHANGEOUTCOMMAND

		#运行genprog4Java进行修复，设置超时时间30m
		timeout 1h $DIROFJAVA8/bin/java -ea -Dlog4j.configurationFile=file:"$GP4J_HOME"/src/log4j.properties -Dfile.encoding=UTF-8 -classpath "$GP4J_HOME"/target/uber-GenProg4Java-0.0.1-SNAPSHOT.jar clegoues.genprog4java.main.Main $BUGWD/rungenprog4java.config | tee $BUGWD/genprog4java/logSeed$seed.txt

		
		#存储变体到tar文件中/home/rtx/repair/d4j/Lang/1b/genprog4java

		tar -cpvf $BUGWD/genprog4java/variantsSeed$seed.tar "$BUGWD"/genprog4java/variantSeed$seed

		mv $BUGWD/genprog4java/variantSeed$seed/original/ $BUGWD/genprog4java/

		rm -r $BUGWD/genprog4java/variantSeed$seed/
		mkdir $BUGWD/genprog4java/variantSeed$seed/
		mv $BUGWD/genprog4java/original/ $BUGWD/genprog4java/variantSeed$seed/
	
      done

done

fi #correct number of params
