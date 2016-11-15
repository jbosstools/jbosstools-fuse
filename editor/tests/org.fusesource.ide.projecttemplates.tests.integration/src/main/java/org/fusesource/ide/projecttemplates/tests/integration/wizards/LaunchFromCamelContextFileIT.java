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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.CBRTemplate;
import org.fusesource.ide.projecttemplates.tests.integration.ProjectTemplatesIntegrationTestsActivator;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.junit.Test;

public class LaunchFromCamelContextFileIT extends FuseIntegrationProjectCreatorRunnableIT{
	
	@Test
	public void testReuseLaunchConfiguration() throws Exception {
		ProjectTemplatesIntegrationTestsActivator.pluginLog().logInfo("Starting LaunchFromCamelContextFileIT.testReuseLaunchConfiguration");
		camelVersion = CamelModelFactory.getLatestCamelVersion();
		testProjectCreation("-CBRSpring-TestReuseLaunchConfig", CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", null);
		ILaunchConfiguration initialLC = launch.getLaunchConfiguration();
		
		reInitializeServer();
		
		launchDebug(project);
		
		assertThat(launch.getLaunchConfiguration()).isEqualTo(initialLC);
		assertThat(launch.getLaunchConfiguration().getAttribute(MavenLaunchConstants.ATTR_GOALS, "")).isEqualTo("clean package org.apache.camel:camel-maven-plugin:run");		
	}

	private void reInitializeServer() throws CoreException, InterruptedException {
		launch.terminate();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		waitJob();
		readAndDispatch(0);
		deploymentFinished = false;
		isDeploymentOk = false;
		launch = null;
	}
	
	protected StructuredSelection getSelectionForLaunch(IProject project) {
		return new StructuredSelection(project.getFile("src/main/resources/META-INF/spring/camel-context.xml"));
	}
	
	@Override
	protected NewProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
		NewProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		newProjectMetadata.setTemplate(new CBRTemplate());
		newProjectMetadata.setBlankProject(false);
		return newProjectMetadata;
	}

}
