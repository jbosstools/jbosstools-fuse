Camel Router Project for Java 
=========================================

To build this project use

    mvn install

To run the project you can execute the following Maven goal

    mvn exec:java

To deploy the project in OSGi. For example using Apache Karaf.
You can run the following command from its shell:

    osgi:install -s mvn:com.mycompany/camel-java-cbr/1.0.0-SNAPSHOT

For more help see the Apache Camel documentation

    http://camel.apache.org/
