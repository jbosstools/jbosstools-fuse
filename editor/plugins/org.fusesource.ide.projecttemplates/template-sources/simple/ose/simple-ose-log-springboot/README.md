# Spring-Boot Camel XML QuickStart

This example demonstrates how to configure Camel routes in Spring Boot via
a Spring XML configuration file.

The application utilizes the Spring [`@ImportResource`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/ImportResource.html) annotation to load a Camel Context definition via a [camel-context.xml](src/main/resources/spring/camel-context.xml) file on the classpath.

### Building

The example can be built with

    mvn clean install


### Running the example locally

The example can be run locally using the following Maven goal:

    mvn spring-boot:run


### Running the example on OpenShift from command-line

It is assumed a running OpenShift platform is already running.

Assuming your current shell is connected to OpenShift so that you can type a command like

```
oc get pods
```

Then the following command will package your app and run it on OpenShift:

```
mvn fabric8:run
```

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

### Running via an S2I Application Template from the command-line

Application templates allow you deploy applications to OpenShift by filling out a form in the OpenShift console that allows you to adjust deployment parameters.  This template uses an S2I source build so that it handles building and deploying the application for you.

First, import the Fuse image streams:

    oc create -f https://raw.githubusercontent.com/jboss-fuse/application-templates/fis-2.0.x.redhat/fis-image-streams.json

Then create the quickstart template:

    oc create -f https://raw.githubusercontent.com/jboss-fuse/application-templates/fis-2.0.x.redhat/quickstarts/spring-boot-camel-xml-template.json

Now when you use the "Add to Project" button in the OpenShift console, you should see a template for this quickstart. 
