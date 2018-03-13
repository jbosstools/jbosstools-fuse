# Wsdl2Rest Wizard Code
Last update: 8-DEC-2017

This plug-in provides a wizard wrapper for the Wsdl2Rest code originally found here: [https://github.com/jboss-fuse/wsdl2rest](https://github.com/jboss-fuse/wsdl2rest)

## Running the wizard
In the project you will find an "example" directory that includes several files - a couple of classes and a wsdl file.

Create a new Fuse Integration project. Leave it as a Blank project that uses the Spring DSL.
Create a new src/main/resources/wsdl directory.
Copy the echoService.wsdl file into the src/main/resources/wsdl directory.

Select the project, click New->Other, and select the JBoss Fuse->WSDL to Camel Rest DSL Wizard. 

On the "Select Incoming WSDL and Project for Generated Output" page, click the "..." button beside the WSDL File field and select echoService.wsdl.
Click Next.
On the "Specify Advanced Options for wsdl2rest Processing", click the "..." beside the Bean Class field and select the EchoServiceImpl class. 
Click Finish.

Select the project and refresh it to see the generated code and new rest-camel-context.xml file. 

The generated echo.Echo class will have many errors due to some missing dependencies.

Add the following dependencies to your project pom.xml:

    <dependency>
      <groupId>org.jboss.spec.javax.ws.rs</groupId>
      <artifactId>jboss-jaxrs-api_2.0_spec</artifactId>
      <version>1.0.0.Final-redhat-1</version>
    </dependency>
    <dependency>
      <groupId>org.apache.camel</groupId>
      <artifactId>camel-jackson</artifactId>
      <version>2.20.1</version>
    </dependency>

This should resolve the errors. 
