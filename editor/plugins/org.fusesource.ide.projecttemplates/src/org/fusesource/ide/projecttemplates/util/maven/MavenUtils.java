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

package org.fusesource.ide.projecttemplates.util.maven;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.fusesource.ide.camel.editor.provider.ToolBehaviourProvider;
import org.fusesource.ide.camel.editor.provider.ext.IDependenciesManager;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.camel.model.service.core.util.FuseBomFilter;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.StagingRepositoriesConstants;
import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.util.ProjectTemplatePatcher;

/**
 * @author lhein
 *
 */
public class MavenUtils {
	
	private static final String REDHAT_NAMING_USED_IN_VERSION = "redhat";
	private static final String FUSE_NAMING_USED_IN_VERSION = "fuse";
	private static final String CAMEL_ARTIFACT_ID_PREFIX = "camel-";
	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	private static final String MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION = "jboss.fuse.bom.version";
	private static final String MAVEN_PROPERTY_CAMEL_VERSION = "camel.version";
	private static final String MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE = "${"+MAVEN_PROPERTY_CAMEL_VERSION+"}";
	
	private MavenUtils() {
		// private
	}
	
	public static void updateContributedPlugins(List<Plugin> plugins, String camelVersion) {
		for (IConfigurationElement e : getExtensionPoints()) {
			try {
				if(e.getAttribute(IDependenciesManager.EXT_POINT_NAME) != null){
					Object o = e.createExecutableExtension(IDependenciesManager.EXT_POINT_NAME);
					if (o instanceof IDependenciesManager) {
						IDependenciesManager dm = (IDependenciesManager) o;
						dm.updatePluginDependencies(plugins, camelVersion);
					}
				}
			} catch (CoreException e1) {
				ProjectTemplatesActivator.pluginLog().logError(e1);
			}
		}
	}

	private static IConfigurationElement[] getExtensionPoints() {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(ToolBehaviourProvider.PALETTE_ENTRY_PROVIDER_EXT_POINT_ID);
	}

	/**
	 * @param plugins
	 * @param camelVersion
	 */
	public static void updateCamelVersionPlugins(Model mavenModel, List<Plugin> plugins, String camelVersion) {
		Properties properties = mavenModel.getProperties();
		if(isMavenPropertyCamelVersionSet(properties)){
			properties.setProperty(MAVEN_PROPERTY_CAMEL_VERSION, camelVersion);
		}
		if(camelVersion.contains(REDHAT_NAMING_USED_IN_VERSION) || camelVersion.contains(FUSE_NAMING_USED_IN_VERSION)) { 
			updatePluginVersionsForProductizedVersion(plugins, camelVersion, properties);
		} else {
			updatePluginVersionForNonProductizedVersion(plugins, camelVersion);
		}
	}
	
	private static void updatePluginVersionForNonProductizedVersion(List<Plugin> plugins, String camelVersion) {
		for (Plugin p : plugins) {
			if (isCamelPlugin(p) && !MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE.equals(p.getVersion())) {
				p.setVersion(camelVersion);
			}
		}
	}
	
	private static void updatePluginVersionsForProductizedVersion(List<Plugin> plugins, String camelVersion, Properties properties) {
		for (Plugin p : plugins) {
			if (isCamelPlugin(p)) {
				//TODO : when io.fabric8 bug is fixed, need to check for camel version.
				/* If inferior to the version in which it is fixed, keep the behavior of this commit,
				 * if upper to the version in which it is fixed, get back to the previous behavior
				 * */
				if(isMavenPropertyCamelVersionSet(properties)){
					p.setVersion(MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE);
				} else {
					p.setVersion(camelVersion);
				}					
			}
		}
	}
	
	/**
	 * @param dependencies
	 * @param camelVersion
	 */
	public static void updateCamelVersionDependencies(Model mavenModel, List<Dependency> dependencies, String camelVersion) {
		Properties properties = mavenModel.getProperties();
		if(isMavenPropertyCamelVersionSet(properties)){
			properties.setProperty(MAVEN_PROPERTY_CAMEL_VERSION, camelVersion);
		}
		if(camelVersion.contains(REDHAT_NAMING_USED_IN_VERSION) || camelVersion.contains(FUSE_NAMING_USED_IN_VERSION)){
			updateDependenciesForProductizedVersion(mavenModel, dependencies, camelVersion, properties);
		} else {
			updateDependenciesForNonProductizedVersion(dependencies, camelVersion);
		}
	}
	
	private static void updateDependenciesForNonProductizedVersion(List<Dependency> dependencies, String camelVersion) {
		for (Dependency dep : dependencies) {
			if (isCamelDependency(dep) && !MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE.equals(dep.getVersion())){
				dep.setVersion(camelVersion);
			}
		}
	}
	
	private static void updateDependenciesForProductizedVersion(Model mavenModel, List<Dependency> dependencies, String camelVersion, Properties properties) {
		for (Dependency dep : dependencies) {
			if (isCamelDependency(dep)) {
				if(isMavenPropertyFuseBomVersionSet(properties) && mavenModel.getDependencyManagement() != null && isFuseBomImported(mavenModel.getDependencyManagement().getDependencies()) && !CamelCatalogUtils.isPureFISVersion(camelVersion)) {
					dep.setVersion(null);
				} else if(isMavenPropertyCamelVersionSet(properties)){
					dep.setVersion(MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE);
				} else {
					dep.setVersion(camelVersion);
				}
			}
		}
	}
	
	private static boolean isFuseBomImported(List<Dependency> dependencies) {
		for (Dependency dep : dependencies) {
			if (isFuseBomImportDependency(dep)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isFuseBomImportDependency(Dependency dep) {
		return new FuseBomFilter().test(dep);
	}
	
	private static boolean isCamelDependency(Dependency dep) {
		return ORG_APACHE_CAMEL.equalsIgnoreCase(dep.getGroupId()) && dep.getArtifactId().startsWith(CAMEL_ARTIFACT_ID_PREFIX);
	}
	
	private static boolean isCamelPlugin(Plugin plugin) {
		return ORG_APACHE_CAMEL.equalsIgnoreCase(plugin.getGroupId()) && plugin.getArtifactId().startsWith(CAMEL_ARTIFACT_ID_PREFIX);
	}

	private static boolean isMavenPropertyFuseBomVersionSet(Properties properties) {
		return properties != null && properties.getProperty(MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION) != null;
	}

	private static boolean isMavenPropertyCamelVersionSet(Properties properties) {
		return properties != null && properties.getProperty(MAVEN_PROPERTY_CAMEL_VERSION) != null;
	}

	public static void updateContributedDependencies(List<Dependency> dependencies, String camelVersion) {
		for (IConfigurationElement e : getExtensionPoints()) {
			try {
				if(e.getAttribute(IDependenciesManager.EXT_POINT_NAME) != null){
					Object o = e.createExecutableExtension(IDependenciesManager.EXT_POINT_NAME);
					if (o instanceof IDependenciesManager) {
						IDependenciesManager dm = (IDependenciesManager) o;
						dm.updateDependencies(dependencies, camelVersion);
					}
				}
			} catch (CoreException e1) {
				ProjectTemplatesActivator.pluginLog().logError(e1);
			}
		}
	}
	
	/**
	 * @param repositories
	 * @param repoURI
	 * @param newId
	 * @return if a modification has been done on the model
	 */
	public static boolean ensureRepositoryExists(List<Repository> repositories, String repoURI, String newId) {
		boolean exists = false;
		for (Repository rep : repositories) {
			if (rep.getUrl().equalsIgnoreCase(repoURI)) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			Repository repo = new Repository();
			repo.setId(newId);
			repo.setUrl(repoURI);
			repositories.add(repo);
		}
		return !exists;
	}

	/**
	 * Align the Fuse Version to the Camel version
	 * 
	 * @param mavenModel
	 * @param camelVersion
	 * @param project 
	 * @param monitor 
	 */
	public static void alignFuseRuntimeVersion(Model mavenModel, String camelVersion, IProject project, IProgressMonitor monitor) {
		String fuseVersionForCamelVersion = CamelCatalogUtils.getBomVersionForCamelVersion(camelVersion, project, mavenModel, monitor);
		if (fuseVersionForCamelVersion != null) {
			Properties properties = mavenModel.getProperties();
			if(isMavenPropertyFuseBomVersionSet(properties) && mavenModel.getDependencyManagement() != null && isFuseBomImported(mavenModel.getDependencyManagement().getDependencies())) {
				properties.setProperty(MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION, fuseVersionForCamelVersion);
			} else {
				alignFuseRuntimeVersionForNonBOMUsage(mavenModel, project, camelVersion, monitor);
			}
		}
	}

	private static void alignFuseRuntimeVersionForNonBOMUsage(Model mavenModel, IProject project, String camelVersion, IProgressMonitor monitor) {
		if(mavenModel.getDependencyManagement() != null){
			for(Dependency dependency : mavenModel.getDependencyManagement().getDependencies()){
				if(isFuseBomImportDependency(dependency)){
					dependency.setVersion(CamelCatalogUtils.getBomVersionForCamelVersion(camelVersion, project, mavenModel, monitor));
					return;
				}
			}
		}
	}
	
	/**
	 * If staging repositories enabled, ensure to have them in the Maven model.
	 * 
	 * @param mavenModel
	 * 
	 * @return if a modification has been done on the model
	 */
	public static boolean manageStagingRepositories(Model mavenModel) {
		if(new StagingRepositoriesPreferenceInitializer().isStagingRepositoriesEnabled()){
			String stagingRepoString = PreferenceManager.getInstance().loadPreferenceAsString(StagingRepositoriesConstants.STAGING_REPOSITORIES);
			boolean hasBeenUpdated = false;
			for(List<String> nameURlPair : new StagingRepositoriesPreferenceInitializer().getStagingRepositoriesAsList(stagingRepoString)) {
				String repoURI = nameURlPair.get(1);
				hasBeenUpdated |= MavenUtils.ensureRepositoryExists(mavenModel.getRepositories(), repoURI, nameURlPair.get(0));
				hasBeenUpdated |= MavenUtils.ensureRepositoryExists(mavenModel.getPluginRepositories(), repoURI, nameURlPair.get(0));
			}
			return hasBeenUpdated;
		}
		return false;
	}
	
	/**
	 * changes all occurrences of Camel version in the pom.xml file with the
	 * version defined in the wizard
	 * 
	 * @param project			the project
	 * @param projectMetaData	the metadata containing the new version
	 * @param monitor			the progress monitor
	 * @return	true on success, otherwise false
	 */
	public static boolean configureCamelVersionForProject(IProject project, String camelVersion, IProgressMonitor monitor) {
		return configurePomCamelVersion(project, null, camelVersion, monitor);
	}
	
	/**
	 * adds staging repositories to the pom file if enabled
	 * 
	 * @param project
	 * @param monitor
	 * @return
	 */
	public static boolean configureStagingRepositories(IProject project, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor,Messages.mavenTemplateConfiguratorAddingStagingRepositories, 3);
		try {
			File pomFile = new File(project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toOSString()); //$NON-NLS-1$
			Model m2m = new CamelMavenUtils().getMavenModel(project);
			subMonitor.setWorkRemaining(2);

			MavenUtils.manageStagingRepositories(m2m);
			subMonitor.setWorkRemaining(1);

			new org.fusesource.ide.camel.editor.utils.MavenUtils().writeNewPomFile(project, pomFile, m2m, subMonitor.split(1));
		} catch (Exception ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
			return false;
		}
		return true;
	}
	
	/**
	 * changes all occurrences of Camel version in the pom.xml file with the
	 * version defined in the wizard
	 * 
	 * @param project			the project
	 * @param projectMetaData	the metadata containing the new version
	 * @param monitor			the progress monitor
	 * @return	true on success, otherwise false
	 */
	public static boolean configurePomCamelVersion(IProject project, CommonNewProjectMetaData projectMetaData, String camelVersion, IProgressMonitor monitor) {
		String newCamelVersion = Strings.isBlank(camelVersion) && projectMetaData != null && !Strings.isBlank(projectMetaData.getCamelVersion()) ? projectMetaData.getCamelVersion() : camelVersion;
		SubMonitor subMonitor = SubMonitor.convert(monitor,Messages.mavenTemplateConfiguratorAdaptingprojectToCamelVersionMonitorMessage, 8);
		try {
			File pomFile = new File(project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toOSString()); //$NON-NLS-1$
			Model m2m = new CamelMavenUtils().getMavenModel(project);
			subMonitor.setWorkRemaining(7);
			if (m2m.getDependencyManagement() != null) {
				MavenUtils.updateCamelVersionDependencies(m2m, m2m.getDependencyManagement().getDependencies(), newCamelVersion);
			}
			subMonitor.setWorkRemaining(6);
			MavenUtils.updateCamelVersionDependencies(m2m, m2m.getDependencies(), newCamelVersion);
			if (m2m.getBuild().getPluginManagement() != null) {
				MavenUtils.updateCamelVersionPlugins(m2m, m2m.getBuild().getPluginManagement().getPlugins(), newCamelVersion);
			}
			subMonitor.setWorkRemaining(5);
			MavenUtils.updateCamelVersionPlugins(m2m, m2m.getBuild().getPlugins(), newCamelVersion);
			subMonitor.setWorkRemaining(4);
			
			MavenUtils.alignFuseRuntimeVersion(m2m, newCamelVersion, project, subMonitor.split(1));
			
			MavenUtils.manageStagingRepositories(m2m);
			subMonitor.setWorkRemaining(2);
			
			if (projectMetaData != null) {
				ProjectTemplatePatcher patcher = new ProjectTemplatePatcher(projectMetaData);
				patcher.patch(m2m, subMonitor.split(1));
			}
			subMonitor.setWorkRemaining(1);
			
			new org.fusesource.ide.camel.editor.utils.MavenUtils().writeNewPomFile(project, pomFile, m2m, subMonitor.split(1));
		} catch (Exception ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
			return false;
		}
		return true;
	}
}
