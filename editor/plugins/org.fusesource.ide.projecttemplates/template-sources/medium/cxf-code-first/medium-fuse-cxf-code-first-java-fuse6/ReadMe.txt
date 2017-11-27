Camel Project for Apache CXF code-first using Java DSL
=========================================================================

To build this project use

    mvn install

To deploy the project in OSGi. For example using Apache Karaf.
You need to install the following features first:

    features:install camel-jaxb
    features:install camel-cxf

And then you can install this example from its shell:

    osgi:install -s mvn:com.mycompany/camel-java-cxf-code-first/1.0.0-SNAPSHOT

The web services from Apache CXF is usually listed at:

    http://localhost:9292/cxf

And the WSDL file for this example at:

    http://localhost:9292/cxf/report/?wsdl

For more help see the Apache Camel documentation

    http://camel.apache.org/
