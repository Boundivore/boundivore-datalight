#!/bin/bash

initMetaStore(){
    su -s /bin/bash hadoop -c "/srv/udp/{{UDP-Version}}/hive/bin/schematool -dbType mysql -initSchema"
}

initMetaStore