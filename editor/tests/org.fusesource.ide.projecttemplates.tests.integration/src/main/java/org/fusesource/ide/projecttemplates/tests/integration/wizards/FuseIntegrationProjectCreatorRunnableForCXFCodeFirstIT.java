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
import static org.junit.Assume.assumeTrue;

import java.util.List;

import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.medium.CXfCodeFirstProjectTemplateForFuse6;
import org.fusesource.ide.projecttemplates.impl.medium.CXfCodeFirstProjectTemplateForFuse7;
import org.fusesource.ide.projecttemplates.impl.medium.CXfCodeFirstProjectTemplateForFuse71;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FuseIntegrationProjectCreatorRunnableForCXFCodeFirstIT extends FuseIntegrationProjectCreatorRunnableIT {

	@Parameters(name = "{0}")
	public static List<String> parameters(){
		return CamelCatalogUtils.getCamelVersionsToTestWith(); 
	}
	
	public FuseIntegrationProjectCreatorRunnableForCXFCodeFirstIT(String version) {
		super();
		camelVersion = version;
	}
	
	@Test
	@Ignore("Deactivate waiting for Blueprint templates to be managed in version 2.17")
	public void testCXFCodeFirstBlueprintProjectCreation() throws Exception {
		//TODO: Known limitations see https://issues.jboss.org/browse/FUSETOOLS-1986
		assumeFalse("Blueprint with 2.15 redhat version is not working, see https://issues.jboss.org/browse/FUSETOOLS-1986", camelVersion.startsWith("2.15"));
		assumeFalse("2.18.x redhat version is not working, see https://issues.apache.org/jira/browse/CAMEL-10602", camelVersion.startsWith("2.18"));
		
		testProjectCreation("-CXFCodeFirstBlueprintProject", CamelDSLType.BLUEPRINT, "src/main/resources/OSGI-INF/blueprint/blueprint.xml", null);
	}

	@Test
	public void testCXFCodeFirstSpringProjectCreation() throws Exception {
		assumeFalse("2.18.x redhat version is not working, see https://issues.apache.org/jira/browse/CAMEL-10602", camelVersion.startsWith("2.18"));
		assumeTrue("Community versions upper to 2.19 are not working with a Fuse BOM refering 2.18- versions, see https://issues.jboss.org/browse/FUSETOOLS-2442", camelVersion.contains("redhat") || camelVersion.contains("fuse") || !isOlderThan220());
		testProjectCreation("-CXFCodeFirstSpringProject-"+camelVersion, CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", null);
	}
	
	@Test
	public void testCXFCodeFirstJavaProjectCreation() throws Exception {
		assumeFalse("2.18.x redhat version is not working, see https://issues.apache.org/jira/browse/CAMEL-10602", camelVersion.startsWith("2.18"));
		assumeTrue("Community versions upper to 2.19 are not working with a Fuse BOM refering 2.18- versions, see https://issues.jboss.org/browse/FUSETOOLS-2442", camelVersion.contains("redhat") || camelVersion.contains("fuse") || !isOlderThan220());
		testProjectCreation("-CXFCodeFirstJavaProject-"+camelVersion, CamelDSLType.JAVA, "src/main/java/com/mycompany/camel/cxf/code/first/java/incident/CamelRoute.java", null);
	}
	
	@Override
	protected NewFuseIntegrationProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
		NewFuseIntegrationProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		if(isOlderThan220()){
			newProjectMetadata.setTemplate(new CXfCodeFirstProjectTemplateForFuse6());
		} else if(isNewerThan221()){
			newProjectMetadata.setTemplate(new CXfCodeFirstProjectTemplateForFuse71());
		} else {
			newProjectMetadata.setTemplate(new CXfCodeFirstProjectTemplateForFuse7());
		}
		return newProjectMetadata;
	}
	
}
