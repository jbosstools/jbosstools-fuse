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
package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.CBRTemplate;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.junit.Ignore;
import org.junit.Test;

public class FuseIntegrationProjectCreatorRunnableForCBRIT  extends FuseIntegrationProjectCreatorRunnableIT{

	private static final String FUSE_CAMEL_2_15_1 = "2.15.1.redhat-621084";
	
	//TODO: use parameterized test when https://issues.jboss.org/browse/FUSETOOLS-1986 is fixed
	
	@Test
	@Ignore("Waiting fix for https://issues.jboss.org/browse/FUSETOOLS-1986")
	public void testCBRBluePrintCreation215() throws Exception {
		camelVersion = FUSE_CAMEL_2_15_1;
		testProjectCreation("-CBRBlueprint", CamelDSLType.BLUEPRINT, "src/main/resources/OSGI-INF/blueprint/blueprint.xml", null);
	}
	
	@Test
	public void testCBRBluePrintCreation217() throws Exception {
		camelVersion = CamelModelFactory.getLatestCamelVersion();
		testProjectCreation("-CBRBlueprint", CamelDSLType.BLUEPRINT, "src/main/resources/OSGI-INF/blueprint/blueprint.xml", null);
	}
	
	@Test
	public void testCBRSpringCreation215() throws Exception {
		camelVersion = FUSE_CAMEL_2_15_1;
		testProjectCreation("-CBRSpring", CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", null);
	}
	
	@Test
	public void testCBRSpringCreation217() throws Exception {
		camelVersion = CamelModelFactory.getLatestCamelVersion();
		testProjectCreation("-CBRSpring", CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", null);
	}
	
	@Test
	public void testCBRJavaProjectCreation215() throws Exception {
		camelVersion = FUSE_CAMEL_2_15_1;
		testProjectCreation("-CBRJavaProject", CamelDSLType.JAVA, "src/main/java/com/mycompany/camel/CamelRoute.java", null);
	}
	
	@Test
	public void testCBRJavaProjectCreation217() throws Exception {
		camelVersion = CamelModelFactory.getLatestCamelVersion();
		testProjectCreation("-CBRJavaProject", CamelDSLType.JAVA, "src/main/java/com/mycompany/camel/CamelRoute.java", null);
	}
	
	@Override
	protected NewProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
		NewProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		newProjectMetadata.setTemplate(new CBRTemplate());
		newProjectMetadata.setBlankProject(false);
		return newProjectMetadata;
	}
	
	
}
