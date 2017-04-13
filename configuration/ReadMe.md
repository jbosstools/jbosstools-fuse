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
