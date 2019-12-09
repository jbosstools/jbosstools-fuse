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

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Test;

public class ComponentGroupedByTagsTreeContenProviderIT {

	@Test
	public void testTopLevel() throws Exception {
		final ComponentGroupedByTagsTreeContenProvider provider = new ComponentGroupedByTagsTreeContenProvider();
		CamelModel model = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(CamelCatalogUtils.DEFAULT_CAMEL_VERSION);
		assertThat(provider.getElements(new ComponentManager(model))).contains("spring", "cloud");
	}

	@Test
	public void testGetChildrenReturnElementsForEveryTags() throws Exception {
		final ComponentGroupedByTagsTreeContenProvider provider = new ComponentGroupedByTagsTreeContenProvider();
		CamelModel model = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(CamelCatalogUtils.DEFAULT_CAMEL_VERSION);
		Object[] tags = provider.getElements(new ComponentManager(model));
		for (Object tag : tags) {
			assertThat(provider.getChildren(tag)).isNotEmpty();
		}
	}

}
