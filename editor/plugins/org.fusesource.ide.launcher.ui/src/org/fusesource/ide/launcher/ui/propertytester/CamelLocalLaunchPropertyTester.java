/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.propertytester;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.fusesource.ide.launcher.ui.Activator;

public class CamelLocalLaunchPropertyTester extends PropertyTester {

	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	private static final String ORG_JBOSS_REDHAT_FUSE = "org.jboss.redhat-fuse";
	private static final String CAMEL_MAVEN_PLUGIN = "camel-maven-plugin";
	private static final String ORG_SPRINGFRAMEWORK_BOOT = "org.springframework.boot";
	private static final String SPRING_BOOT_MAVEN_PLUGIN = "spring-boot-maven-plugin";	
	public static final String IS_LOCAL_LAUNCH_AVAILABLE = "isLocalLaunchAvailable";

	public CamelLocalLaunchPropertyTester() {
		// Keep for reflection instantiation
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if(receiver instanceof IAdaptable){
			IResource resource = ((IAdaptable) receiver).getAdapter(IResource.class);
			if(resource != null && IS_LOCAL_LAUNCH_AVAILABLE.equals(property)){
				Model mavenModel = retrieveMavenModel(resource);
				if(mavenModel != null){
					List<Plugin> plugins = retrievePlugins(mavenModel);
					return containsCamelMavenPlugin(plugins) || containsSpringBootMavenPlugin(plugins);
				}
			}
		}
		return false;
	}

	Model retrieveMavenModel(IResource receiver) {
		IProject project = receiver.getProject();
		IMavenProjectFacade facade = retrieveMavenProjectFacade(project);
		if(facade != null){
			try {
				return facade.getMavenProject(new NullProgressMonitor()).getModel();
			} catch (CoreException e) {
				Activator.getLogger().error(e);
			}
		}
		return null;
	}

	private IMavenProjectFacade retrieveMavenProjectFacade(IProject project) {
		IMavenProjectRegistry mavenProjectRegistry = MavenPlugin.getMavenProjectRegistry();
		IMavenProjectFacade facade = mavenProjectRegistry.getProject(project);
		if(facade == null){
			//if not available in Maven cache, try to create the Maven project facade
			facade = mavenProjectRegistry.create(project, new NullProgressMonitor());
		}
		return facade;
	}

	private List<Plugin> retrievePlugins(Model mavenModel) {
		Build build = mavenModel.getBuild();
		List<Plugin> plugins = new ArrayList<>();
		if(build != null){
			plugins.addAll(build.getPlugins());
			PluginManagement pluginManagement = build.getPluginManagement();
			if(pluginManagement != null){
				plugins.addAll(pluginManagement.getPlugins());
			}
		}
		return plugins;
	}

	private boolean containsCamelMavenPlugin(List<Plugin> plugins) {
		return plugins.parallelStream().anyMatch(this::isCamelMavenPlugin);
	}
	
	private boolean containsSpringBootMavenPlugin(List<Plugin> plugins) {
		return plugins.parallelStream().anyMatch(this::isSpringBootMavenPlugin);
	}
	
	private boolean isCamelMavenPlugin(Plugin plugin) {
		String groupId = plugin.getGroupId();
		return (ORG_APACHE_CAMEL.equals(groupId) || ORG_JBOSS_REDHAT_FUSE.equals(groupId))
				&& CAMEL_MAVEN_PLUGIN.equals(plugin.getArtifactId());
	}

	private boolean isSpringBootMavenPlugin(Plugin plugin) {
		String groupId = plugin.getGroupId();
		return (ORG_SPRINGFRAMEWORK_BOOT.equals(groupId) || ORG_JBOSS_REDHAT_FUSE.equals(groupId)) 
				&& SPRING_BOOT_MAVEN_PLUGIN.equals(plugin.getArtifactId());
	}
}
