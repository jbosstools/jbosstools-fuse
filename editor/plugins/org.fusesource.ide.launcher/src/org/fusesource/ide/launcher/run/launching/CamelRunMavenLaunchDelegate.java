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

package org.fusesource.ide.launcher.run.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.fusesource.ide.launcher.run.util.MavenLaunchUtils;


public class CamelRunMavenLaunchDelegate extends FuseMavenLaunchDelegate {
	
	public CamelRunMavenLaunchDelegate() {
		super(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_ALL);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.MavenLaunchDelegate#getGoals(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	protected String getGoals(ILaunchConfiguration configuration)
			throws CoreException {
		
		IFile pomFile = null;
		String newGoalsAdditionForFile = null;
		
		String filePath = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(configuration);
		if (filePath.trim().length()>0) {
			boolean isBluePrint = CamelUtils.isBlueprintFile(filePath);

			if (filePath.trim().length()>0) {
				newGoalsAdditionForFile = String.format(" -D%s=\"file:%s\"", CamelContextLaunchConfigConstants.ATTR_CONTEXT_FILE, filePath);
				if (isBluePrint) {
					newGoalsAdditionForFile = String.format("%s -D%s", newGoalsAdditionForFile, CamelContextLaunchConfigConstants.BLUEPRINT_CONTEXT);
				}
			}
			
			IFile f = getFileInWorkspace(filePath);
			if (f != null) {
				IProject p = f.getProject();
				pomFile = p.getFile(IMavenConstants.POM_FILE_NAME);
			}
		}
		
		if (isSpringBoot(pomFile)) {
			if (isProductizedMavenPluginUsed(pomFile)) {
				setGoals(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_SPRINGBOOT_FUSE_PRODUCTIZED);
			} else {
				setGoals(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_SPRINGBOOT);
			}
		} else if (isWarPackaging(pomFile)) {
			setGoals(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
		} else {
			if (isProductizedMavenPluginUsed(pomFile)) {
				setGoals(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR_FUSE_PRODUCTIZED);
			} else {
				setGoals(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
			}

		}
		return "-U " + super.getGoals(configuration) + newGoalsAdditionForFile;
	}

	protected boolean isProductizedMavenPluginUsed(IFile pomFile) throws CoreException {
		return MavenLaunchUtils.isProductizedMavenPluginGroupIdUsed(pomFile);
	}

	/**
	 * @param pomFile
	 * @return
	 * @throws CoreException
	 */
	protected boolean isWarPackaging(IFile pomFile) throws CoreException {
		return MavenLaunchUtils.isPackagingTypeWAR(pomFile);
	}

	protected boolean isSpringBoot(IFile pomFile) throws CoreException {
		return MavenLaunchUtils.isSpringBootProject(pomFile);
	}
	
	/**
	 * @param filePath
	 * @return
	 */
	protected IFile getFileInWorkspace(String filePath) {
		return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(filePath));
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.MavenLaunchDelegate#getEclipseProcessName()
	 */
	@Override
	public String getEclipseProcessName() {
		return "Local Camel Context";
	}
}
