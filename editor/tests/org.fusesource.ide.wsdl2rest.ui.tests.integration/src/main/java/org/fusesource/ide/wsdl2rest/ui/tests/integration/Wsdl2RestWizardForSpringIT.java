/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.wsdl2rest.ui.tests.integration;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.wsdl2rest.ui.wizard.Wsdl2RestWizard;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test to ensure we can run the wsdl2rest wizard.
 * 
 * @author brianf
 */
public class Wsdl2RestWizardForSpringIT extends AbstractWsdl2restWizardIT {

	static final String SPRING_CAMEL_PATH = "/src/main/resources/META-INF/spring"; //$NON-NLS-1$
	
	@Rule
	public FuseProject fuseProject = new FuseProject();

	@Test
	public void testWsdl2RestWizardWithNoProjectInCamelPath() throws CoreException, IOException {
		fuseProject.createEmptyCamelFile();
		waitJob();
		runWsdl2RestWizard(SPRING_CAMEL_PATH, fuseProject, Wsdl2RestWizard.DEFAULT_CONFIG_NAME);
	}

	@Test
	public void testWsdl2RestWizardWithProjectInCamelPath() throws CoreException, IOException {
		fuseProject.createEmptyCamelFile();
		waitJob();
		String camelPath = '/' + fuseProject.getProject().getName() + '/' + SPRING_CAMEL_PATH;
		runWsdl2RestWizard(camelPath, fuseProject, Wsdl2RestWizard.DEFAULT_CONFIG_NAME);

		// make sure that the jetty dependency was added as part of the wizard
		// TODO: This will change when we update which Rest Configuration component we're using for generated Spring Rest DSL
		Assert.assertTrue(hasDependency(fuseProject.getProject(), "camel-jetty"));
	}

	@Test
	public void testWsdl2RestWizardWithCustomFileInCamelPath() throws CoreException, IOException {
		fuseProject.createEmptyCamelFile();
		waitJob();
		String camelPath = SPRING_CAMEL_PATH + '/' + "customconfig.xml";
		runWsdl2RestWizard(camelPath, fuseProject, "customconfig.xml");
	}
	
}
