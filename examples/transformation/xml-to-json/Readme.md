Fuse Transformation - XML to JSON
=================================
This application provides a complete example of a data transformation from XML to JSON.  The input and output models have different structures, so this is more than a simple data binding problem.

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

Copy the src/data/abc-order.xml file into the /tmp/inbox directory and check for output JSON in the /tmp/outbox directory.  Note that you will need to kill the Java process (^C in terminal) when using "mvn camel:run", which is the default behavior of this plugin.

####Notable Bits
[Input XML](https://github.com/fusesource/fuseide/blob/master/examples/transformation/xml-to-json/src/data/abc-order.xml)

[Input XSD](https://github.com/fusesource/fuseide/blob/master/examples/transformation/xml-to-json/src/main/resources/abc-order.xsd)

[Generated Java Input](https://github.com/fusesource/fuseide/blob/master/examples/transformation/xml-to-json/src/main/java/abcorder)

[Generated Java Output](https://github.com/fusesource/fuseide/blob/master/examples/transformation/xml-to-json/src/main/java/xyzorderschema)

[Camel Config](https://github.com/fusesource/fuseide/blob/master/examples/transformation/xml-to-json/src/main/resources/META-INF/spring/camel-context.xml)

[Dozer Config](https://github.com/fusesource/fuseide/blob/master/examples/transformation/xml-to-json/src/main/resources/dozerBeanMapping.xml)

[Output JSON Schema](https://github.com/fusesource/fuseide/blob/master/examples/transformation/xml-to-json/src/main/resources/xyz-order-schema.json)

[Output JSON](https://github.com/fusesource/fuseide/blob/master/examples/transformation/xml-to-json/src/data/xyz-order.json)
