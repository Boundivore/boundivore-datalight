# Boundivore-DataLight

## 一、简介

DataLight 是一个开源的大数据运维管理平台，用于简化和自动化大数据服务的部署、管理和监控。它提供了一套全面的工具来管理您的大数据生态系统，旨在帮助企业实现简洁、快速地构建大数据平台。

详情介绍可访问

~~~http
http://boundivore.datalight.com
~~~

## 二、软件架构

<img src=".documents/docs/assets/arch.jpg" alt="arch" style="zoom:20%;" />

## 三、概念与定义

* 服务：HDFS、YARN、HIVE 等称之为服务，其名称在本项目中的标准命名为全大写。
* 组件：HDFS 中的独立进程，例如：NameNode、DataNode 等称之为组件，在本项目中的标准命名方式为帕斯卡命名法。
* 主进程：DataLight Master 进程称之为主进程
* 从进程：DataLight Worker 进程称之为从进程

## 四、主要功能

下表列出了 DataLight 平台支持的主要功能及其当前的支持状态：

| 序号 | 一级功能               | 是否已支持 | 日期 |
| ---- | ------------------- | -------- | ---- |
| 1    | 节点初始化助手          | 是       |      |
| 2    | 主从进程维护            | 是       |      |
| 3    | 集群管理               | 是       |      |
| 4    | 节点初始化             | 是       |      |
| 5    | 节点管理               | 是       |      |
| 6    | 服务组件维护            | 是       |      |
| 7    | 服务组件预配置          | 是       |      |
| 8    | 主从日志管理            | 是       |      |
| 9    | 服务组件日志聚合管理     | 是       |      |
| 10   | 监控管理               | 是       |      |
| 11   | 告警管理               | 是       |      |
| 12   | 用户管理               | 是       |      |
| 13   | 人权管理               | 是       |      |
| 14   | 权限管理               | 是       |      |

## 五、部署说明

部署本项目前，请确保你已经准备好了必要的物理资源、操作系统并且理解了本项目的部署流程。

### 5.1 准备物理资源

确保您有足够的硬件资源来部署和运行本项目。推荐的最小配置包括：

- CPU：4 Cores +
- 内存：8 GB +
- 硬盘：100 GB +
- 网络：1 Gbps +

### 5.2 准备操作系统

本项目目前仅支持（其他系统未测试）：

- CentOS 7.x

### 5.3 准备部署资源

下载本项目的最新版本，以及所有必要的依赖软件和库，包括：

* clone 当前仓库，并编译

* 下载 DLC 资源包，并放置于

* 构建本地目录

  | 目录                                              | 说明                                      | 是否必要 |
  | ------------------------------------------------- | ----------------------------------------- | -------- |
  | /opt/datalight/app                                | 主从进程包目录                            | 是       |
  | /opt/datalight/app/config                         | 主从进程包启动配置目录                    | 是       |
  | /opt/datalight/assistant                          | 初始化节点助手目录                        | 是       |
  | /opt/datalight/assistant/conf                     | 初始化节点助手配置文件                    | 是       |
  | /opt/datalight/assistant/main                     | 初始化节点助手执行总入口                  | 是       |
  | /opt/datalight/assistant/repo                     | 初始化节点助手依赖资源                    | 是       |
  | /opt/datalight/assistant/scripts                  | 初始化节点助手相关子脚本                  | 是       |
  | /opt/datalight/bin                                | 主从进程启停管理脚本                      | 是       |
  | /opt/datalight/conf                               | 环境与服务配置文件目录                    | 是       |
  | /opt/datalight/conf/env                           | 环境配置文件与脚本目录                    | 是       |
  | /opt/datalight/conf/service                       | 服务部署维护配置文件目录                  | 是       |
  | /opt/datalight/docs                               | 项目文档目录                              | 否       |
  | /opt/datalight/docs/api                           | 三方 API 目录                             | 否       |
  | /opt/datalight/docs/package                       | 服务组件打包步骤文档记录目录              | 否       |
  | /opt/datalight/docs/maven                         | Maven 配置目录                            | 否       |
  | /opt/datalight/docs/src-[大写服务名称]-版本号     | 服务组件源码文件修改记录目录              | 否       |
  | /opt/datalight/exporter                           | JMX Eeporter 目录                         | 是       |
  | /opt/datalight/exporter/bin                       | Exporter 运行脚本示例目录                 | 否       |
  | /opt/datalight/exporter/conf                      | Exporter 配置文件模板目录                 | 是       |
  | /opt/datalight/exporter/jar                       | Exporter 插件 Jar 包所在目录              | 是       |
  | /opt/datalight/node                               | 节点操作相关配置与脚本目录                | 是       |
  | /opt/datalight/node/conf                          | 节点操作相关配置文件目录                  | 是       |
  | /opt/datalight/node/scripts                       | 节点操作相关操作的脚本文件所在目录        | 是       |
  | /opt/datalight/orm                                | 数据库相关资料目录                        | 否       |
  | /opt/datalight/orm/dmj                            | 数据库模型设计目录                        | 否       |
  | /opt/datalight/orm/query                          | 项目中可能用到的 SQL 查询示例目录         | 否       |
  | /opt/datalight/orm/sql                            | Master 元数据库 Schema 初始化文件所在目录 | 否       |
  | /opt/datalight/plugins                            | 服务组件对应的插件目录                    | 是       |
  | /opt/datalight/plugins/[大写服务名称]/dlc         | 服务组件对应的 .tar.gz 安装包所在目录     | 是       |
  | /opt/datalight/plugins/[大写服务名称]/jars        | 服务组件对应的插件编译后的 Jar 包所在目录 | 是       |
  | /opt/datalight/plugins/[大写服务名称]/placeholder | 服务组件预配置项配置文件所在目录          | 是       |
  | /opt/datalight/plugins/[大写服务名称]/scripts     | 服务组件聚合操作脚本所在目录              | 是       |
  | /opt/datalight/plugins/[大写服务名称]/templated   | 服务组件配置文件模板所在目录              | 是       |

#### 5.3.1  创建对应目录

首先在 Linux 系统中创建上述对应目录。

#### 5.3.2  准备前端页面

前往 DataLight 前端开源项目，按照对应文档执行编译操作，编译后，将编译文件解压后，拷贝至当前源码项目 datalight-services/services-master/public 目录中。

DataLight 前端开源项目：

~~~http
http://boundivore-datalight-web.com
~~~

#### 5.3.3 准备 Master/Worker

将项目克隆后，使用代码编辑器打开后，执行 datalight-services 模块的编译操作，在 services-master 与 services-worker 项目的 build/libs 目录下，可以分别看到 services-master-[版本号].jar、services-worker-[版本号].jar 两个进程文件，拷贝至 app/ 目录下即可。

#### 5.3.4 准备服务组件插件 Plugins

将项目克隆后，使用代码编辑器打开后，执行 datalight-plugins 模块的编译操作，将该模块下对应服务的插件 jar 包拷贝至指定目录中，例如：/opt/datalight/plugins/[大写服务名称]/jars。

#### 5.3.5 拷贝其他文件目录

在主项目目录中，找到 .documents 文件夹，其下内容，拷贝至上述表格中对应的目录中即可。

#### 5.3.6 准备 DLC 服务包

前往如下地址下载 DLC 服务包：

~~~http
http://x.com
~~~

下载后，解压，将对应服务的 .tar.gz 包放置于对应的 /opt/datalight/plugins/[大写服务名称]/dlc 的目录中即可。

### 5.4 初始化环境

### 5.5 启动 Master 主程序

### 5.6 新建集群

### 5.7 添加节点

### 5.8 添加服务

### 5.9 添加组件

### 5.10 设置预配置文件

### 5.11 执行部署

### 5.12 完成部署

## 六、计划支持的服务

| 服务           | 版本     | 是否已支持 | 完成日期       |
| -------------- |--------|------------|------------|
| ZOOKEEPER      | 3.8.2  | √          | 2023-07-31 |
| HDFS           | 3.2.4  | √          | 2023-07-31 |
| YARN           | 3.2.4  | √           | 2023-08-31 |
| PROMETHEUS     | 2.46.0 | √           | 2023-08-10 |
| ALERTMANAGER   | 0.26.0 | √           | 2023-08-10 |
| GRAFANA        | 10.0.3 | √           | 2023-08-10 |
| HIVE           | 3.1.3  | √           | 2023-09-13 |
| TEZ | 0.9.2  | √ | 2023-09-13 |
| HBASE          | 待定 | √           |            |
| SPARK          | 待定 | √           |            |
| FLINK          | 待定 |            |            |
| HUDI           | 待定 |            |            |
| KUBESPHERE     | 3.3.2  | √           | 2023-09-06 |
| SEATUNNEL      | 待定 |            |            |
| DOPHINSCHDULER | 待定 |            |            |
| RANGER         | 待定 |            |            |
| ATLAS          | 待定 |            |            |
| HUE            | 待定 |            |            |
| KAFKA          | 待定 |            |            |
| ES             | 待定 |            |            |
| TRINO          | 待定 |            |            |
| KYLIN          | 待定 |            |            |
| KUDU           | 待定 |            |            |
| IMPALA         | 待定 |            |            |

## 七、计划完善的功能

## 八、资源下载汇总

### 8.1 DataLight 平台部署包下载

~~~http
http://x.com
~~~

### 8.2 DLC 服务包下载

~~~http
http://x.com
~~~



