Fuse Transformation - JSON to XML
=================================
This application provides a complete example of a data transformation from JSON to XML.  The input and output models have different structures, so this is more than a simple data binding problem.

**Important note on configuration**: this application contains two Camel configuration files:
* META-INF/spring/camel-context.xml : Spring-based configuration of a Camel application
* OSGI-INF/blueprint/camel-blueprint.xml : OSGi Blueprint-based configuration of a Camel application

Why two configuration files?  We want to demonstrate that the mapper works with both types of configuration.  If you want to use Spring, then select the Spring configuration in the Data Mapping wizard.  Once you are done with your mapping, you can test it out using ``mvn camel:run``.  If you want to deploy the application to Karaf, then reference the OSGi Blueprint configuration in the Data Mapping wizard and deploy using the features.xml present in the project.


####Running the App
To build this project use
```
mvn install
```
To run this project with Maven use
```
mvn camel:run
```

Copy the src/data/abc-order.json file into the /tmp/inbox directory and check for output xml in the /tmp/outbox directory.  Note that you will need to kill the Java process (^C in terminal) when using "mvn camel:run", which is the default behavior of this plugin.

