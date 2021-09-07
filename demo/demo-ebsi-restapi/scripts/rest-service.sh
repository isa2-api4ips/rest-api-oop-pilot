#!/usr/bin/env bash


COMPOSE_PROJECT=/home/edelivery/demo-ebsi-restapi
cd $COMPOSE_PROJECT/docker/rest

NAME='rest_restWildfly_1'

tailContainerLog()  { 
    echo "Tail container $1 logs for phrase '$2'"
    while IFS= read -r LOGLINE || [[ -n "$LOGLINE" ]]; do
        printf '%s\n' "$LOGLINE"
        [[ "${LOGLINE}" =~  "$2" ]] && exit 0
        [[ "${LOGLINE}" =~  "ERROR" ]] && exit 1
    done < <(timeout 5m docker logs -f $1)
    exit 3
}


# Start the service nginx
start() {
     # test if docker is already up
    if [[ $(docker ps -f "name=$NAME" --format '{{.Names}}') == $NAME ]] ; then
        echo "Container 'rest_restWildfly_1' is already running!"
        echo "$(docker-compose -f docker-compose-rest.yml ps)"
        
    else
        echo "Start rest service"
        docker-compose -f docker-compose-rest.yml up -d
        echo "Start rest service started!"
        echo
        tailContainerLog $NAME "(WildFly Core 15.0.1.Final) started"
        
    fi

     
}

# Restart the service nginx
stop() {
     echo "Stop and clean rest service"
     docker-compose -f docker-compose-rest.yml down
     echo "rest service stopped!"
}

# Restart the service rest
status() {
     echo "Status"
     echo "$(docker-compose -f docker-compose-rest.yml ps)"
     
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


