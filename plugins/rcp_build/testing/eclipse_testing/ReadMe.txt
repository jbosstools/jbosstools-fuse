Things to know when extending the test suite inside Jubula:

- Jubula exports the Test Suite as XML in UTF-16 Format which breaks the 
  maven resource filtering. You have to export the Test Suite, open it in an
  editor. Then open up a new file in UTF-8 format and copy/paste the contents
  of the UTF-16 test suite export into that file. Don't forget to change the 
  UTF-16 to UTF-8 inside the XML.
  
- The maven build is using resource filtering to replace some variables inside 
  the test suite. The following variables are used:
  
  	* aut.executable.dir    --> folder containing the Fuse IDE executable
  	* aut.executable.name   --> the name of the Fuse IDE executable
  	* workspace.dir         --> the workspace folder to use
  
  Be sure those are still inside the correct positions inside the XML. See below
  for an example how it should look like:
  
    ...
    <confAttrMapEntry>
      <key>AUT_ARGUMENTS</key>
      <value>-data ${workspace.dir}</value>
    </confAttrMapEntry>
    ...
  	<confAttrMapEntry>
	  <key>EXECUTABLE</key>
	  <value>${eclipse.executable.dir}/${eclipse.executable.name}</value>
	</confAttrMapEntry>
	...
	<confAttrMapEntry>
	  <key>WORKING_DIR</key>
	  <value>${eclipse.executable.dir}</value>
	</confAttrMapEntry>
    ...
    
    For developing tests locally it is needed to change those entries inside the
    AUT configuration to fit the local environment. Don't forget to put the 
    variables back when exporting the test suite for the maven integration 
    testing.
    
- How to launch the integration testing locally using the Maven build?

	  mvn clean install -Djubula.home=/Applications/jubula_5.2.00266 
	  					-Dplatform.archive=/Users/lhein/Downloads/eclipse-platform-3.7-macosx-cocoa-x86_64.tar.gz
	  					-Dplatform.archive.name=eclipse-platform-3.7-macosx-cocoa-x86_64
	  					-Djubula.plugin.archive=/Applications/jubula_5.2.00266/rcp-support.zip
	  					-Declipse.executable.dir=/tmp/plugin-test/runtime/eclipse
	  					-Declipse.executable.name=Eclipse.app/Contents/MacOS/eclipse
	  
  What are these parameters?
  
  	  * jubula.home 				= the root folder of your local jubula installation
  	  * platform.archive 			= the path to a Fuse IDE distribution archive which works for you locally
  	  * platform.archive.name		= the pure name of the archive without file endings
  	  * jubula.plugin.archive		= the path to the "rcp-support.zip" which is usually located inside the 
  	  							 	  root jubula installation folder
  	  * eclipse.executable.dir     	= the path to the location where Eclipse was unzipped before
  	  * eclipse.executable.name    	= the name of the executable to launch for testing (for MacOS you need to include the path to the real binary as well)				  
