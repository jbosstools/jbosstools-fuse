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
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.provider.ext.IDependenciesManager;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * @author lhein
 *
 */
public class MavenUtils {
	
	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	private static final String JBOSS_FUSE_PARENT = "jboss-fuse-parent";
	private static final String ORG_JBOSS_FUSE_BOM = "org.jboss.fuse.bom";
	private static final String MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION = "jboss.fuse.bom.version";
	private static final String MAVEN_PROPERTY_CAMEL_VERSION = "camel.version";
	
	/**
	 * @param plugins
	 * @param camelVersion
	 */
	public static void updateCamelVersionPlugins(Model mavenModel, List<Plugin> plugins, String camelVersion) {
		Properties properties = mavenModel.getProperties();
		if(properties != null && properties.getProperty(MAVEN_PROPERTY_CAMEL_VERSION) != null){
			properties.setProperty(MAVEN_PROPERTY_CAMEL_VERSION, camelVersion);
		} else {
			for (Plugin p : plugins) {
				if (ORG_APACHE_CAMEL.equalsIgnoreCase(p.getGroupId()) && p.getArtifactId().startsWith("camel-")) {
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
		Properties properties = mavenModel.getProperties();
		if(camelVersion.contains("redhat")){
			if(properties != null && properties.getProperty(MAVEN_PROPERTY_CAMEL_VERSION) != null){
				properties.setProperty(MAVEN_PROPERTY_CAMEL_VERSION, camelVersion);
			}
			for (Dependency dep : dependencies) {
				if (ORG_APACHE_CAMEL.equalsIgnoreCase(dep.getGroupId()) && dep.getArtifactId().startsWith("camel-")) {
					if(properties != null && properties.getProperty(MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION) != null){
						dep.setVersion(null);
					} else {
						if(properties != null && properties.getProperty(MAVEN_PROPERTY_CAMEL_VERSION) != null){
							dep.setVersion("${"+MAVEN_PROPERTY_CAMEL_VERSION+"}");
						} else {
							dep.setVersion(camelVersion);
						}
					}
				}
			}
		} else {
			if(properties != null && properties.getProperty(MAVEN_PROPERTY_CAMEL_VERSION) != null){
				properties.setProperty(MAVEN_PROPERTY_CAMEL_VERSION, camelVersion);
			}
			for (Dependency dep : dependencies) {
				if (ORG_APACHE_CAMEL.equalsIgnoreCase(dep.getGroupId()) && dep.getArtifactId().startsWith("camel-")) {
					if(!("${"+MAVEN_PROPERTY_CAMEL_VERSION+"}").equals(dep.getVersion())){
						dep.setVersion(camelVersion);
					}
				}
			}
		}
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
		if(properties != null && properties.getProperty(MAVEN_PROPERTY_JBOSS_FUSE_BOM_VERSION) != null){
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
