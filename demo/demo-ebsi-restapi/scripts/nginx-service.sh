#!/usr/bin/env bash


COMPOSE_PROJECT=/home/edelivery/demo-ebsi-restapi
cd $COMPOSE_PROJECT/docker/nginx

NAME='nginx_web_1'

# Start the service nginx
start() {
     # test if docker is already up
    if [[ $(docker ps -f "name=$NAME" --format '{{.Names}}') == $NAME ]] ; then
        echo "Container 'nginx_web_1' is already running!"
        echo "$(docker-compose -f docker-compose-nginx.yml ps)"
    else
        echo "Start nginx"
        docker-compose -f docker-compose-nginx.yml up -d
        echo "Start nginx started!"
    fi

     
}

# Restart the service nginx
stop() {
     echo "Stop and clean nginx"
     docker-compose -f docker-compose-nginx.yml down
     echo "nginx stopped!"
}

# Restart the service nginx
status() {
     echo "Status"
     echo "$(docker-compose -f docker-compose-nginx.yml ps)"
     
}

## parameter logic ###
case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  status)
        status
        ;;
  restart|reload|condrestart)
        stop
        start
        ;;
  *)
        echo $"Usage: $0 {start|stop|restart}"
        exit 1
esac

exit 0


