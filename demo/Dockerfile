FROM jboss/wildfly:23.0.2.Final

ARG VERSION


ENV VERSION=${VERSION} \
    SERVER_CONFIG=${SERVER_CONFIG:-standalone-full} \
    CURR_USER=${USER:-jboss}


COPY ./resources/artefacts/dsd-mock-${VERSION}.war /opt/jboss/wildfly/standalone/deployments/
COPY ./resources/artefacts/national-broker-${VERSION}.war /opt/jboss/wildfly/standalone/deployments/
COPY ./resources/artefacts/national-broker-ui-client-${VERSION}.war /opt/jboss/wildfly/standalone/deployments/
COPY ./resources/config /opt/jboss/wildfly/standalone/data/
# deploy ebsi client for the demo include this only for EBSI Demonstration !!
#COPY ./resources/artefacts/ebsi-webadmin.war /opt/jboss/wildfly/standalone/deployments/

USER root
RUN  chown -R $CURR_USER:$CURR_USER /opt/jboss/wildfly

USER $CURR_USER


COPY ./resources/entrypoint.sh /opt/jboss/wildfly/bin
COPY ./resources/scripts /tmp/
RUN  ${JBOSS_HOME}/bin/jboss-cli.sh --file=/tmp/dsd-configuration-H2.cli
RUN  ${JBOSS_HOME}/bin/jboss-cli.sh --file=/tmp/national-broker-configuration-H2.cli
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/current/*.*

CMD ["/opt/jboss/wildfly/bin/entrypoint.sh"]
