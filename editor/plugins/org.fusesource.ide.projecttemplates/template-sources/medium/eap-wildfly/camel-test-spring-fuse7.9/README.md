Camel CDI Example
-----------------

This example demonstrates using the camel-cdi component with Red Hat Fuse on EAP to integrate CDI beans with Camel routes.

In this example, a Camel route takes a message payload from a servlet HTTP GET request and passes it on to a direct endpoint. The payload
is then passed onto a Camel CDI bean invocation to produce a message response which is displayed on the web browser page.

Prerequisites
-------------

* Maven
* An application server with Red Hat Fuse installed

Running the example
-------------------

To run the example.

1. Start the application server in standalone mode:

    For Linux:

        ${JBOSS_HOME}/bin/standalone.sh -c standalone-full.xml

    For Windows:

        %JBOSS_HOME%\bin\standalone.bat -c standalone-full.xml

2. Build and deploy the project `mvn install -s configuration/settings.xml -Pdeploy`

Testing Camel CDI
-----------------

Web UI
------

Browse to http://localhost:8080/example-camel-cdi/?name=Kermit.

You should see the message "Hello Kermit" output on the web page.

The Camel route is very simple and looks like this:


    from("direct:start").beanRef("helloBean");


The `beanRef` DSL makes camel look for a bean named 'helloBean' in the bean registry. The magic that makes this bean available to Camel is found in the `SomeBean` class.

    @Named("helloBean")
    public class SomeBean {

        public String someMethod(String message) {
            return "Hello " + message;
        }
    }

By using the `@Named` annotation, camel-cdi will add this bean to the Camel bean registry.

## Undeploy

To undeploy the example run `mvn clean -Pdeploy`.

Deploying to OpenShift
----------------------

Prerequisites
-------------

* Fuse Integration Services (FIS) image streams have been installed
* Fuse Integration Services application templates have been installed

Deploying from the OpenShift console
------------------------------------

When logged into the OpenShift console, browse to the 'Add to Project' screen, from the Browse Catalog tab, click Java to open the list of Java templates and then
choose the Red Hat Fuse category.

Find the s2i-fuse71-eap-camel-cdi template and click the Select button. You can accept the default values and click 'Create'. The Application created screen now opens. Click Continue to overview
to go to the Overview tab of the OpenShift console. In the 'Builds' section you can monitor progress of the s2i-fuse71-eap-camel-cdi S2I build.

When the build has completed successfully, click Overview in the left-hand navigation pane to view the running pod for this application. You can test
the application by clicking on application URL link displayed at the top right of the pod overview. For example:

    http://s2i-fuse71-eap-camel-cdi-redhat-fuse.192.168.42.51.nip.io

Note: You can find the correct host name with 'oc get route s2i-fuse71-eap-camel-cdi'

Deploying from the command line
-------------------------------

You can deploy this quickstart example to OpenShift by triggering an S2I build by running the following:

    oc new-app s2i-fuse71-eap-camel-cdi

You can follow progress of the S2I build by running:

    oc logs -f bc/s2i-fuse71-eap-camel-cdi

When the S2I build is complete and the application is running you can test by navigating to route endpoint. You can find the application route
hostname via 'oc get route s2i-fuse71-eap-camel-cdi'. For example:

    http://s2i-fuse71-eap-camel-cdi-redhat-fuse.192.168.42.51.nip.io

Cleaning up
-------------------------------

You can delete all resources created by the quickstart application by running:

    oc delete all -l 'app=s2i-fuse71-eap-camel-cdi'
