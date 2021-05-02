#!/usr/bin/python3
#指定使用python3

#执行方法：python3 applyPatch.py Q1-kGenProg Chart 8 0
#添加文件的可执行权限之后可以直接使用./applyPatch.py

#Linux系统
# 对于每个工具可修复的每个bug产生的每个补丁：将补丁应用到源项目中
#进入补丁目录，找到补丁中所有以Java结尾的文件，循环处理每个Java文件：
#**1.以“.”分隔文件名，一段是一级目录，最后一个是文件名，根据分隔出来的结果到源项目中找到该文件
#**2.删除bug项目中的文件
#**3.将补丁中该文件复制到源项目中，并修改名字
#**4.补丁应用完成后，编译项目-----》放shell脚本中

#参数：先导入系统接口模块import sys，再调用系统命令行参数sys.argv，sys.argv[1]是命令行的第一个参数。用形参来接收sys.argv[1]
#1.repair tool: Q1-kGenProg,kGenProg-Jaccard,kGenProg-Tarantula,kGenProg-file
#2.project:Chart
#3.bugId:1...
#4.seed:0-9

#编写：2021.4.21

import sys
import os
from pathlib import Path

#删除bug项目中的文件f(绝对路径)
def delFile(f,file_name): 
    #判断文件是否存在
    if(os.path.exists(f)):
        #删除该文件
        os.remove(f)
        print (file_name+'文件删除成功')
    else:
        print (file_name+'文件不存在！')

#将bug项目中的文件替换为补丁中对应的文件
def applyPatch(patchdir,bugdir):
        for parentdir,dirname,filenames in os.walk(patchdir):
                #1.获取补丁子目录下所有的Java文件(not null)
                if len(dirname) != 0:
                        for pdir,dname,fnames in os.walk(patchdir+"/"+dirname[0]):
                                for filename in fnames: 
                                        #循环处理每一个Java文件
                                        if os.path.splitext(filename)[1]=='.java':#java文件
                                                print("******deal with file: ********",filename) 
                                                file = []
                                                bugP = ""
                                                for i in range(len(filename.split('.'))): #"."分割文件名
                                                        file.append(filename.split('.')[i])
                                                for j in range(len(file)-2): #文件名和java后缀排除
                                                        #拼接路径
                                                        bugP += os.path.join("/"+file[j])
                                                #print(bugP)

                                                #源bug项目中该文件所在目录和文件
                                                fileP =  bugdir+""+bugP
                                                fname = file[-2] +"."+ file[-1]
                                                
                                                #文件的绝对path
                                                f = fileP+"/"+fname
                                                #print(f)
                                                #2.删除源程序file
                                                delFile(f,fname)
                                                
                                                #3.将补丁中该文件复制到源项目中，并修改名字
                                                s = 'cp '+patchdir+'/'+dirname[0]+'/'+filename+' '+fileP
                                                print(s)
                                                os.system('cp '+patchdir+'/'+dirname[0]+'/'+filename+' '+fileP)
                                                #修改文件名(旧,新)
                                                s = 'mv '+fileP+'/'+filename+' '+fileP+'/'+fname
                                                print(s)
                                                #os.rename(fileP+'/'+filename,fileP+'/'+fname)
                                                os.system('mv '+fileP+'/'+filename+' '+fileP+'/'+fname)

                    
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
    patchdir = "/home/rtx/repair/d4j/"+project+"/"+bugId+"b/"+tool+"/patch"+seed
    #print(patchdir)
    #bug项目中源文件位置(不同项目会有所不同)  #src/java   source
    bugdir = "/home/rtx/repair/d4j/patch/"+tool+"/"+project+"/"+bugId+"b-"+seed+"/"+src
    #print(bugdir)
    
    #调用函数，将补丁应用到源bug项目中
    applyPatch(patchdir,bugdir)

    #4.补丁应用完成后，编译项目
    #os.system('cd '+bugdir)
    #os.system('defects4j compile')
