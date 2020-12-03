# 说明
   本项目是为应用内部分发而设计的，支持android和ios平台(需支持SSL证书)的项目分发下载，需配合共享主机（存放安装包的FTP，SMB等共享）使用

## 安装
### 配置
    下载项目后，在根目录执行gradle build
    替换自己的证书/app/src/main/resources/release/test.jks,并修改application.properties中相关配置
### 本地安装
    java -jar app-0.0.1-SNAPSHOT.jar
### docker安装     
    docker dockerBuildImage
    docker run --restart=always --name pecker -p 443:443 -p 80:80 -d pecker:latest --env admin_pwd=admin
## 初始化
    访问 http://localhost/ 可以看到测试一个应用
  
### 添加共享主机
   访问 https://localhost/server  pecker
   点击底部`新建`（需要登录默认账号/密码 `admin/admin` ）
   共享主机目前支持三种类型 FTP、SMB、SEAFILE,分别需要录入主机的以下信息 
   
   |类型 |主机 | 端口| 账号| 密码|库ID|Token|   
   |:---- |---- | ----| ----| ----|----|----|   
   |FTP |√|√|√|√|||  
   |SMB |√|√|√|√|||  
   |SEAFILE |√|√|√|√|√|√|  
  
  FTP默认端口`21`,SMB默认端口`445`,SEAFILE可以`账号+密码`与 `Token` 二选一录入
    
### 添加应用
   访问 https://localhost/app   
   点击底部`新建`（需要登录默认账号/密码 `admin/admin` ） ,需要录入应用的以下信息   
          
   |字段 |说明|   
   |:---- |---- |  
   |名称 |应用名称 可中文|
   |包名 |应用包名 android的包名 iOS的bundleId|
   |平台 |平台类型（安卓或苹果）|
   |共享主机 |添加或选择一个共享主机|
   |测试 |测试包所在绝对路径 如：`/app/alpha`|
   |正式 |正式包所在绝对路径 如：`/app/trial`|
   |发布 |发布包所在绝对路径 如：`/app/release`|
     
## 帮助
     证书格式转换 https://myssl.com/cert_convert.html    
    
    