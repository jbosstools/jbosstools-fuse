/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.util;

import org.eclipse.core.runtime.IPath;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;

/**
 * @author lheinema
 */
public class CommonNewProjectMetaData {
	
	private String camelVersion;
	private String projectName;
	private IPath locationPath;
	private AbstractProjectTemplate template;
	
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
	
	/**
	 * returns the camel version to be used
	 * 
	 * @return	the camel version
	 */
	public String getCamelVersion() {
		return this.camelVersion;
	}
	
	/**
	 * sets the camel version to be used
	 * 
	 * @param camelVersion
	 */
	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}
	
	/**
	 * returns the template to be used in the project creation
	 * 
	 * @return	the template to use or null
	 */
	public AbstractProjectTemplate getTemplate() {
		return this.template;
	}
	
	/**
	 * sets the template to be used in the project creation
	 * 
	 * @param template	the template to use
	 */
	public void setTemplate(AbstractProjectTemplate template) {
		this.template = template;
	}
}
