#!/usr/bin/env bash


COMPOSE_PROJECT=/home/edelivery/demo-ebsi-restapi/docker/ebsi
cd $COMPOSE_PROJECT

TOMCATC3='ebsi_tomcatRedc3_1'
TOMCATC2='ebsi_tomcatBluec2_1'

tailContainerLog()  { 
    echo "Tail container $1 logs for phrase '$2'"
    while IFS= read -r LOGLINE || [[ -n "$LOGLINE" ]]; do
        printf '%s\n' "$LOGLINE"
        [[ "${LOGLINE}" =~  "$2" ]] && return 0
        [[ "${LOGLINE}" =~  "ERROR" ]] && exit 1
    done < <(timeout 18m docker logs -f $1)
    
    return 3
}

cleanDeploymentFiles(){
    echo "Clean old deployments"
    rm -rf "${COMPOSE_PROJECT}"/c2/conf/domibus/
    rm -rf "${COMPOSE_PROJECT}"/c3/conf/domibus/
    rm -rf "${COMPOSE_PROJECT}"/c2/webapps/*
    rm -rf "${COMPOSE_PROJECT}"/c3/webapps/*
    mkdir -p "${COMPOSE_PROJECT}"/c2/conf/domibus/
    mkdir -p "${COMPOSE_PROJECT}"/c3/conf/domibus/

}

copyExtensionDeploymentFiles(){
    echo "Copy extension artefact to  ${COMPOSE_PROJECT}/c2/conf/domibus/extensions/lib"
    mkdir -p "${COMPOSE_PROJECT}"/c2/conf/domibus/extensions/lib
    mkdir -p "${COMPOSE_PROJECT}"/c2/conf/domibus/extensions/config
    mkdir -p "${COMPOSE_PROJECT}"/c3/conf/domibus/extensions/lib
    mkdir -p "${COMPOSE_PROJECT}"/c3/conf/domibus/extensions/config
    mkdir -p "${COMPOSE_PROJECT}"/c3/conf/domibus/policies
    mkdir -p "${COMPOSE_PROJECT}"/c3/conf/domibus/policies


    cp ${COMPOSE_PROJECT}/../../artefacts/Domibus-EBSI-extension-*-SNAPSHOT.jar "${COMPOSE_PROJECT}"/c2/conf/domibus/extensions/lib
    cp ${COMPOSE_PROJECT}/../../artefacts/conf/*.* "${COMPOSE_PROJECT}"/c2/conf/domibus/extensions/config/
    cp ${COMPOSE_PROJECT}/../../artefacts/policies/*.* "${COMPOSE_PROJECT}"/c2/conf/domibus/policies/

    cp ${COMPOSE_PROJECT}/../../artefacts/Domibus-EBSI-extension-*-SNAPSHOT.jar "${COMPOSE_PROJECT}"/c3/conf/domibus/extensions/lib
    cp ${COMPOSE_PROJECT}/../../artefacts/conf/*.* "${COMPOSE_PROJECT}"/c3/conf/domibus/extensions/config/
    cp ${COMPOSE_PROJECT}/../../artefacts/policies/*.* "${COMPOSE_PROJECT}"/c3/conf/domibus/policies/

}

initializeDomibus(){
    echo "Initialize domibus settings!"
    cp ${COMPOSE_PROJECT}/../../artefacts/policies/*.* "${COMPOSE_PROJECT}"/c2/conf/domibus/policies/
    cp ${COMPOSE_PROJECT}/../../artefacts/policies/*.* "${COMPOSE_PROJECT}"/c3/conf/domibus/policies/
    
    docker-compose -f docker-compose-ebsi-soapui.yml up --force-recreate 
}

# Start the service nginx
start() {
     # test if docker is already up
    if [[ $(docker ps -f "name=$TOMCATC2" --format '{{.Names}}') == $TOMCATC2 ]] ; then
        echo "Container '$TOMCATC2' is already running!"
        echo "$(docker-compose -f docker-compose-ebsi.yml ps)"
    else

        cleanDeploymentFiles
        copyExtensionDeploymentFiles
        echo "Start ebsi services"
        docker-compose -f docker-compose-ebsi.yml up -d
        echo "Start ebsi services started!"
        echo
        tailContainerLog $TOMCATC2 "INFO [main] org.apache.catalina.startup.Catalina.start Server startup in"
        # check also tomcat3
        tailContainerLog $TOMCATC3 "INFO [main] org.apache.catalina.startup.Catalina.start Server startup in"
        # and finnaly analize
        initializeDomibus
        
        
    fi

     
}

# Restart the service nginx
stop() {
     echo "Stop and clean ebsi domibus services"
     docker-compose -f docker-compose-ebsi.yml down --volumes
     echo "domibus services stopped!"
     docker system prune -f --volumes

}

# Status of the ebsi services
status() {
     echo "Status"
     echo "$(docker-compose -f docker-compose-ebsi.yml ps)"
     
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


