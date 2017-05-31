Fuse Transformation - Starter App
=================================
This project serves as our baseline application for adding mapping features to the runtime and tooling for Fuse and FSW.  This project contains only the required source artifacts for creating a complete example demonstrating transformation from XML to JSON.

**Important note on configuration**: this application contains two Camel configuration files:
* META-INF/spring/camel-context.xml : Spring-based configuration of a Camel application
* OSGI-INF/blueprint/camel-blueprint.xml : OSGi Blueprint-based configuration of a Camel application

Why two configuration files?  We want to demonstrate that the mapper works with both types of configuration.  If you want to use Spring, then select the Spring configuration in the Data Mapping wizard.  Once you are done with your mapping, you can test it out using ``mvn camel:run``.  If you want to deploy the application to Karaf, then reference the OSGi Blueprint configuration in the Data Mapping wizard and deploy using the features.xml present in the project.


####Notable Bits
[Input XML](https://github.com/fusesource/fuseide/blob/master/examples/transformation/starter/src/data/abc-order.xml)

[Input XSD](https://github.com/fusesource/fuseide/blob/master/examples/transformation/starter/src/main/resources/abc-order.xsd)

[Output JSON Schema](https://github.com/fusesource/fuseide/blob/master/examples/transformation/starter/src/main/resources/xyz-order.json)

[Output JSON](https://github.com/fusesource/fuseide/blob/master/examples/transformation/starter/src/data/xyz-order.json)
