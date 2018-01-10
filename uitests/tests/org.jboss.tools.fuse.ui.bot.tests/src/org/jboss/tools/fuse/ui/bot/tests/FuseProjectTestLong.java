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

import static org.jboss.tools.fuse.reddeer.ProjectType.BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.ProjectType.JAVA;
import static org.jboss.tools.fuse.reddeer.ProjectType.SPRING;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.eclipse.exception.EclipseLayerException;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.eclipse.reddeer.eclipse.ui.views.log.LogMessage;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.FileUtils;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.preference.MavenUserSettingsPreferencePage;
import org.jboss.tools.fuse.reddeer.preference.StagingRepositoriesPreferencePage;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.ui.bot.tests.utils.FuseProjectDefinition;
import org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory;
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
	private FuseProjectDefinition project;
	private static String maven;

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Fuse project archetypes
	 */
	@Parameters
	public static Collection<FuseProjectDefinition> setupData() {
		return ProjectFactory.getAllAvailableTemplates();
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param template
	 *            a Fuse project archetype
	 */
	public FuseProjectTestLong(FuseProjectDefinition project) {
		this.project = project;
	}

	/**
	 * Changes local repository to an empty one
	 */
	@BeforeClass
	public static void setupMaven() {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		MavenUserSettingsPreferencePage page = new MavenUserSettingsPreferencePage(dialog);
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
		StagingRepositoriesPreferencePage page = new StagingRepositoriesPreferencePage(dialog);
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
		MavenUserSettingsPreferencePage page = new MavenUserSettingsPreferencePage(dialog);
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

	private void createProject(FuseProjectDefinition project) {

		if (project.getTemplate().equals("empty")) {
			ProjectFactory.newProject("test").version(project.getCamelVersion()).type(project.getDsl()).create();
		} else {
			ProjectFactory.newProject("test").version(project.getCamelVersion()).template(project.getTemplate()).type(project.getDsl())
					.create();
		}
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
						+ project);
				return false;
			}
			ConsoleView console = new ConsoleView();
			if (console.getConsoleText().contains("BUILD FAILURE")
					|| console.getConsoleText().contains("[ERROR]") || console.consoleIsTerminated()) {
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

		createProject(project);
		assertTrue("Project '" + project + "' is not present in Project Explorer", isPresent("test"));
		if (hasErrors()) {
			log.warn("Project '" + project + "' was created with errors! Trying to update the project.");
			new CamelProject("test").update();
		}
		assertFalse("Project '" + project + "' was created with errors", hasErrors());
		if ((project.getDsl() != JAVA) && // skip Java DSLs
				(!project.getTemplate().startsWith("empty")) // skip Empty project templates
		)
			assertTrue("Project '" + project + "' cannot be run as Local Camel Context", canBeRun("test"));
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

		createProject(project);

		/*
		 * Check Error log view for possible Camel editor errors
		 */
		LogView err = new LogView();
		List<LogMessage> msg = err.getErrorMessages();
		for (LogMessage logMessage : msg) {
			if (logMessage.getPlugin().equals("org.fusesource.ide.camel.editor"))
				fail("Project '" + project + "' is created with Camel editor errors");
		}

		/*
		 * Possible variants of Camel Context XML, for Fuse project types Spring DSL and Blueprint DSL
		 */
		String[] springContext = { "camel-context.xml", "jboss-camel-context.xml" };
		String[] blueprintContext = { "blueprint.xml", "camel-blueprint.xml" };

		CamelEditor editor = null;
		int i = 0;

		if (project.getDsl() == SPRING) {
			try {
				editor = new CamelEditor(springContext[i]);
			} catch (Exception e) {
				editor = new CamelEditor(springContext[++i]);
			}
		} else if (project.getDsl() == BLUEPRINT) {
			try {
				editor = new CamelEditor(blueprintContext[i]);
			} catch (Exception e) {
				editor = new CamelEditor(blueprintContext[++i]);
			}
		}

		if (project.getDsl() != JAVA) { // skip Java DSLs
			assertTrue("Camel editor wasn't opened and activated -> " + project, editor.isActive());
			assertFalse("Camel editor was opened as 'dirty' -> " + project, editor.isDirty());
			assertFalse("Camel editor wasn't opened properly, it doesn't contains any component -> " + project,
					editor.palleteGetComponents().isEmpty());
		}
	}
}
