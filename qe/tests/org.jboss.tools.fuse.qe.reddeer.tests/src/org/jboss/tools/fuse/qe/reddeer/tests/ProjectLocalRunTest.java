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

import static org.junit.Assert.assertFalse;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.ui.console.ConsoleView;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.api.Shell;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.EditorManipulator;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.wizard.CamelTestCaseWizard;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests option <i>Run a Project as Local Camel Context (with or without tests)</i>. The option is tested on a project
 * from 'Content Based Router' template. If you want to change template (see static variables), you have to
 * change <i>resources/FailingTest.java</i> and <i>resources/PassingTest.java</i> too (to correspondent with a new
 * project).
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class ProjectLocalRunTest extends DefaultTest {

	private static final String PROJECT_NAME = "cbr @1";
	private static final String PROJECT_CAMEL_CONTEXT = "camel-context.xml";

	private static Logger log = Logger.getLogger(ProjectLocalRunTest.class);

	/**
	 * Creates a new Camel Test Case <b>which fails</b>
	 */
	private static void createTestClass() {

		new CamelProject(PROJECT_NAME).selectCamelContext(PROJECT_CAMEL_CONTEXT);
		CamelTestCaseWizard camelTestCase = new CamelTestCaseWizard();
		camelTestCase.open();
		camelTestCase.next();
		camelTestCase.finish();

		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		log.info("The test case for the Fuse project was created.");
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupCreateProject() {

		log.info("Create a new Fuse project from 'Content Based Router' template");
		ProjectFactory.newProject(PROJECT_NAME).version("2.15.1.redhat-621084").template(ProjectTemplate.CBR).type(ProjectType.SPRING).create();
		new CamelProject(PROJECT_NAME).update();
		createTestClass();
	}

	/**
	 * <p>
	 * Tests option Run a Project as Local Camel Context with tests
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>create a new Camel Test Case in the project</li>
	 * <li>write a passing test</li>
	 * <li>Run a Project as Local Camel Context with tests</li>
	 * <li>check if the Console View contains the text "Total 1 routes, of which 1 is started." (true)</li>
	 * <li>check if the Console View contains the text "BUILD FAILURE" (false)</li>
	 * </ol>
	 */
	@Test
	public void testRunProjectWithPassingTests() {
		Shell workbenchShell = new WorkbenchShell();
		log.info("Run a project as Local Camel Context (Project contains a passing test case).");
		new DefaultEditor("CamelContextXmlTest.java").activate();
		EditorManipulator.copyFileContent("resources/PassingTest.java");
		new CamelProject(PROJECT_NAME).runCamelContext();
		workbenchShell.setFocus();
		assertFalse("This build should be successful.", new ConsoleView().getConsoleText().contains("BUILD FAILURE"));
	}

	/**
	 * <p>
	 * Tests option Run a Project as Local Camel Context with tests
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>create a new Camel Test Case in the project</li>
	 * <li>write a failing test</li>
	 * <li>Run a Project as Local Camel Context with tests</li>
	 * <li>check if the Console View contains the text "BUILD FAILURE" (true)</li>
	 * </ol>
	 */
	@Test
	public void testRunProjectWithFailingTests() {
		Shell workbenchShell = new WorkbenchShell();
		log.info("Run a project as Local Camel Context (Project contains a failing test case).");
		new DefaultEditor("CamelContextXmlTest.java").activate();
		EditorManipulator.copyFileContent("resources/FailingTest.java");
		new CamelProject(PROJECT_NAME).runCamelContext();
		new WaitUntil(new ConsoleHasText("BUILD FAILURE"), TimePeriod.getCustom(300));
		workbenchShell.setFocus();
	}

	/**
	 * <p>
	 * Tests option Run a Project as Local Camel Context without tests
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>create a new Camel Test Case in the project</li>
	 * <li>write a failing test</li>
	 * <li>Run a Project as Local Camel Context without tests</li>
	 * <li>check if the Console View contains the text "Total 1 routes, of which 1 is started." (true)</li>
	 * <li>check if the Console View contains the text "BUILD FAILURE" (false)</li>
	 * </ol>
	 */
	@Test
	public void testRunProjectWithoutTests() {
		Shell workbenchShell = new WorkbenchShell();
		log.info("Run a project as Local Camel Context (without tests).");
		new DefaultEditor("CamelContextXmlTest.java").activate();
		EditorManipulator.copyFileContent("resources/FailingTest.java");
		new CamelProject(PROJECT_NAME).runCamelContextWithoutTests(PROJECT_CAMEL_CONTEXT);
		workbenchShell.setFocus();
		assertFalse("This build should be successful.", new ConsoleView().getConsoleText().contains("BUILD FAILURE"));
	}
}
