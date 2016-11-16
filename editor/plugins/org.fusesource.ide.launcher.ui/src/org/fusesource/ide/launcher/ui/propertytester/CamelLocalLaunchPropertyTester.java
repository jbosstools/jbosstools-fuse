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
import org.fusesource.ide.launcher.ui.Activator;

public class CamelLocalLaunchPropertyTester extends PropertyTester {

	private static final String CAMEL_MAVEN_PLUGIN = "camel-maven-plugin";
	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	static final String IS_LOCAL_LAUNCH_AVAILABLE = "isLocalLaunchAvailable";

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
					return containsCamelMavenPlugin(plugins);
				}
			}
		}
		return false;
	}

	Model retrieveMavenModel(IResource receiver) {
		IProject project = receiver.getProject();
		IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().getProject(project);
		try {
			return facade.getMavenProject(new NullProgressMonitor()).getModel();
		} catch (CoreException e) {
			Activator.getLogger().error(e);
		}
		return null;
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
		return plugins.parallelStream().filter(this::isCamelMavenPlugin).findFirst().isPresent();
	}
	
	private boolean isCamelMavenPlugin(Plugin plugin) {
		return ORG_APACHE_CAMEL.equals(plugin.getGroupId()) && CAMEL_MAVEN_PLUGIN.equals(plugin.getArtifactId());
	}

}
