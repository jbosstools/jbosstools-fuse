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
import static org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Fuse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.handler.ShellHandler;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.condition.FuseLogContainsText;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.preference.ConsolePreferencePage;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.jboss.tools.fuse.qe.reddeer.view.FuseJMXNavigator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests <i>JMX Navigator</i> (in a server's fashion):
 * <ul>
 * <li>show a server process</li>
 * <li><i>Suspend</i>, <i>Resume</i> options work</li>
 * </ul>
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@Fuse(server = @Server(type = Fuse, state = RUNNING))
public class JMXNavigatorServerTest {

	private static final String PROJECT_NAME = "cbr-blueprint";

	private static String serverName;
	private static boolean setupIsDone = false;

	@InjectRequirement
	private static FuseRequirement serverReq;

	/**
	 * Prepares test environment
	 */
	@Before
	public void setup() {

		if (setupIsDone) {
			return;
		}

		// Maximizing workbench shell
		new WorkbenchShell().maximize();

		// Disable showing Console view after standard output changes
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		ConsolePreferencePage consolePref = new ConsolePreferencePage();
		dialog.open();
		dialog.select(consolePref);
		consolePref.toggleShowConsoleErrorWrite(false);
		consolePref.toggleShowConsoleStandardWrite(false);
		dialog.ok();

		// Disable showing Error Log view after changes
		ErrorLogView error = new ErrorLogView();
		error.selectActivateOnNewEvents(false);

		ProjectFactory.newProject(PROJECT_NAME).template(ProjectTemplate.CBR).version(CAMEL_2_17_0_REDHAT_630187).type(ProjectType.BLUEPRINT).create();
		serverName = serverReq.getConfig().getName();
		FuseServerManipulator.addModule(serverName, PROJECT_NAME);

		// Deleting Error Log
		new ErrorLogView().deleteLog();

		setupIsDone = true;
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupDefaultClean() {

		new WorkbenchShell();

		// Closing all non workbench shells
		ShellHandler.getInstance().closeAllNonWorbenchShells();
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

	/**
	 * <p>
	 * Test tries to access nodes relevant for JBoss Fuse in JMX Navigator view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>add a new Fuse server</li>
	 * <li>start the server</li>
	 * <li>open JMX Navigator View</li>
	 * <li>try to access node "karaf", "Camel", "cbr-example-context", "Endpoints", "file", "work/cbr/input"</li>
	 * <li>try to access node "karaf", "Camel", "camelContext", "Routes", "cbr-route", "file:work/cbr/input",
	 * "Log _log1", "Choice", "Otherwise", "Log _log4", "file:work/cbr/output/others"</li>
	 * </ol>
	 */
	@Test
	public void testServerInJMXNavigator() {

		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.open();
		assertNotNull("There is no Fuse node in JMX Navigator View!", jmx.getNode("karaf"));
		jmx.connectTo("karaf");
		assertNotNull(
				"The following path is inaccesible: karaf/Camel/cbr-example-context/Endpoints/file/work/cbr/input",
				jmx.getNode("karaf", "Camel", "cbr-example-context", "Endpoints", "file", "work/cbr/input"));
		assertNotNull(
				"The following path is inaccesible: karaf/Camel/camel-*/Routes/cbr-route/file:work/cbr/input/Log _log1/Choice/Otherwise/Log _log4/file:work/cbr/output/others",
				jmx.getNode("karaf", "Camel", "cbr-example-context", "Routes", "cbr-route", "file:work/cbr/input", "Log _log1",
						"Choice", "Otherwise", "Log _log4", "file:work/cbr/output/others"));
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
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
	 * <li>select node "karaf", "Camel", "cbr-example-context"</li>
	 * <li>select the context menu option Suspend Camel Context</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: camel-1) is suspended" (true)</li>
	 * <li>open JMX Navigator View</li>
	 * <li>select node "karaf", "Camel", "cbr-example-context"</li>
	 * <li>select the context menu option Resume Camel Context</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: camel-1) resumed" (true)</li>
	 * </ol>
	 */
	@Test
	public void testServerContextOperationsInJMXNavigator() {

		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.open();
		String camel = jmx.getNode("karaf", "Camel", "cbr-example-context").getText();
		assertNotNull("Camel context was not found in JMX Navigator View!", camel);
		assertTrue("Suspension was not performed", jmx.suspendCamelContext("karaf", "Camel", camel));
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: " + camel + ") is suspended"), TimePeriod.NORMAL);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not suspended!");
		}
		assertTrue("Resume of Camel Context was not performed", jmx.resumeCamelContext("karaf", "Camel", camel));
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: " + camel + ") resumed"), TimePeriod.NORMAL);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not resumed!");
		}
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}
}
