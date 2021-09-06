# Getting started with REST API Demo

##  1. INTRODUCTION
The REST API Demo is the result of the REST API pilot project with purpose to test, validate and demonstrate REST API specificatioins
on ISA2 example use-cases.   

## 1.1. PURPOSE OF THIS DOCUMENT

This page provides a brief description of the REST API project artefact.


## 2. PROJECT

The REST API project contains following components/(maven modules):

- **dsd-mock**:  is example DSD component project. Component contains OpenAPI interface  complaint with  Messaging API specification. 
- **national-broker**:  is example implementation of the national broker. Component has OpenAPI interface  complaint with  Core API specification. 
- **ui-client**:  example of the Client application which uses national-broker services.
- **isa2-messaging-profile-impl**: - The messaging profile-specific code. The messaging profile-specific code. In case if the messaging API project will evolve further, this can become a library that would enable fast and easy development of "Messaging API applications".
- **oop-common**:  implementation of shared "utils" classes.
- **demo**: the 

In order to build and run projects the following tools/applications  must be installed

- **Java JDK1.8** (tested with JDK1.8 and JDK11)
- **Maven  3.6+**
- **docker 19.03+**: (needed to build and run demo environment)
- **docker-compose  1.24+**: (needed to build and run demo environment)
- **linux bash**: to build demo docker image using example build.sh script, an standard Linux shell/a command interpreter is needed.

## 2.1. ARTIFACTS

- **./dsd-mock/target/dsd-mock-${project.version}.war**:  is example implementation of the DSD component implementation. 
- **./national-broker/target/national-broker-${project.version}.war**:  is example implementation of the national broker. 
- **./ui-client/target/national-broker-ui-client-${project.version}.war**:  example of the Client application which uses national-broker services.
- **./isa2-messaging-profile-impl/target/isa2-messaging-profile-impl-${project.version}.jar**: - The messaging profile specification library.
- **./oop-common/target/oop-common-${project.version}.jar**:  implementation of project's shared "utils" classes.
- **rest-api-demo:${project.version}**: rest api demo image.


## 2.2. SOURCE CODE

The GIT repository on URL: https://ec.europa.eu/cefdigital/code/scm/edelivery/isa2-ips-rest-api.git

The bitbucket web UI: https://ec.europa.eu/cefdigital/code/projects/EDELIVERY/repos/isa2-ips-rest-api/browse


## 3. SETTING UP PARAMETERS
The modules: dsd-mock and national-broker have set default parameters in files:
    
    dsd-mock/src/main/resources/dsd.properties
and
    
    national-broker/src/main/resources/national_broker.properties
    
The parameters can be overwritten by setting the java system parameter as example: `-Ddsd.database.create=true -Ddsd.database.script=/tmp/dsd-h2-data.sql`
or by setting the property file to: `file:///${dsd.config.location}/dsd.properties` and `file:///${nationalbroker.config.location}/national_broker.properties`.
The default values for property location is set as:

    dsd.data.location=${JBOSS_HOME}/standalone/data/dsd/
    dsd.config.location=${dsd.data.location}/config
and 

    nationalbroker.data.location=${JBOSS_HOME}/standalone/data/national-broker/
    nationalbroker.config.location=${nationalbroker.data.location}/config
    

