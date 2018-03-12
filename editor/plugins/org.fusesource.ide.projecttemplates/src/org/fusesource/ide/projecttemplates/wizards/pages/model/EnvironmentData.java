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
package org.fusesource.ide.projecttemplates.wizards.pages.model;

public class EnvironmentData {
	
	private String camelVersion;
	private FuseDeploymentPlatform deploymentPlatform;
	private FuseRuntimeKind fuseRuntime;

	public EnvironmentData(String camelVersion, FuseDeploymentPlatform deploymentPlatform, FuseRuntimeKind fuseRuntime) {
		this.camelVersion = camelVersion;
		this.deploymentPlatform = deploymentPlatform;
		this.fuseRuntime = fuseRuntime;
	}

	public String getCamelVersion() {
		return camelVersion;
	}

	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}

	public FuseDeploymentPlatform getDeploymentPlatform() {
		return deploymentPlatform;
	}

	public void setDeploymentPlatform(FuseDeploymentPlatform deploymentPlatform) {
		this.deploymentPlatform = deploymentPlatform;
	}

	public FuseRuntimeKind getFuseRuntime() {
		return fuseRuntime;
	}

	public void setFuseRuntime(FuseRuntimeKind fuseRuntime) {
		this.fuseRuntime = fuseRuntime;
	}

}
