SELECT t1.hostname,
       t1.ipv4,
       t1.ipv6,
       t1.ssh_port,
       t1.cpu_arch,
       t1.cpu_cores,
       t1.ram,
       t1.disk,
       t1.node_state,
       t1.os_version,
       t2.id         AS config_id,
       t2.cluster_id AS cluster_id,
       t2.node_id    AS node_id,
       t2.service_name,
       t2.filename,
       t2.config_content_id,
       t2.config_path,
       t2.config_version,
       t4.sha256,
       t4.config_data,
       t6.component_name
FROM t_dl_node t1
         JOIN t_dl_config t2 ON t1.id = t2.node_id
         JOIN t_dl_config_content t4 ON t2.config_content_id = t4.id
         JOIN t_dl_component t6 ON t2.node_id = t6.node_id AND t2.service_name = t6.service_name
WHERE t2.cluster_id = 1
  AND t2.service_name = 'ZOOKEEPER'
  AND t2.config_path = '/srv/datalight/ZOOKEEPER/conf/zoo.cfg';