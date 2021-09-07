#!/bin/bash

echo "---------------------------"
echo "-- OOP Demo entry point  --"
echo "---------------------------"
echo "JBOSS_HOME: ${JBOSS_HOME}"
echo "JAVA_HOME: ${JAVA_HOME}"
echo "VERSION: ${VERSION}"

SERVER_OPTS="${SERVER_OPTS} -Dnationalbroker.cross-origin.urls=*"
SERVER_OPTS="${SERVER_OPTS} -Ddsd.database.create=true -Ddsd.database.script=/tmp/dsd-h2-data.sql"
SERVER_OPTS="${SERVER_OPTS} -Dnationalbroker.database.create=true -Dnationalbroker.database.script=/tmp/national-broker-h2-data.sql"
SERVER_OPTS="${SERVER_OPTS} -Ddsd.hibernate.format_sql=false -Ddsd.hibernate.show_sql=false  -Dnationalbroker.hibernate.format_sql=false -Dnationalbroker.hibernate.show_sql=false"

echo "Start with server configuration ${SERVER_OPTS}"
JAVA_OPTS="${JAVA_OPTS} -Xms512m -Xmx2G -XX:MaxMetaspaceSize=512m"
echo "Start with JAVA_OPTS configuration ${JAVA_OPTS}"
export JAVA_OPTS

"${JBOSS_HOME}"/bin/standalone.sh ${SERVER_DEBUG:+--debug ${JDK11_RUNTIME:+*:8787}} --server-config="${SERVER_CONFIG}".xml -b=0.0.0.0 ${SERVER_OPTS} -DJAVA_OPTS="${JAVA_OPTS}"


