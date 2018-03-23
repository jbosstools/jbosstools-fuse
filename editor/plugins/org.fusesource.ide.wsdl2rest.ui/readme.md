# Wsdl2Rest Wizard Code
Last update: 23-MAR-2018

This plug-in provides a wizard wrapper for the Wsdl2Rest code originally found here: [https://github.com/jboss-fuse/wsdl2rest](https://github.com/jboss-fuse/wsdl2rest)

## Running the wizard
In the project you will find an "example" directory that includes several files - a couple of classes and a wsdl file.

1. Create a new Fuse Integration project. Leave it as a Blank project that uses the Spring DSL.
2. Create a new src/main/resources/wsdl directory.
3. Copy the HelloService.wsdl file into the src/main/resources/wsdl directory.
4. Select the project, click New->Other, and select the JBoss Fuse->WSDL to Camel Rest DSL Wizard. 
5. On the "Select Incoming WSDL and Project for Generated Output" page, click the "..." button beside the WSDL File field and select HelloService.wsdl.
6. Click Next. 
7. BUG - change the "Destination Java Folder" path to not include /java at the end. It gets appended during the code generation phase right now.
8. Click Finish. The Camel file and associated Java classes will be generated and the project will refresh. 

Once the wizard finishes, you should see a new Camel Context file - rest-camel-context.xml - in the src/main/resources directory as well as the generated code, which will appear in the /src/main/java folder.

## Current Limitations
* Only works with Spring DSL projects currently.
* Must adjust "Destination Java Folder" to eliminate the /java part of the path, which is appended during code generation.
