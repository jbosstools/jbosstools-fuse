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

import static org.jboss.tools.fuse.qe.reddeer.ProjectType.BLUEPRINT;
import static org.jboss.tools.fuse.qe.reddeer.ProjectType.JAVA;
import static org.jboss.tools.fuse.qe.reddeer.ProjectType.SPRING;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.core.exception.CoreLayerException;
import org.jboss.reddeer.eclipse.exception.EclipseLayerException;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.console.ConsoleView;
import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView.ProblemType;
import org.jboss.reddeer.eclipse.ui.views.log.LogMessage;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.qe.reddeer.FileUtils;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.preference.MavenUserSettingsPreferencePage;
import org.jboss.tools.fuse.qe.reddeer.preference.StagingRepositoriesPreferencePage;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tries to create and run projects from all available templates with all available Camel versions as Local Camel
 * Context. Always with empty Maven repository.
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class FuseProjectTestLong extends DefaultTest {

	protected Logger log = Logger.getLogger(FuseProjectTestLong.class);
	private String template;
	private static String maven;

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Fuse project archetypes
	 */
	@Parameters
	public static Collection<String> setupData() {
		List<String> temp = ProjectFactory.getAllAvailableTemplates();
		temp.add("empty:blueprint");
		temp.add("empty:spring");
		temp.add("empty:java");
		List<String> versions = ProjectFactory.getAllAvailableCamelVersions();
		List<String> product = new ArrayList<String>();
		for (String name : temp) {
			for (String version : versions) {
				product.add(name + ":" + version);
			}
		}
		return product;
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param template
	 *            a Fuse project archetype
	 */
	public FuseProjectTestLong(String template) {
		this.template = template;
	}

	/**
	 * Changes local repository to an empty one
	 */
	@BeforeClass
	public static void setupMaven() {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		MavenUserSettingsPreferencePage page = new MavenUserSettingsPreferencePage();
		dialog.open();
		dialog.select(page);
		maven = page.getUserSettings();
		page.setUserSettings(System.getProperty("maven.settings"));
		page.updateSettings();
		page.reindex();
		dialog.ok();
	}

	@BeforeClass
	public static void useStagingRepos() {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		StagingRepositoriesPreferencePage page = new StagingRepositoriesPreferencePage();
		dialog.open();
		dialog.select(page);
		if (System.getProperty("staging.repositories").equals("true")) {
			page.toggleStagingRepositories(true);
		} else {
			page.toggleStagingRepositories(false);
		}
		dialog.ok();
	}

	/**
	 * Deletes a local Maven repository
	 */
	@Before
	public void setupDeleteMavenRepo() {

		FileUtils.deleteDir(new File(System.getProperty("maven.repository")));
	}

	/**
	 * Revertes change of the local repository
	 */
	@AfterClass
	public static void setupMavenBack() {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		MavenUserSettingsPreferencePage page = new MavenUserSettingsPreferencePage();
		dialog.open();
		dialog.select(page);
		page.setUserSettings(maven);
		page.updateSettings();
		page.reindex();
		dialog.ok();

		FileUtils.deleteDir(new File(System.getProperty("maven.repository")));
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupClean() {

		defaultClean();
		new WorkbenchShell();
		new ProjectExplorer().deleteAllProjects();
		new WorkbenchShell();
	}

	private boolean hasErrors() {

		ProblemsView view = new ProblemsView();
		view.open();
		if (view.getProblems(ProblemType.ERROR).isEmpty()) {
			return false;
		}
		return true;
	}

	private boolean isPresent(String name) {

		return new ProjectExplorer().containsProject(name);
	}

	private boolean canBeRun(String name) {

		try {
			log.info("Trying to run the project as Local Camel Context");
			try {
				new CamelProject(name).runCamelContext();
			} catch (CoreLayerException e) {
				log.warn("There is no context menu option to run the project as Local Camel Context. Template: "
						+ template);
				return false;
			}
			ConsoleView console = new ConsoleView();
			if (console.getConsoleText().contains("BUILD FAILURE")
					|| console.getConsoleText().toLowerCase().contains("[ERROR]") || console.consoleIsTerminated()) {
				log.warn("There is a problem with building '" + name + "' project");
				return false;
			}
		} catch (EclipseLayerException e) {
			log.warn("There is no Camel Context file in '" + name + "' project");
		} catch (WaitTimeoutExpiredException e) {
			log.warn("There is a problem with building '" + name + "' project");
			return false;
		}

		return true;
	}

	/**
	 * <p>
	 * Tries to create a Fuse project from <i>${FUSE-TEMPLATE}</i> template and tries to run the project as Local Camel
	 * Context.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project</li>
	 * <li>open Problems view</li>
	 * <li>check if there are some errors</li>
	 * <li>(optional) try to update project - Maven --> Update Maven Project --> Force Update</li>
	 * <li>check if there are some errors</li>
	 * <li>try to run the project as Local Camel Context</li>
	 * <li>check the console output, if there are some build errors</li>
	 * </ol>
	 */
	@Test
	public void testArchetype() {

		String[] templateComposite = template.split(":");
		ProjectType type = BLUEPRINT;
		if (templateComposite[1].startsWith("spring"))
			type = SPRING;
		if (templateComposite[1].startsWith("java"))
			type = JAVA;
		if (templateComposite[0].equals("empty")) {
			ProjectFactory.newProject("test").version(templateComposite[2]).type(type).create();
		} else {
			ProjectFactory.newProject("test").version(templateComposite[2]).template(templateComposite[0]).type(type)
					.create();
		}
		assertTrue("Project '" + template + "' is not present in Project Explorer", isPresent("test"));
		if (hasErrors()) {
			log.warn("Project '" + template + "' was created with errors! Trying to update the project.");
			new CamelProject("test").update();
		}
		assertFalse("Project '" + template + "' was created with errors", hasErrors());
		if ((type != JAVA) && // skip Java DSLs
				(!template.startsWith("empty")) && // skip Empty project templates
				(!template.startsWith("Spring on EAP")) // skip Spring on EAP template (see
														// https://issues.jboss.org/browse/FUSETOOLS-1456)
		)
			assertTrue("Project '" + template + "' cannot be run as Local Camel Context", canBeRun("test"));
	}

	/**
	 * <p>
	 * Verifies that Camel editor in created Fuse project is opened (activated) and opened properly (contains
	 * components)
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project</li>
	 * <li>check Camel editor errors</li>
	 * <li>check if Camel editor is open and active</li>
	 * <li>check if Camel editor isn't open as "dirty"</li>
	 * <li>check if Camel editor is open properly (contains components)</li>
	 * </ol>
	 * 
	 * @author djelinek
	 */
	@Test
	public void testCamelEditor() {

		String[] templateComposite = template.split(":");
		ProjectType type = BLUEPRINT;
		if (templateComposite[1].startsWith("spring"))
			type = SPRING;
		if (templateComposite[1].startsWith("java"))
			type = JAVA;
		if (templateComposite[0].equals("empty")) {
			ProjectFactory.newProject("test").version(templateComposite[2]).type(type).create();
		} else {
			ProjectFactory.newProject("test").version(templateComposite[2]).template(templateComposite[0]).type(type)
					.create();
		}

		/*
		 * Check Error log view for possible Camel editor errors
		 */
		ErrorLogView err = new ErrorLogView();
		List<LogMessage> msg = err.getErrorMessages();
		for (LogMessage logMessage : msg) {
			if (logMessage.getPlugin().equals("org.fusesource.ide.camel.editor"))
				fail("Project '" + template + "' is created with Camel editor errors");
		}

		/*
		 * Possible variants of Camel Context XML, for Fuse project types Spring DSL and Blueprint DSL
		 */
		String[] springContext = { "camel-context.xml", "jboss-camel-context.xml" };
		String[] blueprintContext = { "blueprint.xml", "camel-blueprint.xml" };

		CamelEditor editor = null;
		int i = 0;

		if (type == SPRING) {
			try {
				editor = new CamelEditor(springContext[i]);
			} catch (Exception e) {
				editor = new CamelEditor(springContext[++i]);
			}
		} else if (type == BLUEPRINT) {
			try {
				editor = new CamelEditor(blueprintContext[i]);
			} catch (Exception e) {
				editor = new CamelEditor(blueprintContext[++i]);
			}
		}

		if (type != JAVA) { // skip Java DSLs
			assertTrue("Camel editor wasn't opened and activated -> " + template, editor.isActive());
			assertFalse("Camel editor was opened as 'dirty' -> " + template, editor.isDirty());
			assertFalse("Camel editor wasn't opened properly, it doesn't contains any component -> " + template,
					editor.palleteGetComponents().isEmpty());
		}
	}
}
