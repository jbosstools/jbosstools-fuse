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
package org.fusesource.ide.camel.editor.component.wizard;

import org.fusesource.ide.camel.tests.util.CamelModelRegistrationRule;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentGroupedByTagsTreeContenProviderTest {

	@Rule
	public CamelModelRegistrationRule camelModelRegistration = new CamelModelRegistrationRule();

	@Test
	public void testTopLevel() throws Exception {
		final ComponentGroupedByTagsTreeContenProvider provider = new ComponentGroupedByTagsTreeContenProvider();
		assertThat(provider.getElements(new ComponentManager(camelModelRegistration.getCamelModel().getComponentModel()))).contains("spring", "cloud");
	}

	@Test
	public void testGetChildrenReturnElementsForEveryTags() throws Exception {
		final ComponentGroupedByTagsTreeContenProvider provider = new ComponentGroupedByTagsTreeContenProvider();
		Object[] tags = provider.getElements(new ComponentManager(camelModelRegistration.getCamelModel().getComponentModel()));
		for (Object tag : tags) {
			assertThat(provider.getChildren(tag)).isNotEmpty();
		}
	}

}
