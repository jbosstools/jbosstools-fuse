/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.AbstractBooleanAssert;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CamelServiceCatalogLoadingIT {
	
	private String camelVersion;
	
	@Parameters(name = "{0}")
	public static List<String> parameters() {
		return Arrays.asList(
				"2.10.7",
				"2.11.4",
				"2.12.5",
				"2.13.4",
				"2.14.4",
				"2.15.6",
				"2.16.4",
				"2.17.7",
				"2.18.5",
				"2.19.3",
				"2.20.2",
				"2.21.5",
				"2.22.3",
				"2.23.1");
	}
	
	public CamelServiceCatalogLoadingIT(String camelVersion) {
		this.camelVersion = camelVersion;
	}
	
	@Test
	public void testCatalogLoad() {
		CamelModel camelModel = new CamelService(getLogger()).getCamelModel(camelVersion, CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
		
		assertThat(camelModel.getComponents()).isNotEmpty();
		assertThat(camelModel.getDataFormats()).isNotEmpty();
		assertThat(camelModel.getEips()).isNotEmpty();
		assertThat(camelModel.getLanguages()).isNotEmpty();
		checkModelPatchedForCamelContextForVersionPriorTo218(camelModel);
	}

	protected void checkModelPatchedForCamelContextForVersionPriorTo218(CamelModel camelModel) {
		Eip camelContextEIP = camelModel.getEip("camelContext");
		assertThat(camelContextEIP).isNotNull();
		check(camelContextEIP, "2.18.0", "logExhaustedMessageBody");
		check(camelContextEIP, "2.16.0", "typeConverterExists");
	}

	protected void check(Eip camelContextEIP, String versionSinceContaining, String propertyKey) {
		AbstractBooleanAssert<?> hasProperty = assertThat(camelContextEIP.getProperties().containsKey(propertyKey));
		if(versionSinceContaining.compareTo(camelVersion) <= 0) {
			hasProperty.isTrue();
		} else {
			hasProperty.isFalse();
		}
	}

	private IPluginLog getLogger() {
		if(camelVersion.compareTo("2.15.0") > 1) {
			return new PluginLogAcceptingNoInteraction();
		} else {
			return CamelServiceImplementationActivator.pluginLog();
		}
	}
	
}
