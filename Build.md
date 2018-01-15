# Build Guide

This document describes how to get and build the sources. It also covers how to setup the Eclipse workspace to run _JBoss Fuse Tooling_ from inside a vanilla Eclipse.

## Get the code

The easiest way to get started with the code is to [create your own fork](http://help.github.com/forking/) at github, and then clone your fork:

    git clone git@github.com:<you>/jbosstools-fuse.git
    cd jbosstools-fuse
    git remote add upstream https://github.com/jbosstools/jbosstools-fuse.git

At any time, you can pull changes from the upstream and merge them onto your master:

    git checkout master               # switches to the 'master' branch
    git pull --rebase upstream master # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
    git push origin                   # pushes all the updates to your fork, which should be in-sync with 'upstream'

The general idea is to keep your 'master' branch in-sync with the 'upstream/master'.

## Building JBoss Fuse Tooling

To build _JBoss Fuse Tooling_ requires specific versions of Java (1.6+) and +Maven (3.0+).

This command will run the build:

    mvn clean verify

If you just want to check if things compiles/builds you can run:

    mvn clean verify -DskipTests

## Generating the target platform

If you want to do development in Eclipse for Fuse Tooling you need to use the JBDS Integration Stack Target Platform.

Here's how to retrieve the target files describing the Target Platform...

    cd jbosstools-fuse/targetplatform
    mvn clean verify -Pmultiple2repo -Dmirror-target-to-repo.includeSources=true

Once the build is done you end up with a file *fuse-multiple.target* inside the *target* subfolder. Now open this file with Target File editor from Eclipse IDE and click "Set as target Platform" at the top right. Be patient... and it will be ready.

## Eclipse Setup

At _master_ branch we always try to use the latest Eclipse version. Please refer to the target platform plugin to see which versions of Eclipse are supported. The _master_ branch was using *Eclipse Luna* when this document was created.

Now its time to open your Eclipse if you haven't done so.

Here's how to setup Eclipse...

- create a new workspace for working on _JBoss Fuse Tooling_
- import the project into Eclipse from directory "fuseide" (_Import... -> General -> Existing Project_)

Now your Eclipse has set the target platform you need for running _JBoss Fuse Tooling_. A rebuild of all imported projects is done directly after setting the target platform. Make sure there are no more errors displayed in any of the projects.

### Access Restriction errors

To get rid of the access restriction warnings open menu

    Eclipse -> Preferences -> Java -> Compiler / Errors/Warnings -> Deprecated and restricted API

then turn Forbidden reference (access rules) to "Warning" (it was "Error").

## Running JBoss Fuse Tooling

Before trying to run the Fuse Tooling you should have built the project, imported it successfully into Eclipse and also set the correct target platform (_see the steps above_).

To run open the Run Configurations dialog and select one of the following *Eclipse Applications*:

    JBTIS Neon Linux x86_64.launch    # Eclipse Neon based
    JBTIS Neon Mac OS.launch          # Eclipse Neon based
    JBTIS Neon Windows.launch         # Eclipse Neon based

It may be possible that you have to adapt the launch configuration to fit your environment. If your OS doesn't have a launch configuration yet just create one by copying one of the Linux configurations and then inside the _Plugins_ tab make sure to hit the _Add required_ button. That should fix most of the problems.

## Where stuff gets downloaded in Tycho builds

Tycho downloads all needed plugins into the folder

    <user.home>/.m2/repository/p2

If you kill a Tycho build you can sometimes end up with corrupted downloads leading to failed builds.
The usual exception you will see then is something like this...

    java.util.zip.ZipException: error in opening zip file
      at java.util.zip.ZipFile.open(Native Method)
      at java.util.zip.ZipFile.<init>(ZipFile.java:114)
      at org.codehaus.tycho.osgitools.DefaultBundleReader.doLoadManifest(DefaultBundleReader.java:85)
      at org.codehaus.tycho.osgitools.DefaultBundleReader.loadManifest(DefaultBundleReader.java:47)
      at org.codehaus.tycho.osgitools.EquinoxResolver.loadManifest(EquinoxResolver.java:199)
      at org.codehaus.tycho.osgitools.EquinoxResolver.addBundle(EquinoxResolver.java:175)
      at org.codehaus.tycho.osgitools.EquinoxResolver.newState(EquinoxResolver.java:157)
      at org.codehaus.tycho.osgitools.EquinoxResolver.newResolvedState(EquinoxResolver.java:52)

The quick fix is to trash your folder:

     ~/.m2/repository/p2

Be careful deleting that folder as with the next build it will download lots of plugins from the internet again.
