package org.fusesource.ide.camel.editor.wizard;

import java.io.File;
import java.io.InputStream;

import org.fusesource.camel.tooling.util.ArchetypeHelper;
import org.fusesource.ide.camel.editor.Activator;


// TODO this class is in this module due to some odd OSGi class loader issue
// trying to use the ArchetypeHelper from the branding module :)
public class NewFuseProjectHelper {
	
	/**
	 * Create a new project from an archetype
	 */
	public void createProject(InputStream archetypeJarIn, File outputDir, String groupId, String artifactId, String version, String packageName) {
	      ArchetypeHelper helper = new ArchetypeHelper(archetypeJarIn, outputDir, groupId, artifactId, version);
	      if (packageName != null && packageName.length() > 0) {
	    	  helper.packageName_$eq(packageName);
	      }
	      Activator.getLogger().debug("Creating archetype for outputDir: " + outputDir);
	      
	      helper.execute();
	      
	      Activator.getLogger().debug("Done!");
	}

}
