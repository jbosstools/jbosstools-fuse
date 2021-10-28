/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.integration.globalconfiguration.wizards.pages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.camel.editor.component.wizard.ComponentManager;
import org.fusesource.ide.camel.editor.component.wizard.WhiteListComponentFilter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WhiteListComponentFilterIT {

	@Mock
	private Viewer viewer;

	private CamelModel camelModel;
	
	@Before
	public void setup() {
		camelModel = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(CamelCatalogUtils.getLatestCamelVersion());
		doReturn(new ComponentManager(camelModel)).when(viewer).getInput();
	}

	@Test
	public void testWhiteListSelectComponentAvailableInWhiteList() throws Exception {
		final Component component = new Component();
		component.setScheme("cxf");
		assertThat(new WhiteListComponentFilter().select(null, null, component)).isTrue();
	}

	@Ignore("Broken due to FUSETOOLS-2290 rework. The palette needs an instantiated CamelDesignEditor with assigned opened project to generate the palette")
	@Test
	public void testWhiteListSelectComponentAvailableInWhiteListFromExtensionPoint() throws Exception {
		final Component component = new Component();
		component.setScheme("org.fusesource.ide.camel.editor.tests.integration.PaletteContribution1");
		assertThat(new WhiteListComponentFilter().select(null, null, component)).isTrue();
	}

	@Test
	public void testWhiteListFiltersComponentNotAvailableInWhiteList() throws Exception {
		final Component component = new Component();
		component.setScheme("jira");
		assertThat(new WhiteListComponentFilter().select(null, null, component)).isFalse();
	}

	@Test
	public void testWhiteListSelectTagsWithComponentAvailableInWhiteList() throws Exception {
		assertThat(new WhiteListComponentFilter().select(viewer, null, "database")).isTrue();
	}

	@Test
	public void testWhiteListFiltersTagsWithComponentNotAvailableInWhiteList() throws Exception {
		assertThat(new WhiteListComponentFilter().select(viewer, null, "batch")).isFalse();
	}

}
