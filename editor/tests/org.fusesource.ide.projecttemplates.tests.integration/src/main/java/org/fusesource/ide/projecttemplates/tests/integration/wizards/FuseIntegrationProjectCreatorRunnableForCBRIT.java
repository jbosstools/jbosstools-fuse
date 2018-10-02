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

import static org.junit.Assume.assumeFalse;

import java.util.List;

import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.CBRTemplateForFuse6;
import org.fusesource.ide.projecttemplates.impl.simple.CBRTemplateForFuse7;
import org.fusesource.ide.projecttemplates.impl.simple.CBRTemplateForFuse71;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FuseIntegrationProjectCreatorRunnableForCBRIT extends FuseIntegrationProjectCreatorRunnableIT{

	@Parameters(name = "{0}")
	public static List<String> parameters(){
		return CamelCatalogUtils.getCamelVersionsToTestWith(); 
	}
	
	public FuseIntegrationProjectCreatorRunnableForCBRIT(String version) {
		super();
		camelVersion = version;
	}
	
	@Test
	public void testCBRBluePrintCreation() throws Exception {
		assumeFalse("Blueprint with 2.15 redhat version is not working, see https://issues.jboss.org/browse/FUSETOOLS-1986", camelVersion.startsWith("2.15"));
		testProjectCreation("-CBRBlueprint-"+camelVersion, CamelDSLType.BLUEPRINT, "src/main/resources/OSGI-INF/blueprint/blueprint.xml", null);
	}
		
	@Test
	public void testCBRSpringCreation() throws Exception {
		testProjectCreation("-CBRSpring-"+camelVersion, CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", null);
	}
	
	@Test
	public void testCBRJavaProjectCreation() throws Exception {
		testProjectCreation("-CBRJavaProject-"+camelVersion, CamelDSLType.JAVA, "src/main/java/com/mycompany/camel/CamelRoute.java", null);
	}
	
	@Override
	protected NewFuseIntegrationProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
		NewFuseIntegrationProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		if(isOlderThan220()){
			newProjectMetadata.setTemplate(new CBRTemplateForFuse6());
		} else if(isNewerThan221()){
			newProjectMetadata.setTemplate(new CBRTemplateForFuse71());
		} else {
			newProjectMetadata.setTemplate(new CBRTemplateForFuse7());
		}
		return newProjectMetadata;
	}

}
