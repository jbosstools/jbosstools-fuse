# Contribution Guide

Please make sure you read and understand this document before starting development on Fuse Tooling as it helps us to merge your pull requests faster and keeps the commit history clean.


## Get the code

The easiest way to get started with the code is to [create your own fork](http://help.github.com/forking/) at github, and then clone your fork:

	$ git clone git@github.com:<you>/fuseide.git
	$ cd fuseide
	$ git remote add upstream git://github.com/fusesource/fuseide.git

At any time, you can pull changes from the upstream and merge them onto your master:

	$ git checkout master               # switches to the 'master' branch
	$ git pull upstream master          # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
	$ git push origin                   # pushes all the updates to your fork, which should be in-sync with 'upstream'

The general idea is to keep your 'master' branch in-sync with the 'upstream/master'.


## Building JBoss Fuse Tooling

To build _JBoss Fuse Tooling_ requires specific versions of Java (1.6+) and +Maven (3.0+). See this [link](https://github.com/fusesource/fuseide/blob/master/ReadMe.md) for more information on how to setup, run and configure build.

This command will run the build:

    $ mvn clean package

If you just want to check if things compiles/builds you can run:

    $ mvn clean package -Dmaven.test.skip=true

But *do not* push changes without having the new and existing unit tests pass!


## Contribute fixes and features

_JBoss Fuse Tooling_ is open source, and we welcome anybody who wants to participate and contribute!

If you want to fix a bug or make any changes, please log an issue in the [JBoss Fuse Tooling JIRA](https://issues.jboss.org/browse/FUSETOOLS) describing the bug or new feature and give it a fitting component type. Then we highly recommend making the changes on a topic branch named with the JIRA issue number. For example, this command creates a branch for the FUSETOOLS-1234 issue:

	$ git checkout -b fusetools-1234

After you're happy with your changes and a full build (with unit tests) runs successfully, commit your changes on your topic branch (with good comments). Then it's time to check for any recent changes that were made in the official repository:

	$ git checkout master               # switches to the 'master' branch
	$ git pull upstream master          # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
	$ git checkout fusetools-1234       # switches to your topic branch
	$ git rebase master                 # reapplies your changes on top of the latest in master
	                                      (i.e., the latest from master will be the new base for your changes)

If the pull grabbed a lot of changes, you should rerun your build with tests enabled to make sure your changes are still good.

You can then push your topic branch and its changes into your public fork repository:

	$ git push origin fusetools-1234         # pushes your topic branch into your public fork of JBoss Fuse Tooling

And then [generate a pull-request](http://help.github.com/pull-requests/) where we can review the proposed changes, comment on them, discuss them with you, and if everything is good merge the changes right into the official repository.
