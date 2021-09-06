#!/usr/bin/env bash


COMPOSE_PROJECT=/home/edelivery/demo


echo "Copy extension artefact to  ${COMPOSE_PROJECT}/c2/conf/domibus/extensions/lib"
mkdir -p "${COMPOSE_PROJECT}"/c2/conf/domibus/extensions/lib
mkdir -p "${COMPOSE_PROJECT}"/c2/conf/domibus/extensions/config

cp Domibus-EBSI-extension-*-SNAPSHOT.jar "${COMPOSE_PROJECT}"/c2/conf/domibus/extensions/lib
cp conf/*.* "${COMPOSE_PROJECT}"/c2/conf/domibus/extensions/config/

    # deploy extension
 echo "Copy ebsi webadmin artefact to  ${COMPOSE_PROJECT}/c3/deployments/"
mkdir -p "${COMPOSE_PROJECT}"/c3/deployments
cp ebsi-webadmin.war "${COMPOSE_PROJECT}"/c3/deployments/


