#Deployment

1. Build artefacts

        cd ${PROJECT_CODE}
        mvn clean install

1. Run wildfly configuration

        ${JBOSS_HOME}/bin/jboss-cli.sh --file=${PROJECT_CODE}/national-broker/config/wildfly/domibus-configuration-H2.cli

1. Deploy application

        cp ${PROJECT_CODE}/national-broker/target/national-broker-1.0-SNAPSHOT.war  ${JBOSS_HOME}/standalone/deployments/

1. Startup application

        ./standalone.sh -b 0.0.0.0 -c standalone-full.xml

Startup application and initialize dtabase 
        
        ./standalone.sh -b 0.0.0.0 -c standalone-full.xml -Dnationalbroker.database.create=true -Dnationalbroker.database.script=${PROJECT_CODE}/national_broker/src/main/setup/database-scripts/h2-data.sql
        ex.:
        ./standalone.sh -b 0.0.0.0 -c standalone-full.xml -Dnationalbroker.database.create=true -Dnationalbroker.database.script=/cef/code/oop_rest_api_pilot/national-broker/src/main/setup/database-scripts/h2-data.sql


#Sample URL with context root for Wildfly
http://localhost:8080/national-broker

##Sample URL to access H2 DB console
http://localhost:8080/national-broker/h2_console



