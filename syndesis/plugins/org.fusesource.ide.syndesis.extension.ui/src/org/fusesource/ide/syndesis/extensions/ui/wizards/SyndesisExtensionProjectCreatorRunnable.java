/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.ui.wizards;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnable;
import org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnableUtils;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;
import org.fusesource.ide.syndesis.extensions.ui.util.NewSyndesisExtensionProjectMetaData;

/**
 * @author lhein
 */
public final class SyndesisExtensionProjectCreatorRunnable extends BasicProjectCreatorRunnable {

	public static final String SYNDESIS_RESOURCE_PATH = "src/main/resources/META-INF/syndesis/syndesis-extension-definition.json";
	
	private NewSyndesisExtensionProjectMetaData syndesisMetaData;
	
	public SyndesisExtensionProjectCreatorRunnable(NewSyndesisExtensionProjectMetaData metadata) {
		super(metadata);
		this.syndesisMetaData = metadata;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnable#doAdditionalProjectConfiguration(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doAdditionalProjectConfiguration(IProject prj, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 20);
		super.doAdditionalProjectConfiguration(prj, subMonitor.split(10));
		// syndesis related config updates
		try {
			updateSyndesisConfiguration(prj, subMonitor.split(10));	
		} catch (CoreException ex) {
			SyndesisExtensionsUIActivator.pluginLog().logError(ex);
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnable#openRequiredFilesInEditor(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void openRequiredFilesInEditor(IProject prj, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 30);
		
		super.openRequiredFilesInEditor(prj, subMonitor.split(10));
		
		// finally open the camel context file
		openCamelContextFile(prj, subMonitor.split(10));
		// and open the syndesis config file
		openSyndesisConfiguration(prj, subMonitor.split(10));
	}

	/**
	 * responsible to update the manifest.mf to use the project name as Bundle-SymbolicName
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	protected void updateSyndesisConfiguration(IProject project, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
		
		try {
			File pomFile = project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toFile();
			Model pomModel = new CamelMavenUtils().getMavenModel(project);

			configureProjectVersions(pomModel);
			configureMetaDataInJSONFile(project, subMonitor.split(1));

			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pomFile))) {
				MavenPlugin.getMaven().writeModel(pomModel, out);
				project.getFile(IMavenConstants.POM_FILE_NAME).refreshLocal(IResource.DEPTH_ZERO, subMonitor.split(10));
			}
		} catch (CoreException | IOException e1) {
			SyndesisExtensionsUIActivator.pluginLog().logError(e1);
		} finally {
			subMonitor.setWorkRemaining(0);
		}
	}
	
	private void configureMetaDataInJSONFile(IProject project, IProgressMonitor monitor) {
		IResource jsonFile = project.getFile(SYNDESIS_RESOURCE_PATH);
		Display.getDefault().asyncExec( () -> updateJsonFile(jsonFile, monitor) );
	}

	private void updateJsonFile(IResource jsonFile, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 3);
		if (!jsonFile.exists()) {
			new BuildAndRefreshJobWaiterUtil().waitJob(monitor);
		}
		SyndesisExtension extenstion = null;
		try (InputStream is = jsonFile.getLocationURI().toURL().openStream()) {
			extenstion = SyndesisExtension.getJSONFactoryInstance(is);
			if (extenstion != null && syndesisMetaData.getSyndesisExtensionConfig() != null) {
				if (!Strings.isBlank(syndesisMetaData.getSyndesisExtensionConfig().getExtensionId())) 	extenstion.setExtensionId(syndesisMetaData.getSyndesisExtensionConfig().getExtensionId());
				if (!Strings.isBlank(syndesisMetaData.getSyndesisExtensionConfig().getName()))			extenstion.setName(syndesisMetaData.getSyndesisExtensionConfig().getName());
				if (!Strings.isBlank(syndesisMetaData.getSyndesisExtensionConfig().getDescription())) 	extenstion.setDescription(syndesisMetaData.getSyndesisExtensionConfig().getDescription());
				if (!Strings.isBlank(syndesisMetaData.getSyndesisExtensionConfig().getVersion())) 		extenstion.setVersion(syndesisMetaData.getSyndesisExtensionConfig().getVersion());
			}
		} catch (IOException ex) {
			SyndesisExtensionsUIActivator.pluginLog().logError(ex);
		} finally {
			subMonitor.setWorkRemaining(1);
		}
		
		if (extenstion != null) {
			try (OutputStream os = new BufferedOutputStream(new FileOutputStream(jsonFile.getLocation().toOSString()))) {
				SyndesisExtension.writeToFile(os, extenstion);
				jsonFile.refreshLocal(0, subMonitor);
			} catch (CoreException | IOException ex) {
				SyndesisExtensionsUIActivator.pluginLog().logError(ex);
			}
		}
		subMonitor.setWorkRemaining(0);
	}
	
	private SyndesisExtension getExtension() {
		return syndesisMetaData.getSyndesisExtensionConfig();
	}
	
	private void configureProjectVersions(Model pomModel) {
		Properties props = pomModel.getProperties();
		props.setProperty("syndesis.version", getExtension().getSyndesisVersion());
		pomModel.setProperties(props);
	}
	
	private void openSyndesisConfiguration(IProject project, IProgressMonitor monitor) {
		if (project != null) {
			final IFile holder = project.getFile(SYNDESIS_RESOURCE_PATH);
			Display.getDefault().asyncExec( () -> {
				try {
					if (!holder.exists()) {
						new BuildAndRefreshJobWaiterUtil().waitJob(monitor);
					}
					IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IDE.openEditor(activePage, holder, OpenStrategy.activateOnOpen());
				} catch (PartInitException e) {
					SyndesisExtensionsUIActivator.pluginLog().logError("Cannot open syndesis configuration file in editor", e); //$NON-NLS-1$
				}
			});
		}
	}
	
	/**
	 * Open the first detected camel context file in the editor
	 *
	 * @param project
	 */
	private void openCamelContextFile(IProject project, IProgressMonitor monitor) {
		if (project != null) {
			// first looks for xml camel file
			IFile camelFile = BasicProjectCreatorRunnableUtils.searchCamelContextXMLFile(project);
			
			// if we found something to open then we open the editor
			if (camelFile != null && camelFile.exists()) {
				BasicProjectCreatorRunnableUtils.openCamelFile(camelFile, monitor, false);
			}
		}
	}
}
