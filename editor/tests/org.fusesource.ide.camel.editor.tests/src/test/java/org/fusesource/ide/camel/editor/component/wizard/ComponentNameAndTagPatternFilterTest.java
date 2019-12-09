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
package org.fusesource.ide.camel.editor.component.wizard;

import java.util.ArrayList;

import org.eclipse.jface.viewers.StructuredViewer;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ComponentNameAndTagPatternFilterTest {

	@Mock
	private StructuredViewer viewer;

	@Before
	public void setup() {
		doReturn(new ComponentLabelProvider()).when(viewer).getLabelProvider();
	}

	@Test
	public void testIsLeafMatch_whenComponentTitleMatch() throws Exception {
		final ComponentNameAndTagPatternFilter filter = new ComponentNameAndTagPatternFilter();
		filter.setPattern("test");
		final Component component = createComponent();
		assertThat(filter.isLeafMatch(viewer, component)).isFalse();
		filter.setPattern("myComponent");
		assertThat(filter.isLeafMatch(viewer, component)).isTrue();
	}

	@Test
	public void testIsLeafMatch_whenTagMatch() throws Exception {
		final ComponentNameAndTagPatternFilter filter = new ComponentNameAndTagPatternFilter();
		filter.setPattern("mytag");
		final Component component = createComponent();
		assertThat(filter.isLeafMatch(viewer, component)).isTrue();
	}

	@Test
	public void testIsLeafMatch_whenDescriptionMatch() throws Exception {
		final ComponentNameAndTagPatternFilter filter = new ComponentNameAndTagPatternFilter();
		filter.setPattern("Des");
		final Component component = createComponent();
		assertThat(filter.isLeafMatch(viewer, component)).isTrue();
	}

	@Test
	public void testIsLeafMatch_whenTagsNull() throws Exception {
		final ComponentNameAndTagPatternFilter filter = new ComponentNameAndTagPatternFilter();
		filter.setPattern("whatever");
		final Component component = createComponent();
		component.setTags(null);
		assertThat(filter.isLeafMatch(viewer, component)).isFalse();
	}

	/**
	 * @return
	 */
	private Component createComponent() {
		final Component component = new Component();
		component.setTitle("myComponent");
		component.setDescription("myDescription");
		ArrayList<String> tags = new ArrayList<>();
		tags.add("anotherTag");
		tags.add("myTag");
		component.setTags(tags);
		return component;
	}

}
