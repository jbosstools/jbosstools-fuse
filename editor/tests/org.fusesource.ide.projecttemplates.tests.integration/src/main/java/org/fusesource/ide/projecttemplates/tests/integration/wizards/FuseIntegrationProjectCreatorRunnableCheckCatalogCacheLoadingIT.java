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
package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import javax.management.MalformedObjectNameException;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.DebugException;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FuseIntegrationProjectCreatorRunnableCheckCatalogCacheLoadingIT
		extends FuseIntegrationProjectCreatorRunnableIT {
	
	private static Map<CamelCatalogCoordinates, CamelModel> cachedCatalog;
	
	@BeforeClass
	public static void beforeClass() {
		cachedCatalog = CamelCatalogCacheManager.getInstance().getCachedCatalog();
	}
	
	@AfterClass
	public static void afterClass() {
		CamelCatalogCacheManager.setCachedCatalog(cachedCatalog);
	}
	
	@Before
	public void setup() throws Exception {
		CamelCatalogCacheManager.getInstance().clear();
		camelVersion = "2.17.0.redhat-630187";
		super.setup();
	}
	
	@Test
	public void testLoadingSingleCatalogWhenCreatingProject() throws Exception {
		testProjectCreation("-SimpleBlueprintProject-"+camelVersion, CamelDSLType.BLUEPRINT, "src/main/resources/OSGI-INF/blueprint/blueprint.xml", null);
	}
	
	@Override
	protected void launchDebug(IProject project)
			throws InterruptedException, IOException, MalformedObjectNameException, DebugException {
		// Not launching the project, this is not what we want to test
	}

	@Override
	protected void additionalChecks(IProject project) {
		super.additionalChecks(project);
		assertThat(CamelCatalogCacheManager.getInstance().getCachedCatalog().size()).isEqualTo(1);
	}
	
}
