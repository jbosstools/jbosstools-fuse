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

import static org.jboss.tools.fuse.reddeer.ProjectType.BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.ProjectType.JAVA;
import static org.jboss.tools.fuse.reddeer.ProjectType.SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.OPENSHIFT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.EAP;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.SPRINGBOOT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.eclipse.exception.EclipseLayerException;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.preference.StagingRepositoriesPreferencePage;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardAdvancedPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimePage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType;
import org.jboss.tools.fuse.ui.bot.tests.utils.FuseProjectDefinition;
import org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * <p>
 * Tries to create and run projects from a single combination of <i>Camel version</i> and <i>Project template</i>
 * </p>
 * <p>
 * Use the following arguments:
 * <ul>
 * <li>-DfuseDeploymentType=... --- OpenShift / Standalone</li>
 * <li>-DfuseRuntimeType=... --- SpringBoot / Karaf / EAP</li>
 * <li>-DfuseCamelVersion=... --- e.g. 2.18.1.redhat-000012</li>
 * </ul>
 * </p>
 * 
 * @author tsedmik
 */
@CleanWorkspace
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class SingleFuseProjectTest extends DefaultTest {

	public static final String DEPLOYMENT_TYPE = System.getProperty("fuseDeploymentType", "OpenShift");
	public static final String RUNTIME_TYPE = System.getProperty("fuseRuntimeType", "SpringBoot");
	public static final String CAMEL_VERSION = System.getProperty("fuseCamelVersion", "2.18.1.redhat-000021");
	public static final String STAGING_REPOS = System.getProperty("staging.repositories", "false");

	private static final String PROJECT_NAME = "rfhaSS234ss";

	protected Logger log = Logger.getLogger(SingleFuseProjectTest.class);
	private FuseProjectDefinition project;

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Fuse project archetypes
	 */
	@Parameters(name = "{0}")
	public static Collection<FuseProjectDefinition> setupData() {
		NewFuseIntegrationProjectWizardDeploymentType deploymentType = DEPLOYMENT_TYPE.equals("OpenShift") ? OPENSHIFT
				: STANDALONE;
		NewFuseIntegrationProjectWizardRuntimeType runtimeType;
		switch (RUNTIME_TYPE) {
		case "SpringBoot":
			runtimeType = SPRINGBOOT;
			break;
		case "Karaf":
			runtimeType = KARAF;
			break;
		case "EAP":
			runtimeType = EAP;
			break;
		default:
			runtimeType = KARAF;
		}

		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName(PROJECT_NAME);
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		secondPage.setDeploymentType(deploymentType);
		secondPage.setRuntimeType(runtimeType);
		secondPage.typeCamelVersion(CAMEL_VERSION);
		wiz.next();
		NewFuseIntegrationProjectWizardAdvancedPage lastPage = new NewFuseIntegrationProjectWizardAdvancedPage(wiz);
		List<FuseProjectDefinition> projects = new ArrayList<>();
		for (String[] template : lastPage.getAllAvailableTemplates()) {
			ProjectType dsl;
			if (template[template.length - 1].toLowerCase().contains("blueprint")) {
				dsl = ProjectType.BLUEPRINT;
			} else if (template[template.length - 1].toLowerCase().contains("spring")) {
				dsl = ProjectType.SPRING;
			} else {
				dsl = ProjectType.JAVA;
			}
			FuseProjectDefinition tmp = new FuseProjectDefinition(runtimeType, deploymentType, template,
					CAMEL_VERSION, dsl);
			projects.add(tmp);
		}
		wiz.cancel();
		return projects;
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param template
	 *            a Fuse project archetype
	 */
	public SingleFuseProjectTest(FuseProjectDefinition project) {
		this.project = project;
	}

	@BeforeClass
	public static void useStagingRepos() {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		StagingRepositoriesPreferencePage page = new StagingRepositoriesPreferencePage(dialog);
		dialog.open();
		dialog.select(page);
		if (STAGING_REPOS.equals("true")) {
			page.toggleStagingRepositories(true);
		} else {
			page.toggleStagingRepositories(false);
		}
		dialog.ok();
	}

	@After
	public void setupClean() {
		new WorkbenchShell();
		new ProjectExplorer().deleteAllProjects();
		new WorkbenchShell();
	}

	private void createProject(FuseProjectDefinition project) {

		ProjectFactory.newProject(PROJECT_NAME).deploymentType(project.getDeploymentType())
				.runtimeType(project.getRuntimeType()).version(project.getCamelVersion())
				.template(project.getTemplate()).create();
	}

	private boolean hasErrors() {

		ProblemsView view = new ProblemsView();
		view.close();
		AbstractWait.sleep(TimePeriod.getCustom(5));
		view.open();
		return !view.getProblems(ProblemType.ERROR).isEmpty();
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
				log.warn("There is no context menu option to run the project as Local Camel Context.");
				return false;
			}
			ConsoleView console = new ConsoleView();
			if (console.getConsoleText().contains("BUILD FAILURE") || console.getConsoleText().contains("[ERROR]")
					|| console.consoleIsTerminated()) {
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

	private boolean isEditorOK() {

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

		return editor.isActive() && !editor.isDirty();
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
	 * <li>check if Camel Editor is opened and not dirty</li>
	 * <li>try to run the project as Local Camel Context</li>
	 * <li>check the console output, if there are some build errors</li>
	 * </ol>
	 */
	@Test
	public void testArchetype() {
		createProject(project);
		assertTrue("Project '" + project + "' is not present in Project Explorer", isPresent(PROJECT_NAME));
		if (hasErrors()) {
			log.warn("Project '" + project + "' was created with errors! Trying to update the project.");
			new CamelProject(PROJECT_NAME).update();
		}
		assertFalse("Project '" + project + "' was created with errors", hasErrors());
		if (project.getDsl() != JAVA) {
			assertTrue("Project '" + project + "' has something with Camel Editor", isEditorOK());
			if (project.getTemplate()[project.getTemplate().length - 1].toLowerCase().contains("empty")
					|| project.getTemplate()[project.getTemplate().length - 1].toLowerCase()
							.contains("spring bean - spring dsl") && project.getRuntimeType().equals(EAP)) {
				return;
			}
			assertTrue("Project '" + project + "' cannot be run as Local Camel Context", canBeRun("test"));
		}

	}
}