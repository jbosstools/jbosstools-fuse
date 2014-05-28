JBoss Fuse Tooling for Eclipse
================

This repository holds the tooling for Eclipse which was formerly known as Fuse IDE. It lets you work with Camel Routes, Fabric and other Fuse Runtimes.
For further information see the following site: http://fusesource.com/products/fuse-ide/

Community
================

IRC
----------------
Join channel "#fusetools" at FreeNode

Issues
----------------
http://fusesource.com/issues/browse/FUSETOOLS

Forums
----------------
https://community.jboss.org/en/jbossfuse

Mailing lists
----------------
coming soon



Build Notes
================

Your first clone
----------------
Once you have cloned this repo via

    git clone git@github.com:fusesource/fuseide.git
    
you can now try building it...

    cd fuseide
    mvn clean install -Dmaven.test.skip=true

Tycho downloads all the eclipse/p2 stuff to: ~/.m2/repository/p2/

Then to open in Eclipse. Before you start make sure you are on Kepler or later Eclipse.

Here's how to setup eclipse...

* create a new workspace for working on Fuse Tooling
* import the project into Eclipse from directory "fuseide" (Import... -> General -> Existing Project)
* open the module Target Platform: org.fusesource.ide.targetplatform/
* open jbtis_4.1_dev.target for Kepler
* OR
* open jbtis_4.2_dev.target for Luna
* wait until all is resolved and then click the "Set as Target Platform" link in the top right


To remove Access Restriction errors
-----------------------------------
To get rid of the final Access restriction warnings

  Eclipse -> Preferences -> Java -> Compiler / Errors/Warnings -> Deprecated and restricted API

then turn Forbidden reference (access rules) to "Warning" from "Error"


Running Fuse Tooling
----------------

Before trying to run the Fuse Tooling you should have built the project (see above) and also imported it sucessfully into Eclipse (see above). 
In Eclipse, select all your resources and refresh (F5).

To run open the Run Configurations dialog and select one of the following "Eclipse Applications":

    Kepler: JBTIS Kepler Linux.launch
    Luna:   JBTIS Luna Linux.launch

It may be possible that you have to adapt the launch configuration to fit your environment using the Run toolbar submenu item. If your OS doesn't have a launch configuration already just create one by copying one of the Linux configurations and then inside the plugins tab make sure to hit the "Add required" button. That should fix most of the issues.


Testing the Update Site
----------------

To test the update site contents has everything you need here's what to do:

* download and install a plain vanilla Eclipse Kepler or Luna 
* run this vanilla Eclipse
* in Eclipse choose "Install new software", then "Add" to add a new update site
* choose "Local" in the following dialog and set the location to file://<your path to fuseide>/site/target/repository
* Close the dialog with OK and then select the new added update site from the drop down box
* Then in the tree below check all the "JBoss Fuse Tooling" entries
* hit Next to install the software
* if all is fine it should install without errors, otherwise it will point you to the problem when you examine the details


Where stuff gets downloaded in Tycho builds
----------------

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

The quick fix is to trash your folder:
    
     ~/.m2/repository/p2

Be careful deleting that folder as with the next build it will download lots of bundles from the internet.


Code Generation (NO NEED FOR THIS STEP UNLESS CAMEL VERSION USED BY FUSE TOOLING CHANGED)
----------------

NOTE this step is only required to be done if the Camel model changes or the documentation changes!
The generated files are checked into git - so if you have a git clone you can miss this out!

This step is only required if you upgrade the version of Camel, change the code generation scripts or update the model metadata.

From the fuseide directory type:

    cd tools/ide-codegen
    mvn compile exec:java

The generator then runs and updates the sources. Don't forget to push the regenerated source files.


Regenerating the archetypes and downloading the latest XSDs
----------------

To download the latest archetypes and XSDs, from the fuseide directory type:

    cd tools/ide-buildtools
    mvn compile exec:java

Be sure to have the file 

    <userhome>/.repo.fusesource.com.properties

which should contain the credentials for the FuseSource Nexus EA repository like
    username=<your login>
    password=<your password>

Without this file the build will not work!


Changing versions before / after a release
----------------

There is a bash script called "changeVersion.sh" in the root folder of the project you can use for changing the bundle and maven versions in an easy way.

Invocation:

    ./changeVersion.sh <oldVersion> <newVersion>
    

Example:

    ./changeVersion.sh 7.3.0-SNAPSHOT 7.3.0
    

Contributing
================

If you want to contribute to Fuse Tooling make sure you read the [contribution guide](https://github.com/fusesource/fuseide/blob/master/Contributing.md "Contribution Guide").

