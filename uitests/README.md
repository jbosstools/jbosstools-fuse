# Red Hat Fuse UI Tests

## Structure

 - uitests/plugins/org.jboss.tools.fuse.reddeer
   RedDeer framework based on JBoss Tools Fuse
 - uitests/tests/org.jboss.tools.fuse.ui.bot.tests
   UI bot tests based on the above RedDeer framework for JBoss Tools Fuse

## Executing tests from command line

The UI tests are disabled by default. To enable them you need to set

    -DskipUITests=false

It is also recommended to ignore local artifacts

    -Dtycho.localArtifacts=ignore

You may also get errors from baseline comparison, so disable it

    -Dtycho.baseline=disable
    -DskipBaselineComparison=true

So, the final command should look like

    mvn clean verify -pl uitests/tests/org.jboss.tools.fuse.ui.bot.tests -am \
    	-DskipUITests=false \
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

Current version of JBoss Tools Fuse is built on Eclipse Oxygen, so download Eclipse Oxygen for JEE Developers. There are two ways how we can execute the tests from IDE
1. Importing all jbosstools-fuse plugins and setting the appropriate target platform
2. Install all needed dependencies into Eclipse IDE and import only the test plugins

### Executing tests from IDE with a target platform

1. Install RedDeer 2.x (at least UI Tools), e.g. from http://download.eclipse.org/reddeer/releases/2.0.0/
2. Import the project as Existing Projects into Workspace
3. Check 'Search for nested projects' and select all projects except 'm2-repo-cleaner'
4. In preferences go to Plug-in Development > Target Platform
5. Add empty target definition and include the Target Platform as a directory
6. In preferences also allow forbidden references (Java > Compiler > Errors/Warnings)
7. One project requires API baseline, so create it via Quick Fix
8. After restart you should see a launcher called 'RedDeer Test'
9. Run the launcher (at the moment it takes about 2 minutes to run the tests)

### Executing tests from IDE without target platform

1. Install Red Hat Fuse Tools, e.g from http://download.jboss.org/jbosstools/oxygen/staging/updates/
2. Install RedDeer 2.x, e.g. from http://download.eclipse.org/reddeer/releases/2.0.0/
3. Import the tests as Existing Projects into Workspace
4. Now, we can launch 'RedDeer Test' which is available in Run configuration
