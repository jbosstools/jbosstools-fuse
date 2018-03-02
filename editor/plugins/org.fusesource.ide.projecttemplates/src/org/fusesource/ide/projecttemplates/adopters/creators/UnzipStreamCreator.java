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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.projecttemplates.adopters.util.InvalidProjectMetaDataException;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.osgi.framework.Bundle;

/**
 * base class for unzipping a template from a zip archive
 * 
 * @author lhein
 */
public abstract class UnzipStreamCreator extends InputStreamCreator {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport#create(org.eclipse.core.resources.IProject, org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData)
	 */
	@Override
	public boolean create(IProject project, CommonNewProjectMetaData metadata, IProgressMonitor monitor) {
		return unzipStream(project, metadata, monitor);
	}
	
	/**
	 * unzips the template into the created project
	 * 
	 * @param project
	 * @return
	 */
	protected boolean unzipStream(IProject project, CommonNewProjectMetaData metadata, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.unzipStreamCreatorUnzippingTemplateFileMonitorMessage,  2);
	     try {
	    	 // create output directory is not exists
	    	 File folder = new File(project.getLocation().toOSString());
	    	 if(!folder.exists()){
	    		 folder.mkdir();
	    	 }
	    	 InputStream is = getTemplateStream(metadata);
	    	 subMonitor.worked(1);
	    	 
			 if (is instanceof ZipInputStream) {
				// get the zip file content
				ZipInputStream zis = (ZipInputStream)is;
				// get the zipped file list entry
				ZipEntry ze = zis.getNextEntry();
				while (ze != null) {
					String fileName = ze.getName();
					File newFile = new File(folder + File.separator + fileName);
					if (ze.isDirectory()) {
						newFile.mkdirs();
					} else {
						// create all non exists folders
						// else you will hit FileNotFoundException for
						// compressed folder
						final Path newFilePath = newFile.toPath();
						Files.createDirectories(newFilePath.getParent());
						Files.copy(zis, newFilePath, StandardCopyOption.REPLACE_EXISTING);
					}
					ze = zis.getNextEntry();
				}
				zis.closeEntry();
				zis.close();
				subMonitor.worked(1);
			 } else {
				ProjectTemplatesActivator.pluginLog().logError("Unable to unzip stream of type " + is.getClass().getName()); //$NON-NLS-1$
				return false;
			 }
	     } catch(IOException|InvalidProjectMetaDataException ex) {
	    	 ProjectTemplatesActivator.pluginLog().logError(ex);
	    	 return false;
	     }
	     return true;
	}

	protected InputStream getTemplateStream(String bundleEntry) {
		return getTemplateStream(ProjectTemplatesActivator.getBundleContext().getBundle(), bundleEntry);
	}
	
	protected InputStream getTemplateStream(Bundle bundle, String bundleEntry) {
		URL archiveUrl = bundle.getEntry(bundleEntry);
		if (archiveUrl != null) {
			try {
				return new ZipInputStream(archiveUrl.openStream(), StandardCharsets.UTF_8);
			} catch (IOException ex) {
				ProjectTemplatesActivator.pluginLog().logError(ex);
			}			
		}
		return null;
	}
}
