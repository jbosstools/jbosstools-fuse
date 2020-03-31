# Configuration Folder Description
This folder contains configuration read by the tooling at runtime directly from Github master repository. That said you should make changes in here very carefully as your changes will affect any user directly and immediately! 

## Where can I lookup the versions?
Each release of Fuse Runtime has its own redhat-fuse pom which defines the versions it uses. The versions can be looked up at:

[https://maven.repository.redhat.com/ga/org/jboss/redhat-fuse/redhat-fuse/<fuse-release-version>](https://maven.repository.redhat.com/ga/org/jboss/redhat-fuse/redhat-fuse)


## camel2bom.properties
This file is used for mapping specific Camel versions to specific Fuse BOM versions. This is used when creating a project or changing the Camel version of a project. Then we try to utilize the correct BOM import aligned with the Fuse Runtime version that fits this Camel version. 

Syntax:

	<camel.version>=<fuse.bom.version>

Example:
	
	2.17.0.redhat-630224=6.3.0.redhat-224
	
So whenever the user creates a project for Camel version 2.17.0.redhat-630224 the tooling will automatically choose the bom version 6.3.0.redhat-224 and set that in the projects pom.xml file.
All versions not mentioned here will not have BOM support and just set the Camel version directly on the dependencies.


## camel2bom.fuse7.properties
see camel2bom.fuse71.properties
The difference is that for this one it is searching for old bom ids schemes (without redhat-fuse). New versions should be provided in this file and the camel2bom.fuse71.properties for users that didn't upgraded their Fuse Tooling instance.


## camel2bom.fuse71.properties
This file is used for mapping specific Camel versions to specific Fuse BOM versions. This is used when creating a project or changing the Camel version of a project. Then we try to utilize the correct BOM import aligned with the Fuse Runtime version that fits this Camel version. 

Fuse BOM:

	<groupId>org.jboss.redhat-fuse</groupId>
	<artifactId>fuse-karaf-bom</artifactId>
	 
Syntax:

	<camel.version>=<fuse.bom.version>

Example:
	
	2.21.0.fuse-720050-redhat-00001=7.2.0.fuse-720020-redhat-00001
	
So whenever the user creates a project for Camel version 2.21.0.fuse-720050-redhat-00001 the tooling will automatically choose the bom version 7.2.0.fuse-720020-redhat-00001 and set that in the projects pom.xml file.
All versions not mentioned here will not have BOM support and just set the Camel version directly on the dependencies.

## camel2bom.fuse7onOpenShift.properties
see camel2bom.fuse7.properties above with some slight changes. Here we map the Camel version to a specific Fabric8 version.

Syntax:

	<version.camel>=<version.fabric8>

Example:

	2.21.0.fuse-720050-redhat-00001=3.0.11.fuse-720027-redhat-00001


## camel2bom.fuse7wildfly.properties
see camel2bom.fuse7.properties above with some slight changes. Here we map the Camel version to a specific Wildfly Camel BOM version.

Syntax:

	<version.camel>=<version.bom.wildfly.camel>

Example:

	2.21.0.fuse-720050-redhat-00001=5.2.0.fuse-720023-redhat-00001


## fisBomToFabric8MavenPlugin.fuse7.properties
see camel2bom.fuse7.properties above with some slight changes. Here we map the Fabric8 version to a specific Fabric8 Maven Plugin version.

Syntax:

	<version.fabric8>=<version.fabric8.maven.plugin>

Example:

	3.0.11.fuse-720027-redhat-00001=3.5.33.fuse-720026-redhat-00001


## camelVersionToDisplayName.properties
Here you can specify the human readable name for a specific camel version inside the Fuse wizards.

Syntax:

	<camel.version>=<human readable name for the camel version including Fuse release it ships with>

Example:

	2.21.0.fuse-720050-redhat-00001=2.21.0.fuse-720050-redhat-00001 (Fuse 7.2.0 GA)


## defaultVersionToSelect.properties
Here you can specify which versions should be selected by default in the Fuse wizards.

Syntax:

	<component>=<component.default.version>

Example:

	camel=2.21.0.fuse-720050-redhat-00001


## fismarker.properties
This file is used for mapping specific FIS Camel versions to specific FIS BOM versions. This is used when creating a project or changing the Camel version of a project. Then we try to utilize the correct BOM import aligned with the Fuse Runtime version that fits this Camel version. 

Syntax:

	<fis.camel.version>=<fis.fuse.bom.version>

Example:
	
	2.18.1.redhat-000012=2.2.170.redhat-000013
	
So whenever the user creates a project for Camel version 2.18.1.redhat-000012 the tooling will automatically choose the bom version 2.2.170.redhat-000013 and set that in the projects pom.xml file.
All versions not mentioned here will not have BOM support and just set the Camel version directly on the dependencies.


## ignite.properties (DEPRECATED)
This file is currently used by older versions of the tooling to map versions for Syndesis, Spring and Camel. It has been replaced in the latest version with igniteVersionToDisplayName.properties file.


## igniteVersionToDisplayName.properties
This file maps the Fuse Online version to a human readable name to be displayed in the New Fuse Online Extension Wizard. It replaces the old ignite.properties.
The released versions can be found at [https://maven.repository.redhat.com/ga/io/syndesis/extension/extension-bom/](https://maven.repository.redhat.com/ga/io/syndesis/extension/extension-bom/).

Syntax:

	<fuse.online.version>=<human readable name of this version>

Example:
	
	1.3-SNAPSHOT=1.3-SNAPSHOT (Fuse Online TP4)
	

You can also put SNAPSHOT versions into the <fuse.online.version> field (see above example) which will make the Wizard look for the latest available version of that SNAPSHOT in the repositories.
