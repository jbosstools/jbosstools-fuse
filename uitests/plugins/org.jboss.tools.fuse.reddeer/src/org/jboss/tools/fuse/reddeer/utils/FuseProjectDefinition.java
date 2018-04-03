/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.utils;

import java.util.Arrays;

import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType;

/**
 * Represents a Fuse Integration project
 * 
 * @author tsedmik
 */
public class FuseProjectDefinition {

	private NewFuseIntegrationProjectWizardRuntimeType runtimeType;
	private NewFuseIntegrationProjectWizardDeploymentType deploymentType;
	private String[] template;
	private String camelVersion;
	private ProjectType dsl;

	public FuseProjectDefinition(NewFuseIntegrationProjectWizardRuntimeType runtimeType,
			NewFuseIntegrationProjectWizardDeploymentType deploymentType, String[] template, String camelVersion, ProjectType dsl) {
		super();
		this.runtimeType = runtimeType;
		this.deploymentType = deploymentType;
		this.template = template;
		this.camelVersion = camelVersion;
		this.dsl = dsl;
	}

	public String getCamelVersion() {
		return camelVersion;
	}

	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}

	public NewFuseIntegrationProjectWizardRuntimeType getRuntimeType() {
		return runtimeType;
	}

	public void setRuntimeType(NewFuseIntegrationProjectWizardRuntimeType runtimeType) {
		this.runtimeType = runtimeType;
	}

	public NewFuseIntegrationProjectWizardDeploymentType getDeploymentType() {
		return deploymentType;
	}

	public void setDeploymentType(NewFuseIntegrationProjectWizardDeploymentType deploymentType) {
		this.deploymentType = deploymentType;
	}

	public String[] getTemplate() {
		return template;
	}

	public void setTemplate(String[] template) {
		this.template = template;
	}

	public ProjectType getDsl() {
		return dsl;
	}

	public void setDsl(ProjectType dsl) {
		this.dsl = dsl;
	}

	@Override
	public String toString() {
		return "FuseProjectDefinition [runtimeType=" + runtimeType + ", deploymentType=" + deploymentType
				+ ", camelVersion=" + camelVersion + ", template=" + Arrays.toString(template) + "]";
	}

	
}
