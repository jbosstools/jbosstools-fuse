Your first clone
================

Once you have cloned this repo via

    git clone ssh://git@forge.fusesource.com/ridersource.git
    cd ridersource

Before you build make sure you have the subscriber repo in your ~/.m2/settings.xml...

<servers>
  <server>
    <id>fusesource.subscriber</id>
    <username>USERNAME</username>
    <password>PASSWORD</password>
  </server>
  <server>
    <id>fusesource.subscriber-snapshot</id>
    <username>USERNAME</username>
    <password>PASSWORD</password>
  </server>
  <server>
    <id>fusesource-nexus-subscriber</id>
    <username>USERNAME</username>
    <password>PASSWORD</password>
  </server>
  <server>
    <id>fusesource-nexus-subscriber-snapshot</id>
    <username>USERNAME</username>
    <password>PASSWORD</password>
  </server>
</servers>
<mirrors>
  <mirror>
    <id>fusesource-proxy</id>
    <mirrorOf>*</mirrorOf>
    <url>http://repo.fusesource.com/nexus/content/groups/m2-proxy</url>
  </mirror>
</mirrors>


Now try building it...

    cd eclipse-tooling
    mvn install

Tycho downloads all the eclipse/p2 stuff to: ~/.m2/repository/p2/

Then to open in Eclipse. Before you start make sure you are on 3.6.x or later of Eclipse.
3.6.x Eclipse PDE generates some compile errors which are not really errors - these are fixed in 3.7.x of Eclipse.
Notice Claus had problem with Eclipse Helios 3.6.2 so he downloaded Eclipse 3.7 M7 which worked. James is currently on 3.7 RC4 without issues.

Here's how to setup eclipse...

* set your eclipse workspace dir to the eclipse-tooling dir
* import the project into eclipse from this directory (import... -> general -> existing project)
* open the Target Platform: org.fusesource.ide.targetplatform/rider.target
* click the "Set as Target Platform" link in the top right


To remove Access Restriction errors
-----------------------------------
aha - to get rid of the final Access restriction warnings

  Eclipse -> Preferences -> Java -> Compiler / Errors/Warnings -> Deprecated and restricted API

then turn Forbidden reference (access rules) to Warning from Error


Building
========

NOTE - typically you don't need to worry about the Code Generation step described below.

To build you'll need Maven 3.x. You also might want to update your ~/.m2/settings.xml file to be like the
etc/settings.xml file (replacing the USERNAME and PASSWORD values).

Ideally you'll perform a build in the root ridersource directory. Then

  cd eclipse-tooling
  maven clean install

In Eclipse, select all your resources and refresh (F5).

You should be able to then open the file:

  org.fusesource.ide.targetplatform/rider.target

Then on the top right select "Set as Target Platform".

If in doubt, do a clean build too.

To run click the Run arrow icon in the toolbar then click Rider
Notice Claus could not run "Rider" but the "Rider with Java" worked fine.


Testing the Update Site
=======================

To test the update site contents has everything you need here's what I did:

* create a new Blank Launch config which includes just a base minimal Eclipse install and enable software install
* run this Blank Application, then try install using the update site of
* file://SOMETHING/ridersource/eclipse-tooling/org.fusesource.ide.updatesite/target/site

where SOMETHING is the dir you checked out ridersource into


Where stuff gets downloaded in Tycho builds
===========================================

If you kill a Tycho build you can sometimes get corrupted downloads leading to failed builds next time you try.

Usually with a Zip exception like this...

    java.util.zip.ZipException: error in opening zip file
      at java.util.zip.ZipFile.open(Native Method)
      at java.util.zip.ZipFile.<init>(ZipFile.java:114)
      at org.codehaus.tycho.osgitools.DefaultBundleReader.doLoadManifest(DefaultBundleReader.java:85)
      at org.codehaus.tycho.osgitools.DefaultBundleReader.loadManifest(DefaultBundleReader.java:47)
      at org.codehaus.tycho.osgitools.EquinoxResolver.loadManifest(EquinoxResolver.java:199)
      at org.codehaus.tycho.osgitools.EquinoxResolver.addBundle(EquinoxResolver.java:175)
      at org.codehaus.tycho.osgitools.EquinoxResolver.newState(EquinoxResolver.java:157)
      at org.codehaus.tycho.osgitools.EquinoxResolver.newResolvedState(EquinoxResolver.java:52)

The quick fix is to trash your ~/.m2/repository/p2 directory!




Code Generation (NO NEED FOR THIS STEP UNLESS CAMEL VERSIONS CHANGE)
====================================================================

NOTE this step is only required to be done if the Camel model changes or the documentation changes!
The generated files are checked into git - so if you have a git clone you can miss this out!

This step is only required if you upgrade the version of Camel, change the code generation scripts or update the model metadata.

From the root directory type:

  mvn install
  cd ide-codegen
  mvn compile exec:java

The generator then runs and updates the IDE source.


Regenerating the archetypes and downloading the latest XSDs
===========================================================

To download the latest archetypes and XSDs:

    cd ide-buildtools
    mvn compile exec:java

Be sure to have the file 

    <userhome>/.repo.fusesource.com.properties

which should contain the credentials for the FuseSource Nexus like
    username=<your login>
    password=<your password>

Without this file the build will not work!
