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

import static org.jboss.reddeer.requirements.server.ServerReqState.RUNNING;
import static org.jboss.tools.fuse.qe.reddeer.ProjectType.SPRING;
import static org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.EAP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.ui.browser.BrowserView;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests deployment of 'Spring on EAP' project on Fuse on EAP runtime
 * 
 * @author tsedmik
 */
@Fuse(server = @Server(type = EAP, state = RUNNING))
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class DeploymentEAPTest extends DefaultTest {

	private static final String PROJECT_NAME = "wildfly-spring";

	@InjectRequirement
	private static FuseRequirement serverRequirement;

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupInitial() {

		ProjectFactory.newProject(PROJECT_NAME).template(ProjectTemplate.EAP).version(CAMEL_2_17_0_REDHAT_630187).type(SPRING).create();
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupManageServers() {

		FuseServerManipulator.removeAllModules(serverRequirement.getConfig().getName());
	}

	/**
	 * <p>
	 * Test tries to deploy a project on Fuse on EAP server.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new project from 'Spring on EAP' template</li>
	 * <li>add a new Fuse on EAP server</li>
	 * <li>add the project to the server</li>
	 * <li>check if the server contains the project in Add and Remove ... dialog window</li>
	 * <li>start the server</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: spring-context) started"</li>
	 * <li>check if Fuse Shell view contains text "Deployed "wildfly-spring.war""</li>
	 * <li>open Browser View and try to open URL "http://localhost:8080/wildfly-spring"</li>
	 * <li>remove all deployed modules</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: spring-context) is shutdown"</li>
	 * <li>check if Fuse Shell view contains text "Undeployed "wildfly-spring.war""</li>
	 * </ol>
	 */
	@Test
	public void testDeployment() {

		FuseServerManipulator.addModule(serverRequirement.getConfig().getName(), PROJECT_NAME);
		assertTrue(FuseServerManipulator.hasServerModule(serverRequirement.getConfig().getName(), PROJECT_NAME));
		new WaitUntil(new ConsoleHasText("(CamelContext: spring-context) started"), TimePeriod.LONG);
		new WaitUntil(new ConsoleHasText("Deployed \"camel-test-spring.war\""), TimePeriod.LONG);
		BrowserView browser = new BrowserView();
		browser.open();
		browser.openPageURL("http://localhost:8080/camel-test-spring");
		assertTrue(browser.getText().contains("Hello null"));
		FuseServerManipulator.removeAllModules(serverRequirement.getConfig().getName());
		new WaitUntil(new ConsoleHasText("(CamelContext: spring-context) is shutdown"), TimePeriod.LONG);
		new WaitUntil(new ConsoleHasText("Undeployed \"camel-test-spring.war\""), TimePeriod.LONG);
		assertFalse(FuseServerManipulator.hasServerModule(serverRequirement.getConfig().getName(), PROJECT_NAME));
		browser.open();
		browser.openPageURL("http://localhost:8080/camel-test-spring");
		assertTrue(browser.getText().contains("404"));
	}
}
