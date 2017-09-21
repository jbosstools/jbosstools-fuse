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
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.EAP;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Fuse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.ui.browser.BrowserView;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseShellSSH;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests a Fuse project deployment
 * 
 * @author tsedmik
 */
@Fuse(server = @Server(type = {EAP, Fuse}, state = RUNNING))
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class DeploymentTest extends DefaultTest {

	private static final String PROJECT_NAME = "project";
	private static final String PROJECT_IS_DEPLOYED = "Route: cbr-route started and consuming from: Endpoint[file://work/cbr/input]";
	private static final String PROJECT_IS_UNDEPLOYED = "Route: cbr-route shutdown complete, was consuming from: Endpoint[file://work/cbr/input]";
	private static final String PROJECT_EAP_IS_DEPLOYED = "(CamelContext: spring-context) started";
	private static final String PROJECT_EAP_IS_UNDEPLOYED = "(CamelContext: spring-context) is shutdown";
	private static final String BROWSER_URL = "http://localhost:8080/camel-test-spring";
	private static final String BROWSER_CONTENT_DEPLOYED = "Hello null";
	private static final String BROWSER_CONTENT_UNDEPLOYED = "404";

	@InjectRequirement
	private static FuseRequirement serverRequirement;

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupInitial() {

		String version = serverRequirement.getConfig().getCamelVersion();
		if (serverRequirement.getConfig().getServerBase().getClass().getName().contains("EAP")) {
			ProjectFactory.newProject(PROJECT_NAME).template(ProjectTemplate.EAP).version(version).type(SPRING).create();
		} else {
			ProjectFactory.newProject(PROJECT_NAME).version(version).template(ProjectTemplate.CBR).type(ProjectType.BLUEPRINT).create();
		}
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupManageServers() {

		new WorkbenchShell().setFocus();
		FuseServerManipulator.removeAllModules(serverRequirement.getConfig().getName());
		FuseServerManipulator.stopServer(serverRequirement.getConfig().getName());
		FuseServerManipulator.removeServer(serverRequirement.getConfig().getName());
	}

	/**
	 * <p>
	 * Test tries to deploy a project on Fuse on Karaf server.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>add a new Fuse server</li>
	 * <li>add the project to the server</li>
	 * <li>check if the server contains the project in Add and Remove ... dialog window</li>
	 * <li>start the server</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text Route: cbr-route started and consuming from:
	 * Endpoint[file://work/cbr/input] (project is deployed)</li>
	 * <li>remove all deployed modules</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text Route: cbr-route shutdown complete, was consuming from:
	 * Endpoint[file://work/cbr/input] (project is undeployed)</li>
	 * </ol>
	 * 
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
	 * <li>open Browser View and try to open URL "http://localhost:8080/wildfly-spring"</li>
	 * <li>remove all deployed modules</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: spring-context) is shutdown"</li>
	 * </ol>
	 */
	@Test
	public void testServerDeployment() {

		// add module
		FuseServerManipulator.addModule(serverRequirement.getConfig().getName(), PROJECT_NAME);
		assertTrue(FuseServerManipulator.hasServerModule(serverRequirement.getConfig().getName(), PROJECT_NAME));

		// check deployment
		if (serverRequirement.getConfig().getServerBase().getClass().getName().contains("EAP")) {
			new WaitUntil(new ConsoleHasText(PROJECT_EAP_IS_DEPLOYED), TimePeriod.LONG);
			BrowserView browser = new BrowserView();
			browser.open();
			browser.openPageURL(BROWSER_URL);
			assertTrue(browser.getText().contains(BROWSER_CONTENT_DEPLOYED));
		} else {
			assertTrue("The project was not properly deployed!", new FuseShellSSH().containsLog(PROJECT_IS_DEPLOYED));
		}

		// remove module
		FuseServerManipulator.removeAllModules(serverRequirement.getConfig().getName());
		AbstractWait.sleep(TimePeriod.getCustom(30));
		assertFalse(FuseServerManipulator.hasServerModule(serverRequirement.getConfig().getName(), PROJECT_NAME));

		// check deployment
		if (serverRequirement.getConfig().getServerBase().getClass().getName().contains("EAP")) {
			new WaitUntil(new ConsoleHasText(PROJECT_EAP_IS_UNDEPLOYED), TimePeriod.LONG);
			BrowserView browser = new BrowserView();
			browser.open();
			browser.openPageURL(BROWSER_URL);
			assertTrue(browser.getText().contains(BROWSER_CONTENT_UNDEPLOYED));
		} else {
			assertTrue("The project was not properly undeployed!", new FuseShellSSH().containsLog(PROJECT_IS_UNDEPLOYED));
		}
	}
}
