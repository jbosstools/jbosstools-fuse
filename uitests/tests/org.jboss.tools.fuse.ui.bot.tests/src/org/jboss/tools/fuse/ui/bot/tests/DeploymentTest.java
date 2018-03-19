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
package org.jboss.tools.fuse.ui.bot.tests;

import static org.eclipse.reddeer.requirements.server.ServerRequirementState.RUNNING;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EAP_SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.EAP;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.browser.WebBrowserView;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.runtime.ServerTypeMatcher;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerEAP;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerFuse;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.reddeer.utils.FuseShellSSH;
import org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests a Fuse project deployment
 * 
 * @author tsedmik
 */
@Fuse(state = RUNNING)
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

	@RequirementRestriction
	public static RequirementMatcher getRestrictionMatcher() {
		return new RequirementMatcher(Fuse.class, "server", new ServerTypeMatcher(ServerEAP.class, ServerFuse.class));
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupInitial() {

		String version = serverRequirement.getConfiguration().getCamelVersion();
		if (serverRequirement.getConfiguration().getServer().getClass().getName().contains("EAP")) {
			ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(EAP).version(version)
					.template(EAP_SPRING).create();
		} else {
			ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).version(version)
					.template(CBR_BLUEPRINT).create();
		}
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupManageServers() {

		new WorkbenchShell().setFocus();
		FuseServerManipulator.removeAllModules(serverRequirement.getConfiguration().getServer().getName());
		FuseServerManipulator.stopServer(serverRequirement.getConfiguration().getServer().getName());
		FuseServerManipulator.removeServer(serverRequirement.getConfiguration().getServer().getName());
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
		FuseServerManipulator.addModule(serverRequirement.getConfiguration().getServer().getName(), PROJECT_NAME);
		assertTrue(FuseServerManipulator.hasServerModule(serverRequirement.getConfiguration().getServer().getName(),
				PROJECT_NAME));

		// check deployment
		if (serverRequirement.getConfiguration().getServer().getClass().getName().contains("EAP")) {
			new WaitUntil(new ConsoleHasText(PROJECT_EAP_IS_DEPLOYED), TimePeriod.LONG);
			WebBrowserView browser = new WebBrowserView();
			browser.open();
			browser.openPageURL(BROWSER_URL);
			assertTrue(browser.getText().contains(BROWSER_CONTENT_DEPLOYED));
		} else {
			assertTrue("The project was not properly deployed!", new FuseShellSSH().containsLog(PROJECT_IS_DEPLOYED));
		}

		// remove module
		FuseServerManipulator.removeAllModules(serverRequirement.getConfiguration().getServer().getName());
		AbstractWait.sleep(TimePeriod.getCustom(30));
		assertFalse(FuseServerManipulator.hasServerModule(serverRequirement.getConfiguration().getServer().getName(),
				PROJECT_NAME));

		// check deployment
		if (serverRequirement.getConfiguration().getServer().getClass().getName().contains("EAP")) {
			new WaitUntil(new ConsoleHasText(PROJECT_EAP_IS_UNDEPLOYED), TimePeriod.LONG);
			WebBrowserView browser = new WebBrowserView();
			browser.open();
			browser.openPageURL(BROWSER_URL);
			assertTrue(browser.getText().contains(BROWSER_CONTENT_UNDEPLOYED));
		} else {
			assertTrue("The project was not properly undeployed!",
					new FuseShellSSH().containsLog(PROJECT_IS_UNDEPLOYED));
		}
	}
}
