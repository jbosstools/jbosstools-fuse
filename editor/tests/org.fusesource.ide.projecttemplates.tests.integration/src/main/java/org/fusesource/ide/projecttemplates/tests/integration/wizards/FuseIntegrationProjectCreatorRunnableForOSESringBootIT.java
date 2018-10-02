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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuseOnOpenShiftToBomMapper;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.OSESpringBootXMLTemplateForFuse6;
import org.fusesource.ide.projecttemplates.impl.simple.OSESpringBootXMLTemplateForFuse7;
import org.fusesource.ide.projecttemplates.impl.simple.OSESpringBootXMLTemplateForFuse71;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FuseIntegrationProjectCreatorRunnableForOSESringBootIT extends FuseIntegrationProjectCreatorRunnableIT {
	
	@Parameters(name = "{0}")
	public static List<String> parameters(){
		return Arrays.asList(CamelCatalogUtils.CAMEL_VERSION_LATEST_FIS_20, CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY, CamelForFuseOnOpenShiftToBomMapper.FUSE_710_CAMEL_VERSION);
	}
	
	public FuseIntegrationProjectCreatorRunnableForOSESringBootIT(String camelVersion) {
		this.camelVersion = camelVersion;
	}
	
	@Test
	public void testOSESpringBootProjectCreation() throws Exception {
        testProjectCreation("-OSESpringBootProject-"+camelVersion, CamelDSLType.SPRING, "src/main/resources/spring/camel-context.xml", null);
	}
	
	@Override
	protected NewFuseIntegrationProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
	NewFuseIntegrationProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		if (isOlderThan220()) {
			newProjectMetadata.setTemplate(new OSESpringBootXMLTemplateForFuse6());
		} else if (isNewerThan221()){
			newProjectMetadata.setTemplate(new OSESpringBootXMLTemplateForFuse71());
		} else {
			newProjectMetadata.setTemplate(new OSESpringBootXMLTemplateForFuse7());
		}
		return newProjectMetadata;
	}
	
	@Override
	protected void additionalChecks(IProject project) {
		assertThat(project.findMember("src/main/java/META-INF/MANIFEST.MF")).as("A bad Manifest has been generated").isNull();
		assertThat(project.getFile(".settings/fusetooling/Deploy " + project.getName() + " on OpenShift.launch").getLocation().toFile()).exists();
	}
	
	protected EnvironmentData createEnvironmentData() {
		return new EnvironmentData(camelVersion, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.SPRINGBOOT);
	}
}
