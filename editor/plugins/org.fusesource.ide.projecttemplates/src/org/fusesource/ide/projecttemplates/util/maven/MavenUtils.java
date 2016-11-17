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
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * @author lhein
 *
 */
public class MavenUtils {
	
	private static final String REDHAT_NAMING_USED_IN_VERSION = "redhat";
	private static final String CAMEL_ARTIFACT_ID_PREFIX = "camel-";
	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	private static final String JBOSS_FUSE_PARENT = "jboss-fuse-parent";
	private static final String ORG_JBOSS_FUSE_BOM = "org.jboss.fuse.bom";
	private static final String MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION = "jboss.fuse.bom.version";
	private static final String MAVEN_PROPERTY_CAMEL_VERSION = "camel.version";
	private static final String MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE = "${"+MAVEN_PROPERTY_CAMEL_VERSION+"}";
	
	/**
	 * @param plugins
	 * @param camelVersion
	 */
	public static void updateCamelVersionPlugins(Model mavenModel, List<Plugin> plugins, String camelVersion) {
		Properties properties = mavenModel.getProperties();
		// TODO: remove me after 2.18.1 went GA
		if (camelVersion.equalsIgnoreCase("2.18.1")) {
			camelVersion = "2.18.1-SNAPSHOT";
		}
		if(isMavenPropertyCamelVersionSet(properties)){
			properties.setProperty(MAVEN_PROPERTY_CAMEL_VERSION, camelVersion);
		} else {
			for (Plugin p : plugins) {
				if (ORG_APACHE_CAMEL.equalsIgnoreCase(p.getGroupId()) && p.getArtifactId().startsWith(CAMEL_ARTIFACT_ID_PREFIX)) {
					p.setVersion(camelVersion);
				}
			}
		}
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
	 * @param dependencies
	 * @param camelVersion
	 */
	public static void updateCamelVersionDependencies(Model mavenModel, List<Dependency> dependencies, String camelVersion) {
		// TODO: remove me after 2.18.1 went GA
		if (camelVersion.equalsIgnoreCase("2.18.1")) {
			camelVersion = "2.18.1-SNAPSHOT";
		}
		Properties properties = mavenModel.getProperties();
		if(isMavenPropertyCamelVersionSet(properties)){
			properties.setProperty(MAVEN_PROPERTY_CAMEL_VERSION, camelVersion);
		}
		if(camelVersion.contains(REDHAT_NAMING_USED_IN_VERSION)){
			for (Dependency dep : dependencies) {
				if (isCamelDependency(dep)) {
					if(isMavenPropertyFuseBomVersionSet(properties)){
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
				if (isCamelDependency(dep)
						&& !MAVEN_PROPERTY_CAMEL_VERSION_REFERENCE.equals(dep.getVersion())){
					dep.setVersion(camelVersion);
				}
			}
		}
	}

	private static boolean isCamelDependency(Dependency dep) {
		return ORG_APACHE_CAMEL.equalsIgnoreCase(dep.getGroupId()) && dep.getArtifactId().startsWith(CAMEL_ARTIFACT_ID_PREFIX);
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
	 * 
	 * @param repositories
	 * @param repoURI
	 * @param newId
	 */
	public static void ensureRepositoryExists(List<Repository> repositories, String repoURI, String newId) {
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
	}

	/**
	 * Align the Fuse Version to the Camel version
	 * 
	 * @param mavenModel
	 * @param camelVersion
	 */
	public static void alignFuseRuntimeVersion(Model mavenModel, String camelVersion) {
		Properties properties = mavenModel.getProperties();
		if(isMavenPropertyFuseBomVersionSet(properties)){
			properties.setProperty(MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION, CamelModelFactory.getFuseVersionForCamelVersion(camelVersion));
		} else {
			if(mavenModel.getDependencyManagement() != null){
				for(Dependency dependency : mavenModel.getDependencyManagement().getDependencies()){
					if(ORG_JBOSS_FUSE_BOM.equals(dependency.getGroupId()) && JBOSS_FUSE_PARENT.equals(dependency.getArtifactId())){
						dependency.setVersion(CamelModelFactory.getFuseVersionForCamelVersion(camelVersion));
						return;
					}
				}
			}
		}
	}

}
