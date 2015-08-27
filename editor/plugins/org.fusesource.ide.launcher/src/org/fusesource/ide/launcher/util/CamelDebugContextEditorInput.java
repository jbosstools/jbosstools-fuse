/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.launcher.util;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.io.ICamelEditorInput;
import org.fusesource.ide.camel.model.io.XmlContainerMarshaller;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugFacade;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;

/**
 * @author lhein
 */
public class CamelDebugContextEditorInput  implements ICamelEditorInput {
	public static final String ID = CamelDebugContextEditorInput.class.getName();

	private final CamelDebugFacade debugger;
	private final ILaunchConfiguration launchConfig;
	
	private File tempFile;
	private IFile lastSaveAsFile;
	private CamelDebugContextFileStoreEditorInput lastInput;

	/**
	 * 
	 * @param debugger
	 * @param launchConfig
	 */
	public CamelDebugContextEditorInput(CamelDebugFacade debugger, ILaunchConfiguration launchConfig) {
		this.debugger = debugger;
		this.launchConfig = launchConfig;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		// the following code is needed otherwise you can't edit remote routes
		if (adapter.equals(IFile.class) || adapter.equals(IResource.class)) {
			getFileEditorInput();
			IPath p = Path.fromOSString(tempFile.getPath());
			IFile file= ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(p);
			return file;
		}
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null; // TODO: fix this ! Activator.getDefault().getImageDescriptor("camel.png");
	}

	@Override
	public String getName() {
		return "CamelContext: " + debugger.getContextId();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@Override
	public RouteContainer loadModel() {
		return new XmlContainerMarshaller().loadRoutesFromText(this.debugger.getContextXmlDump());
	}

	@Override
	public void save(String xml) {
		this.debugger.updateContext(xml);
	}

	@Override
	public IEditorInput getFileEditorInput() {
		if (this.lastInput == null) {
			try {
				// first get the file path from the launch config
				String fileUnderDebug = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(this.launchConfig);
				// then get the project for the file
				IProject p = CamelDebugUtils.getProjectForFilePath(fileUnderDebug);
				// lets create the temporary file under target
				File targetFolder = p.getLocation().append("target").toFile();
				// create the target folder if not existing
				if (!targetFolder.exists()) targetFolder.mkdirs();
				// create a unique temp folder inside target
				File tempDir = File.createTempFile(".CamelContextInDebug_", "_temp", targetFolder);
				tempDir.delete();
				tempDir.mkdirs();
				
				// now create the file which will hold the camel context
				tempFile = new File(tempDir, fileUnderDebug.substring(fileUnderDebug.lastIndexOf("/")+1));
				
				// lets create the IFile from it...
				IFileSystem fileSystem = EFS.getLocalFileSystem();
				IFileStore fileStore = fileSystem.fromLocalFile(tempFile);

				// retrieve the contents from the debugger
				String xml = debugger.getContextXmlDump();
				// and write the contents to the file
				if (xml != null) IOUtils.writeText(tempFile, xml);
				
				this.lastInput = new CamelDebugContextFileStoreEditorInput(fileStore, this.debugger);
				return this.lastInput;
			} catch (Exception e) {
				Activator.getLogger().warning("Failed to create temporary file: " + e, e);
			}			
		} 
		return this.lastInput;
	}
	
	/**
	 * refresh the input
	 */
	public void refresh() {
//		try {
//			// retrieve the contents from the debugger
//			String xml = debugger.getContextXmlDump();
//			// and write the contents to the file
//			if (xml != null) IOUtils.writeText(tempFile, xml);
//		} catch (Exception ex) {
//			Activator.getLogger().error(ex);
//		}
	}

	@Override
	public IFile getLastSaveAsFile() {
		return lastSaveAsFile;
	}

	@Override
	public void setLastSaveAsFile(IFile lastSaveAsFile) {
		this.lastSaveAsFile = lastSaveAsFile;
	}

}
