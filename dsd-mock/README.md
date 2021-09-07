#Deployment

1. Build artefacts

        cd ${PROJECT_CODE}
        mvn clean install

1. Run wildfly configuration

        ${JBOSS_HOME}/bin/jboss-cli.sh --file=${PROJECT_CODE}/src/main/setup/config/wildfly/domibus-configuration-H2.cli


1. Deploy application

        cp ${PROJECT_CODE}/dsd-mock/target/national_broker-1.0-SNAPSHOT.war  ${JBOSS_HOME}/standalone/deployments/

1. Startup application

        ./standalone.sh -b 0.0.0.0 -c standalone-full.xml

Startup application and initialize database 
        
        ./standalone.sh -b 0.0.0.0 -c standalone-full.xml -Ddsd.database.create=true -Ddsd.database.script=${PROJECT_CODE}/dsd-mock/src/main/setup/database-scripts/h2-data.sql
        ex.:
        ./standalone.sh -b 0.0.0.0 -c standalone-full.xml -Ddsd.database.create=true -Ddsd.database.script=/cef/code/oop_rest_api_pilot/dsd-mock/src/main/setup/database-scripts/h2-data.sql


#Sample URL with context root for Wildfly
http://localhost:13003/nationalbroker

##Sample URL to access H2 DB console
http://localhost:13003/nationalbroker/h2_console


#DSD MOCK OpenAPI generation

NOTE: Version 3.1 - 3.0.99
NOTE: reference by object 3.1 - 3.0.99
Objects are referenced directly
"$ref" : "https://raw.githubusercontent.com/jrihtarsic/OpenApiTest/main/profile/specifications/core-api/problem.json"
and not from common 
"$ref" : "./core-api-objects.json#/components/schemas/Problem"

see the issue
https://github.com/swagger-api/swagger-parser/issues/1311
Parameters @id and @schema


