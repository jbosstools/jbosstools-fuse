# JBoss Tools Fuse QE

## Structure

 - qe/plugins/org.jboss.tools.fuse.qe.reddeer
   RedDeer framework based on JBoss Tools Fuse
 - qe/tests/org.jboss.tools.fuse.qe.reddeer.tests
   RedDeer tests for JBoss Tools Fuse

## Executing tests from command line

The QE tests are disabled by default. To enable them you need to set

    -DskipQETests=false

It is also recommended to ignore local artifacts

    -Dtycho.localArtifacts=ignore

You may also get errors from baseline comparison, so disable it

    -Dtycho.baseline=disable
    -DskipBaselineComparison=true

So, the final command should look like

    mvn clean verify -pl qe/tests/org.jboss.tools.fuse.qe.reddeer.tests -am \
    	-DskipQETests=false \
    	-Dtycho.localArtifacts=ignore \
    	-Dtycho.baseline=disable \
    	-DskipBaselineComparison=true \
    	-DfailIfNoTests=false \
    	-Dtest=SmokeTests

## Executing tests against an existing Eclipse instance

    -Dtest.installBase=${ECLIPSE_HOME}
    -Dtest.installBase=${DEVSTUDIO_HOME}/studio

## Debugging tests when running from command line

Just add the following system property

    -DdebugPort=8001

and in IDE create a configuration for Remote Java Application in Run > Debug Configurations...

## Executing tests from IDE

Current version of JBoss Tools Fuse is built on Eclipse Oxygen, so download Eclipse Oxygen for JEE Developers. For executing the tests from IDE you need

 - Target Platform (targetplatform/multiple)
 - RedDeer UI Tools (http://download.jboss.org/jbosstools/neon/development/updates/reddeer/)

and follow these steps:

1. Import the project as Existing Projects into Workspace (Search for nested projects)
2. Check 'Search for nested projects' and select all projects except 'm2-repo-cleaner'
3. In preferences go to Plug-in Development > Target Platform
4. Add empty target definition and include the Target Platform as a directory
5. In preferences also allow forbidden references (Java > Compiler > Errors/Warnings)
6. One project requires API baseline, so create it via Quick Fix
7. Install RedDeer UI Tools which includes RedDeer launcher
8. After restart you should see a launcher called 'RedDeer Test Linux x86_64'
9. Run the launcher (at the moment it takes about 2 minutes to run the tests)
