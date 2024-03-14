# 服务详细配置 YAML 参数说明

下面以 HDFS 服务配置为例，进行注释说明：

~~~yaml
datalight:
  service:
   	# 服务名称
    name: HDFS
    # 服务版本号
    version: 3.2.4
    # 服务 DLC 资源安装包名称
    tgz: dlc-hdfs-3.2.4.tar.gz
    # 配置文件相关
    conf-dirs:
    	# 配置文件在服务中的绝对路径
      - service-conf-dir: '{{SERVICE_DIR}}/HDFS/exporter/conf'
      	# 配置文件模板路径
        templated-dir: '{{DATALIGHT_DIR}}/plugins/HDFS/templated/exporter/conf'

      - service-conf-dir: '{{SERVICE_DIR}}/HDFS/bin'
        templated-dir: '{{DATALIGHT_DIR}}/plugins/HDFS/templated/bin'

      - service-conf-dir: '{{SERVICE_DIR}}/HDFS/etc/hadoop'
        templated-dir: '{{DATALIGHT_DIR}}/plugins/HDFS/templated/etc/hadoop'

      - service-conf-dir: '{{SERVICE_DIR}}/HDFS/sbin'
        templated-dir: '{{DATALIGHT_DIR}}/plugins/HDFS/templated/sbin'

	# 服务配置文件管理插件包
    config-event-handler-jar: 'plugins-hdfs-1.0.0.jar'
    # 服务配置文件操作主入口类
    config-event-handler-clazz: 'cn.boundivore.dl.plugin.hdfs.config.event.ConfigEventHandler'

	# 部署服务时初始化相关步骤
    initialize:
      steps:
      	# 步骤类型，COMMON_SCRIPT 的类型对应的脚本存放于/datalight/scripts 目录下
        - type: COMMON_SCRIPT
          name: '清理过期的部署环境'
          # 脚本名称
          shell: 'service-remove.sh'
          # 脚本静态参数
          args: [ ]
          # 脚本交互参数，格式举例：[ '遇到指定值1', '输入指定值1','遇到指定值2','输出指定值2','遇到指定值3则退出脚本交互']
          interactions: [ ]
          # 脚本退出码为何值认为是正常退出
          exits: '0'
          # 脚本执行超时时间，单位：毫秒
          timeout: 10000
          # 脚本执行完毕后，停留时间，单位：毫秒
          sleep: 0

        - type: COMMON_SCRIPT
          name: '初始化部署服务所需的环境'
          shell: 'service-init-env.sh'
          args: [ ]
          interactions: [ ]
          exits: '0'
          timeout: 60000
          sleep: 0

		# 步骤类型，JAR 类型会执行下方的 JAR 文件，并以 clazz 为入口，通过反射方式调用
        - type: JAR
          name: '初始化服务配置文件'
          jar: 'plugins-hdfs-1.0.0.jar'
          clazz: 'cn.boundivore.dl.plugin.hdfs.config.ConfigHDFS'
          args: [ ]
          interactions: [ ]
          exits: '0'
          sleep: 0
	# 该服务下的组件配置
    components:
    	# 组件名称
      - name: JournalNode
      	# 组件部署优先级，数字越小，优先级越高
        priority: 1
        # 组件允许最大部署实例个数，-1 为无限制
        max: -1
        # 组件最小部署实例个数，最小值为 0，即，可不部署
        min: 3
        # 当前组件与哪些组件在同节点互斥，填写组件名称。
        mutexes: [ ]
        actions:
        	# 组件行为
          - type: DEPLOY
          	# 开始执行时组件状态
            start-state: DEPLOYING
            # 执行成功组件状态
            success-state: STARTED
            # 执行失败组件状态
            fail-state: SELECTED
            steps:
            	# 步骤类型，SCRIPT 类型的脚本存放于 datalight/plugins/MONITOR/scripts
              - type: SCRIPT
                name: '部署后启动 JournalNode'
                shell: 'hdfs-operation.sh'
                args: [ 'JournalNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: START
            start-state: STARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '启动 JournalNode'
                shell: 'hdfs-operation.sh'
                args: [ 'JournalNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: STOP
            start-state: STOPPING
            success-state: STOPPED
            fail-state: STARTED
            steps:
              - type: SCRIPT
                name: '停止 JournalNode'
                shell: 'hdfs-operation.sh'
                args: [ 'JournalNode', 'stop' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: RESTART
            start-state: RESTARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '重启 JournalNode'
                shell: 'hdfs-operation.sh'
                args: [ 'JournalNode', 'restart' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

      - name: NameNode1
        priority: 2
        max: 1
        min: 1
        mutexes: [ 'NameNode2' ]
        actions:
          - type: DEPLOY
            start-state: DEPLOYING
            success-state: STARTED
            fail-state: SELECTED
            steps:
              - type: SCRIPT
                name: '格式化 NameNode1'
                shell: 'hdfs-namenode1-format.sh'
                args: [ ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

              - type: SCRIPT
                name: '部署后启动 NameNode1'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: START
            start-state: STARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '启动 NameNode1'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: STOP
            start-state: STOPPING
            success-state: STOPPED
            fail-state: STARTED
            steps:
              - type: SCRIPT
                name: '停止 NameNode1'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'stop' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: RESTART
            start-state: RESTARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '重启 NameNode1'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'restart' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

      - name: NameNode2
        priority: 3
        max: 1
        min: 1
        mutexes: [ 'NameNode1' ]
        actions:
          - type: DEPLOY
            start-state: DEPLOYING
            success-state: STARTED
            fail-state: SELECTED
            steps:
              - type: SCRIPT
                name: '待命 NameNode2'
                shell: 'hdfs-namenode2-standby.sh'
                args: [ ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

              - type: SCRIPT
                name: '部署后启动 NameNode2'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: START
            start-state: STARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '启动 NameNode2'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: STOP
            start-state: STOPPING
            success-state: STOPPED
            fail-state: STARTED
            steps:
              - type: SCRIPT
                name: '停止 NameNode2'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'stop' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: RESTART
            start-state: RESTARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '重启 NameNode2'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'restart' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

      - name: ZKFailoverController1
        priority: 4
        max: 1
        min: 1
        mutexes: [ ]
        actions:
          - type: DEPLOY
            start-state: DEPLOYING
            success-state: STARTED
            fail-state: SELECTED
            steps:
              - type: SCRIPT
                name: '格式化 ZKFC'
                shell: 'hdfs-zkfc-format.sh'
                args: [ ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

              - type: SCRIPT
                name: '部署后启动 ZKFailoverController1'
                shell: 'hdfs-operation.sh'
                args: [ 'ZKFailoverController', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: START
            start-state: STARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '启动 ZKFailoverController1'
                shell: 'hdfs-operation.sh'
                args: [ 'ZKFailoverController', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: STOP
            start-state: STOPPING
            success-state: STOPPED
            fail-state: STARTED
            steps:
              - type: SCRIPT
                name: '停止 ZKFailoverController1'
                shell: 'hdfs-operation.sh'
                args: [ 'ZKFailoverController', 'stop' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: RESTART
            start-state: RESTARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '重启 ZKFailoverController1'
                shell: 'hdfs-operation.sh'
                args: [ 'ZKFailoverController', 'restart' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

      - name: ZKFailoverController2
        priority: 5
        max: 1
        min: 1
        mutexes: [ ]
        actions:
          - type: DEPLOY
            start-state: DEPLOYING
            success-state: STARTED
            fail-state: SELECTED
            steps:
              - type: SCRIPT
                name: '部署后启动 ZKFailoverController2'
                shell: 'hdfs-operation.sh'
                args: [ 'ZKFailoverController', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: START
            start-state: STARTING
            success-state: STARTED
            fail-state: STOPPED

            steps:
              - type: SCRIPT
                name: '启动 ZKFailoverController2'
                shell: 'hdfs-operation.sh'
                args: [ 'ZKFailoverController', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: STOP
            start-state: STOPPING
            success-state: STOPPED
            fail-state: STARTED
            steps:
              - type: SCRIPT
                name: '停止 ZKFailoverController2'
                shell: 'hdfs-operation.sh'
                args: [ 'ZKFailoverController', 'stop' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: RESTART
            start-state: RESTARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '重启 ZKFailoverController2'
                shell: 'hdfs-operation.sh'
                args: [ 'ZKFailoverController', 'restart' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

      - name: DataNode
        priority: 6
        max: -1
        min: 1
        mutexes: [ ]
        actions:
          - type: DEPLOY
            start-state: DEPLOYING
            success-state: STARTED
            fail-state: SELECTED
            steps:
              - type: SCRIPT
                name: '部署后启动 DataNode'
                shell: 'hdfs-operation.sh'
                args: [ 'DataNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: START
            start-state: STARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '启动 DataNode'
                shell: 'hdfs-operation.sh'
                args: [ 'DataNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: STOP
            start-state: STOPPING
            success-state: STOPPED
            fail-state: STARTED
            steps:
              - type: SCRIPT
                name: '停止 DataNode'
                shell: 'hdfs-operation.sh'
                args: [ 'DataNode', 'stop' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: RESTART
            start-state: RESTARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '重启 DataNode'
                shell: 'hdfs-operation.sh'
                args: [ 'DataNode', 'restart' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

      - name: HttpFS
        priority: 7
        max: 2
        min: 2
        mutexes: [ ]
        actions:
          - type: DEPLOY
            start-state: DEPLOYING
            success-state: STARTED
            fail-state: SELECTED
            steps:
              - type: SCRIPT
                name: '部署后启动 HttpFS'
                shell: 'hdfs-operation.sh'
                args: [ 'HttpFS', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: START
            start-state: STARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '启动 HttpFS'
                shell: 'hdfs-operation.sh'
                args: [ 'NameNode', 'start' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: STOP
            start-state: STOPPING
            success-state: STOPPED
            fail-state: STARTED
            steps:
              - type: SCRIPT
                name: '停止 HttpFS'
                shell: 'hdfs-operation.sh'
                args: [ 'HttpFS', 'stop' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

          - type: RESTART
            start-state: RESTARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: SCRIPT
                name: '重启 HttpFS'
                shell: 'hdfs-operation.sh'
                args: [ 'HttpFS', 'restart' ]
                interactions: [ ]
                exits: '0'
                timeout: 60000
                sleep: 1000

      - name: HDFSClient
        priority: 8
        max: -1
        min: 0
        mutexes: [ ]
        actions:
          - type: DEPLOY
            start-state: DEPLOYING
            success-state: STARTED
            fail-state: SELECTED
            steps:
              - type: COMMAND
                name: '正在完成 HDFSClient 部署'
                shell: 'echo done'
                args: [ ]
                interactions: [ ]
                exits: '0'
                sleep: 0

          - type: START
            start-state: STARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
              - type: COMMAND
                name: '启动 HDFSClient'
                shell: 'echo done'
                args: [ ]
                interactions: [ ]
                exits: '0'
                timeout: '0'
                sleep: 0

          - type: STOP
            start-state: STOPPING
            success-state: STOPPED
            fail-state: STARTED
            steps:
              - type: COMMAND
                name: '停止 HDFSClient'
                shell: 'echo done'
                args: [ ]
                interactions: [ ]
                exits: '0'
                timeout: '0'
                sleep: 0

          - type: RESTART
            start-state: RESTARTING
            success-state: STARTED
            fail-state: STOPPED
            steps:
            	# 步骤类型，COMMAND 类型的 shell 为 bash 命令本身，无自定义脚本
              - type: COMMAND
                name: '重启 HDFSClient'
                shell: 'echo done'
                args: [ ]
                interactions: [ ]
                exits: '0'
                timeout: '0'
                sleep: 0


~~~