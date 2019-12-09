/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.launcher.run.util;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.m2e.core.MavenPlugin;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.launcher.Activator;

/**
 * MavenLaunchUtils
 * 
 * @author Igor Fedorenko
 */
public class MavenLaunchUtils {

	/**
	 * Substitute any variable
	 */
	public static String substituteVar(String s) {
		if (s == null) {
			return s;
		}
		try {
			return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(s);
		} catch (CoreException e) {
			Activator.getLogger().error("Could not substitute variable {}.", e);
			return null;
		}
	}
	
	/**
	 * checks if the packaging type is WAR
	 * 
	 * @param pathToPomXML
	 * @return
	 * @throws CoreException
	 */
	public static boolean isPackagingTypeWAR(String pathToPomXML) throws CoreException {
		return isPackagingTypeWAR(ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(pathToPomXML)));
	}
	
	/**
	 * checks if the packaging type is WAR
	 * 
	 * @param pomFile
	 * @return
	 * @throws CoreException
	 */
	public static boolean isPackagingTypeWAR(IFile pomFile) throws CoreException {
		if (pomFile == null || !pomFile.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getBundleID(), "Can't determine packaging type because given pom file reference is null!"));
		}
		Model model = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
		return "war".equalsIgnoreCase(model.getPackaging());
	}
	
	public static boolean isSpringBootProject(IFile pomFile) throws CoreException {
		if (pomFile == null || !pomFile.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getBundleID(), "Can't determine project type because given pom file reference is null!"));
		}
		Model model = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
		return CamelCatalogUtils.hasSpringBootDependency(model.getDependencies());
	}

	public static boolean isProductizedMavenPluginGroupIdUsed(IFile pomFile) throws CoreException {
		if (pomFile == null || !pomFile.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getBundleID(), "Can't determine if productized group id used because given pom file reference is null!"));
		}
		Model model = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
		Build build = model.getBuild();
		if (build != null) {
			return CamelCatalogUtils.hasProductizedMavenPluginGroupId(build.getPlugins());
		} else {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getBundleID(), "Can't determine if productized group id used because given pom file doesn't contain a build section!"));
		}
	}
}