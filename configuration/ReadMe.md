# Configuration Folder Description
This folder contains configuration read by the tooling at runtime directly from Github master repository. That said you should make changes in here very carefully as your changes will affect any user directly and immediately! 


## camel2bom.properties
This file is used for mapping specific Camel versions to specific Fuse BOM versions. This is used when creating a project or changing the Camel version of a project. Then we try to utilize the correct BOM import aligned with the Fuse Runtime version that fits this Camel version. 

Syntax:

	<camel.version>=<fuse.bom.version>

Example:
	
	2.17.0.redhat-630224=6.3.0.redhat-224
	
So whenever the user creates a project for Camel version 2.17.0.redhat-630224 the tooling will automatically choose the bom version 6.3.0.redhat-224 and set that in the projects pom.xml file.
All versions not mentioned here will not have BOM support and just set the Camel version directly on the dependencies.


## camel2bom.fuse7.properties
TODO


## camel2bom.fuse7onOpenShift.properties
TODO


## camel2bom.fuse7wildfly.properties
TODO


## fismarker.properties
This file is used for mapping specific FIS Camel versions to specific FIS BOM versions. This is used when creating a project or changing the Camel version of a project. Then we try to utilize the correct BOM import aligned with the Fuse Runtime version that fits this Camel version. 

Syntax:

	<fis.camel.version>=<fis.fuse.bom.version>

Example:
	
	2.18.1.redhat-000012=2.2.170.redhat-000013
	
So whenever the user creates a project for Camel version 2.18.1.redhat-000012 the tooling will automatically choose the bom version 2.2.170.redhat-000013 and set that in the projects pom.xml file.
All versions not mentioned here will not have BOM support and just set the Camel version directly on the dependencies.


## camelVersionToDisplayName.properties
TODO


## defaultVersionToSelect.properties
TODO


## fisBomToFabric8MavenPlugin.fuse7.properties
TODO


## ignite.properties (DEPRECATED)
This file is currently used by older versions of the tooling to map versions for Syndesis, Spring and Camel. It has been replaced in the latest version with igniteVersionToDisplayName.properties file.


## igniteVersionToDisplayName.properties
This file maps the Fuse Online version to a human readable name to be displayed in the New Fuse Online Extension Wizard. It replaces the old ignite.properties.

Syntax:

	<fuse.online.version>=<human readable name of this version>

Example:
	
	1.3-SNAPSHOT=1.3-SNAPSHOT (Fuse Online TP4)
	

You can also put SNAPSHOT versions into the <fuse.online.version> field (see above example) which will make the Wizard look for the latest available version of that SNAPSHOT in the repositories.
