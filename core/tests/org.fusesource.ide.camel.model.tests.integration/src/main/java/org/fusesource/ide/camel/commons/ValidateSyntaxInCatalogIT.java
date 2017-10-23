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
package org.fusesource.ide.camel.commons;

import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class ValidateSyntaxInCatalogIT {

	@Test
	public void checkComponentSyntaxAreValid() throws Exception {
		StringBuilder sb = new StringBuilder();
		checkForRuntimeProvider(sb, CamelCatalogUtils.getCamelVersionsToTestWith(), CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
		checkForRuntimeProvider(sb, CamelCatalogUtils.getPureFISVersions(), CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT);
		if (sb.length() != 0) {
			fail(sb.toString());
		}

	}

	private void checkForRuntimeProvider(StringBuilder sb, List<String> supportedCamelVersions, String runtimeProvider) {
		for (String camelVersion : supportedCamelVersions) {
			CamelModel camelModel = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(camelVersion);
			for (Component component : camelModel.getComponents()) {
				for (Parameter param : new ArrayList<>(component.getParameters())) {
					AbstractCamelModelElement selectedEP = new CamelEndpoint(component.getSyntax());
					try {
						PropertiesUtils.getPropertyFromUri(selectedEP, param, component);
					} catch (Exception e) {
						sb.append(camelVersion + " " + component.getName() + " " + param.getName() + " " + component.getSyntax() + "\n");
					}
				}
			}
		}
	}

}
