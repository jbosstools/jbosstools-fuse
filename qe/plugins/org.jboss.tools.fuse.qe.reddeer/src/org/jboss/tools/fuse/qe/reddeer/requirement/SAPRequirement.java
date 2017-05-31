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
package org.jboss.tools.fuse.qe.reddeer.requirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.reddeer.junit.requirement.CustomConfiguration;
import org.jboss.reddeer.junit.requirement.Requirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.SAPRequirement.SAP;

/**
 * 
 * @author apodhrad
 * 
 */

public class SAPRequirement implements Requirement<SAP>, CustomConfiguration<SAPConfig> {

	private SAPConfig config;
	@SuppressWarnings("unused")
	private SAP sap;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SAP {

	}

	@Override
	public boolean canFulfill() {
		return true;
	}

	@Override
	public void fulfill() {

	}

	@Override
	public void setDeclaration(SAP sap) {
		this.sap = sap;
	}

	@Override
	public Class<SAPConfig> getConfigurationClass() {
		return SAPConfig.class;
	}

	@Override
	public void setConfiguration(SAPConfig config) {
		this.config = config;
	}

	public SAPConfig getConfig() {
		return this.config;
	}

	@Override
	public void cleanUp() {
		// TODO cleanUp()
	}
}
