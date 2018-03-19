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
package org.jboss.tools.fuse.reddeer.wizard;

/**
 * Represents runtime types in "New Fuse Integration Project" wizard
 * 
 * @author tsedmik
 */
public enum NewFuseIntegrationProjectWizardRuntimeType {

	SPRINGBOOT("Spring Boot"),
	KARAF("Karaf/Fuse on Karaf"),
	EAP("Wildfly/Fuse on EAP");

	private String label;

	private NewFuseIntegrationProjectWizardRuntimeType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
