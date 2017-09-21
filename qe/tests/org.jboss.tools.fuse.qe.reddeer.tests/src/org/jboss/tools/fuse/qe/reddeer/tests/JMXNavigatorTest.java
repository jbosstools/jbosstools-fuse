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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.exception.CoreLayerException;
import org.jboss.reddeer.core.handler.ShellHandler;
import org.jboss.reddeer.core.matcher.WithTooltipTextMatcher;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.ui.console.ConsoleView;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.preference.ConsolePreferencePage;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.jboss.tools.fuse.qe.reddeer.view.FuseJMXNavigator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests <i>JMX Navigator</i> view that:
 * <ul>
 * <li>shows running processes (local context) correctly</li>
 * <li><i>Suspend</i>, <i>Resume</i> options work</li>
 * </ul>
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class JMXNavigatorTest {

	private static final String PROJECT_NAME = "camel-spring";
	private static final String PROJECT_CAMEL_CONTEXT = "camel-context.xml";

	private static Logger log = Logger.getLogger(JMXNavigatorTest.class);

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupCreateProject() {

		log.info("Maximizing workbench shell.");
		new WorkbenchShell().maximize();

		log.info("Disable showing Console view after standard output changes");
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		ConsolePreferencePage consolePref = new ConsolePreferencePage();
		dialog.open();
		dialog.select(consolePref);
		consolePref.toggleShowConsoleErrorWrite(false);
		consolePref.toggleShowConsoleStandardWrite(false);
		dialog.ok();

		log.info("Disable showing Error Log view after changes");
		new ErrorLogView().selectActivateOnNewEvents(false);

		log.info("Create a new Fuse project from 'Content Based Router' template");
		ProjectFactory.newProject(PROJECT_NAME).version("2.15.1.redhat-621084").template(ProjectTemplate.CBR)
				.type(ProjectType.SPRING).create();
		new CamelProject(PROJECT_NAME).update();

		log.info("Run the Fuse project as Local Camel Context");
		new CamelProject(PROJECT_NAME).runCamelContextWithoutTests(PROJECT_CAMEL_CONTEXT);
	}

	/**
	 * Prepares test environment
	 */
	@Before
	public void defaultSetup() {

		new WorkbenchShell();

		log.info("Deleting Error Log.");
		new ErrorLogView().deleteLog();
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void defaultClean() {

		new WorkbenchShell();

		log.info("Closing all non workbench shells.");
		ShellHandler.getInstance().closeAllNonWorbenchShells();

		log.info("Save editor");
		try {
			new DefaultToolItem(new WorkbenchShell(), 0, new WithTooltipTextMatcher(new RegexMatcher("Save All.*")))
					.click();
		} catch (Exception e) {
			log.info("Nothing to save");
		}
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void defaultFinalClean() {

		new WorkbenchShell();

		log.info("Try to terminate a console.");
		ConsoleView console = new ConsoleView();
		console.open();
		try {
			console.terminateConsole();
			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		} catch (CoreLayerException ex) {
			log.warn("Cannot terminate a console. Perhaps there is no active console.");
		}

		log.info("Deleting all projects");
		ProjectFactory.deleteAllProjects();
	}

	/**
	 * <p>
	 * Test tries to access nodes relevant for Local Camel Context in JMX Navigator view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>open Project Explorer View</li>
	 * <li>run the Fuse project as Local Camel Context</li>
	 * <li>open JMX Navigator View</li>
	 * <li>try to access node "Local Camel Context", "Camel", "camelContext", "Endpoints", "file", "work/cbr/input"</li>
	 * <li>try to access node "Local Camel Context", "Camel", "camelContext", "Routes", "cbr-route",
	 * "file:work/cbr/input", "Log _log1", "Choice", "Otherwise", "Log _log4", "file:work/cbr/output/others"</li>
	 * </ol>
	 */
	@Test
	public void testProcessesView() {

		FuseJMXNavigator jmx = new FuseJMXNavigator();
		assertNotNull(
				"The following path is inaccesible: Local Camel Context/Camel/camelContext-.../Endpoints/file/work/cbr/input",
				jmx.getNode("Local Camel Context", "Camel", "cbr-example-context", "Endpoints", "file",
						"work/cbr/input"));
		assertNotNull(
				"The following path is inaccesible: Local Camel Context/Camel/camelContext-.../Routes/cbr-route/file:work/cbr/input/Log _log1/Choice/Otherwise/Log _log4/file:work/cbr/output/others",
				jmx.getNode("Local Camel Context", "Camel", "cbr-example-context", "Routes", "cbr-route",
						"file:work/cbr/input", "Log _log1", "Choice", "Otherwise", "Log _log4",
						"file:work/cbr/output/others"));
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Test tries context menu options related to Camel Context runs as Local Camel Context - Suspend/Resume context.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>open Project Explorer View</li>
	 * <li>run the Fuse project as Local Camel Context</li>
	 * <li>open JMX Navigator View</li>
	 * <li>select node "Local Camel Context", "Camel", "camelContext-..."</li>
	 * <li>select the context menu option Suspend Camel Context</li>
	 * <li>check if the Console View contains the text "Route: cbr-route suspend complete" (true)</li>
	 * <li>open JMX Navigator View</li>
	 * <li>select node "Local Camel Context", "Camel", "camelContext-..."</li>
	 * <li>select the context menu option Resume Camel Context</li>
	 * <li>check if the Console View contains the text "Route: cbr-route resumed" (true)</li>
	 * </ol>
	 */
	@Test
	public void testContextOperations() {

		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.open();
		assertTrue("Suspension was not performed",
				jmx.suspendCamelContext("Local Camel Context", "Camel", "cbr-example-context"));
		try {
			new WaitUntil(new ConsoleHasText("Route: cbr-route suspend complete"), TimePeriod.NORMAL);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not suspended!");
		}
		assertTrue("Resume of Camel Context was not performed",
				jmx.resumeCamelContext("Local Camel Context", "Camel", "cbr-example-context"));
		try {
			new WaitUntil(new ConsoleHasText("Route: cbr-route resumed"), TimePeriod.NORMAL);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not resumed!");
		}
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}
}
