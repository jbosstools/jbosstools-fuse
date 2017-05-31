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
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Fuse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.eclipse.m2e.core.ui.wizard.MavenImportWizard;
import org.jboss.reddeer.eclipse.ui.problems.Problem;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView.ProblemType;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.qe.reddeer.JiraIssue;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.condition.FuseLogContainsText;
import org.jboss.tools.fuse.qe.reddeer.preference.ConsolePreferencePage;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests quickstarts which contains JBoss Fuse Runtime
 * 
 * @author tsedmik
 */
@Fuse(server = @Server(type = Fuse, state = RUNNING))
@RunWith(RedDeerSuite.class)
public class QuickStartsTest {

	@InjectRequirement
	private FuseRequirement serverRequirement;

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupEnv() {

		new WorkbenchShell().maximize();

		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		ConsolePreferencePage consolePref = new ConsolePreferencePage();
		dialog.open();
		dialog.select(consolePref);
		consolePref.toggleShowConsoleErrorWrite(false);
		consolePref.toggleShowConsoleStandardWrite(false);
		dialog.ok();

		new ProblemsView().open();
	}

	/**
	 * Prepares test environment
	 */
	@Before
	public void setupDefault() {

		new WorkbenchShell();
		new ErrorLogView().open();
		new ErrorLogView().deleteLog();
	}

	/**
	 * Removes all deployed projects
	 */
	@After
	public void setupUndeployProjects() {

		AbstractWait.sleep(TimePeriod.NORMAL);
		new WorkbenchShell();
		FuseServerManipulator.removeAllModules(serverRequirement.getConfig().getName());
	}

	/**
	 * Deletes all created projects
	 */
	@After
	public void setupDeleteProjects() {
		ProjectFactory.deleteAllProjects();
	}

	/**
	 * <p>
	 * Test tries to deploy 'beginner-camel-cbr' quickstart to JBoss Fuse Runtime.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>import 'beginner-camel-cbr' project from JBoss Fuse quickstarts</li>
	 * <li>check that project is ok (no errors, unresolved dependencies, ...)</li>
	 * <li>deploy the project to JBoss Fuse</li>
	 * <li>check whether the project is successfully deployed (check whether JBoss Fuse log contains '(CamelContext:
	 * cbr-example-context) started')</li>
	 * </ol>
	 */
	@Test
	public void testBeginnerCamelCBR() {

		String quickstart = serverRequirement.getConfig().getServerBase().getHome() + "/quickstarts/beginner/camel-cbr";
		MavenImportWizard.importProject(quickstart);
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
		CamelProject project = new CamelProject("beginner-camel-cbr");
		project.enableCamelNature();
		FuseServerManipulator.addModule(serverRequirement.getConfig().getName(), "beginner-camel-cbr");
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: cbr-example-context) started"));
		} catch (WaitTimeoutExpiredException e) {
			fail("Project 'beginner-camel-cbr' was not sucessfully deployed!");
		}
		checkProblemsView();
	}

	/**
	 * <p>
	 * Test tries to deploy 'beginner-camel-eips' quickstart to JBoss Fuse Runtime.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>import 'beginner-camel-eips' project from JBoss Fuse quickstarts</li>
	 * <li>check that project is ok (no errors, unresolved dependencies, ...)</li>
	 * <li>deploy the project to JBoss Fuse</li>
	 * <li>check whether the project is successfully deployed (check whether JBoss Fuse log contains '(CamelContext:
	 * eip-example-context) started')</li>
	 * </ol>
	 */
	@Test
	public void testBeginnerCamelEIPs() {

		String quickstart = serverRequirement.getConfig().getServerBase().getHome()
				+ "/quickstarts/beginner/camel-eips";
		MavenImportWizard.importProject(quickstart);
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
		CamelProject project = new CamelProject("beginner-camel-eips");
		project.enableCamelNature();
		FuseServerManipulator.addModule(serverRequirement.getConfig().getName(), "beginner-camel-eips");
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: eip-example-context) started"));
		} catch (WaitTimeoutExpiredException e) {
			fail("Project 'beginner-camel-eips' was not sucessfully deployed!");
		}
		checkProblemsView();
	}

	/**
	 * <p>
	 * Test tries to deploy 'beginner-camel-errorhandler' quickstart to JBoss Fuse Runtime.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>import 'beginner-camel-errorhandler' project from JBoss Fuse quickstarts</li>
	 * <li>check that project is ok (no errors, unresolved dependencies, ...)</li>
	 * <li>deploy the project to JBoss Fuse</li>
	 * <li>check whether the project is successfully deployed (check whether JBoss Fuse log contains '(CamelContext:
	 * errors-example-context) started')</li>
	 * </ol>
	 */
	@Test
	public void testBeginnerCamelErrorHandler() {

		String quickstart = serverRequirement.getConfig().getServerBase().getHome()
				+ "/quickstarts/beginner/camel-errorhandler";
		MavenImportWizard.importProject(quickstart);
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
		CamelProject project = new CamelProject("beginner-camel-errorhandler");
		project.enableCamelNature();
		FuseServerManipulator.addModule(serverRequirement.getConfig().getName(), "beginner-camel-errorhandler");
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: errors-example-context) started"));
		} catch (WaitTimeoutExpiredException e) {
			fail("Project 'beginner-camel-errorhandler' was not sucessfully deployed!");
		}
		checkProblemsView();
	}

	/**
	 * <p>
	 * Test tries to deploy 'beginner-camel-log' quickstart to JBoss Fuse Runtime.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>import 'beginner-camel-log' project from JBoss Fuse quickstarts</li>
	 * <li>check that project is ok (no errors, unresolved dependencies, ...)</li>
	 * <li>deploy the project to JBoss Fuse</li>
	 * <li>check whether the project is successfully deployed (check whether JBoss Fuse log contains '(CamelContext:
	 * log-example-context) started')</li>
	 * </ol>
	 */
	@Test
	public void testBeginnerCamelLog() {

		String quickstart = serverRequirement.getConfig().getServerBase().getHome() + "/quickstarts/beginner/camel-log";
		MavenImportWizard.importProject(quickstart);
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
		CamelProject project = new CamelProject("beginner-camel-log");
		project.enableCamelNature();
		FuseServerManipulator.addModule(serverRequirement.getConfig().getName(), "beginner-camel-log");
		try {
			new WaitUntil(new FuseLogContainsText("(CamelContext: log-example-context) started"));
		} catch (WaitTimeoutExpiredException e) {
			fail("Project 'beginner-camel-log' was not sucessfully deployed!");
		}
		checkProblemsView();
	}

	private void checkProblemsView() {
		
		List<Problem> problems = new ProblemsView().getProblems(ProblemType.ERROR);
		if (problems.size() == 0) {
			return;
		}
		if (problems.size() == 1 && problems.get(0).getDescription().startsWith("Referenced file contains errors (http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd).")) {
			throw new JiraIssue("ENTESB-6115");
		}
		fail("There are some errors in Problems view");
	}
}
