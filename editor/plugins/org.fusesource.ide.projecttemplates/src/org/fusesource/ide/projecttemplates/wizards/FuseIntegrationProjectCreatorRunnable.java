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
package org.fusesource.ide.projecttemplates.wizards;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnable;
import org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnableUtils;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;

/**
 * @author Aurelien Pupier
 *
 */
public final class FuseIntegrationProjectCreatorRunnable extends BasicProjectCreatorRunnable {

	private static final String ORG_APACHE_FELIX = "org.apache.felix";
	private static final String MAVEN_BUNDLE_PLUGIN = "maven-bundle-plugin";
	
	/**
	 * @param metadata
	 */
	public FuseIntegrationProjectCreatorRunnable(NewFuseIntegrationProjectMetaData metadata) {
		super(metadata);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnable#doAdditionalProjectConfiguration(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doAdditionalProjectConfiguration(IProject prj, IProgressMonitor monitor) {
		super.doAdditionalProjectConfiguration(prj, monitor);
		
		// update the pom maven bundle plugin config to reflect project name as Bundle-(Symbolic)Name
		try {
			updateBundlePluginConfiguration(prj, monitor);
		} catch (CoreException ex) {
			ProjectTemplatesActivator.pluginLog().logError("Unable to create project...", ex); //$NON-NLS-1$
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnable#openRequiredFilesInEditor(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void openRequiredFilesInEditor(IProject prj, IProgressMonitor monitor) {
		super.openRequiredFilesInEditor(prj, monitor);
		try {
			openCamelContextFile(prj, monitor);
		} catch (CoreException ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
		}
	}

	/**
	 * responsible to update the manifest.mf to use the project name as Bundle-SymbolicName
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	protected void updateBundlePluginConfiguration(IProject project, IProgressMonitor monitor) throws CoreException {
		try {
			File pomFile = project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toFile();
			Model pomModel = new CamelMavenUtils().getMavenModel(project);

			customizeBundlePlugin(pomModel, project);

			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pomFile))) {
				MavenPlugin.getMaven().writeModel(pomModel, out);
				project.getFile(IMavenConstants.POM_FILE_NAME).refreshLocal(IResource.DEPTH_ZERO, monitor);
			}
		} catch (CoreException | XmlPullParserException | IOException e1) {
			ProjectTemplatesActivator.pluginLog().logError(e1);
		}
	}
	
	private void customizeBundlePlugin(Model pomModel, IProject project) throws XmlPullParserException, IOException {
		Build build = pomModel.getBuild();
		Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
		Plugin plugin = pluginsByName.get(ORG_APACHE_FELIX + ":" + MAVEN_BUNDLE_PLUGIN); //$NON-NLS-1$
		if (plugin != null) {
			manageConfigurations(plugin, project, pomModel);
		}
	}

	private void manageConfigurations(Plugin plugin, IProject project, Model pomModel) throws XmlPullParserException, IOException {
		Xpp3Dom config = (Xpp3Dom)plugin.getConfiguration();
		if (config == null) {
			config = Xpp3DomBuilder.build(new ByteArrayInputStream((
					"<configuration>" + //$NON-NLS-1$
					"</configuration>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			plugin.setConfiguration(config);
		}
		manageInstructions(config, project, pomModel);
	}

	private void manageInstructions(Xpp3Dom config, IProject project, Model pomModel) throws XmlPullParserException, IOException {
		Xpp3Dom instructions = config.getChild("instructions"); //$NON-NLS-1$
		if (instructions == null) {
			instructions = Xpp3DomBuilder.build(new ByteArrayInputStream(("<instructions>" + //$NON-NLS-1$
					"</instructions>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			config.addChild(instructions);
		}
		manageCustomInstructions(instructions, project, pomModel);
	}
	
	private void manageCustomInstructions(Xpp3Dom instructions, IProject project, Model pomModel) throws XmlPullParserException, IOException {
		Xpp3Dom bundleSymbolicName = instructions.getChild("Bundle-SymbolicName"); //$NON-NLS-1$
		if (bundleSymbolicName == null) {
			bundleSymbolicName = Xpp3DomBuilder.build(
					new ByteArrayInputStream(("<Bundle-SymbolicName>" + BasicProjectCreatorRunnableUtils.getBundleSymbolicNameForProjectName(project.getName()) + "</Bundle-SymbolicName>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			instructions.addChild(bundleSymbolicName);
		}
		String description = pomModel.getDescription();
		String desc = description != null && description.trim().length()>0 ? description : String.format("%s.%s", pomModel.getGroupId(), pomModel.getArtifactId()); 
		Xpp3Dom bundleName = instructions.getChild("Bundle-Name"); //$NON-NLS-1$
		if (bundleName == null) {
			bundleName = Xpp3DomBuilder.build(
					new ByteArrayInputStream(("<Bundle-Name>" + String.format("%s [%s]", desc, project.getName()) + "</Bundle-Name>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			instructions.addChild(bundleName);
		}
	}
	
	/**
	 * Open the first detected camel context file in the editor
	 *
	 * @param project
	 */
	private void openCamelContextFile(IProject project, IProgressMonitor monitor) throws CoreException {
		if (project != null) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
			boolean isJavaEditorToOpen = false;
			
			// first looks for xml camel file
			IFile camelFile = BasicProjectCreatorRunnableUtils.searchCamelContextXMLFile(project);
			
			// if no camel xml found we look for java files
			if (camelFile == null && project.hasNature(JavaCore.NATURE_ID)) {
				camelFile = BasicProjectCreatorRunnableUtils.searchCamelContextJavaFile(project, subMonitor.split(1));
				isJavaEditorToOpen = true;
			}
			subMonitor.setWorkRemaining(1);

			// if we found something to open then we open the editor
			if (camelFile != null) {
				BasicProjectCreatorRunnableUtils.openCamelFile(camelFile, subMonitor.split(1), isJavaEditorToOpen);
			}
			subMonitor.setWorkRemaining(0);
		}
	}
}
