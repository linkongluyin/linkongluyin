[toc]



分布式SQL计算：

对数据进行统计分析，SQL是目前最为方便的编程工具。大数据体系中充斥着非常多的统计分析场景。所以，使用SQL去处理数据，在大数据中也是有极大的需求的。

> MapReduce支持程序开发（Java、Python等）但不支持SQL开发



分布式SQL计算 - Hive

Apache Hive是一款分布式SQL计算的工具， 其主要功能是：

- 将SQL语句 翻译成MapReduce程序运行

![image-20230419224306460](https://raw.githubusercontent.com/linkongluyin/images/main/202304192243612.png)

> 基于Hive为用户提供了分布式SQL计算的能力写的是SQL、执行的是MapReduce
>
> 以分布式的形式，执行SQL语句，进行数据统计分析



为什么使用Hive:

使用Hadoop MapReduce直接处理数据所面临的问题:

- 人员学习成本太高 需要掌握java、Python等编程语言
- MapReduce实现复杂查询逻辑开发难度太大 

使用Hive处理数据的好处：

- 操作接口采用类SQL语法，提供快速开发的能力（简单、容易上手）
- 底层执行MapReduce，可以完成分布式海量数据的SQL处理



# Hive架构

Apache Hive其2大主要组件就是：SQL解析器以及元数据存储

![image-20230419224750484](https://raw.githubusercontent.com/linkongluyin/images/main/202304192247530.png)

整体架构：

![image-20230419224930083](https://raw.githubusercontent.com/linkongluyin/images/main/202304192249129.png)

## Hive组件

### 元数据存储

通常是存储在关系数据库如 mysql/derby中。Hive 中的元数据包括表的名字，表的列和分区及其属性，表的属性（是否为外部表等），表的数据所在目录等。

Hive提供了 Metastore 服务进程提供元数据管理功能



### Driver驱动程序

Driver驱动程序包括：

- 语法解析器
- 计划编译器
- 优化器
- 执行器

完成 HQL 查询语句从词法分析、语法分析、编译、优化以及查询计划的生成。

生成的查询计划存储在 HDFS 中，并在随后有执行引擎调用执行。

这部分内容不是具体的服务进程，而是封装在Hive所依赖的Jar文件即Java代码中。





### 用户接口

包括 CLI、JDBC/ODBC、WebGUI。其中，CLI(command line interface)为shell命令行；

Hive中的Thrift服务器允许外部客户端通过网络与Hive进行交互，类似于JDBC或ODBC协议。

WebGUI是通过浏览器访问Hive。

Hive提供了 Hive Shell、 ThriftServer等服务进程向用户提供操作接口



## Hive部署

- Hive是单机工具，只需要部署在一台服务器即可。
- Hive虽然是单机的，但是它可以提交分布式运行的MapReduce程序运行。

Hive是单机工具，就需要准备一台服务器供Hive使用即可。

同时Hive需要使用元数据服务，即需要提供一个关系型数据库，需要选择一台服务器安装关系型数据库即可。

| **服务**                                                | **机器**    |
| ------------------------------------------------------- | ----------- |
| Hive本体                                                | 部署在node1 |
| 元数据服务所需的关系型数据库（课程选择最为流行的MySQL） | 部署在node1 |

> 为了简单起见，都安装到node1服务器上

### 安装MySQL数据库

在node1节点使用yum在线安装MySQL5.7版本

```shell
# 更新密钥
rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022

# 安装Mysql yum库
rpm -Uvh http://repo.mysql.com//mysql57-community-release-el7-7.noarch.rpm

# yum安装Mysql
yum -y install mysql-community-server

# 启动Mysql设置开机启动
systemctl start mysqld
systemctl enable mysqld

# 检查Mysql服务状态
systemctl status mysqld

# 第一次启动mysql，会在日志文件中生成root用户的一个随机密码，使用下面命令查看该密码
grep 'temporary password' /var/log/mysqld.log

# 修改root用户密码
mysql -u root -p -h localhost
Enter password:
 
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'Root!@#$';

# 如果你想设置简单密码，需要降低Mysql的密码安全级别
set global validate_password_policy=LOW; # 密码安全级别低
set global validate_password_length=4;	 # 密码长度最低4位即可

# 然后就可以用简单密码了（课程中使用简单密码，为了方便，生产中不要这样）
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';

/usr/bin/mysqladmin -u root password 'root'

grant all privileges on *.* to root@"%" identified by 'root' with grant option;  
flush privileges;
```



### 配置Hadoop

Hive的运行依赖于Hadoop（HDFS、MapReduce、YARN都依赖）

同时涉及到HDFS文件系统的访问，所以需要配置Hadoop的代理用户

即设置hadoop用户允许代理（模拟）其它用户



配置如下内容在Hadoop的`core-site.xml`中，并分发到其它节点，且重启HDFS集群

![image-20230419230128779](https://raw.githubusercontent.com/linkongluyin/images/main/202304192301866.png)

> *表示代理所有主机和用户



### 下载解压Hive

- 切换到hadoop用户 `su - hadoop`
- 下载Hive安装包：wget http://archive.apache.org/dist/hive/hive-3.1.3/apache-hive-3.1.3-bin.tar.gz
- 解压到node1服务器的：/data/server内 `tar -zxvf apache-hive-3.1.3-bin.tar.gz -C /data/server`
- mv /data/server/apache-hive-3.1.3-bin /data/server/hive



### 配置MySQL Driver包

- 下载MySQL驱动包：https://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.34/mysql-connector-java-5.1.34.jar
- 将下载好的驱动jar包，放入：Hive安装文件夹的lib目录内 `mv mysql-connector-java-5.1.34.jar /data/server/hive/lib/`



### 配置Hive

- 在Hive的conf目录内，新建hive-env.sh文件，填入以下环境变量内容

```shell
export HADOOP_HOME=/data/server/hadoop
export HIVE_CONF_DIR=/data/server/hive/conf
export HIVE_AUX_JARS_PATH=/data/server/hive/lib
```

- 在Hive的conf目录内，新建hive-site.xml文件

```xml
<configuration>
  <property>
    <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://node1:3306/hive?createDatabaseIfNotExist=true&amp;useSSL=false&amp;useUnicode=true&amp;characterEncoding=UTF-8</value>
  </property>

  <property>
    <name>javax.jdo.option.ConnectionDriverName</name>
    <value>com.mysql.jdbc.Driver</value>
  </property>

  <property>
    <name>javax.jdo.option.ConnectionUserName</name>
    <value>root</value>
  </property>

  <property>
    <name>javax.jdo.option.ConnectionPassword</name>
    <value>123456</value>
  </property>

  <property>
    <name>hive.server2.thrift.bind.host</name>
    <value>node1</value>
  </property>

  <property>
    <name>hive.metastore.uris</name>
    <value>thrift://node1:9083</value>
  </property>

  <property>
    <name>hive.metastore.event.db.notification.api.auth</name>
    <value>false</value>
  </property>

</configuration>
```



初始化元数据库

支持，Hive的配置已经完成，现在在启动Hive前，需要先初始化Hive所需的元数据库

- 在MySQL中新建数据库：hive

  ```sql
  CREATE DATABASE hive CHARSET UTF8;
  ```

- 执行元数据库初始化命令：

  ```shell
  cd /data/server/hive
  
  bin/schematool -initSchema -dbType mysql -verbos
  # 初始化成功后，会在MySQL的hive库中新建74张元数据管理的表。
  ```

  

### 启动Hive（使用Hadoop用户）

- 确保Hive文件夹所属为hadoop用户

- 创建一个hive的日志文件夹： 

  ```shell
  mkdir /data/server/hive/logs
  ```

- 启动元数据管理服务（必须启动，否则无法工作）

  ```shell
  前台启动：bin/hive --service metastore 
  后台启动：nohup bin/hive --service metastore >> logs/metastore.log 2>&1 &
  ```

- 启动客户端，二选一（当前先选择Hive Shell方式）

  ```shell
  Hive Shell方式（可以直接写SQL）： bin/hive
  
  Hive ThriftServer方式（不可直接写SQL，需要外部客户端链接使用）： bin/hive --service hiveserver2
  ```

  