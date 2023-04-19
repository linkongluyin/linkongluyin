[toc]

# Hadoop

## 概念

Hadoop是Apace软件基金会下的顶级开源项目，用于提供：

- 分布式数据存储
- 分布式数据计算
- 分布式资源调度

为一体的整体解决方案

Apache Hadoop是典型的分布式软件框架，可以部署在一台甚至成千上万台服务器结点上协同工作。

个人或企业可以借助Hadoop构建大规模的服务器集群，完成海量存储和计算。



通常意义上讲，Hadoop是一个整体，其内部还会细分为三个组件，分别是：

- HDFS组件：HDFS是Hadoop内的分布式存储组件，可以构建分布式文件系统以用于数据存储
- MapReduce组件：MapReduce是Hadoop内分布式计算组件，提供编程接口供用户开发分布式计算程序
- Yarn组件：Yarn是Hadoop内分布式资源调度组件，可提供用户调度大规模集群的资源使用



## Hadoop HDFS

为什么需要分布式存储：

- 数据量太大，单机存储能力有上限，需要靠数量来解决问题
- 数量的提升带来的是网络传输，磁盘读写、cpu、内存等各方面的综合提升。分布式组合在一起可以达到1+1>2的效果



### 架构分析

大数据体系中，分布式调度主要有2类架构模式

- 去中心化：没有明确的中心，众多服务器之间基于特定的规则进行同步协调
- 中心化模式：以一个结点作为中心，去统一调度其他结点



大数据框架，大多数的架构基础上都是符合中心化模式。即：有一个中心节点来统筹其他服务器的工作，同一指挥，同一调派，避免混乱。这种模式，也被称之为：一主多从模式，简称主从模式（Master And Slave）



### HDFS的基础架构

![](https://raw.githubusercontent.com/linkongluyin/images/main/202304182213168.png)

HDFS是Hadoop三大是组件（HDFS、MapReduce、Yarn）之一

- 全称是：Hadoop Distributed File System （Hadoop分布式文件系统）
- 是Hadoop技术栈内提供的分布式数据存储解决方案
- 可以在多台服务器上构建存储集群，存储海量数据



HDFS集群（分布式存储）

- 主角色：NameNode 
  - HDFS系统的主角色，是一个独立的进程
  - 负责管理HDFS整个文件系统
  - 负责管理DataNode
- 从角色：DataNode
  - HDFS系统的从角色，是一个独立进程
  - 主要负责数据的存储，即存入数据和取出数据
- 主角色辅助角色：SecondaryNameNode
  - NameNode的辅助，是一个独立的进程
  - 主要帮助NameNode完成元数据整理工作（打杂的）

![](https://raw.githubusercontent.com/linkongluyin/images/main/202304182217509.png)



### HDFS集群部署

官网：https://hadoop.apache.org/releases.html

环境准备：

| 节点  |  CPU  | 内存 |                  服务                  |
| :---: | :---: | :--: | :------------------------------------: |
| node1 | 1核心 |  4G  | NameNode 、DataNode、SecondaryNameNode |
| node2 | 1核心 |  2G  |                DataNode                |
| node3 | 1核心 |  2G  |                DataNode                |

 

注意：防火墙、Hadoop用户创建、SSH免密、JDK部署等操作需要提前完成

1. 上传Hadoop安装包到Node节点

2. 解压缩安装包到指定的位置

```shell
[root@k8s-master ~]# tar xf hadoop-3.3.4.tar.gz -C /data 
[root@k8s-master ~]# cd /data
[root@k8s-master data]# mv hadoop-3.3.4/ hadoop

[root@k8s-master data]# vim /etc/profile
export JAVA_HOME=/usr/local/jdk
export HADOOP_HOME=/data/hadoop
export PATH=${JAVA_HOME}/bin:${HADOOP_HOME}/bin:$PATH
# 保存退出后需加载环境变量
[root@k8s-master data]# source /etc/profile
```

3. 进入Hadoop安装包内

​	各文件夹的含义：

- bin：存放Hadoop的各类程序（命令）
- etc：存放Hadoop的配置文件
- include：C语言的一些头文件
- lib：存放linux系统的动态链接库
- libexec：存放配置Hadoop系统的脚本文件
- licences-binary：存放许可证文件
- sbin：管理员程序（super bin）
- share：存放二进制源码（Java jar包）

4. 修改配置文件，应用自定义设置

   配置HDFS集群，主要涉及一下文件的修改：

   - workers：配置从节点（DataNode）有哪些
   - hadoop-env.sh：配置Hadoop的相关环境变量
   - core-site.xml：Hadoop 的核心配置文件
   - hdfs-site.xml：HDFS的核心配置文件

   > 这些文件均在${HADOOP_HOME}/etc/hadoop中

5. 配置workers文件

   ```shell
   # 表明集群记录了三个从节点（DataNode）
   
   vim workers
   # 填入以下内容
   node1
   node2
   node3
   ```

6. 配置hadoop-env.sh

   ```shell
   # 填入一下内容
   export JAVA_HOME=
   export HADOOP_HOME=
   # 配置文件目录位置
   export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
   # 运行日志存放路径
   export HADOOP_LOG_DIR=${HADOOP_HOME}/logs
   ```

7. 配置core-site.xml文件

   ```xml
   
   (1)配置fs.defaultFS 
   <!--
   key：fs.defaultFS; 
   含义：HDFS文件系统的网络通讯路径
   值：hdfs://node1:8020 协议为hdfs；namenode为node1；namenode通讯端口为8020
   -->
   <property> 
       <name>fs.defaultFS</name> 
       <value>hdfs://node1:8020</value> 
   </property> 
   
   (2)配置hadoop临时目录 
   <!--
   key：io.file.buffer.size
   含义：io操作文件缓冲区大小
   值：131072bit
   -->
   <property>
       <name>io.file.buffer.size</name>
       <value>131072</value>
    </property> 
   ```

   - hdfs://node1:8020 为整个HDFS内部的通讯地址，应用协议为hdfs://（hadoop内置协议）
   - 表明DataNode将和node1的8020端口通讯，node1是NameNode所在的机器
   - 此配置固定了node1必须启动NameNode进程

8. 配置hdfs-site.xml

   ```xml
   <!--在文件中填入如下内容-->
   <configuration>
       <!--
       key：dfs.datanode.data.dir.perm
       含义：hdfs文件系统，默认创建的文件权限设置
       值：700 即：rwx------
       -->
       <property> 
       	<name>dfs.datanode.data.dir.perm</name> 
       	<value>700</value> 
   	</property>
       <!--
       key：dfs.namenode.name.dir
       含义：NameNode元数据存储位置
       值：/data/namenode 在node1结点下的/data/namenode
       -->
       <property> 
       	<name>dfs.namenode.name.dir</name> 
       	<value>/data/namenode</value> 
   	</property>
       <!--
       key：dfs.namenode.hosts
       含义：NameNode允许哪几个结点的DataNode连接（即允许加入集群）
       值：node1，node2，node3 这三台服务器被授权
       -->    
       <property> 
       	<name>dfs.namenode.hosts</name> 
       	<value>node1,node2,node3</value> 
   	</property>
       <!--
       key：dfs.blocksize
       含义：hdfs默认块大小
       值：256MB
       -->      
       <property> 
       	<name>dfs.blocksize</name> 
       	<value>268435456</value> 
   	</property>
       <!--
       key：dfs.namenode.handler.count
       含义：namenode处理的并发线程数
       值：100，以100个并行度处理文件系统管理任务
       -->      
       <property> 
       	<name>dfs.namenode.handler.count</name> 
       	<value>100</value> 
   	</property>
       <!--
       key：dfs.datanode.data.dir
       含义：datanode的数据存储目录
       值：/data/datenode 即数据存放在node1，node2，node3的/data/datenode下
       -->       
       <property> 
       	<name>dfs.datanode.data.dir</name> 
       	<value>/data/datenode</value> 
   	</property>    
   </configuration>
   
   ```

9. 准备数据目录

   刚才配置的数据存储路径，需要手动创建出来

   ```shell
   # node1
   mkdir -p /data/namenode
   mkdir -p /data/datenode
   
   # node2 node3
   mkdir -p /data/datenode
   ```

10. 分发配置文件

    将上述修改好的文件夹发送到其他结点上

    ```shell
    scp -r hadoop-3.3.4 node2:$(pwd)/
    scp -r hadoop-3.3.4 node3:$(pwd)/
    ```

11. 配置环境变量

    为了方便直接使用命令，可以将Hadoop下的bin、sbin添加到环境变量中

    ```shell
    vim /etc/profile
    
    export HADOOP_HOME=
    export PATH=$PATH:$HADOOP_HOME/bin:HADOOP_HOME/sbin
    ```

12. 授权hadoop用户

    hadoop部署的准备工作基本完成；为了确保安全，hadoop系统不以root用户启动；所以需要对文件权限进行授权。

    ```shell
    # useradd hadoop
    chown -R hadoop:hadoop /data
    chown -R hadoop:hadoop /usr/local/hadoop
    ```

13. 格式化整个文件系统

    格式化namenode 

    ```shell
    # 确保以hadoop用户执行
    su - hadoop
    # 格式化namenode
    hadoop namenode --format
    ```

    启动

    ```shell
    # 一键启动hdfs集群
    start-dfs.sh
    # 一键关闭hdfs集群
    stop-dfs.sh
    
    # 启动后可以使用jps查看
    ```

14. 查看HDFS web UI

    启动完成后，可以在浏览器中打开：http://node1:9870 即可以查看hdfs文件系统的管理网页（node1 ip）



### HDFS的shell操作

#### 进程启停管理

**一键启停脚本**

Hadoop HDFS组件内置了HDFS集群的一键启停脚本

- $HADOOP_HOME/sbin/start-dfs.sh 一键启动HDFS集群

  执行原理：

  - 在此执行脚本的机器上，启动SecondaryNameNode
  - 读取core-site.xml内容（fs.defaultFS项），确认NameNode所在机器，启动NameNode
  - 读取workers内容，确定DataNode所在机器，启动全部的DataNode

- $HADOOP_HOME/sbin/stop-dfs.sh 一键关闭HDFS集群

  执行原理：

  - 在此执行脚本的机器上，关闭SecondaryNameNode
  - 读取core-site.xml内容（fs.defaultFS项），确认NameNode所在机器，关闭NameNode
  - 读取workers内容，确定DataNode所在机器，关闭全部的DataNode



**单进程启停**

- $HADOOP_HOME/sbin/hadoop-daemon.sh 此脚本可以单独控制所在机器的进程的启停

  用法：hadoop-daemon.sh （start  | status | stop）（namenode | secondarynamenode | datanode ）

- $HADOOP_HOME/sbin/hdfs 此程序也可以用于单独控制所在机器的进程的启停

  用法 hdfs --daemon start  | status | stop）（namenode | secondarynamenode | datanode ）



#### 文件系统操作命令

HDFS通Linux系统一样，均是以/作为根目录的组织形式

关于HDFS文件系统的操作命令，Hadoop提供了两套命令

1）创建文件夹

- hadoop fs -mkdir [-p] <path> ...
- hdfs dfs -mkdir [-p] <path> ...
  - path 为待创建的目录
  - -p选项的行为和linux中mkdir -p效果一致

2）查看指定目录下的内容

- hadoop fs -ls [-R] [-h] [<path> ...]
- hdfs dfs -ls [-R] [-h] [<path> ...]
  - path 指定目录路径
  - -h 人性化显示文件size
  - -R递归查看指定目录及其子目录

3）上传文件到HDFS指定目录下

- hadoop fs -put [-f] [-p] <localsrc> ... <dst>
- hdfs dfs -put [-f] [-p] <localsrc> ... <dst>
  - -f：覆盖目标文件（已存在下）
  - -p：保留访问和修改时间，所有权和权限
  - localsrc：本地文件系统（客户端所在机器）
  - dst：目标文件系统（HDFS）

4）查看HDFS文件内容

- hadoop fs -cat <src>

- hdfs dfs -cat <src>

  读取大文件可以使用管道符配合more

  - hdfs dfs -cat <src> | more
  - hdfs dfs -cat <src> | more

5）下载HDFS文件

- hadoop fs -get [-f] [-p] <src> ... <localdst>

- hdfs dfs -get [-f] [-p] <src> ... <localdst>

  下载文件到本地文件系统指定目录，localdst必须是目录

  - -f：覆盖目标文件（已存在下）
  - -p：保留访问和修改时间，所有权和权限

6）拷贝HDFS文件

- hadoop fs -cp [-f] <src> ... <dst>
- hdfs dfs -cp [-f]  <src> ... <dst>

7）追加数据到HDFS文件中

- hadoop fs -appendToFile [-f] <localsrc> ... <dst>
- hdfs dfs -appendToFile [-f] <localsrc> ... <dst>
  - 将所有给定本地文件的内容追加到dst文件
  - dst如果文件不存在，将创建该文件
  - 如果<localsrc>为-，则输入为从标准输入中读取

8）HDFS数据移动操作

- hadoop fs -mv  <src> ... <dst>
- hdfs dfs -mv <src> ... <dst>
  - 移动文件到指定的路径
  - 可以使用该命令移动数据，重命名文件

9）HDFS删除操作

- hadoop fs -rm -r [-skipTrash ] [URI]
- hdfs dfs  -rm -r [-skipTrash ] [URI]
  - 删除指定的路径的文件或文件夹
  - -skipTrash 跳过回收站 直接删除

> 回收站功能默认关闭，如果需要开启需要再core-site.xml文件内配置
>
> ```xml
> <property> 
>     	<name>fs.trash.interval</name> 
>     	<value>1440</value> 
> </property>
> 
> <property> 
>     	<name>fs.trash.checkpoint.interval</name> 
>     	<value>120</value> 
> </property>  
> ```
>
> 无需重启集群；在哪台机器上配置的，就在哪台机器执行命令生效
>
> 回收站位置默认在：/user/用户名(hadoop)./Trash

官方命令：https://hadoop.apache.org/docs/r3.3.5/hadoop-project-dist/hadoop-common/FileSystemShell.html



### HDFS存储原理

分布式存储：每个服务器（节点）存储文件的一部分

设定统一的管理单位，block块

block块，HDFS最小存储单位，每个256MB（可修改）

数据是在HDFS上划分成一个个block块进行存储

为了确保安全性，数据block有多个副本



#### HDFS副本块数量的配置：

在`hdf-site.xml`中配置如下属性：

```xml
<property> 
    	<name>dfs.replication</name> 
    	<value>3</value> 
</property>  
```

> 这个属性默认是3，一般情况下无需主动配置（除非设置非3的数值）
>
> 如果需要自定义这个属性，需要再每一台服务器改该配置文件，并设置属性

- 除了配置文件外，还可以在上传文件的时候，临时决定被上传文件以多少个副本存储。

  `hadoop fs -D dfs.replication = 2 -put test.txt /tmp/`

  如上命令，就可以在上传test.txt的时候，临时设置其副本数量为2

- 对于已经存在的HDFS的文件，修改dfs.replication属性不会生效，如果修改已存在的文档可以通过命令`hadoop fs -setrep [-R] 2 path`

  如上命令，指定path的内容将会修改为2个副本存储

  -R选项可选，使用-R表示对子目录也生效



#### fsck命令检查文件的副本数

同时，我们可以使用hdfs提供的fsck命令来检查文件的副本数

`hdfs fsck path [-files [-blocks [-locations]]]`

fsck可以检查指定路径是否正常

- -files 可以列出路径文件内的文件状态
- -files -blocks 输出文件块报告（有几个块，多少副本）
- -files -blocks -locations 输出每一个block的详情



**block配置**

对于块儿block，hdfs默认设置为256MB一个，也就是1G文件会被划分为4个block存储

块的大小可以通过参数设置

```xml
<property> 
    	<name>dfs.blocksize</name> 
    	<value>2684356</value> 
    	<description>设置hdfs块的大小，单位是b</description>
</property>  
```



#### NameNode元数据



![image-20230419135858274](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419135858274.png)



1）edits文件

edits文件，是一个流水账文件，记录了hdfs中的每一次操作，以及本次操作影响的文件其对应的block

![](https://raw.githubusercontent.com/linkongluyin/images/main/edits%25E6%2596%2587%25E4%25BB%25B6.png)

2）fsimages文件

将全部的edits文件，合并为最终结果，得到一个fsimage文件

记录某一时间节点的当前文件系统全部文件的状态和信息，维护整个文件系统元数据。



#### NameNode元数据管理维护

NameNode基于edits文件和FsImage的配合，完成整个文件系统文件的管理。

- 每次对HDFS大的操作，均被edits文件记录
- edits达到大小上线后，开启新的edits记录
- 定期进行edits的合并操作
  - 如果当前没有fsimage文件，将全部的edits合并为第一个fsimage
  - 如果已经㛮了fsimage文件，将全部的edits和已经存在的fsimage进行合并，形成in新的fsimage
- 重复上述流程



#### 元数据合并控制参数

对于元数据合并，是一个定时过程；基于

- dfs.namenode.chechpoint.period，默认3600秒，即一小时
- dfs.namenode.checkpoint.txns，默认100000，即100w次事务

> 只要有一个达到条件的就执行
>
> 检查是否达到条件，默认60秒检查一次，基于：
>
> `dfs.namenode.checkpoint.check.period`，默认60s决定



#### SecondaryNameNode的作用

![](https://raw.githubusercontent.com/linkongluyin/images/main/202304182217509.png)

合并元数据的事情是他做的；NameNode主要写edits，fsimage是他做的

SecondaryNameNode会通过http从NameNode拉取数据（edits和fsimage），然后合并完成后提供NameNode使用。



#### HDFS数据读写流程

![image-20230419141806538](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419141806538.png)

1. 客户端向NameNode发起请求
2. NameNode审核权限、剩余空间后，满足条件允许写入，斌告知客户端写入的DataNode地址
3. 客户端向指定的DataNode发送数据包
4. 被写入数据的DataNode同时万数据副本的复制工作，将其接收的数据分发给其他的DataNode
5. 如上图；DataNode1复制给DataNode2，然后基于DataNode2复制给DataNode3和DataNode4
6. 写入完成客户端通知NameNode，NameNode做元数据记录工作

> - NameNode不负责数据写入，只负责元数据记录和权限审批
> - 客户端直接向1台DataNode写数据，这个DataNode一般是离客户端最近（网络距离）的那一个
> - 数据块副本的复制工作，由DataNode之间自行完成（构建一个pipeline，按书序复制分发，如上图1给2,2给3和4）



#### 数据读取流程

![image-20230419142518462](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419142518462.png)

1. 客户端向NameNode申请读取某文件
2. NameNode判断客户端权限等细节，允许读取，并返回此文件的block列表
3. 客户端拿到block列表后自行寻找DataNode读取即可

> - 数据同样不通过NameNode提供
> - NameNode提供的block列表，会基于网络距离计算尽量提供离客户端最近的
> - 因为一个block有三分，会尽量找离客户端醉经的那一份让其读取

