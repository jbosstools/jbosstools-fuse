/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
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

import org.eclipse.reddeer.junit.requirement.AbstractConfigurableRequirement;
import org.jboss.tools.fuse.reddeer.requirement.CamelCatalogRequirement.CamelCatalog;

/**
 * @author djelinek
 */
public class CamelCatalogRequirement extends AbstractConfigurableRequirement<CamelCatalogConfiguration, CamelCatalog> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface CamelCatalog {
	}

	@Override
	public Class<CamelCatalogConfiguration> getConfigurationClass() {
		return CamelCatalogConfiguration.class;
	}

	@Override
	public void fulfill() {
	}

	@Override
	public void cleanUp() {
	}

}
