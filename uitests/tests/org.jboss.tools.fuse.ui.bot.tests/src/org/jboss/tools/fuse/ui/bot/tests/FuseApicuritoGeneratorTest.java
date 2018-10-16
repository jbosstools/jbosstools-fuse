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
import static org.junit.Assert.fail;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.wizard.ImportFromArchiveWizard;
import org.jboss.tools.fuse.reddeer.wizard.ImportFromArchiveWizardFirstPage;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;

/**
 * Tests projects created via fuse-apicurito-generator (<a href=
 * "https://github.com/jboss-fuse/fuse-apicurito-generator">https://github.com/jboss-fuse/fuse-apicurito-generator</a>)
 * 
 * Use the following arguments:
 * <ul>
 * <li>-DapicuritoProject=... --- Filesystem path to the project project (zip archive) which was created with
 * fuse-apicurito-generator</li>
 * </ul>
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
@OpenPerspective(FuseIntegrationPerspective.class)
public class FuseApicuritoGeneratorTest {

	public static final String PROJECT_PATH = System.getProperty("apicuritoProject");
	public static final String NO_PATH_ERROR = "Please, set path to the project '-DapicuritoProject'";
	public static final String PROJECT_SUFFIX = "_expanded";
	public static String projectName;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@BeforeClass
	public static void setup() {
		if (PROJECT_PATH == null || PROJECT_PATH.equals("none")) {
			fail(NO_PATH_ERROR);
		}
		String[] path = PROJECT_PATH.split("/");
		projectName = path[path.length - 1] + PROJECT_SUFFIX;
	}
	
	@Test
	public void testImportProject() {
		importProject();
		errorCollector.checkThat("The imported project is not present in Project Explorer", true, equalTo(isPresent(projectName)));
		errorCollector.checkThat("The Camel editor is dirty", true, equalTo(isEditorOK(projectName)));
		errorCollector.checkThat("The project was imported with errors / warnings", false, equalTo(hasErrors()));
	}

	private void importProject() {
		ImportFromArchiveWizard wizard = new ImportFromArchiveWizard();
		wizard.open();
		ImportFromArchiveWizardFirstPage firstPage = new ImportFromArchiveWizardFirstPage(wizard);
		firstPage.getImportSourceCMB().setText(PROJECT_PATH);
		wizard.finish(TimePeriod.getCustom(600));
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	private boolean isPresent(String name) {
		return new ProjectExplorer().containsProject(name);
	}

	private boolean hasErrors() {
		ProblemsView view = new ProblemsView();
		view.open();
		return !view.getProblems(ProblemType.ALL).isEmpty();
	}

	private boolean isEditorOK(String name) {
		new CamelProject(name).openFirstCamelContext();
		CamelEditor editor = new CamelEditor();
		return editor.isActive() && !editor.isDirty();
	}
}
