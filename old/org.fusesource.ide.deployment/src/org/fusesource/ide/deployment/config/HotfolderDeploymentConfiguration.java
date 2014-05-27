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

package org.fusesource.ide.deployment.config;

/**
 * @author lhein
 */
public class HotfolderDeploymentConfiguration extends
		AbstractDeploymentConfiguration {
	
	protected String hotDeployPath;
		
	/**
	 * @return the hotDeployPath
	 */
	public String getHotDeployPath() {
		return this.hotDeployPath;
	}
	
	/**
	 * @param hotDeployPath the hotDeployPath to set
	 */
	public void setHotDeployPath(String hotDeployPath) {
		this.hotDeployPath = hotDeployPath;
	}	
}
