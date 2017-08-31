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

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.OSESpringBootXMLTemplate;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.junit.Test;

public class FuseIntegrationProjectCreatorRunnableForOSESringBootIT extends FuseIntegrationProjectCreatorRunnableIT {
	
	public FuseIntegrationProjectCreatorRunnableForOSESringBootIT() {
		camelVersion = "2.18.1.redhat-000015";
	}
	
	@Test
	public void testOSESpringBootProjectCreation() throws Exception {
        testProjectCreation("-OSESpringBootProject-"+camelVersion, CamelDSLType.SPRING, "src/main/resources/spring/camel-context.xml", null);
	}
	
	@Override
	protected NewProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
		NewProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		newProjectMetadata.setTemplate(new OSESpringBootXMLTemplate());
		newProjectMetadata.setBlankProject(false);
		return newProjectMetadata;
	}
	
	@Override
	protected void additionalChecks(IProject project) {
		assertThat(project.findMember("src/main/java/META-INF/MANIFEST.MF")).as("A bad Manifest has been generated").isNull();
		assertThat(project.getFile(".settings/fusetooling/Deploy " + project.getName() + " on OpenShift.launch").getLocation().toFile()).exists();
	}
	
	@Override
	protected void additionalMavenAttributes(ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.setAttribute(MavenLaunchConstants.ATTR_DEBUG_OUTPUT, true);
	}
}
