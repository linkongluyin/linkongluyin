[toc]

计算：对数据进行处理，使用统计分析等手段得到需要的结果

分布式计算：顾名思义，即分布式的形式完成数据的统计，得到需要的结果

分布式（数据计算）常见的2中工作模式：

- 分散 -> 汇总模式

![image-20230419143638625](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419143638625.png)

- 中心调度 -> 步骤执行模式

![image-20230419143918999](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419143918999.png)



# MapReduce

MapReduce即Hadoop内提供的进行分布式计算组件

MapReduce是 “分散 -> 汇总”模式的分布式计算框架，可提供开发人员进行分布式数据计算。

MapReduce提供2个编程接口：

- Map：提供“分散”的功能，由服务器分布式对数据进行处理
- Reduce：提供“汇总（聚合）”的功能，将分布式的处理结果汇总统计

## MapReduce执行原理

- 将要执行的需求，分解为多个Map Task 和 Reduce Task
- 将Map Task和Reduce Task分配到对应的服务器去执行

![image-20230419145816224](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419145816224.png)



# Yarn

MapReduce是基于Yarn运行的，即没有Yarn“无法”运行MapReduce程序

**资源调度**

- 资源：服务器硬件资源；如 CPU 内存 硬盘 网络
- 资源调度：管控服务器硬件资源，提供更好的利用率
- 分布式资源调度：管控整个分布式服务器集群的全部资源，整合进行统一调度



## Yarn的资源调度

Yarn管控整个集群的资源进行调度，那么应用程序在运行时，就是在Yarn的监管（管理）下运行的

这就像：全部资源都是公司（Yarn）的，由公司分配给个人（具体的程序）去使用



![image-20230419154022521](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419154022521.png)



程序在Yarn中运行：

- 程序向Yarn申请所需资源

- Yarn为程序分配所需资源供程序使用



MapReduce和Yarn的关系

- Yarn用来调度资源给MapReduce分配和管理运行资源
- 所以，MapReduce需要Yarn才能执行（普遍情况下）



## Yarn架构

![image-20230419154549481](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419154549481.png)

- ResourceManager：整个集群的资源调度者，负责协调调度各个程序所需要的资源
- NodeManager：单个服务器的资源调度，负责调度单个服务器上的资源给应用程序使用

![image-20230419155000498](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419155000498.png)



![image-20230419155556349](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419155556349.png)

- 容器（container）是Yarn的NodeManager在所属服务器上分配资源的手段
- 创建一个资源容器，即由NodeManager占用这部分资源
- 然后运行程序运行在NodeManager创建的这个容器内



## Yarn的辅助架构

Yarn架构中除了核心角色：

- ResourceManager：集群资源总管家
- NodeManager：单机资源管家



还可以搭配2个辅助角色使Yarn集群运行更加稳定

- 代理服务器（proxyServer）：web Application Proxy web 应用程序代理
- 历史服务器（JobHistroyServer）：应用程序历史信息记录服务器



**web应用代理**

代理服务器，即web应用代理是yarn的一部分。默认情况下，他将作为资源管理器（RM）的一部分运行，但是可以配置为在独立模式下运行。使用代理的原因是减少通过yarn进行基于网络攻击的可能性

这是因为，yarn在运行时会提供一个web ui站点（同HDFS的web ui站点一样）可供用户在浏览器中查看yarn的运行信息。



对外提供的web站点会有安全性问题，而代理服务器的功能就是最大限度的保证web ui访问是安全的

- 警告用户正在访问一个不受信任的站点
- 剥离用户访问的Cookie等

开启代理服务器，可以提高yarn在开放网络中的安全性（但不是绝对安全，只是辅助提高一些）



代理服务器一般集成在ResourceManager中；也可以将其分离出来单独启动，如果要分离代理服务器：

1）在yarn-site.xml中配置yarn.web-proxy.address参数即可

```xml
<property> 
    	<name>yarn.web-proxy.address</name> 
    	<value>node1:8099</value>
    	<description>代理服务器的主机和端口</description>
</property> 
```

2）通过命令启动即可

`$HADOOP_YARN_HOME/sbin/yarn-daemon.sh start proxyserver`



**JobHistoryServer历史服务器**

历史服务器的功能很简单：记录历史运行的程序和信息以及产生的日志提供web ui界面供用户浏览器查看



> 优势：统一将每个容器内的日志收集到HDFS文件系统中，使得用户在浏览器中方便访问



JobHistoryServer历史服务器功能：

- 提供web ui站点，共用户在浏览器查看程序日志
- 可以保留历史数据，随时查看历史运行程序信息



JobHistoryServer需要配置

- 开启日志聚合，即从容器内抓取日志到HDFS集中存储

```xml
```



- 配置历史服务器端口和主机

```xml

```



# MapReduce & Yarn 

## 部署说明

![image-20230419162024910](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419162024910.png)

MapReduce运行在Yarn容器内，无需启动独立进程

所以关于MapReduce和Yarn的部署，其实就是2件事情：

- MapReduce：修改相关配置文件，但是没有进程可以启动
- Yarn：修改相关配置文件，并启动ResourceManager、NodeManager进程以及辅助进程

![image-20230419162359538](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419162359538.png)



## 集群规划

node1配置较高，集群规划如下

![image-20230419162554527](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419162554527.png)



### MapReduce配置文件

在$HADOOP_HOME/etc/hadoop内，修改

`mapred-env.sh`文件，添加环境变量

```shell
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

export JAVA_HOME=/export/server/jdk

export HADOOP_JOB_HISTORYSERVER_HEAPSIZE=1000

export HADOOP_MAPRED_ROOT_LOGGER=INFO,RFA


##
## THIS FILE ACTS AS AN OVERRIDE FOR hadoop-env.sh FOR ALL
## WORK DONE BY THE mapred AND RELATED COMMANDS.
##
## Precedence rules:
##
## mapred-env.sh > hadoop-env.sh > hard-coded defaults
##
## MAPRED_xyz > HADOOP_xyz > hard-coded defaults
##

###
# Job History Server specific parameters
###

# Specify the max heapsize for the JobHistoryServer.  If no units are
# given, it will be assumed to be in MB.
# This value will be overridden by an Xmx setting specified in HADOOP_OPTS,
# and/or MAPRED_HISTORYSERVER_OPTS.
# Default is the same as HADOOP_HEAPSIZE_MAX.
#export HADOOP_JOB_HISTORYSERVER_HEAPSIZE=

# Specify the JVM options to be used when starting the HistoryServer.
# These options will be appended to the options specified as HADOOP_OPTS
# and therefore may override any similar flags set in HADOOP_OPTS
#export MAPRED_HISTORYSERVER_OPTS=

# Specify the log4j settings for the JobHistoryServer
# Java property: hadoop.root.logger
#export HADOOP_JHS_LOGGER=INFO,RFA

```

![image-20230419173807706](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419173807706.png)

`mapred-site.xml`文件

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
    <description></description>
  </property>

  <property>
    <name>mapreduce.jobhistory.address</name>
    <value>node1:10020</value>
    <description></description>
  </property>


  <property>
    <name>mapreduce.jobhistory.webapp.address</name>
    <value>node1:19888</value>
    <description></description>
  </property>


  <property>
    <name>mapreduce.jobhistory.intermediate-done-dir</name>
    <value>/data/mr-history/tmp</value>
    <description></description>
  </property>


  <property>
    <name>mapreduce.jobhistory.done-dir</name>
    <value>/data/mr-history/done</value>
    <description></description>
  </property>
<property>
  <name>yarn.app.mapreduce.am.env</name>
  <value>HADOOP_MAPRED_HOME=$HADOOP_HOME</value>
</property>
<property>
  <name>mapreduce.map.env</name>
  <value>HADOOP_MAPRED_HOME=$HADOOP_HOME</value>
</property>
<property>
  <name>mapreduce.reduce.env</name>
  <value>HADOOP_MAPRED_HOME=$HADOOP_HOME</value>
</property>
</configuration>
```

![image-20230419173818371](C:/Users/admin/AppData/Roaming/Typora/typora-user-images/image-20230419173818371.png)

### Yarn配置文件

- 在$HADOOP_HOME/etc/hadoop内，修改`yarn-env.sh`文件，添加如下4行环境变量内容

  ```shell
  export JAVA_HOME=/export/server/jdk
  export HADOOP_HOME=/export/server/hadoop
  export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
  # export YARN_CONF_DIR=$HADOOP_HOME/etc/hadoop
  # export YARN_LOG_DIR=$HADOOP_HOME/logs/yarn
  export HADOOP_LOG_DIR=$HADOOP_HOME/logs
  ```

![image-20230419174031486](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419174031486.png)

- `yarn-site.xml`文件

```xml
<?xml version="1.0"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<configuration>

<!-- Site specific YARN configuration properties -->
<property>
    <name>yarn.log.server.url</name>
    <value>http://node1:19888/jobhistory/logs</value>
    <description></description>
</property>

  <property>
    <name>yarn.web-proxy.address</name>
    <value>node1:8089</value>
    <description>proxy server hostname and port</description>
  </property>


  <property>
    <name>yarn.log-aggregation-enable</name>
    <value>true</value>
    <description>Configuration to enable or disable log aggregation</description>
  </property>

  <property>
    <name>yarn.nodemanager.remote-app-log-dir</name>
    <value>/tmp/logs</value>
    <description>Configuration to enable or disable log aggregation</description>
  </property>


<!-- Site specific YARN configuration properties -->
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>node1</value>
    <description></description>
  </property>

  <property>
    <name>yarn.resourcemanager.scheduler.class</name>
    <value>org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler</value>
    <description></description>
  </property>

  <property>
    <name>yarn.nodemanager.local-dirs</name>
    <value>/data/nm-local</value>
    <description>Comma-separated list of paths on the local filesystem where intermediate data is written.</description>
  </property>


  <property>
    <name>yarn.nodemanager.log-dirs</name>
    <value>/data/nm-log</value>
    <description>Comma-separated list of paths on the local filesystem where logs are written.</description>
  </property>


  <property>
    <name>yarn.nodemanager.log.retain-seconds</name>
    <value>10800</value>
    <description>Default time (in seconds) to retain log files on the NodeManager Only applicable if log-aggregation is disabled.</description>
  </property>
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
    <description>Shuffle service that needs to be set for Map Reduce applications.</description>
  </property>
</configuration>
```

![image-20230419174233951](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419174233951.png)

![image-20230419174243357](https://raw.githubusercontent.com/linkongluyin/images/main/image-20230419174243357.png)