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
package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.util.List;

import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.AMQTemplate;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FuseIntegrationProjectCreatorRunnableForAMQIT extends FuseIntegrationProjectCreatorRunnableIT {

	@Parameters(name = "{0}")
	public static List<String> parameters(){
		return CamelCatalogUtils.getCamelVersionsToTestWith(); 
	}
	
	public FuseIntegrationProjectCreatorRunnableForAMQIT(String version) {
		super();
		camelVersion = version;
	}
	
	@Test
	public void testAMQBlueprintProjectCreation() throws Exception {
		//TODO: Known limitations see https://issues.jboss.org/browse/FUSETOOLS-1986
		assumeFalse("Blueprint with 2.15 redhat version is not working, see https://issues.jboss.org/browse/FUSETOOLS-1986", camelVersion.startsWith("2.15"));
		assumeTrue("Community versions upper to 2.19 are not working with a Fuse BOM refering 2.18- versions, see https://issues.jboss.org/browse/FUSETOOLS-2442", camelVersion.contains("redhat") || camelVersion.contains("fuse"));
		assumeTrue(isOlderThan220());
		
		testProjectCreation("-AMQBlueprintProject-"+camelVersion, CamelDSLType.BLUEPRINT, "src/main/resources/OSGI-INF/blueprint/camel-blueprint.xml", null);
	}

	@Test
	public void testAMQSpringProjectCreation() throws Exception {
		assumeTrue(isOlderThan220());
		testProjectCreation("-AMQSpringProject-"+camelVersion, CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", null);
	}
	
	@Override
	protected NewFuseIntegrationProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
		NewFuseIntegrationProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		newProjectMetadata.setTemplate(new AMQTemplate());
		return newProjectMetadata;
	}
	
}
