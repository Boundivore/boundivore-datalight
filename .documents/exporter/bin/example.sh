# java -javaagent:./jmx_exporter.jar=12345:config.yaml -jar yourJar.jar
#export YARN_RESOURCEMANAGER_OPTS="-Djava.net.preferIPv4Stack=true \
#-Dcom.sun.management.jmxremote.authenticate=false \
#-Dcom.sun.management.jmxremote.ssl=false \
#-Dcom.sun.management.jmxremote.local.only=false \
#-Dcom.sun.management.jmxremote.port={{jmxRemotePort_ResourceManager}} \
#-javaagent:${DATALIGHT_DIR}/exporter/jar/jmx_exporter.jar={{jmxExporterPort_ResourceManager}}:${SERVICE_DIR}/YARN/exporter/conf/jmx_config_ResourceManager.yaml"

SERVER_JVMFLAGS="-javaagent:{{DATALIGHT_DIR}}/exporter/jar/jmx_exporter.jar={{exporterPort}}:{{JMX_CONF_FILE}}"