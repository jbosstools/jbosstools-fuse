# Contribution Guide

Please make sure you read and understand this document before starting development on Fuse Tooling as it helps us to merge your pull requests faster and keeps the commit history clean.

## Git configuration

For code review simplicity, all files should be pushed with LF for line-ending (Linux-style).
The file .gitattributes at the root of the repository should ensure that but it is recommended to set this option also in your Eclipse environment.
To configure Eclipse workspace preferences:

- Windows -> Preferences
- General -> Workspace
- In group "New text file line delimiters", ensure you select a Linux style behavior.

## Get the code

The easiest way to get started with the code is to [create your own fork](https://help.github.com/forking/) at github, and then clone your fork:

    git clone git@github.com:<you>/jbosstools-fuse.git
    cd jbosstools-fuse
    git remote add upstream https://github.com/jbosstools/jbosstools-fuse.git

At any time, you can pull changes from the upstream and merge them onto your master:

    git checkout master               # switches to the 'master' branch
    git pull --rebase upstream master # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
    git push origin                   # pushes all the updates to your fork, which should be in-sync with 'upstream'

The general idea is to keep your 'master' branch in-sync with the 'upstream/master'.

## Building Red Hat Fuse Tooling

To build _Red Hat Fuse Tooling_, it requires specific versions of Java (11) and +Maven (3.0+). See this [link](./Build.md) for more information on how to setup, run and configure build.

This command will run the build:

    mvn clean verify

If you just want to check if things compile/build you can skip the tests by running:

    mvn clean verify -DskipTests -DskipUITests

But *do not* push changes without having the new and existing unit tests pass!

## Contribute fixes and features

_Red Hat Fuse Tooling_ is open source, and we welcome anybody who wants to participate and contribute!

If you want to fix a bug or make any changes, please log an issue in the [Red Hat Fuse Tooling JIRA](https://issues.jboss.org/browse/FUSETOOLS) describing the bug or new feature and give it a fitting component type. Then we highly recommend making the changes on a topic branch named with the JIRA issue number. For example, this command creates a branch for the _FUSETOOLS-1234_ issue:

    git checkout -b FUSETOOLS-1234

After you're happy with your changes and a full build (with tests) runs successfully, commit your changes on your topic branch (with a meaningful comment).

    git commit -s -m "FUSETOOLS-1234 - I fixed problem xyz or I added a new feature for purpose xyz and some more meaningful descriptions"

Don't forget the code sign-off (_-s_) in the above comment or your PR will not be accepted.
Then it's time to check for any recent changes that were made in the official repository meanwhile:

    git checkout master               # switches to the 'master' branch
    git pull --rebase upstream master # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
    git checkout FUSETOOLS-1234       # switches to your topic branch
    git rebase upstream master        # re-applies your changes on top of the latest in master
                                      (i.e., the latest from master will be the new base for your changes)

If the pull grabbed a lot of changes, you should rerun your build with tests enabled to make sure your changes are still good.

You can then push your topic branch and its changes into your public fork repository:

    git push origin FUSETOOLS-1234         # pushes your topic branch into your public fork of Red Hat Fuse Tooling

And then [generate a pull-request](https://help.github.com/pull-requests/) where we can review the proposed changes, comment on them, discuss with you, and if everything is good then to merge the changes right into the official repository.

## Setup the Target Platform for development

- Go into folder 'targetplatform'
- Build targetplatform (mvn clean install)
- Open targetplatform/target/fuse-multiple.target
- Click *Set as Target Platform*

In case, the Target Platform doesn't resolve:

- Open targetplatform/fuse-jbosstools.target
- Click Update and Reload
- Build targetplatform (mvn clean install)
- Open targetplatform/target/fuse-multiple.target
- Click *set as Target Platform*

## Setup the Target Platform for development with SDKs (ALTERNATIVE APPROACH)

- Go into folder 'targetplatform'
- Build the targetplaform (_mvn clean verify -Pmultiple2repo -Dmirror-target-to-repo.includeSources=true_)
- copy the generated repository folder under the 'target' subfolder to a safe place and reference it in Eclipse Target Platform Definition

If the build fails then most likely a version has been changed upstream and we need to update our target definition file. To do that follow these [instructions](https://github.com/jbosstools/jbosstools-devdoc/blob/master/building/target_platforms/target_platforms_updates.adoc#update-versions-of-ius-in-the-target-files).

## Testing the Update Site

If you introduced new dependencies or changed versions it is always a good idea to check if the update site still contains everything needed to install successfully. To test the update site contents here's what to do:

- download and install a plain vanilla Eclipse Kepler, Luna or whatever version used currently
- run this vanilla Eclipse
- in Eclipse choose _Install new software_, then _Add_ to add a new update site
- choose _Local_ in the following dialog and set the location to file://your_path_to_jbosstools-fuse>/site/target/repository
- close the dialog with _OK_ and then select the new added update site from the drop down box
- then in the tree below check all the _Red Hat Fuse Tooling_ entries
- hit _Next_ to install the software
- if all is fine it should install without errors, otherwise it will point you to the problem when you examine the details

## Changing versions before / after a release

There is a bash script called [_changeVersion.sh_](https://github.com/fusesource/fuseide/blob/master/changeVersion.sh "Version Change Script") in the root folder of the project. You can use that for changing the bundle and Maven versions in an easy way.

*Invocation:*

    ./changeVersion.sh <oldVersion> <newVersion>

_Example:_

    ./changeVersion.sh 7.3.0-SNAPSHOT 7.3.0
    
## Profiling built application

Profiling built application can be very useful when users are hitting performance issues and developers cannot reproduce the problem.
The following lines will explain how to do a profiling of Fuse Tooling specific classes using [VisualVM](https://visualvm.github.io) on a [Code Ready Studio](https://developers.redhat.com/products/codeready-studio/overview/) installation.

### Install Profiling Tool in VisualVM

- Launch VisualVM (that you downloaded from [VisualVM website](https://visualvm.github.io)
- Click on Tools -> Plugins -> Available Plugins, select "Startup profiler" and click install

### Retrieve VM arguments from VisualVM to pass to Code Ready Studio

- Click on the "Start New Process and Profile its startup" icon (it should the one at the top right of the toolbar)
- Modify parameters for VM and architecture depending on your installation, then click continue
- In _Profile classes_, provide `org.fusesource.ide.**`
- Check _Include outgoing calls_, set `org.fusesource.ide.**,org.jboss.tools.**`
- Click on _continue_
- Click _copy to clipboard_
- Click on "Profile"
- If asking for a calibration click yes

Please note that it is focusing on Fuse Tooling classes. In case, the performance issues are due to other part of the code, the classes to profile will need to be adapted. We are restraining the scope of classes to profile, otherwise the time to do a profile can be very very long.

### Include VM arguments

- Go to Code Ready Studio installation folder, and open studio/devstudio.ini file
- right after _-vmargs_ , on a new line, paste the argument from previous step (something like _-agentpath:xxxx_) and save it
- Double-check if there is a _-vm_ provided that it is matching the version that you selected in VisualVM configuration
- Launch Code Ready Studio

### Take a Snapshot

In VisualVm, a Tree graph should appear.

If profiling startup:

- In toolbar of the tree graph, click on _Snapshot_

If profiling another use case:

- In toolbar of the tree graph, click on _Reset collected results_
- Play the steps to reproduce the performance issue in Code Ready Studio
- When done, go back to VisualVM and click on _Snapshot_

### Share a Snapshot

- Click on "Export Snapshot Data"
- The generated *.nps file can be shared

### Load a Snapshot for analysis (for Developers only)

- Click on _File -> Load_
- Choose the _*.nps_ file
