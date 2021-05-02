#!/usr/bin/python3

#指定使用python3

#执行方法：python3 applyPatch-GenProg4Java.py GenProg4Java Chart 8 0

#Linux系统
# 对于GenProg4Java可修复的每个bug产生的每个补丁：将补丁应用到源项目中
#进入补丁目录，找到补丁中所有以Java结尾的文件，循环处理每个Java文件：
#**1.获取文件在bug项目里的位置---使用pathlib.Path.rglob()方法可以直接遍历汇总所有文件的绝对路径，使用字符串截取，在variant1之后、文件名之前的就是文件在项目里的位置
#**2.删除bug项目中的文件
#**3.将补丁中该文件复制到源bug项目中

#参数：先导入系统接口模块import sys，再调用系统命令行参数sys.argv，sys.argv[1]是命令行的第一个参数。用形参来接收sys.argv[1]
#1.repair tool: GenProg4Java
#2.project:Chart
#3.bugId:1...
#4.seed:0-9

#编写：2021.4.24

import sys
import os
from pathlib import Path

#删除bug项目中的文件f(绝对路径)
def delFile(d,file_name): 
    f = os.path.join(d+'/'+file_name)
    #判断文件是否存在
    if(os.path.exists(f)):
        #删除该文件
        os.remove(f)
        print (file_name+'文件删除成功')
    else:
        print (file_name+'文件不存在！')

#将bug项目中的文件替换为补丁中对应的文件
def applyPatch(patchdir,bugdir,seed):
        #使用pathlib来遍历文件
        p = Path(patchdir)
        # 遍历出补丁中所有Java文件的绝对路径，循环处理每一个文件
        for file in p.rglob('*.java'):
                d, filename = os.path.split(file)  #分割路径，返回目录和文件名
                
                print("******deal with file: ********",filename)
                
                #文件在项目中的位置
                dir = d.split('variant'+seed)[1]
                #print(d)
                #print('分割路径后：'+dir)

                #源bug项目中该文件所在绝对目录
                fileP = bugdir+dir

                #2.删除源程序file
                delFile(fileP,filename)
                                                
                #3.将补丁中该文件复制到源项目中
                # s = 'cp '+file+' '+fileP
                # print(s)
                os.system('cp '+str(file)+' '+fileP)
  
#主函数
if __name__=="__main__":
    #print("main")
    #接收命令行的参数
    tool = sys.argv[1]
    project = sys.argv[2]
    bugId = sys.argv[3]
    seed = sys.argv[4]
    src = sys.argv[5]
    #print(tool)   

    #补丁的位置
    patchdir = "/home/rtx/repair/d4j/"+project+"/"+bugId+"b/"+tool+"/variant"+seed
    #print(patchdir)
    #bug项目中源文件位置
    bugdir = "/home/rtx/repair/d4j/patch/"+tool+"/"+project+"/"+bugId+"b-"+seed+"/"+src
    #print(bugdir)
    
    #调用函数，将补丁应用到源bug项目中
    applyPatch(patchdir,bugdir,seed)
