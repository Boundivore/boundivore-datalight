[[中文]](/README.md)

# Boundivore-DataLight

## 1. Introduction

DataLight is an open-source big data operations and management platform designed to simplify and automate the deployment, management, and monitoring of big data services. It provides a comprehensive set of tools to manage your big data ecosystem, aiming to help enterprises build an OLAP and OLTP unified business platform in a simple and fast way. Users can quickly integrate their business services or other big data components through plugins in a plug-and-play manner for unified management within the platform.

Resource summary:

- [Official Website](http://datalight.boundivore.cn/)
- [Backend Gitee Main Repository](https://gitee.com/boundivore/boundivore-datalight)
- [Backend Github Mirror Repository](https://github.com/Boundivore/boundivore-datalight)
- [Frontend Gitee Main Repository](https://gitee.com/boundivore/boundivore-datalight-web)
- [Frontend Github Mirror Repository](https://github.com/Boundivore/boundivore-datalight-web)
- [Product Manual](https://poe.com/chat/.documents/docs/产品手册.md)
- [Development Manual](https://poe.com/chat/.documents/docs/开发手册.md)
- [Development Plan](https://poe.com/chat/.documents/docs/开发规划.md)
- [Resource Package Download](https://pan.baidu.com/s/1342bIcEBDQHdFA35KUTjbQ) `Extract Code: data`
- [Video Tutorials](https://space.bilibili.com/3546708503955571/channel/seriesdetail?sid=4187881&ctype=0)
- [Support and Service Policy](./.documents/docs/开源 DataLight 大数平台支持与服务政策.md)

## 2. Software Architecture

This software architecture implements a highly available, scalable distributed computing and big data processing platform through the Master-Worker model.

- UI Layer: Users operate and manage through the UI layer. The UI layer interacts with the Master Restful API to send user operation requests.
- Master Layer: The Master layer is the core of the entire system, responsible for managing and coordinating operations of the Worker layer. It contains multiple nodes, each responsible for different functional modules.
- MemoryMeta: In-memory metadata cache layer.
- MasterService: Master service business logic management.
- ServicePlugin: Service plugin management.
- Reporter: Master/Worker bidirectional interaction service.
- Master Layer: Receives requests from the UI layer through Restful API and coordinates the operations of the Worker layer. In the future, if necessary, the Master layer will maintain heartbeat connections with the Zookeeper cluster to ensure distributed lock management and Master failover. The Master layer also performs metadata read and write operations through the MySQL database.
- WorkerService: Worker node business logic management.
- Components: Various components integrated into the platform.
- KubeAPI: Service management that interacts with Kubernetes/KubeSphere API.

## 3. Concepts and Definitions

- Service: HDFS, YARN, HIVE, etc. are referred to as services. Their names in this project are standardized as all uppercase, and service names are globally unique.
- Component: Independent processes in HDFS, such as NameNode, DataNode, etc. are referred to as components. In this project, the standard naming convention is Pascal case, and component names are globally unique.
- Master Process: The DataLight Master process is referred to as the master process, and its node is referred to as the platform master node.
- Worker Process: The DataLight Worker process is referred to as the worker process, and its node is referred to as the platform worker node.

## 4. Main Features

The following table lists the first-level functions of the DataLight platform and their support status:

| No.  | First-level Function                              | Supported |
| ---- | ------------------------------------------------- | --------- |
| 1    | Node Initialization Assistant                     | Yes       |
| 2    | Master-Worker Process Maintenance                 | Yes       |
| 3    | Cluster Management                                | Yes       |
| 4    | Node Initialization                               | Yes       |
| 5    | Node Operation and Management                     | Yes       |
| 6    | Service Component Assembly Information Management | Yes       |
| 7    | Service Operation and Management                  | Yes       |
| 8    | Component Operation and Management                | Yes       |
| 9    | Service Component Pre-configuration Management    | Yes       |
| 10   | Service Component Configuration File Management   | Yes       |
| 11   | Distributed Log Global Management                 | Yes       |
| 12   | Monitoring Management                             | Yes       |
| 13   | Alert Management                                  | Yes       |
| 14   | User Management                                   | Yes       |
| 15   | Permission Management                             | Yes       |

## 5. Compilation Environment

This project requires the following for compilation:

- JDK 8
- Gradle 7.4+

## 6. Deployment Instructions

Before deploying this project, please ensure that you have prepared the necessary physical resources, operating system, and understand the deployment process of this project.

### 6.1 Prepare Physical Resources

Ensure you have sufficient hardware resources to deploy and run this project. The recommended minimum configuration includes:

- CPU: 4 Cores +
- Memory: 8 GB +
- Hard Disk: 100 GB +
- Network: 1 Gbps +
- Number of Nodes: >= 3

### 6.2 Prepare Operating System

This project currently only supports (other systems have not been tested):

- CentOS 7.x (Recommended: CentOS-7-x86_64-DVD-2009.iso)

### 6.3 Prepare Deployment Resources

Clone this project and download the required service component packages and dependency libraries, including:

- Clone the current repository and compile it.

- Download service component packages and place them in the plugins directory.

- Build the Linux local directory structure as follows, including the corresponding files in each directory (can be downloaded directly from the network drive or copied from the .document directory of the project):

  | Directory                                                   | Description                                                  | Required |
  | ----------------------------------------------------------- | ------------------------------------------------------------ | -------- |
  | /opt/datalight/app                                          | Master-Worker process package directory                      | Yes      |
  | /opt/datalight/app/config                                   | Master-Worker process startup configuration directory        | Yes      |
  |                                                             |                                                              |          |
  | /opt/datalight/assistant                                    | Node initialization assistant directory                      | Yes      |
  | /opt/datalight/assistant/conf                               | Node initialization assistant configuration file             | Yes      |
  | /opt/datalight/assistant/main                               | Node initialization assistant execution entry                | Yes      |
  | /opt/datalight/assistant/repo                               | Node initialization assistant dependent resources            | Yes      |
  | /opt/datalight/assistant/scripts                            | Node initialization assistant related scripts                | Yes      |
  |                                                             |                                                              |          |
  | /opt/datalight/bin                                          | Master-Worker process start/stop management scripts          | Yes      |
  |                                                             |                                                              |          |
  | /opt/datalight/conf                                         | Environment and service configuration file directory         | Yes      |
  | /opt/datalight/conf/env                                     | Environment configuration file and script directory          | Yes      |
  | /opt/datalight/conf/service                                 | Service deployment maintenance configuration file directory  | Yes      |
  | /opt/datalight/conf/permission                              | Permission declaration and note files (currently unused)     | No       |
  | /opt/datalight/conf/web                                     | Service component WebUI configuration file directory         | Yes      |
  |                                                             |                                                              |          |
  | /opt/datalight/docs                                         | Project documentation directory                              | No       |
  | /opt/datalight/docs/api                                     | Third-party API directory                                    | No       |
  | /opt/datalight/docs/asserts                                 | Documentation resource directory                             | No       |
  | /opt/datalight/docs/package                                 | Service component packaging steps documentation directory    | No       |
  | /opt/datalight/docs/maven                                   | Maven configuration directory                                | No       |
  | /opt/datalight/docs/src-[UPPERCASE SERVICE NAME]-version    | Service component source file modification record directory  | No       |
  |                                                             |                                                              |          |
  | /opt/datalight/exporter                                     | JMX Exporter directory                                       | Yes      |
  | /opt/datalight/exporter/bin                                 | Exporter run script example directory                        | No       |
  | /opt/datalight/exporter/conf                                | Exporter configuration file template directory               | Yes      |
  | /opt/datalight/exporter/jar                                 | Exporter plugin Jar package directory                        | Yes      |
  |                                                             |                                                              |          |
  | /opt/datalight/node                                         | Node operation related configuration and script directory    | Yes      |
  | /opt/datalight/node/conf                                    | Node operation related configuration file directory          | Yes      |
  | /opt/datalight/node/scripts                                 | Node operation related script directory                      | Yes      |
  |                                                             |                                                              |          |
  | /opt/datalight/orm                                          | Database related material directory                          | No       |
  | /opt/datalight/orm/dmj                                      | Database model design directory                              | No       |
  | /opt/datalight/orm/query                                    | SQL query examples directory that may be used in the project | No       |
  | /opt/datalight/orm/sql                                      | Master metadata database Schema initialization file directory | No       |
  |                                                             |                                                              |          |
  | /opt/datalight/plugins                                      | Service component plugin directory                           | Yes      |
  | /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/dashboard   | Only exists in MONITOR, monitoring templates for each service component | Yes      |
  | /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/dlc         | Service component resource package (.tar.gz)                 | Yes      |
  | /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/jars        | Directory for compiled JAR packages of service component plugins | Yes      |
  | /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/placeholder | Directory for service component pre-configuration files      | Yes      |
  | /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/scripts     | Directory for service component aggregation operation scripts | Yes      |
  | /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/templated   | Directory for service component configuration file templates | Yes      |
  |                                                             |                                                              |          |
  | /opt/datalight/scripts                                      | Common script directory                                      | Yes      |
  | /opt/datalight/scripts/tools                                | General tool script directory                                | Yes      |

#### 6.3.1 Create Corresponding Directories

Before starting, first confirm that the corresponding directories exist on the master node.

#### 6.3.2 Prepare Frontend Pages

Go to the DataLight frontend open-source project, follow the corresponding documentation to perform the compilation operation. After compilation, unzip the compiled files and copy them to the `boundivore-datalight\datalight-services\services-master\src\main\resources\public` directory in the backend source code project.

DataLight Frontend Open-Source Project:

```http
https://gitee.com/boundivore/boundivore-datalight-web
```

#### 6.3.3 Prepare Master/Worker

After cloning the project, open it with a code editor and perform the compilation operation of the datalight-services module (execute boot-jar, or download the compiled jar package from the network drive). In the build/libs directory of the services-master and services-worker projects, you can see the services-master-[version].jar and services-worker-[version].jar process files, respectively. Copy them to the app/ directory.

#### 6.3.4 Prepare Service Component Plugins

After cloning the project, open it with a code editor and perform the compilation operation of the datalight-plugins module. Copy the plugin jar packages for the corresponding services in that module to the specified directory (plugins/[SERVICE NAME]/jar), for example: /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/jars.

#### 6.3.5 Copy Other File Directories

In the main project directory, find the .documents folder and copy its contents to the corresponding directories in the table above.

#### 6.3.6 Prepare DLC Service Packages

Go to the following address to download the DLC service packages:

```http
Link: https://pan.baidu.com/s/1342bIcEBDQHdFA35KUTjbQ 
Extract Code: data 
```

After downloading, extract and place the .tar.gz packages for the corresponding services in the corresponding /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/dlc directory.

### 6.4 Initialize Environment

Before starting the Master program, you need to confirm that all nodes ready to serve have completed relevant initialization operations. The DataLight platform has encapsulated related tools to help users quickly initialize all nodes at once, or users can also manually perform initialization operations.

#### 6.4.1 Automatic Initialization

The automatic initialization process depends on the expect tool, which will be automatically installed in the script, i.e., automatically execute:

```shell
yum -y install expect
```

If you want to install this tool manually, you can modify the `init-main.sh` script.

------

The automatic initialization tool is located in the assistant directory under the deployment directory, with the following contents that need to be modified.

Note: MySQL 5.7 should be installed manually, the automatic initialization tool will not include this content.

Examples of each configuration file are provided below.

- init-main-cluster.properties

  ```properties
  # Configuration information for the list of nodes to be initialized. The key prefix is a sequence number that cannot be repeated. Add as many nodes as you need to initialize.
  # Suggestion: 1.node.ip should be the IP of the node currently executing the script
  1.node.ip=192.168.137.10
  1.node.ssh.port=22
  1.node.hostname=node01
  1.node.user.root=root
  1.node.pwd.root=123456
  1.node.user.datalight=datalight
  1.node.pwd.datalight=12345678
  
  2.node.ip=192.168.137.11
  2.node.ssh.port=22
  2.node.hostname=node02
  2.node.user.root=root
  2.node.pwd.root=123456
  2.node.user.datalight=datalight
  2.node.pwd.datalight=12345678
  
  3.node.ip=192.168.137.12
  3.node.ssh.port=22
  3.node.hostname=node03
  3.node.user.root=root
  3.node.pwd.root=123456
  3.node.user.datalight=datalight
  3.node.pwd.datalight=12345678
  ```

- init-main-single-settings.txt

  The script will execute the following content sequentially on "each node" according to the "configuration order" to complete the initialization of each node.

  ```text
  init-stop-firewall.sh
  init-ssh-remove-ask.sh
  init-close-swap.sh
  init-optimize-sysctl.sh
  init-systemd-conf.sh
  init-hostname.sh
  init-hosts.sh
  init-user-group.sh
  init-datalight-env.sh
  init-jdk.sh
  init-yum-install.sh
  init-chrony-server-config.sh
  init-chrony-client-config.sh
  ```

#### 6.4.2 Manual Initialization

If you use the automatic initialization above, you do not need to perform manual initialization. The following operations are for users who do not want to use the automatic initialization tool.

Manual initialization involves the following:

- Disable firewall (or open necessary access restrictions for services)

- Disable SSH access confirmation

- Disable Swap memory exchange

- Optimize memory management and I/O performance

- Set the maximum number of memory mapped areas a process can have to 655300

  ```shell
  vm.max_map_count=655300  # Set the maximum number of memory mapped areas a process can have to 655300.
  vm.dirty_ratio=10  # Specify the maximum percentage of "dirty" pages in available memory to 10%.
  vm.dirty_background_ratio=5  # Specify the memory usage percentage threshold for background write dirty page operations to start at 5%.
  vm.dirty_writeback_centisecs=200  # Set the time interval for the kernel to periodically write dirty pages back to disk to 200 centiseconds (i.e., every 2 seconds).
  vm.vfs_cache_pressure=200  # Control the kernel's aggressiveness in reclaiming directory and inode caches to 200.
  vm.dirty_expire_centisecs=6000  # Set the maximum time a dirty page can stay in memory before being forced to write back to disk to 6000 centiseconds (i.e., 60 seconds).
  ```

- Optimize memory management, I/O performance, and resource limits to improve system efficiency and stability when handling large amounts of data and memory mapping tasks

  ```shell
  vm.max_map_count=655300  # Set the maximum number of memory mapped areas a process can have to 655300.
  vm.dirty_ratio=10  # Specify the maximum percentage of "dirty" pages in available memory to 10%.
  vm.dirty_background_ratio=5  # Specify the memory usage percentage threshold for background write dirty page operations to start at 5%.
  vm.dirty_writeback_centisecs=200  # Set the time interval for the kernel to periodically write dirty pages back to disk to 200 centiseconds (i.e., every 2 seconds).
  vm.vfs_cache_pressure=200  # Control the kernel's aggressiveness in reclaiming directory and inode caches to 200.
  vm.dirty_expire_centisecs=6000  # Set the maximum time a dirty page can stay in memory before being forced to write back to disk to 6000 centiseconds (i.e., 60 seconds).
  
  sed -i '/^DefaultLimitNOFILE=/d' "$system_conf"  # Delete existing DefaultLimitNOFILE settings in system.conf
  sed -i '/^DefaultLimitNPROC=/d' "$system_conf"  # Delete existing DefaultLimitNPROC settings in system.conf
  echo "DefaultLimitNOFILE=131072" >> "$system_conf"  # Add new DefaultLimitNOFILE settings
  echo "DefaultLimitNPROC=131072" >> "$system_conf"  # Add new DefaultLimitNPROC settings
  
  cat > "/etc/security/limits.conf" << EOF
  root        soft    nproc   131072  # Set soft nproc limit for root user to 131072
  root        hard    nproc   131072  # Set hard nproc limit for root user to 131072
  root        soft    nofile  131072  # Set soft nofile limit for root user to 131072
  root        hard    nofile  131072  # Set hard nofile limit for root user to 131072
  *           soft    nproc   131072  # Set soft nproc limit for all users to 131072
  *           hard    nproc   131072  # Set hard nproc limit for all users to 131072
  *           soft    nofile  131072  # Set soft nofile limit for all users to 131072
  *           hard    nofile  131072  # Set hard nofile limit for all users to 131072
  *           hard    fsize   unlimited  # Set hard file size limit for all users to unlimited
  *           soft    fsize   unlimited  # Set soft file size limit for all users to unlimited
  *           soft    cpu     unlimited  # Set soft CPU time limit for all users to unlimited
  *           hard    cpu     unlimited  # Set hard CPU time limit for all users to unlimited
  *           soft    as      unlimited  # Set soft address space limit for all users to unlimited
  *           hard    as      unlimited  # Set hard address space limit for all users to unlimited
  EOF
  ```

  - Modify hostnames of all nodes

  - Modify intranet IP and hostname mapping relationships for all nodes

  - Create DataLight username and group in Linux

  - Set system, user, and pseudo-terminal environment variables

  - Install JDK 1.8

  - Install yum dependencies

    ```shell
    yum -y install epel-release  # Install EPEL repository, providing additional packages
    yum -y install jq  # Install jq for processing JSON data
    yum -y install curl  # Install curl for command-line data transfer
    yum -y install chrony  # Install chrony for time synchronization
    yum -y install expect  # Install expect for automating interactive shell scripts
    yum -y install openssl openssl-devel patch  # Install OpenSSL and development packages and patch tool
    yum -y install lrzsz  # Install lrzsz for ZMODEM file transfer
    yum -y install unzip zip  # Install unzip and zip for extracting and compressing files
    yum -y install yum-utils  # Install yum-utils, providing additional yum commands
    yum -y install net-tools  # Install net-tools, providing network tools like ifconfig
    
    yum -y install gcc gcc-c++  # Install GCC and G++ compilers
    yum -y install make  # Install make, a build automation tool
    yum -y install autoconf automake libtool curl  # Install autoconf, automake, libtool, and curl for building and configuring software
    yum -y install zlib lzo-devel zlib-devel openssl openssl-devel ncurses-devel ruby  # Install zlib, lzo-devel, zlib-devel, openssl, openssl-devel, ncurses-devel, and ruby for compression, encryption, terminal control, and Ruby language support
    yum -y install snappy snappy-devel bzip2 bzip2-devel lzo lzo-devel lzop libXtst  # Install snappy, snappy-devel, bzip2, bzip2-devel, lzo, lzo-devel, lzop, and libXtst for data compression and graphical interface support
    ```

  - Install and configure chronyd time management server on the master node

  - Install and configure chronyd time synchronization client on worker nodes

  - Install MySQL 5.7 (if you need MySQL 8+ support, you can adapt the platform and service component source code yourself)

### 6.5 Initialize DataLight Database

After the environment initialization is complete, follow these steps to initialize the platform database:

- Create the db_datalight database

  ```sql
  CREATE DATABASE db_datalight DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

- Execute the initialization SQL file

  ```sql
  USE db_datalight;
  SOURCE /opt/datalight/orm/sql/db_datalight.sql;
  ```

### 6.6 Start the Master Program

After completing the above initialization operations, the nodes are ready for work. You can start the Master process by executing the following on the master node:

```shell
/opt/datalight/bin/datalight.sh start master 8001
```

After successful startup, you will see the following log content:

```shell
No masterIp provided or masterIp is empty. Skipping IP validation.
master starting in 10263...
master started.
/opt/datalight/bin/datalight.sh done.
```

### 6.7 Login

After successfully starting the Master process, you can access the following page to log in:

```http
http://<Master-IP>:8001
```

Note: The default username and password for the first login is: admin/admin

The page looks like this:

![Login](https://poe.com/chat/.documents/docs/assets/%E7%99%BB%E5%BD%95.png)

## 7. Product Manual

After completing the above content, please refer to the [Product Manual](https://poe.com/chat/.documents/docs/产品手册.md) to continue with page deployment operations.

## 8. Planned Supported Services

| Service         | Version  | Supported                                                 | Completion Date |
| --------------- | -------- | --------------------------------------------------------- | --------------- |
| ZOOKEEPER       | 3.8.2    | √                                                         | 2023-07         |
| HDFS            | 3.2.4    | √                                                         | 2023-07         |
| YARN            | 3.2.4    | √                                                         | 2023-08         |
| PROMETHEUS      | 2.46.0   | √                                                         | 2023-08         |
| ALERTMANAGER    | 0.26.0   | √                                                         | 2023-08         |
| GRAFANA         | 10.0.3   | √                                                         | 2023-08         |
| HIVE            | 3.1.3    | √                                                         | 2024-04         |
| TEZ             | 0.10.2   | √                                                         | 2024-04         |
| HBASE           | 2.5.9    | √                                                         | 2024-04         |
| SPARK           | 3.1.3    | √                                                         | 2024-05         |
| FLINK           | 1.19.0   | √                                                         | 2024-05         |
| ZKUI            | 2.0.0    | √                                                         | 2024-08         |
| KYUUBI          | 1.9.0    | √                                                         | 2024-08         |
| HUDI            | TBD      |                                                           |                 |
| KUBESPHERE      | 3.3.2    | √                                                         | 2023-09         |
| SEATUNNEL       | TBD      |                                                           |                 |
| DOPHINSCHEDULER | 3.1.9    | √                                                         | 2024-10         |
| KERBEROS        | 1.15.1   | √                                                         | 2024-08         |
| LDAP            | 2.4.44   | √                                                         | 2024-08         |
| SSSD            | 1.16.5   | √                                                         | 2024-08         |
| RANGER          | 2.4.0    | √                                                         | 2024-09         |
| ATLAS           | TBD      |                                                           |                 |
| HUE             | TBD      |                                                           |                 |
| KAFKA           | 2.6.0    | √                                                         | 2024-05         |
| ES              | Any      | Due to protocol issues, users can integrate it themselves |                 |
| TRINO           | TBD      |                                                           |                 |
| KYLIN           | TBD      |                                                           |                 |
| KUDU            | TBD      |                                                           |                 |
| IMPALA          | TBD      |                                                           |                 |
| MINIO           | 20241218 | √                                                         | 2024-12         |
| More....        |          |                                                           |                 |

## Participate in Open Source

If you are interested in this project, we welcome positive feedback to help make the project better.

WeChat Official Account:

QQ Discussion Group:

WeChat Contact:

## Open Source License

This project is licensed under the Apache 2.0 open source license. For detailed content, please see [Apache 2.0 LICENSE](http://www.apache.org/licenses/).

## Acknowledgements

### Users

Thanks to all contributors and committers

#### Contributors

Special thanks to the following people for their contributions to this project:

- [@boundivore](https://gitee.com/boundivore)
- [@Tracy-88](https://gitee.com/Tracy-88)

#### Committers

Special thanks to the following people for submitting code to this project:

- [@boundivore](https://gitee.com/boundivore)
- [@Tracy-88](https://gitee.com/Tracy-88)

We greatly appreciate their support and contributions!

### Projects

We would like to especially thank all the open-source projects or code libraries used during the development of this project.

Without the support of these projects, this project would not have been possible.

Special acknowledgments to the following open-source projects:

- **[Spring](https://spring.io/)**: A lightweight application framework and inversion of control container.
- **[Hutool](https://hutool.cn/)**: A small but comprehensive Java utility class library.
- **[Sa-Token](https://sa-token.dev33.cn/)**: A lightweight Java authority authentication framework.
- **[SshJ](https://github.com/hierynomus/sshj)**: An SSH library for Java.

## Recommendations

Here, we recommend some other excellent open-source projects that might be helpful to you:

- **[Hutool](https://hutool.cn/)**: Hutool is a small but comprehensive Java utility class library that helps developers improve efficiency by simplifying code and providing rich utility classes. Its functions cover string operations, date processing, file operations, HTTP requests, and many other aspects, making it a valuable assistant for Java developers.
- **[Sa-Token](https://sa-token.dev33.cn/)**: Sa-Token is a lightweight Java authority authentication framework that provides simple and easy-to-use login authentication, permission verification, session management, and other functions. It supports various login methods and permission control strategies, suitable for various Java Web applications.
- **[Mybatis-Plus](https://baomidou.com/)**: MyBatis-Plus is the best partner for MyBatis and a powerful ORM framework enhancement tool, adhering to the principle of "only enhancement, no change." It provides efficient single-table CRUD operations, code generation, automatic paging, logical deletion, automatic filling, and other rich features, greatly simplifying the development process and improving efficiency.

We hope these recommended projects can help you improve efficiency and solve common problems in your development process.
