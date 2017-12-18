/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.util;

import org.eclipse.core.runtime.IPath;

/**
 * @author lheinema
 */
public class CommonNewProjectMetaData {
	private String projectName;
	private IPath locationPath;
	
	/**
	 * @return the locationPath
	 */
	public IPath getLocationPath() {
		return this.locationPath;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return this.projectName;
	}
	
	/**
	 * @param locationPath the locationPath to set
	 */
	public void setLocationPath(IPath locationPath) {
		this.locationPath = locationPath;
	}
	
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
