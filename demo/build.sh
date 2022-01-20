#!/usr/bin/env bash
# declare variables
DOCKER="$(cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"
OOP_FOLDER=${DOCKER}/..
VERSION=$(grep -Eom1 "<version>[^<]+" < "${OOP_FOLDER}/pom.xml" | sed "s/<version>//")

# export variables for docker image
export DOCKER
export VERSION
# Print variables
echo "DOCKER: ${DOCKER}"

echo "VERSION: ${VERSION}"

# clean artefacts
rm -rf ${DOCKER}/resources/artefacts
rm -rf ${DOCKER}/resources/scripts
rm -rf ${DOCKER}/resources/config
mkdir -p ${DOCKER}/resources/artefacts
mkdir -p ${DOCKER}/resources/scripts
mkdir -p ${DOCKER}/resources/config


# copy artefacts bundles
cp ${OOP_FOLDER}/dsd-mock/target/dsd-mock-${VERSION}.war ${DOCKER}/resources/artefacts
cp ${OOP_FOLDER}/national-broker/target/national-broker-${VERSION}.war ${DOCKER}/resources/artefacts
cp ${OOP_FOLDER}/national-broker-ui-client/target/national-broker-ui-client-${VERSION}.war ${DOCKER}/resources/artefacts
# DEPLOY EBSI DEMO APPLICATION FOR THE DEMO!!!
#cp /cef/code/ebsi-client/example/ebsi-did-webadmin/target/ebsi-webadmin.war ${DOCKER}/resources/artefacts

# copy scripts
cp ${OOP_FOLDER}/dsd-mock/src/main/setup/config/wildfly/dsd-configuration-H2.cli ${DOCKER}/resources/scripts/dsd-configuration-H2.cli
cp ${OOP_FOLDER}/dsd-mock/src/main/setup/database-scripts/h2-data.sql ${DOCKER}/resources/scripts/dsd-h2-data.sql
cp -r ${OOP_FOLDER}/dsd-mock/src/main/setup/config/dsd ${DOCKER}/resources/config
cp ${OOP_FOLDER}/national-broker/src/main/setup/config/wildfly/national-broker-configuration-H2.cli ${DOCKER}/resources/scripts/national-broker-configuration-H2.cli
cp ${OOP_FOLDER}/national-broker/src/main/setup/database-scripts/h2-data.sql ${DOCKER}/resources/scripts/national-broker-h2-data.sql
cp -r ${OOP_FOLDER}/national-broker/src/main/setup/config/national-broker ${DOCKER}/resources/config

cd ${DOCKER}
docker-compose -f docker-compose.build.yml build
