如需引用外部配置，可在 datalight.sh 脚本中添加如下内容：
~~~shell
--spring.cloud.bootstrap.location=/xxx/bootstrap-master.yaml
--spring.cloud.bootstrap.location=/xxx/bootstrap-worker.yaml

--Dspring.config.location=/xxx/application-master.yaml
--Dspring.config.location=/xxx/application-worker.yaml

-Dlogging.config=/xxx/config/logback-master.xml
-Dlogging.config=/xxx/config/logback-worker.xml
~~~