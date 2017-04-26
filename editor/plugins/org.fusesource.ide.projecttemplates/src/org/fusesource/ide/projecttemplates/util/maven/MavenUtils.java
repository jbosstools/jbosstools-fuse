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

import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.fusesource.ide.camel.editor.provider.ToolBehaviourProvider;
import org.fusesource.ide.camel.editor.provider.ext.IDependenciesManager;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.preferences.initializer.StagingRepositoriesPreferenceInitializer;

/**
 * @author lhein
 *
 */
public class MavenUtils {
	
	private static final String REDHAT_NAMING_USED_IN_VERSION = "redhat";
	private static final String FUSE_NAMING_USED_IN_VERSION = "fuse";
	private static final String CAMEL_ARTIFACT_ID_PREFIX = "camel-";
	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	private static final String JBOSS_FUSE_PARENT = "jboss-fuse-parent";
	private static final String ORG_JBOSS_FUSE_BOM = "org.jboss.fuse.bom";
	private static final String MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION = "jboss.fuse.bom.version";
	private static final String MAVEN_PROPERTY_CAMEL_VERSION = "camel.version";
	private static final String MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE = "${"+MAVEN_PROPERTY_CAMEL_VERSION+"}";
	
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
			for (Plugin p : plugins) {
				if (isCamelPlugin(p)) {
					if(isMavenPropertyFuseBomVersionSet(properties) && mavenModel.getDependencyManagement() != null && isFuseBomImported(mavenModel.getDependencyManagement().getDependencies()) && !CamelCatalogUtils.isPureFISVersion(camelVersion)) {
						p.setVersion(null);
					} else if(isMavenPropertyCamelVersionSet(properties)){
						p.setVersion(MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE);
					} else {
						p.setVersion(camelVersion);
					}					
				}
			}
		} else {
			for (Plugin p : plugins) {
				if (isCamelPlugin(p) && !MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE.equals(p.getVersion())) {
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
		} else {
			for (Dependency dep : dependencies) {
				if (isCamelDependency(dep) && !MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE.equals(dep.getVersion())){
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
		return 	ORG_JBOSS_FUSE_BOM.equals(dep.getGroupId()) &&
				JBOSS_FUSE_PARENT.equals(dep.getArtifactId()) &&
				"pom".equals(dep.getType()) &&
				"import".equals(dep.getScope());
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
	 */
	public static void alignFuseRuntimeVersion(Model mavenModel, String camelVersion) {
		if (CamelCatalogUtils.getFuseVersionForCamelVersion(camelVersion) != null) {
			Properties properties = mavenModel.getProperties();
			if(isMavenPropertyFuseBomVersionSet(properties) && mavenModel.getDependencyManagement() != null && isFuseBomImported(mavenModel.getDependencyManagement().getDependencies())) {
				properties.setProperty(MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION, CamelCatalogUtils.getFuseVersionForCamelVersion(camelVersion));
			} else {
				if(mavenModel.getDependencyManagement() != null){
					for(Dependency dependency : mavenModel.getDependencyManagement().getDependencies()){
						if(isFuseBomImportDependency(dependency)){
							dependency.setVersion(CamelCatalogUtils.getFuseVersionForCamelVersion(camelVersion));
							return;
						}
					}
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
			boolean hasBeenUpdated = false;
			for(List<String> nameURlPair : new StagingRepositoriesPreferenceInitializer().getStagingRepositories()){
				String repoURI = nameURlPair.get(1);
				hasBeenUpdated |= MavenUtils.ensureRepositoryExists(mavenModel.getRepositories(), repoURI, nameURlPair.get(0));
				hasBeenUpdated |= MavenUtils.ensureRepositoryExists(mavenModel.getPluginRepositories(), repoURI, nameURlPair.get(0));
			}
			return hasBeenUpdated;
		}
		return false;
	}

}
