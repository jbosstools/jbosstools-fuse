/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.eclipse.reddeer.requirements.server.ServerRequirementState.PRESENT;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.eclipse.core.resources.Resource;
import org.eclipse.reddeer.eclipse.m2e.core.ui.wizard.MavenImportWizard;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.ConsolePreferencePage;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Test compliance between Fuse Tooling and Quickstarts distributed with Fuse runtime
 * 
 * @author tsedmik
 */
@CleanWorkspace
@RunWith(RedDeerSuite.class)
@Fuse(state = PRESENT)
@OpenPerspective(FuseIntegrationPerspective.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class QuickStartsTest {

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@InjectRequirement
	private static FuseRequirement serverRequirement;

	private String quickstart;

	public QuickStartsTest(String quickstart) {
		this.quickstart = quickstart;
	}

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available quickstarts
	 */
	@Parameters(name = "{0}")
	public static Collection<String> setupData() {
		File quickstartsHome = new File(serverRequirement.getConfiguration().getServer().getHome() + "/quickstarts/");
		List<String> allQuickstarts = new ArrayList<>();
		searchQuickstart(quickstartsHome, allQuickstarts);
		return allQuickstarts;
	}

	/**
	 * Recursive algorithm which search for quickstarts in Fuse runtime distribution. A Folder with quickstart is
	 * determined as a folder with child folder 'src'.
	 * 
	 * @param startingPoint
	 *                          File from where is searching started
	 * @param result
	 *                          List of absolute paths to all quickstarts found
	 */
	private static void searchQuickstart(File startingPoint, List<String> result) {
		for (String item : startingPoint.list()) {
			File itemAsFile = new File(startingPoint.getAbsolutePath() + "/" + item);
			if (itemAsFile.isFile()) {
				continue;
			}
			if (itemAsFile.isDirectory() && itemAsFile.getName().equals("src")) {
				result.add(itemAsFile.getParentFile().getAbsolutePath());
				break;
			}
			searchQuickstart(itemAsFile, result);
		}
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupEnv() {

		new WorkbenchShell().maximize();

		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		ConsolePreferencePage consolePref = new ConsolePreferencePage(dialog);
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
		new LogView().open();
		new LogView().deleteLog();
	}

	/**
	 * Deletes all created projects
	 */
	@After
	public void setupDeleteProjects() {
		ProjectFactory.deleteAllProjects();
	}

	/**
	 * Remove all deployed project on the runtime
	 */
	@After
	public void setupRemoveAllDeployedProjects() {
		FuseServerManipulator.removeAllModules(serverRequirement.getConfiguration().getServer().getName());
	}

	/**
	 * <p>
	 * Test tries to import a quickstart from Red Hat Fuse Runtime.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>import a project from Red Hat Fuse quickstarts</li>
	 * <li>check that project is ok (no errors, unresolved dependencies, ...)</li>
	 * </ol>
	 */
	@Test
	public void testQuickStart() {

		MavenImportWizard.importProject(quickstart, TimePeriod.getCustom(1000));
		String projectName = new ProjectExplorer().getProjects().get(0).getName();
		new CamelProject(projectName).update();
		LogChecker.assertNoFuseError();
		collector.checkThat("Problems view contains some errors", new ProblemsView().getProblems(ProblemType.ERROR).isEmpty(), equalTo(true));
		checkCamelEditor(projectName);
	}

	private void checkCamelEditor(String projectName) {
		for (Resource res : new ProjectExplorer().getProject(projectName).getChildren()) {
			if (res.getText().equals("Camel Contexts")) {
				new CamelProject(projectName).openFirstCamelContext();
				collector.checkThat("Camel Editor is dirty", new DefaultEditor().isDirty(), equalTo(false));
				break;
			}
		}
	}
}
