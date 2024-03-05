# Boundivore-DataLight

## I. Introduction

DataLight is an open-source big data operations management platform, designed to simplify and automate the deployment, management, and monitoring of big data services. It provides a comprehensive set of tools to manage your big data ecosystem, aiming to help businesses build big data platforms efficiently and swiftly.

For more details, visit

~~~http
http://boundivore.datalight.com
~~~

## II. Software Architecture

<img src=".documents/docs/assets/arch.jpg" alt="arch" style="zoom:20%;" />

## III. Concepts and Definitions

* Service: Big data services such as HDFS, YARN, HIVE are referred to as services, and they are standardly named in uppercase in this project.
* Component: Independent processes within HDFS, such as NameNode, DataNode, are referred to as components, and they follow the PascalCase naming convention in this project.
* Master Process: The DataLight Master process is referred to as the master process.
* Worker Process: The DataLight Worker process is referred to as the worker process.

## IV. Main Features

The table below lists the main features supported by the DataLight platform and their current support status:

| No. | Primary Function         | Supported | Date |
| --- | ----------------------- | --------- | ---- |
| 1   | Node Initialization Assistant | Yes       |      |
| 2   | Master and Worker Process Maintenance | Yes |      |
| 3   | Cluster Management          | Yes       |      |
| 4   | Node Initialization         | Yes       |      |
| 5   | Node Management             | Yes       |      |
| 6   | Service Component Maintenance | Yes     |      |
| 7   | Service Component Pre-configuration | Yes |      |
| 8   | Master and Worker Log Management | Yes  |      |
| 9   | Service Component Log Aggregation Management | Yes |      |
| 10  | Monitoring Management       | Yes       |      |
| 11  | Alert Management            | Yes       |      |
| 12  | User Management             | Yes       |      |
| 13  | Rights Management           | Yes       |      |
| 14  | Permission Management       | Yes       |      |


## V. Deployment Instructions
Before deploying this project, please ensure that you have prepared the necessary physical resources, operating system, and understand the deployment process of this project.

**5.1 Prepare Physical Resources**
Make sure you have sufficient hardware resources to deploy and run this project. The recommended minimum configuration includes:

- CPU: 4 Cores +
- Memory: 8 GB +
- Hard Drive: 100 GB +
- Network: 1 Gbps +

**5.2 Prepare Operating System**
This project currently only supports (other systems have not been tested):

- CentOS 7.x

**5.3 Prepare Deployment Resources**
Download the latest version of the project, as well as all the necessary dependency software and libraries, including:

- Clone the current repository and compile.
- Download the DLC Resource Pack and place it in the appropriate location.
- Build a local directory structure.

Directory | Description | Necessary
--- | --- | ---
/opt/datalight/app | Master-slave process package directory | Yes
/opt/datalight/app/config | Master-slave process package startup configuration directory | Yes
/opt/datalight/assistant | Initialization node assistant directory | Yes
/opt/datalight/assistant/conf | Initialization node assistant configuration files | Yes
/opt/datalight/assistant/main | Initialization node assistant main entry point | Yes
/opt/datalight/assistant/repo | Initialization node assistant dependency resources | Yes
/opt/datalight/assistant/scripts | Related sub-scripts for initialization node assistant | Yes
/opt/datalight/bin | Master-slave process start-stop management scripts | Yes
/opt/datalight/conf | Environment and service configuration files directory | Yes
/opt/datalight/conf/env | Environment configuration files and scripts directory | Yes
/opt/datalight/conf/service | Service deployment and maintenance configuration files directory | Yes
/opt/datalight/docs | Project documentation directory | No
/opt/datalight/docs/api | Third-party API directory | No
/opt/datalight/docs/package | Service component packaging steps documentation directory | No
/opt/datalight/docs/maven | Maven configuration directory | No
/opt/datalight/docs/src-[UPPERCASE SERVICE NAME]-version | Service component source code change record directory | No
/opt/datalight/exporter | JMX Exporter directory | Yes
/opt/datalight/exporter/bin | Exporter script examples directory | No
/opt/datalight/exporter/conf | Exporter configuration file templates directory | Yes
/opt/datalight/exporter/jar | Exporter plugin Jar files directory | Yes
/opt/datalight/node | Node operation related configuration and scripts directory | Yes
/opt/datalight/node/conf | Node operation related configuration files directory | Yes
/opt/datalight/node/scripts | Directory for scripts related to node operations | Yes
/opt/datalight/orm | Database related materials directory | No
/opt/datalight/orm/dmj | Database model design directory | No
/opt/datalight/orm/query | Directory for SQL query examples that may be used in the project | No
/opt/datalight/orm/sql | Master meta-database schema initialization file directory | No
/opt/datalight/plugins | Service component corresponding plugins directory | Yes
/opt/datalight/plugins/[UPPERCASE SERVICE NAME]/dlc | Directory for service component corresponding .tar.gz package | Yes
/opt/datalight/plugins/[UPPERCASE SERVICE NAME]/jars | Directory for compiled Jar files of service component plugins | Yes
/opt/datalight/plugins/[UPPERCASE SERVICE NAME]/placeholder | Directory for pre-configured files of service components | Yes
/opt/datalight/plugins/[UPPERCASE SERVICE NAME]/scripts | Directory for aggregated operation scripts of service components | Yes
/opt/datalight/plugins/[UPPERCASE SERVICE NAME]/templated | Directory for configuration file templates of service components | Yes

**5.3.1 Create Corresponding Directories**
First, create the above corresponding directories in the Linux system.

#### 5.3.2 Prepare Front-end Pages

Navigate to the DataLight Front-end Open Source Project and follow the corresponding documentation to compile the project. After compilation, extract the compiled files and copy them to the datalight-services/services-master/public directory of the current source code project.

DataLight Front-end Open Source Project:

~~~http
http://boundivore-datalight-web.com
~~~

#### 5.3.3 Prepare Master/Worker

After cloning the project, open it with a code editor and perform compilation operations for the datalight-services module. In the build/libs directory of the services-master and services-worker projects, you should see two process files named services-master-[version number].jar and services-worker-[version number].jar. Copy these files to the app/ directory.

#### 5.3.4 Prepare Service Component Plugins

After cloning the project, open it with a code editor and perform compilation operations for the datalight-plugins module. Copy the corresponding service plugin jar files from this module to the designated directory, such as: /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/jars.

#### 5.3.5 Copy Other File Directories

In the main project directory, locate the .documents folder and copy its contents to the corresponding directories mentioned in the table above.

#### 5.3.6 Prepare DLC Service Package

Navigate to the following address to download the DLC service package:

~~~http
http://x.com
~~~

After downloading, extract the package and place the corresponding .tar.gz files in the /opt/datalight/plugins/[UPPERCASE SERVICE NAME]/dlc directory.

### 5.4 Initialize the Environment

### 5.5 Start the Master Main Program

### 5.6 Create a Cluster

### 5.7 Add Nodes

### 5.8 Add Services

### 5.9 Add Components

### 5.10 Set Preconfiguration Files

### 5.11 Execute Deployment

### 5.12 Complete Deployment

## VI. Services Planned to Support

| Service         | Version | Supported | Completion Date |
|-----------------|---------|-----------|-----------------|
| ZOOKEEPER       | 3.8.2   | √         | 2023-07-31      |
| HDFS            | 3.2.4   | √         | 2023-07-31      |
| YARN            | 3.2.4   | √         | 2023-08-31      |
| PROMETHEUS      | 2.46.0  | √         | 2023-08-10      |
| ALERTMANAGER    | 0.26.0  | √         | 2023-08-10      |
| GRAFANA         | 10.0.3  | √         | 2023-08-10      |
| HIVE            | 3.1.3   | √         | 2023-09-13      |
| TEZ             | 0.9.2   | √         | 2023-09-13      |
| HBASE           | TBD     | √         |                 |
| SPARK           | TBD     | √         |                 |
| FLINK           | TBD     |           |                 |
| HUDI            | TBD     |           |                 |
| KUBESPHERE      | 3.3.2   | √         | 2023-09-06      |
| SEATUNNEL       | TBD     |           |                 |
| DOPHINSCHDULER  | TBD     |           |                 |
| RANGER          | TBD     |           |                 |
| ATLAS           | TBD     |           |                 |
| HUE             | TBD     |           |                 |
| KAFKA           | TBD     |           |                 |
| ES              | TBD     |           |                 |
| TRINO           | TBD     |           |                 |
| KYLIN           | TBD     |           |                 |
| KUDU            | TBD     |           |                 |
| IMPALA          | TBD     |           |                 |

## VII. Functions Planned to be Improved

## VIII. Summary of Resource Downloads

### 8.1 DataLight Platform Deployment Package Download

~~~http
http://x.com
~~~

### 8.2 DLC Service Package Download

~~~http
http://x.com
~~~