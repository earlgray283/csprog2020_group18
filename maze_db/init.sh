#!/usr/bin/bash

docker \
    exec -i codefighter_db_server_1 \
    sh -c 'cat /docker-entrypoint-initdb.d/*.sql | mysql -uroot -proot'