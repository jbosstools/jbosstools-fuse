${symbol_hash} wildfly-camel-spring

This is a template Apache Camel Spring application for the WildFly-Camel subsystem. 

This project is setup to allow you to create a Apache Camel Spring application, which can be deployed to an application
server running the WildFly-Camel subsystem. An example Spring XML Camel Route has been created for you, together with an Arquillian
integration test.

${symbol_hash}${symbol_hash} Prerequisites

* Minimum of Java 1.7
* Maven 3.2 or greater
* WildFly application server version ${version-wildfly}

${symbol_hash}${symbol_hash} Getting started

1. Install WildFly-Camel subsystem distribution version 1.0.0-SNAPSHOT on your application server

2. Conifgure a `$JBOSS_HOME` environment variable to point at your application server installation directory

3. Start the application server from the command line

For Linux:

`$JBOSS_HOME/bin/standalone.sh -c standalone-camel.xml`

For Windows:

`%JBOSS_HOME%\bin\standalone.bat -c standalone-camel.xml`

${symbol_hash}${symbol_hash}${symbol_hash} Building the application

To build the application do:

`mvn clean install`

${symbol_hash}${symbol_hash}${symbol_hash} Run Arquillian Tests
    
By default, tests are configured to be skipped as Arquillian requires the use of a container.

If you already have a running application server, you can run integration tests with:

`mvn clean test -Parq-remote`

Otherwise you can get Arquillian to start and stop the server for you (Note: you must have `JBOSS_HOME` configured beforehand):

`mvn clean test -Parq-managed`

${symbol_hash}${symbol_hash}${symbol_hash} Deploying the application

To deploy the application to a running application server do:

`mvn clean package wildfly:deploy` 

The server console should display lines like the following:

```
(MSC service thread 1-16) Apache Camel (CamelContext: spring-context) is starting
(MSC service thread 1-16) Camel context starting: spring-context
(MSC service thread 1-6) Bound camel naming object: java:jboss/camel/context/spring-context
(MSC service thread 1-16) Route: route4 started and consuming from: Endpoint[direct://start]
(MSC service thread 1-16) Total 1 routes, of which 1 is started
```

${symbol_hash}${symbol_hash}${symbol_hash} Access the application

The application will be available at <http://localhost:8080/wildfly-camel-spring?name=Kermit>

${symbol_hash}${symbol_hash}${symbol_hash} Undeploying the application

`mvn wildfly:undeploy`

${symbol_hash}${symbol_hash} Further reading

* [WildFly-Camel documentation] (https://www.gitbook.com/book/wildflyext/wildfly-camel)
* [Apache Camel documentation] (http://camel.apache.org/)
