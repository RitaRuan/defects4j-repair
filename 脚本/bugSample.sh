#!/bin/bash

#--------------------------------
#功能：bug随机抽样
#参数：1.项目名  2.sample(0.5/0.3) 3.项目原bug数量
#输出：bugSample.csv
#--------------------------------

if [ "$#" -ne 3 ]; then
    echo "This script should be run with 2 parameters:"
	echo "1st param: project name, sentence case (ex: Lang, Chart, Closure, Math, Time)"
	echo "2nd param: bug sample (ex: 0.5,0.3...)"
	echo "3nd param: the number of project's bug(ex: 项目Chart是26个...)"
    exit 0
fi

projectName=$1     
sample=$2
number=$3

#抽取的bug数量
bugSampleN=$number*$sample

#创建与bug数量相同的数组
$ array=()
$ result=()
for(i=1;i<$number;i++)
do
	$ array+=($i)
done

#抽取出$bugSampleN个bug
while [ $i -lt $bugSampleN ];do
	#以时间产生随机数向39取余得到0~bug数量-1的值
	a=$(expr $(date +%N) % $number)                                                                 
	
	#生成随机数
 	random=${array[$a]} 
	echo "抽出的bugid为：$random"   
	#新数据，存入数组
	for i in ${result[@]}
	do
  		[ "$i" != "$random" ] && $ result+=${array[$a]}    
	done
	#[[ ${result[@]/${random}/} != ${result[@]} ]] && echo "Yes" || echo "No"
	

	#$ result+=${array[$a]}                                                                                                                  
	#步长
	i=$(( $i + 1 ))

done

#写入csv文件
filePath="/home/ubuntu/rtx/d4j/"
resultFile="${filePath}/${projectName}_Sample.csv"

cd filePath
if [ ! -f ${resultFile} ]
then
	touch ${resultFile}
	echo projectName,bugN,SampleN,bugID > ${resultFile}
fi

echo ${projectName},${number},"${bugSampleN}","${result}">> ${resultFile}

echo "写入文件完成"

