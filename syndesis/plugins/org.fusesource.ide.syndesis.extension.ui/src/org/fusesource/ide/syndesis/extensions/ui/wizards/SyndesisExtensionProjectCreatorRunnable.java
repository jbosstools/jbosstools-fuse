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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
import org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnable;
import org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnableUtils;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;
import org.fusesource.ide.syndesis.extensions.ui.util.NewSyndesisExtensionProjectMetaData;

/**
 * @author lhein
 */
public final class SyndesisExtensionProjectCreatorRunnable extends BasicProjectCreatorRunnable {

	private static final String SYNDESIS_PLUGIN_GROUPID = "io.syndesis.extension";
	private static final String SYNDESIS_PLUGIN_ARTIFACTID = "extension-maven-plugin";
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
			customizeSyndesisPlugin(pomModel);

			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pomFile))) {
				MavenPlugin.getMaven().writeModel(pomModel, out);
				project.getFile(IMavenConstants.POM_FILE_NAME).refreshLocal(IResource.DEPTH_ZERO, subMonitor.split(10));
			}
		} catch (CoreException | XmlPullParserException | IOException e1) {
			SyndesisExtensionsUIActivator.pluginLog().logError(e1);
		}
	}
	
	private void customizeSyndesisPlugin(Model pomModel) throws XmlPullParserException, IOException {
		Build build = pomModel.getBuild();
		Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
		Plugin plugin = pluginsByName.get(SYNDESIS_PLUGIN_GROUPID + ":" + SYNDESIS_PLUGIN_ARTIFACTID); //$NON-NLS-1$
		if (plugin != null) {
			manageConfiguration(plugin);
		}
	}

	private void manageConfiguration(Plugin plugin) throws XmlPullParserException, IOException {
		Xpp3Dom config = (Xpp3Dom)plugin.getConfiguration();
		if (config == null) {
			config = Xpp3DomBuilder.build(new ByteArrayInputStream((
					"<configuration>" + //$NON-NLS-1$
					"</configuration>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			plugin.setConfiguration(config);
		}
		manageInstructions(config);
	}

	private SyndesisExtension getExtension() {
		return syndesisMetaData.getSyndesisExtensionConfig();
	}
	
	private void manageInstructions(Xpp3Dom config) throws XmlPullParserException, IOException {
		setOrChangeConfigValue(config, "extensionId", getExtension().getExtensionId()); //$NON-NLS-1$
		setOrChangeConfigValue(config, "name", getExtension().getName()); //$NON-NLS-1$
		setOrChangeConfigValue(config, "description", getExtension().getDescription()); //$NON-NLS-1$
		setOrChangeConfigValue(config, "version", getExtension().getVersion()); //$NON-NLS-1$
	}
	
	private void setOrChangeConfigValue(Xpp3Dom config, String keyName, String newValue) throws XmlPullParserException, IOException {
		Xpp3Dom elem = config.getChild(keyName); 
		if (elem == null) {
			elem = Xpp3DomBuilder.build(
					new ByteArrayInputStream((String.format("<%s>%s</%s>", keyName, newValue, keyName)).getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			config.addChild(elem);
		} else {
			elem.setValue(newValue);
		}
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
