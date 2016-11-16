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

import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.camel.editor.component.wizard.ComponentManager;
import org.fusesource.ide.camel.editor.component.wizard.WhiteListComponentFilter;
import org.fusesource.ide.camel.editor.provider.DiagramTypeProvider;
import org.fusesource.ide.camel.editor.provider.ToolBehaviourProvider;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WhiteListComponentFilterIT {

	@Mock
	private Viewer viewer;

	private ComponentModel componentModel;

	@Before
	public void setup() {
		new ToolBehaviourProvider(new DiagramTypeProvider()).getPalette();
		CamelModelFactory.initializeModels();
		final CamelModel camelModel = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
		componentModel = camelModel.getComponentModel();
		doReturn(new ComponentManager(componentModel)).when(viewer).getInput();
	}

	@Test
	public void testWhiteListSelectComponentAvailableInWhiteList() throws Exception {
		final Component component = new Component();
		component.setScheme("cxf");
		assertThat(new WhiteListComponentFilter().select(null, null, component)).isTrue();
	}

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
