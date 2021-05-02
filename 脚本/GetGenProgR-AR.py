#读取GneProg-A修复生成的补丁，提取修复结果保存到csv中
#文件夹不为空，则可以修复----不好实现，直接把生成的补丁读出来，就得到所有可以修复的bug的信息
#修复时间在最后一行
#2021.4.19

import pandas as pd
import numpy as np
import os
import re
from pathlib import Path
import time

#读取文件最后1行的内容
def loadData(filePath):
    result = []

    #打开文件，提取修复时间
    with open(filePath,'r',encoding='utf-8') as f:               
            lines = f.readlines()#读取所有行
            last_line=lines[-1]  #最后一行
            # print("修复成功")
            inputInfo = last_line.replace('\n',' ')

    return inputInfo


#主函数：if __name__ == '__main__':

#文件根路径
file_path = "E:/repair/GenProg-A"  #"E:/repair"

#使用pathlib来遍历文件
p = Path(file_path)

#计时
start = time.time()
project = []
bugID = []
seed = []
repair_time = []
patch_name = []

# 直接遍历出文件绝对路径，pathlib.Path.rglob()方法可以直接遍历汇总所有文件的绝对路径（使用os.walk()遍历时需要拼接文件的绝对路径）
for file in p.rglob('*.txt'):
    f = os.path.split(file)  #分割路径
    print(f'文件名:{f[1]}')
    patch_name.append(f[1])
    dir_name = os.path.split(f[0])
    print(f'目录名:{dir_name}')
    s = dir_name[1][11]
    seed.append(s)
    print(f'seed:{s}')
    bug = os.path.split(dir_name[0])
    Id = bug[1][:-1]
    bugID.append(Id)
    print(f'bugID:{Id}')
    p = os.path.split(bug[0])[1]
    project.append(p)
    print(f'project:{p}')
    print(f'now read project:{p} bug:{Id} with seed: {s}')

    #提取文件中的修复结果
    inputInfo = loadData(file)
    #print('修复结果是 %s' % (inputInfo))
    repair_time.append(inputInfo)
    

#写入结果文件 #直接使用pandas读写，pandas以列为序
# pro = []
# bugid = []
# for l in range(int(len(project)/10)):
#     pro.append(project[l*10])
#     bugid.append(bugID[l*10]) 
# len = len(bugid)
# print(f'读了{len}个bug')


#写入结果文件 #直接使用pandas读写，pandas以列为序
df = pd.DataFrame({'project':project,
                    'patch':patch_name,
                    'bugID':bugID,
                    'seed':seed,  
                    'repair_time':repair_time
                })
                                
#写入文件
df.to_csv('Q1-GenProg-A.csv', mode='a', index=False,header=True)  #追加写

cost = round(time.time() - start, 2)
print(f'处理数据共用时{cost}秒')



