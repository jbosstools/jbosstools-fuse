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
package org.fusesource.ide.camel.editor.integration.globalconfiguration.wizards.pages;

import org.fusesource.ide.camel.editor.globalconfiguration.endpoint.wizards.pages.ComponentGroupedByTagsTreeContenProvider;
import org.fusesource.ide.camel.editor.globalconfiguration.endpoint.wizards.pages.ComponentManager;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
public class ComponentGroupedByTagsTreeContenProviderIT {

	private ComponentModel componentModel;

	@Before
	public void setup() {
		CamelModelFactory.initializeModels();
		final CamelModel camelModel = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
		componentModel = camelModel.getComponentModel();
	}

	@Test
	public void testTopLevel() throws Exception {
		final ComponentGroupedByTagsTreeContenProvider provider = new ComponentGroupedByTagsTreeContenProvider();
		assertThat(provider.getElements(new ComponentManager(componentModel))).contains("spring", "cloud");
	}

	@Test
	public void testGetChildrenReturnElementsForEveryTags() throws Exception {
		final ComponentGroupedByTagsTreeContenProvider provider = new ComponentGroupedByTagsTreeContenProvider();
		Object[] tags = provider.getElements(new ComponentManager(componentModel));
		for (Object tag : tags) {
			assertThat(provider.getChildren(tag)).isNotEmpty();
		}
	}
}
