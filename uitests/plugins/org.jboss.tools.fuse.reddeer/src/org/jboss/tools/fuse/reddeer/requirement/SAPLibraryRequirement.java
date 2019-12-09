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
package org.jboss.tools.fuse.reddeer.requirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.reddeer.junit.requirement.ConfigurableRequirement;
import org.jboss.tools.fuse.reddeer.requirement.SAPLibraryRequirement.SAPLibrary;

/**
 * 
 * @author apodhrad
 * 
 */

public class SAPLibraryRequirement implements ConfigurableRequirement<SAPLibraryConfiguration, SAPLibrary> {

	private SAPLibraryConfiguration config;
	private SAPLibrary sap;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SAPLibrary {

	}

	@Override
	public void fulfill() {

	}

	@Override
	public void setDeclaration(SAPLibrary sap) {
		this.sap = sap;
	}

	@Override
	public SAPLibrary getDeclaration() {
		return this.sap;
	}

	@Override
	public Class<SAPLibraryConfiguration> getConfigurationClass() {
		return SAPLibraryConfiguration.class;
	}

	@Override
	public void setConfiguration(SAPLibraryConfiguration config) {
		this.config = config;
	}

	@Override
	public SAPLibraryConfiguration getConfiguration() {
		return this.config;
	}

	@Override
	public void cleanUp() {
		// TODO cleanUp()
	}
}
