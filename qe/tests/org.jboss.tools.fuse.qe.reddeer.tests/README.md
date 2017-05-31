# JBoss Fuse Tooling Tests

## Prerequisites

- for supported operating systems and JDKs see [JBDS Supported Configurations](https://access.redhat.com/articles/427493#JBDS_9)
- Maven 3.0.5 or 3.1.1 (**not higher!**)
- Git 1.8.3.1 or higher
- some VNC server + viewer (e.g. [TigerVNC](http://tigervnc.org/)

## Environment preparation

##### 1. configure Maven repositories (see [Get the code](https://github.com/jbosstools/jbosstools-integration-stack-tests#getthecode))
##### 2. obtain required Git repository (see [Build the code](https://github.com/jbosstools/jbosstools-integration-stack-tests#buildthecode))
##### 3. run and connect to a different display

We use a different display to separate a test run from our current session. We can use e.g. **TigerVNC** and these commands:
```
vncserver :2 -geometry 1600x900 -depth 24
vncviewer localhost:2 &
```
* vncserver starts a new instance of VNC server with subsequent parameters:
  * *:2* - display's number
  * *-geometry 1600x900* - screen resolution
  * *-depth 24* - color depth in bits
* vncviewer connects via VNC protocol to the given address (display's number can be specified)

##### 4. install and configure JBDS (see [Run tests from IDE](https://github.com/jbosstools/jbosstools-integration-stack-tests#runtestsfromide))

##### 5. get JBoss Fuse Server

1. download JBoss Fuse from [http://www.jboss.org/products/fuse/download/](http://www.jboss.org/products/fuse/download/)
2. create a RedDeer Configuration file
  * The configuration file must follow the [SOA XSD schema](http://www.jboss.org/schema/reddeer/3rdparty/SOARequirements.xsd)
  * Example:
```
<?xml version="1.0" encoding="UTF-8"?>
<testrun
  xmlns="http://www.jboss.org/NS/Req"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:server="http://www.jboss.org/NS/SOAReq"
  xsi:schemaLocation="http://www.jboss.org/NS/Req	http://www.jboss.org/schema/reddeer/RedDeerSchema.xsd
                      http://www.jboss.org/NS/SOAReq	http://www.jboss.org/schema/reddeer/3rdparty/SOARequirements.xsd">

  <requirements>
    <server:server-requirement name="JBoss Fuse 6.2.1">
      <server:fuse version="6.2">
        <server:home>/home/tsedmik/devel/git/server-installer/fuse-6.2.1.GA/target/jboss-fuse-6.2.1.redhat-084</server:home>
        <server:host>localhost</server:host>
        <server:port>8101</server:port>
        <server:username>admin</server:username>
        <server:password>admin</server:password>
      </server:fuse>
    </server:server-requirement>
  </requirements>

</testrun>
```

## How to run

To run tests with Maven we execute following commands:
```
cd jbosstools-integration-stack-tests
DISPLAY=:2 mvn clean verify -pl tests/org.jboss.tools.fuse.ui.bot.test -am -Dtest=*name_of_the_test_case_or_suite*
```
If we execute these commands, test will be run. Please, note the following points:
* *-pl tests/org.jboss.tools.fuse.ui.bot.test -am* - specify a project (containing tests for JBoss Fuse Tooling) to build. It also build all dependent project - in this case tests/org.jboss.tools.fuse.reddeer which contains some sort of framework manipulates with JBoss Fuse Tooling.
* *-Dtest=...* - if we want to run a specific test case or suite, use this parameter (e.g. *-Dtest=ServerTest* runs *ServerTest.java* only).
* *-DdebugPort=8123* - if we want to debug tests (ran with Maven) in JBoss Developer Studio or Eclipse, use this parameter. 
* *-Dtest.installBath=...* - if we want to run tests against existing JBDS + JBDSIS installation (e.g. `-Dtest.installBase=~/jbds-installer/jbds-8.0.2.GA_jbdsis-8.0.0.GA/target/jbdevstudio/studio`)
* *-Drd.config=...* - if we want to run the tests with a configuration (to convince tests to use specific server runtime) - see section _5. get JBoss Fuse Server_

### How to run tests for IDE

##### 1. import the following projects as an existing maven project
- org.jboss.tools.common.reddeer
- org.jboss.tools.runtime.reddeer
- org.jboss.tools.fuse.reddeer
- org.jboss.tools.fuse.ui.bot.test

##### 2. setup run/debug configuration
In folder _launchers_ is prepared default run configuration for JBoss Fuse Tooling. We can run it via _Run --> Run/Debug Configurations --> RedDeer Test --> Fuse Tooling_. This launcher runs smoke tests suite. We can simply change test suite/class via Test class.

If we want to create our own launcher, here is the recipe:
* Select *Run --> Run/Debug Configurations...*
* Select *RedDeer Test* --> press *New*
* Type *Name* and on tab Test select *Run a single test*
* Select *org.jboss.tools.fuse.ui.bot.test* as *Project*
* Select via *Search...* button some test suite or test case
* **Important!** - on tab *Arguments* set following VM arguments:
  1. *-Dosgi.requiredJavaVersion=1.6 -XX:MaxPermSize=256m -Xms40m -Xmx512m*
  2. *-Drd.config=...* - this parameter **must be set** and should contains a path to the configuration file, that we prepared in Step 5 of section Environment Preparation.
  3. *-Dusage_reporting_enabled=false* - close *Usage reporting* window (displayed on JBDS startup)
* on tab *Environment*:
  1. *set variable DISPLAY=:2* - it allows us to see test run on different display (see step 2 of section Environment Preparation).

##### 3. run tests for JBoss Fuse Tooling
* Simply run configuration created in Step 2
* Open some VNC viewer application and connect to localhost:2 to see test run
