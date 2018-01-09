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
package org.fusesource.ide.projecttemplates.adopters.configurators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.fusesource.ide.projecttemplates.util.camel.CamelFacetDataModelProvider;
import org.fusesource.ide.projecttemplates.util.camel.ICamelFacetDataModelProperties;

/**
 * this configurator provides additional helper 
 * methods to retrieve a facet data model and to install a facet on the project
 * 
 * @author lhein
 */
public class DefaultTemplateConfigurator implements TemplateConfiguratorSupport {
	
	private static final String LAUNCH_CONFIGURATION_FILE_EXTENSION = ".launch";
	private static final String SETTINGS_FUSETOOLING = ".settings/fusetooling";
	private static final String PLACEHOLDER_PROJECTNAME_IN_LAUNCH_CONFIGURATION = "%%%PLACEHOLDER_PROJECTNAME%%%";
	private static final String PLACEHOLDER_BOMVERSION = "%%%PLACEHOLDER_BOMVERSION%%%";
	
	protected String bomVersion;
	
	public DefaultTemplateConfigurator(String bomVersion) {
		this.bomVersion = bomVersion;
	}
	
	@Override
	public boolean configure(IProject project, CommonNewProjectMetaData metadata, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.defaultTemplateConfiguratorConfiguringJavaProjectMonitorMessage, 10);
		IProjectFacetVersion javaFacet = ProjectFacetsManager.getProjectFacet("jst.java").getDefaultVersion(); //$NON-NLS-1$
		try {
			configureVersions(project, subMonitor.split(1));
			// add java facet
			installFacet(project, "jst.java", javaFacet.getVersionString(), null, subMonitor.split(1)); //$NON-NLS-1$
			project.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
			// add m2 facet
			installFacet(project, "jboss.m2", null, null, subMonitor.split(1)); //$NON-NLS-1$
			project.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
			// now add jst utility
			installFacet(project, "jst.utility", null, null, subMonitor.split(1)); //$NON-NLS-1$
			project.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
			project.getFile(".classpath").delete(true, subMonitor.split(1)); //$NON-NLS-1$
			project.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
			configureLaunchConfiguration(project, subMonitor.split(1));
		} catch (CoreException ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
			return false;
		}
		return true;
	}

	protected void configureVersions(IProject project, IProgressMonitor monitor) throws CoreException {
		if (bomVersion != null) {
			IFile pom = project.getFile("pom.xml");
			Path pomAbsolutePath = pom.getLocation().toFile().toPath();
			replace(bomVersion, PLACEHOLDER_BOMVERSION, pomAbsolutePath, pomAbsolutePath);
			project.refreshLocal(IResource.DEPTH_ONE, monitor);
		}
	}

	private void configureLaunchConfiguration(IProject project, IProgressMonitor monitor) throws CoreException {
		IFolder fuseToolingSettingsFolder = project.getFolder(SETTINGS_FUSETOOLING);
		if(fuseToolingSettingsFolder.exists()){
			IResource[] potentialLaunchConfigurations = fuseToolingSettingsFolder.members();
			SubMonitor subMonitor = SubMonitor.convert(monitor, potentialLaunchConfigurations.length + 1);
			for(IResource potentialLaunchConfiguration : potentialLaunchConfigurations) {
				if(potentialLaunchConfiguration.getName().endsWith(LAUNCH_CONFIGURATION_FILE_EXTENSION) && potentialLaunchConfiguration instanceof IFile) {
					replaceInLaunchConfiguration(project, (IFile)potentialLaunchConfiguration);
				}
				subMonitor.worked(1);
			}
			fuseToolingSettingsFolder.refreshLocal(IResource.DEPTH_ONE, subMonitor.split(1));
		}
	}

	private void replaceInLaunchConfiguration(IProject project, IFile fileInWhichToReplace) {
		replace(fileInWhichToReplace, project.getName(), PLACEHOLDER_PROJECTNAME_IN_LAUNCH_CONFIGURATION);
	}

	protected void replace(IFile fileInWhichToReplace, String valueRoReplace, String placeHolderToReplace) {
		Path filePathInWhichToReplace = fileInWhichToReplace.getLocation().toFile().toPath();
		String targetFileName = fileInWhichToReplace.getName().replaceAll(placeHolderToReplace, valueRoReplace);
		Path newFilePathWitReplacedValue = filePathInWhichToReplace.getParent().resolve(targetFileName);
		replace(valueRoReplace, placeHolderToReplace, filePathInWhichToReplace, newFilePathWitReplacedValue);
		cleanTemplate(filePathInWhichToReplace);
	}

	protected void replace(String valueRoReplace, String placeHolderToReplace, Path filePathInWhichToReplace, Path newFilePathWitReplacedValue) {
		try (Stream<String> lines = Files.lines(filePathInWhichToReplace)) {
			List<String> replaced = lines
					.map(line-> line.replaceAll(placeHolderToReplace, valueRoReplace))
					.collect(Collectors.toList());
			Files.write(newFilePathWitReplacedValue, replaced);
		} catch (IOException e) {
			ProjectTemplatesActivator.pluginLog().logError(e);
		}
	}

	private void cleanTemplate(Path templateLaunchConfiguration) {
		File templateLaunchConfigurationFile = templateLaunchConfiguration.toFile();
		if(!templateLaunchConfigurationFile.delete()){
			templateLaunchConfigurationFile.deleteOnExit();
		}
	}

	/**
	 * creates the datamodel used for the facet installation delegate
	 * 
	 * @param projectMetaData	the projects metadata
	 * @return	a facet configuration
	 */
	protected IDataModel getCamelFacetDataModel(NewFuseIntegrationProjectMetaData projectMetaData) {
		CamelFacetDataModelProvider dmProv = new CamelFacetDataModelProvider();
		dmProv.create();
		IDataModel dm = dmProv.getDataModel();
		dm.setStringProperty(ICamelFacetDataModelProperties.CAMEL_CONTENT_FOLDER, ICamelFacetDataModelProperties.DEFAULT_CAMEL_CONFIG_RESOURCE_FOLDER);
		dm.setStringProperty(ICamelFacetDataModelProperties.CAMEL_DSL, projectMetaData.getDslType().toString());
		dm.setStringProperty(ICamelFacetDataModelProperties.CAMEL_PROJECT_VERSION, projectMetaData.getCamelVersion());
		dm.setBooleanProperty(ICamelFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE, true);
		dm.setProperty(ICamelFacetDataModelProperties.CAMEL_PROJECT_METADATA, projectMetaData);
		return dm;
	}
	
	/**
	 * installs the given facet with the given version and config in the project
	 * 
	 * @param project		the project to install the facet into
	 * @param facetName		the name / id of the facet
	 * @param facetVersion	the facet version to use or null to use default
	 * @param config		the datamodel or null if not needed
	 * @param monitor		the progress monitor to use
	 * @throws CoreException on errors
	 */
	protected void installFacet(IProject project, String facetName, String facetVersion, IDataModel config, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
		IFacetedProject fp = ProjectFacetsManager.create(project, true, subMonitor.newChild(1));
		if (facetVersion != null) {
			fp.installProjectFacet(ProjectFacetsManager.getProjectFacet(facetName).getVersion(facetVersion), config, subMonitor.newChild(1));
		} else {
			fp.installProjectFacet(ProjectFacetsManager.getProjectFacet(facetName).getDefaultVersion(), config, subMonitor.newChild(1));
		}
	}
}
