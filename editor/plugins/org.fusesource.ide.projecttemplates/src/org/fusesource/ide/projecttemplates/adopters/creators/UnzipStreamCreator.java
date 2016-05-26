/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.adopters.creators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

/**
 * base class for unzipping a template from a zip archive
 * 
 * @author lhein
 */
public abstract class UnzipStreamCreator extends InputStreamCreator {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport#create(org.eclipse.core.resources.IProject, org.fusesource.ide.projecttemplates.util.NewProjectMetaData)
	 */
	@Override
	public boolean create(IProject project, NewProjectMetaData metadata) {
		return unzipStream(project, metadata);
	}
	
	/**
	 * unzips the template into the created project
	 * 
	 * @param project
	 * @return
	 */
	protected boolean unzipStream(IProject project, NewProjectMetaData metadata) {
		byte[] buffer = new byte[1024];
    	
	     try {
	    	 // create output directory is not exists
	    	 File folder = new File(project.getLocation().toOSString());
	    	 if(!folder.exists()){
	    		 folder.mkdir();
	    	 }
	    	 InputStream is = getTemplateStream(metadata);
	    	 
	    	 if (is instanceof ZipInputStream) {
		    	 // get the zip file content
		    	 ZipInputStream zis = (ZipInputStream)getTemplateStream(metadata);
		    	 // get the zipped file list entry
		    	 ZipEntry ze = zis.getNextEntry();
		    	 while(ze != null) {
		    		 String fileName = ze.getName();
		    		 File newFile = new File(folder + File.separator + fileName);
		    		 if (ze.isDirectory()) {
		    			newFile.mkdirs(); 
		    		 } else {
			    		 // create all non exists folders
			    		 // else you will hit FileNotFoundException for compressed folder
			    		 new File(newFile.getParent()).mkdirs();
			    		 FileOutputStream fos = new FileOutputStream(newFile);             
			    		 int len;
			    		 while ((len = zis.read(buffer)) > 0) {
			    			 fos.write(buffer, 0, len);
			    		 }
			    		 fos.close();   
		    		 }
		    		 ze = zis.getNextEntry();
		    	 }
		    	 zis.closeEntry();
		    	 zis.close();	    		 
	    	 } else {
	    		 ProjectTemplatesActivator.pluginLog().logError("Unable to unzip stream of type " + is.getClass().getName());
	    		 return false;
	    	 }
	     } catch(IOException ex) {
	    	 ProjectTemplatesActivator.pluginLog().logError(ex);
	    	 return false;
	     }
	     return true;
	}
}
