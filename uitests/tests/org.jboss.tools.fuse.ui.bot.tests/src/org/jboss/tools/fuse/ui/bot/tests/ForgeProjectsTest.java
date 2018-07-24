/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.core.resources.Resource;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.fuse.reddeer.ForgeOption;
import org.jboss.tools.fuse.reddeer.condition.ForgeConsoleHasText;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.view.ForgeConsoleView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tries to create and run projects from all available templates which are available via JBoss Forge
 * <p>
 * Use the following argument to specify type of projects:
 * <ul>
 * <li>-DfuseRuntimeType=... --- "Spring Boot" / Karaf</li>
 * </ul>
 * </p>
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@RunWith(RedDeerSuite.class)
public class ForgeProjectsTest extends DefaultTest {

	protected Logger log = Logger.getLogger(ForgeProjectsTest.class);

	public static final String CHOICE_PROJECT_TYPE = "fuse";
	public static final String CHOICE_BUILD_SYSTEM = "Maven";
	public static final String RUNTIME_TYPE = System.getProperty("fuseRuntimeType", "Spring Boot");
	public static final String PROJECT_NAME = "test-forge-project";

	public static final String RESPONSE_PROJECT_NAME = "* Project name:";
	public static final String RESPONSE_TOP_PACKAGE = "? Top level package";
	public static final String RESPONSE_VERSION = "? Version";
	public static final String RESPONSE_FINAL_NAME = "? Final name:";
	public static final String RESPONSE_LOCATION = "? Project location";
	public static final String RESPONSE_TARGET_ROOT = "? Use Target Location Root";
	public static final String RESPONSE_PROJECT_TYPE = "* Project type:";
	public static final String RESPONSE_BUILD_SYSTEM = "* Build system";
	public static final String RESPONSE_ARCHETYPE_CATALOG = " Archetype catalog version";
	public static final String RESPONSE_PROJECT_TYPE2 = "? Project type ";
	public static final String RESPONSE_ARCHETYPE = "* Archetype:";
	public static final String RESPONSE_ERROR = "***ERROR***";
	public static final String RESPONSE_PROJECT_CREATED = "***SUCCESS*** Project named '" + PROJECT_NAME
			+ "' has been created.";

	public static final String CMD_RETURN = "\n";
	public static final String CMD_NEW_PROJECT = "project-new \n";

	private ForgeOption project;

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Parameters(name = "{0}")
	public static Collection<ForgeOption> setupData() {
		ForgeConsoleView view = new ForgeConsoleView();
		view.open();
		view.start();
		List<ForgeOption> params = getAvailableArchetypes();
		view.setConsoleText(CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_ERROR));
		return params;
	}

	public ForgeProjectsTest(ForgeOption project) {
		this.project = project;
	}

	@Before
	public void setupDeleteProjects() {
		new ProjectExplorer().deleteAllProjects();
	}

	private static List<ForgeOption> getAvailableArchetypes() {
		ForgeConsoleView view = new ForgeConsoleView();
		view.setConsoleText(CMD_NEW_PROJECT);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_PROJECT_NAME));
		view.setConsoleText(PROJECT_NAME + CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_TOP_PACKAGE));
		view.setConsoleText(CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_VERSION));
		view.setConsoleText(CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_FINAL_NAME));
		view.setConsoleText(CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_LOCATION));
		view.setConsoleText(CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_TARGET_ROOT));
		view.setConsoleText(CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_PROJECT_TYPE));
		for (ForgeOption option : view.getOptions()) {
			if (CHOICE_PROJECT_TYPE.equals(option.getName())) {
				view.setConsoleText(option.getNumber() + CMD_RETURN);
				break;
			}
		}
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_BUILD_SYSTEM));
		view.setConsoleText(CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_ARCHETYPE_CATALOG), TimePeriod.LONG);
		view.setConsoleText(CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_PROJECT_TYPE2));
		for (ForgeOption option : view.getOptions()) {
			if (RUNTIME_TYPE.equals(option.getName())) {
				view.setConsoleText(option.getNumber() + CMD_RETURN);
			}
		}
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_ARCHETYPE));
		return view.getOptions();
	}

	@Test
	public void testArchetype() {
		ForgeConsoleView view = new ForgeConsoleView();
		view.open();
		createProject(project);
		if (hasErrors()) {
			log.warn("Project '" + PROJECT_NAME + "' was created with errors! Trying to update the project.");
			new CamelProject(PROJECT_NAME).update();
		}
		checkCamelEditor(PROJECT_NAME);
		collector.checkThat("Problems view contains some errors", hasErrors(), equalTo(false));
	}

	private void createProject(ForgeOption archetype) {
		ForgeConsoleView view = new ForgeConsoleView();
		view.activate();
		getAvailableArchetypes();
		view.setConsoleText(archetype.getNumber() + CMD_RETURN);
		new WaitUntil(new ForgeConsoleHasText(RESPONSE_ARCHETYPE), TimePeriod.VERY_LONG);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		new WaitUntil(new ProjectExists(PROJECT_NAME));
		view.setConsoleText("cd .." + CMD_RETURN);
	}

	private void checkCamelEditor(String projectName) {
		for (Resource res : new ProjectExplorer().getProject(projectName).getChildren()) {
			if ("Camel Contexts".equals(res.getText())) {
				new CamelProject(projectName).openFirstCamelContext();
				collector.checkThat("Camel Editor is dirty", new DefaultEditor().isDirty(), equalTo(false));
				break;
			}
		}
	}

	private boolean hasErrors() {
		ProblemsView view = new ProblemsView();
		view.open();
		return !view.getProblems(ProblemType.ERROR).isEmpty();
	}
}
