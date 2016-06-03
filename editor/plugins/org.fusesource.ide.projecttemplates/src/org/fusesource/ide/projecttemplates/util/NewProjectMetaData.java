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
package org.fusesource.ide.projecttemplates.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IRuntime;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;

/**
 * @author lhein
 */
public class NewProjectMetaData {
	private String projectName;
	private String camelVersion;
	private IRuntime targetRuntime;
	private CamelDSLType dslType;
	private boolean blankProject;
	private IPath locationPath;
	private AbstractProjectTemplate template;
	
	/**
	 * @return the template
	 */
	public AbstractProjectTemplate getTemplate() {
		return this.template;
	}
	
	/**
	 * @return the locationPath
	 */
	public IPath getLocationPath() {
		return this.locationPath;
	}
	
	/**
	 * @return the camelVersion
	 */
	public String getCamelVersion() {
		return this.camelVersion;
	}
	
	/**
	 * @return the dslType
	 */
	public CamelDSLType getDslType() {
		return this.dslType;
	}
	
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return this.projectName;
	}
	
	/**
	 * @return the targetRuntime
	 */
	public IRuntime getTargetRuntime() {
		return this.targetRuntime;
	}
	
	/**
	 * @return the blankProject
	 */
	public boolean isBlankProject() {
		return this.blankProject;
	}
	
	/**
	 * @param template the template to set
	 */
	public void setTemplate(AbstractProjectTemplate template) {
		this.template = template;
	}
	
	/**
	 * @param locationPath the locationPath to set
	 */
	public void setLocationPath(IPath locationPath) {
		this.locationPath = locationPath;
	}
	
	/**
	 * @param camelVersion the camelVersion to set
	 */
	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}
	
	/**
	 * @param dslType the dslType to set
	 */
	public void setDslType(CamelDSLType dslType) {
		this.dslType = dslType;
	}
	
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	/**
	 * @param targetRuntime the targetRuntime to set
	 */
	public void setTargetRuntime(IRuntime targetRuntime) {
		this.targetRuntime = targetRuntime;
	}
	
	/**
	 * @param blankProject the blankProject to set
	 */
	public void setBlankProject(boolean blankProject) {
		this.blankProject = blankProject;
	}
}
