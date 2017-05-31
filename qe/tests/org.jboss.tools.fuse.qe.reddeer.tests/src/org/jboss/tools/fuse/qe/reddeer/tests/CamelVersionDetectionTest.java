/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.tests;

import static org.jboss.reddeer.requirements.server.ServerReqState.PRESENT;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.EAP;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Fuse;
import static org.junit.Assert.assertTrue;

import org.jboss.reddeer.core.handler.ShellHandler;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.jboss.tools.fuse.qe.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests wizard for creating a new Fuse Integration Project
 * 
 * @author djelinek
 */
@RunWith(RedDeerSuite.class)
@Fuse(server = @Server(type = { Fuse, EAP }, state = PRESENT))
public class CamelVersionDetectionTest {
	
	@InjectRequirement
	private FuseRequirement serverRequirement;

	/**
	 * Prepare/Clean test environment
	 */
	@Before
	@After
	public void setupDeleteProjects() {
		ProjectFactory.deleteAllProjects();
		ErrorLogView log = new ErrorLogView();
		log.open();
		log.deleteLog();
		ShellHandler.getInstance().closeAllNonWorbenchShells();
	}
	
	/**
	 * <p>
	 * Tests 'Camel Version detection'
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Select installed 'Target Runtime'</li>
	 * <li>Check whether the detected version of Camel is same as expected runtime version of Camel</li>
	 * </ol>
	 */
	@Test
	public void testCamelVersionDetection() {
		NewFuseIntegrationProjectWizard projectWizard = new NewFuseIntegrationProjectWizard();
		projectWizard.open();
		projectWizard.setProjectName("camel-version");
		projectWizard.next();
		projectWizard.selectTargetRuntime(projectWizard.getTargetRuntimes().get(1));
		String detected = projectWizard.getCamelVersion();
		String expected = serverRequirement.getConfig().getCamelVersion();
		assertTrue("Camel detection failed -> Detected: '" + detected + "', Expected: '" + expected + "'",
				detected.equals(expected));
		projectWizard.cancel();
	}

}
