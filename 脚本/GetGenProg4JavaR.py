#读取GneProg4Java修复的log.txt，提取修复结果保存到csv中
#结果在倒数第5行：Repair Found: StmtAppend(114,245) (in variant66)
#修复时间在最后一行
#2021.3.25

import pandas as pd
import numpy as np
import os
import re
from pathlib import Path
import time

#读取文件倒数5行的内容
def loadData(filePath):
    result = []

    #打开文件
    with open(filePath,'r',encoding='utf-8') as f:  
        alllines = f.readlines()#读取所有行
        lines=alllines[-6:] #倒数6行
        print(lines)
        print(f'修复结果：{lines[0]}')
        for line in lines:
       
            if line.strip().split(':')[0] == 'Repair Found':
                result.append(line.replace('\n',' '))
            
            if line.strip().split(':')[0] == 'Total elapsed time':
                result.append(line.replace('\n',' '))

        if len(result) != 2:
                result.append('Failure ')
        if len(result) != 2:
                result.append(' time limit')
       
    #inputInfo = '' 
    inputInfo = result[0] + result[1]
    return inputInfo


#主函数：if __name__ == '__main__':

#文件根路径
file_path = "D:/VScode/repair/Chart"  #"E:/repair/GenProg4Java"

#使用pathlib来遍历文件
p = Path(file_path)

#计时
start = time.time()
project = []
bugID = []
#动态变量名
names = locals()
for i in range(10):
    names['seed'+str(i)] = []

# 直接遍历出文件绝对路径，pathlib.Path.rglob()方法可以直接遍历汇总所有文件的绝对路径（使用os.walk()遍历时需要拼接文件的绝对路径）
for file in p.rglob('*.txt'):
    f = os.path.split(file)  #分割路径
    #bug信息
    #print(f'文件:{f[1]}')
    file_name = f[1]
    #print(f'文件名:{file_name}')
    seed = file_name[7]
    #print(f'seed:{seed}')
    bug = os.path.split(f[0])
    Id = bug[1][:-1]
    #print(Id)
    bugID.append(Id)
    #print(f'bugID:{Id}')
    p = os.path.split(bug[0])[1]
    project.append(p)
    print(f'now read project:{p} bug:{Id} with seed: {seed}')

    #提取文件中的修复结果
    inputInfo = loadData(file)
    #print('修复结果是 %s' % (inputInfo))
    names['seed'+str(seed)].append(inputInfo)
    

#写入结果文件 #直接使用pandas读写，pandas以列为序
pro = []
bugid = []
for l in range(int(len(project)/10)):
    pro.append(project[l*10])
    bugid.append(bugID[l*10]) 
len = len(bugid)
print(f'读了{len}个bug')


#内容的长度需要一致1：10
df = pd.DataFrame({'project':pro,
                    'bugID':bugid,
                    'seed0':names.get('seed'+str(0)),   ##get获取动态变量名
                    'seed1':names.get('seed'+str(1)),
                    'seed2':names.get('seed'+str(2)),
                    'seed3':names.get('seed'+str(3)),
                    'seed4':names.get('seed'+str(4)),
                    'seed5':names.get('seed'+str(5)),
                    'seed6':names.get('seed'+str(6)),
                    'seed7':names.get('seed'+str(7)),
                    'seed8':names.get('seed'+str(8)),
                    'seed9':names.get('seed'+str(9))

                })
                                
#写入文件
df.to_csv('Q1-GenProg4Java.csv', mode='a', index=False,header=True)  #追加写

cost = round(time.time() - start, 2)
print(f'处理数据共用时{cost}秒')



