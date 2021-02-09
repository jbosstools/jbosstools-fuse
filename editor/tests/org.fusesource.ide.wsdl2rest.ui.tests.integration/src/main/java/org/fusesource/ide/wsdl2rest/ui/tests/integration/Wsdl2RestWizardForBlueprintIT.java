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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test to ensure we can run the wsdl2rest wizard.
 * 
 * @author brianf
 */
public class Wsdl2RestWizardForBlueprintIT extends AbstractWsdl2restWizardIT {

	static final String BLUEPRINT_CAMEL_PATH = "/src/main/resources/OSGI-INF/blueprint"; //$NON-NLS-1$

	@Rule
	public FuseProject fuseProject = new FuseProject(true);
	
	@Before
	public void setup() throws CoreException, IOException {
		fuseProject.createEmptyBlueprintCamelFile();
		waitJob();
	}

	@Test
	public void testWsdl2RestWizardWithBlueprintNoProjectInCamelPath() throws CoreException, IOException {
		runWsdl2RestWizard(BLUEPRINT_CAMEL_PATH, fuseProject, Wsdl2RestWizard.DEFAULT_BLUEPRINT_CONFIG_NAME);
		
		// make sure that the servlet dependency was added as part of the wizard
		Assert.assertTrue(hasDependency(fuseProject.getProject(), "camel-servlet"));
	}
	
	@Test
	public void testWsdl2RestWizardWithBlueprintProjectInCamelPath() throws CoreException, IOException {
		String camelPath = '/' + fuseProject.getProject().getName() + '/' + BLUEPRINT_CAMEL_PATH;
		runWsdl2RestWizard(camelPath, fuseProject, Wsdl2RestWizard.DEFAULT_BLUEPRINT_CONFIG_NAME);
	}
	
}
