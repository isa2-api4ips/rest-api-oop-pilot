# REST API Pilot Demo on Docker
This is REST API Demo sample Docker build files to facilitate installation, configuration, and environment setup for DevOps users. The demo docker uses base image **jboss/wildfly:23.0.2.Final** for and the h2 database.

Note: the docker image is not production ready, and it should be used only for DEMO and development purposes.

This project offers sample Dockerfile for the REST API project with deployed following artefacts:

 - **DSD MOCK component**: (dsd-mock-${project.version}.war)
 - **Nation Broker component**: (national-broker-${project.version}.war)
 - **Sample UI-Client**: (national-broker-ui-client-${project.version}.war)

#How to build and run

To assist in building the images, you can use the **`build.sh`** script. See below for instructions and usage.

The `build.sh` script is just a utility shell script providing an easy way for beginners to get started. Expert users are welcome to directly call `docker build` with their preferred set of parameters.

##Building REST API Pilot Demo Docker Install Images

To build REST API pilot Demo docker image, first project artefacts must be build. Detailed instruction on how to build DSD-MOCK, National-Broker and IU client are explained  here: Getting started with REST API Pilot project

Then open folder demo and start build.sh script:

    # build maven artifacts
    cd demo
    # build docker image
    ./build.sh# After the build we can see the image domibustest/rest-api-demo in local registry
    
    docker image ls    
    REPOSITORY TAG IMAGE ID CREATED SIZE
    domibustest/rest-api-demo 1.0.0-SNAPSHOT 8b8f33550d75 10 seconds ago 1.23GB

##Start REST API Pilot Demo Docker image

Bellow is command how to start docker image

    #Start docker container
    docker run --name rest-api-demo -p 8080:8080  domibustest/rest-api-demo:1.0.0-SNAPSHOT


In case if we want to set properties outside the docker container we can bind configurations folder to local host
Start docker container with external configuration
    
    # create DSD configuration folder
    mkdir dsd-conf
    # copy configuration properties example
    cp ${isa2-ips-rest-api}/dsd-mock/src/main/resources/dsd.properties ./dsd-conf/
 
    # create national broker configuration folder
    mkdir broker-conf
    # copy configuration properties example
    cp ${isa2-ips-rest-api}/national-broker/src/main/resources/national_broker.properties ./broker-conf/
 
 
    docker run --name rest-api-demo \
            -p 8080:8080  \
            -v "dsd-conf:/opt/jboss/wildfly/standalone/data/dsd/config" \
            -v "broker-conf:/opt/jboss/wildfly/standalone/data/national-broker/config"  \
             domibustest/rest-api-demo:1.0.0-SNAPSHOT


Endpoints:
 - UI-Client: http://localhost:8080/ui-client/
 - national-broker swagger ui: http://localhost:8080/national-broker/swagger-ui.html
 - dsd mock swagger ui:  http://localhost:8080/dsd-mock/index.html