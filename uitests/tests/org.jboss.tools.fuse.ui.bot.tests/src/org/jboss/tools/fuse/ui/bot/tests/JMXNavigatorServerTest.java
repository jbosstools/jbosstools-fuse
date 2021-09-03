/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.eclipse.reddeer.requirements.server.ServerRequirementState.RUNNING;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.jre.JRERequirement.JRE;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.condition.FuseLogContainsText;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.ConsolePreferencePage;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.runtime.ServerTypeMatcher;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerFuse;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.view.FuseJMXNavigator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests <i>JMX Navigator</i> (in a server's fashion):
 * <ul>
 * <li>show a server process</li>
 * <li><i>Suspend</i>, <i>Resume</i> options work</li>
 * </ul>
 * 
 * @author tsedmik, fpospisi
 */
@JRE
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@Fuse(state = RUNNING)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class JMXNavigatorServerTest {

	public static final String PROJECT_NAME = "cbr-blueprint";

	private static String serverName;

	@InjectRequirement
	private static FuseRequirement serverReq;

	@RequirementRestriction
	public static RequirementMatcher getRestrictionMatcher() {
		return new RequirementMatcher(Fuse.class, "server", new ServerTypeMatcher(ServerFuse.class));
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setup() {

		// Maximizing workbench shell
		new WorkbenchShell().maximize();

		// Disable showing Console view after standard output changes
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		ConsolePreferencePage consolePref = new ConsolePreferencePage(dialog);
		dialog.open();
		dialog.select(consolePref);
		consolePref.toggleShowConsoleErrorWrite(false);
		consolePref.toggleShowConsoleStandardWrite(false);
		dialog.ok();

		// Disable showing Error Log view after changes
		LogView error = new LogView();
		error.open();
		error.setActivateOnNewEvents(false);

		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF)
				.version(serverReq.getConfiguration().getCamelVersion()).template(CBR_BLUEPRINT).create();
		new CamelProject(PROJECT_NAME).update();
		serverName = serverReq.getConfiguration().getServer().getName();
		FuseServerManipulator.addModule(serverName, PROJECT_NAME);

		// Deleting Error Log
		new LogView().deleteLog();
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupDefaultClean() {

		new WorkbenchShell();

		// Closing all non workbench shells
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupDefaultFinalClean() {

		new WorkbenchShell();

		// Deleting all projects
		new ProjectExplorer().deleteAllProjects();

		// Stopping and deleting configured servers
		FuseServerManipulator.deleteAllServers();
		FuseServerManipulator.deleteAllServerRuntimes();
	}
	
	@Parameter
	public String connectionType;

	@Parameter(1)
	public String server;

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"Server Connections", serverReq.getConfiguration().getServer().getName()},
			{ "Local Processes", "karaf"}, });
	}

	/**
	 * <p>
	 * Test tries to access nodes relevant for Red Hat Fuse in JMX Navigator view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>add a new Fuse server</li>
	 * <li>start the server</li>
	 * <li>open JMX Navigator View</li>
	 * <li>try to access node "karaf" (or server name), "Camel", "cbr-example-context", "Endpoints", "file",
	 * "work/cbr/input"</li>
	 * <li>try to access node "karaf" (or server name), "Camel", "camelContext", "Routes", "cbr-route",
	 * "file:work/cbr/input", "Log _log1", "Choice", "Otherwise", "Log _log4", "file:work/cbr/output/others"</li>
	 * </ol>
	 */
	@Test
	public void testServerInJMXNavigator() {
		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.open();

		String camel = jmx.getNode(connectionType, server, "Camel", "cbr-example-context").getText();
		assertNotNull("Camel context was not found in JMX Navigator View!", camel);
		assertTrue("Suspension was not performed", jmx.suspendCamelContext(connectionType, server, "Camel", camel));
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: " + camel + ") is suspended"), TimePeriod.DEFAULT);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not suspended!");
		}
		assertTrue("Resume of Camel Context was not performed",
				jmx.resumeCamelContext(connectionType, server, "Camel", camel));
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: " + camel + ") resumed"), TimePeriod.DEFAULT);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not resumed!");
		}
	}

	/**
	 * <p>
	 * Test tries context menu options related to Camel Context deployed on the Fuse server - Suspend/Resume context.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>add a new Fuse server</li>
	 * <li>add the project to the server</li>
	 * <li>start the server</li>
	 * <li>open JMX Navigator View</li>
	 * <li>select node "karaf" (or server name), "Camel", "cbr-example-context"</li>
	 * <li>select the context menu option Suspend Camel Context</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: camel-1) is suspended" (true)</li>
	 * <li>open JMX Navigator View</li>
	 * <li>select node "karaf" (or server name), "Camel", "cbr-example-context"</li>
	 * <li>select the context menu option Resume Camel Context</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: camel-1) resumed" (true)</li>
	 * </ol>
	 */
	@Test
	public void testServerContextOperationsInJMXNavigator() {

		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.open();

		String camel = jmx.getNode(connectionType, server, "Camel", "cbr-example-context").getText();
		assertNotNull("Camel context was not found in JMX Navigator View!", camel);
		assertTrue("Suspension was not performed", jmx.suspendCamelContext(connectionType, server, "Camel", camel));
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: " + camel + ") is suspended"), TimePeriod.DEFAULT);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not suspended!");
		}
		assertTrue("Resume of Camel Context was not performed",
				jmx.resumeCamelContext(connectionType, server, "Camel", camel));
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: " + camel + ") resumed"), TimePeriod.DEFAULT);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not resumed!");
		}
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}
}