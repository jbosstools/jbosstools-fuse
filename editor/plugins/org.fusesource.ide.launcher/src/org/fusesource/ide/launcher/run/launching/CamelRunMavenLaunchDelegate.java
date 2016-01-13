/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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
		boolean isBluePrint = false;
		String newGoalsAdditionForFile = null;
		
		// we need to check for WAR packaging...as this needs Jetty:run instead of Camel:Run, and we 
		// check if its a blueprint or a spring context
		String filePath = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(configuration);
		if (filePath.trim().length()>0) {
			isBluePrint = CamelUtils.isBlueprintFile(filePath);

			if (filePath.trim().length()>0) {
				newGoalsAdditionForFile = String.format(" -D%s=file:%s", CamelContextLaunchConfigConstants.ATTR_CONTEXT_FILE, filePath);
				if (isBluePrint) {
					newGoalsAdditionForFile = String.format("%s -D%s", newGoalsAdditionForFile, CamelContextLaunchConfigConstants.BLUEPRINT_CONTEXT);
				}
			}
			
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(filePath));
			if (f != null) {
				IProject p = f.getProject();
				pomFile = p.getFile("pom.xml");
			}
		}
		
		if (MavenLaunchUtils.isPackagingTypeWAR(pomFile)) {
			setGoals(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
		} else {
			setGoals(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
		}
		return super.getGoals(configuration) + newGoalsAdditionForFile;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.MavenLaunchDelegate#getEclipseProcessName()
	 */
	@Override
	public String getEclipseProcessName() {
		return "Local Camel Context";
	}
}
