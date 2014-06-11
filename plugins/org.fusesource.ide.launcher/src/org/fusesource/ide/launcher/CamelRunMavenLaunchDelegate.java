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

package org.fusesource.ide.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.fusesource.ide.commons.util.CamelUtils;


public class CamelRunMavenLaunchDelegate extends FuseMavenLaunchDelegate {
	
	public CamelRunMavenLaunchDelegate() {
		super(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.MavenLaunchDelegate#getGoals(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	protected String getGoals(ILaunchConfiguration configuration)
			throws CoreException {
		String filePath = configuration.getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, "");
		if (filePath.trim().length()>0) {
			if (!filePath.trim().startsWith("file:")) {
				filePath = String.format("%s%s", CamelContextLaunchConfigConstants.ATTR_PROTOCOL_PREFIX, filePath.trim());
			}
			boolean isBluePrint = CamelUtils.isBlueprintFile(filePath);
			filePath = String.format(" -D%s=%s", CamelContextLaunchConfigConstants.ATTR_CONTEXT_FILE, filePath);
			if (isBluePrint) {
				filePath = String.format("%s -D%s", filePath, CamelContextLaunchConfigConstants.BLUEPRINT_CONTEXT);
			}
		}
		return super.getGoals(configuration) + filePath;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.MavenLaunchDelegate#getEclipseProcessName()
	 */
	@Override
	public String getEclipseProcessName() {
		return "Local Camel Context";
	}
}
