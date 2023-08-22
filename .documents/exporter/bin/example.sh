# java -javaagent:./jmx_exporter.jar=12345:config.yaml -jar yourJar.jar
SERVER_JVMFLAGS="-javaagent:{{DATALIGHT_DIR}}/exporter/jar/jmx_exporter.jar={{exporterPort}}:{{JMX_CONF_FILE}}"