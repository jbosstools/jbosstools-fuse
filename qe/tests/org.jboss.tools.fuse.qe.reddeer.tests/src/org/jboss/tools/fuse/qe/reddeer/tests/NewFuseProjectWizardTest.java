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

import static org.jboss.reddeer.requirements.server.ServerReqState.PRESENT;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.EAP;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Fuse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.jboss.reddeer.core.handler.ShellHandler;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.tools.fuse.qe.reddeer.FileUtils;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.ResourceHelper;
import org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.ext.ProjectExt;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.jboss.tools.fuse.qe.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests wizard for creating a new Fuse Integration Project
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
@Fuse(server = @Server(type = { Fuse, EAP }, state = PRESENT))
public class NewFuseProjectWizardTest {

	@InjectRequirement
	private FuseRequirement serverRequirement;

	/**
	 * Prepares test environment
	 */
	@After
	public void setupDeleteProjects() {
		ProjectFactory.deleteAllProjects();
		new ErrorLogView().deleteLog();
		ShellHandler.getInstance().closeAllNonWorbenchShells();
	}

	/**
	 * <p>
	 * Tests non default workspace location of a project
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Change project location</li>
	 * <li>Finish wizard</li>
	 * <li>Check whether the project was created in selected location</li>
	 * </ol>
	 */
	@Test
	public void testDifferentWorkspaceLocation() {
		File targetLocation = new File(
				ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "resources/projects") + "/test");
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		assertFalse("The path is editable, but 'Use default Workspace location' is selected!", wiz.isPathEditable());
		wiz.setProjectName("test");
		wiz.useDefaultLocation(false);
		wiz.setLocation(targetLocation.getAbsolutePath());
		wiz.finish();
		File actualLocation = new File(new ProjectExt().getLocation("test"));
		assertEquals("Location of a project is different!", targetLocation, actualLocation);
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests 'Target Runtime' option
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>setup a JBoss Fuse runtime</li>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Check whether the configured runtime is present in 'Target Runtime' combobox</li>
	 * <li>Select the configured runtime and check whether Camel version is set properly and user cannot change it</li>
	 * <li>Select 'Target Runtime' to 'No Runtime Selected'</li>
	 * <li>Check whether user can change Camel Version</li>
	 * <li>Cancel the wizard</li>
	 * </ol>
	 */
	@Test
	public void testRuntime() {
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("test");
		wiz.next();
		assertEquals("There is something wrong in 'Target Runtime' Combo box!", 2, wiz.getTargetRuntimes().size());
		for (String temp : wiz.getTargetRuntimes()) {
			if (!(temp.equals("No Runtime selected")
					|| temp.equals(serverRequirement.getConfig().getServerBase().getRuntimeName()))) {
				fail("'Target Runtime' Combo box contains something wrong!");
			}
		}
		wiz.selectTargetRuntime(serverRequirement.getConfig().getServerBase().getRuntimeName());
		assertFalse("Path should not be editable!. The runtime is set.", wiz.isCamelVersionEditable());
		assertEquals("Camel versions are different (runtime vs wizard)!", serverRequirement.getConfig().getCamelVersion(), wiz.getCamelVersion());
		wiz.cancel();
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests 'Camel Version' option
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Verify that 'No Runtime Selected' is in 'Target Runtime'</li>
	 * <li>Change the version of Camel to '2.17.3'</li>
	 * <li>Finish the wizard</li>
	 * <li>Check whether the project has in 'pom.xml' right version of Camel</li>
	 * </ol>
	 */
	@Test
	public void testCamelVersion() {
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("test");
		wiz.next();
		wiz.selectTargetRuntime("No Runtime selected");
		wiz.selectCamelVersion("2.17.3");
		wiz.next();
		wiz.startWithEmptyProject();
		wiz.setProjectType(ProjectType.BLUEPRINT);
		wiz.finish();
		assertFalse("Project was created with errors", hasErrors());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
		try {
			String pom = FileUtils.getFileContent(new ProjectExt().getLocation("test") + "/pom.xml");
			assertTrue(pom.contains("<version>2.17.3</version>"));
		} catch (IOException e) {
			fail("Cannot access project's pom.xml file!");
		}
	}

	/**
	 * <p>
	 * Tries to create an empty project.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Hit 'Next'</li>
	 * <li>Check whether the 'Start with an empty project' is selected and 'Blueprint DSL'</li>
	 * <li>Finish the wizard</li>
	 * <li>Check the project - should be created without any problems</li>
	 * <li>Check Error Log - no error from Fuse Tooling should be present</li>
	 * </ol>
	 */
	@Test
	public void testEmptyProject() {
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("test");
		wiz.next();
		wiz.next();
		wiz.startWithEmptyProject();
		wiz.setProjectType(ProjectType.BLUEPRINT);
		wiz.finish();
		assertFalse("Project was created with errors", hasErrors());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tries to create a project from 'Content Based Router'
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Hit 'Next'</li>
	 * <li>Select 'Use a predefined template' and 'JBoss Fuse --> Beginner --> Content Based Router</li>
	 * <li>Select 'Blueprint DSL'</li>
	 * <li>Finish the wizard</li>
	 * <li>Check the project - should be created without any problems</li>
	 * <li>Check Error Log - no error from Fuse Tooling should be present</li>
	 * </ol>
	 */
	@Test
	public void testCBRProject() {
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("test");
		wiz.next();
		wiz.selectCamelVersion(SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630254);
		wiz.next();
		wiz.selectTemplate("JBoss Fuse", "Beginner", "Content Based Router");
		wiz.setProjectType(ProjectType.BLUEPRINT);
		wiz.finish();
		assertFalse("Project was created with errors", hasErrors());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tries to create an empty project from all available project types (Blueprint, Spring, Java)
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Hit 'Next'</li>
	 * <li>Check whether the 'Start with an empty project' is selected</li>
	 * <li>Try to create a project from all available project types (Blueprint, Spring, Java)
	 * <li>Finish the wizard</li>
	 * <li>Check the project - should be created without any problems</li>
	 * <li>Check Error Log - no error from Fuse Tooling should be present</li>
	 * <li>Check whether the project uses selected DSL</li>
	 * <li></li>
	 * </ol>
	 */
	@Test
	public void testProjectTypes() {

		// Blueprint DSL
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("blueprint");
		wiz.next();
		wiz.next();
		wiz.startWithEmptyProject();
		wiz.setProjectType(ProjectType.BLUEPRINT);
		wiz.finish();
		try {
			assertTrue("Created Camel File is not in Blueprint DSL",
					new CamelEditor("blueprint.xml").xpath("/blueprint").length() > 0);
		} catch (CoreException e) {
			fail("Something went wrong with access to Camel File - blueprint.xml");
		}
		assertFalse("Project with Blueprint DSL was created with errors", hasErrors());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);

		// Spring DSL
		wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("spring");
		wiz.next();
		wiz.next();
		wiz.startWithEmptyProject();
		wiz.setProjectType(ProjectType.SPRING);
		wiz.finish();
		try {
			assertTrue("Created Camel File is not in Spring DSL",
					new CamelEditor("camel-context.xml").xpath("/beans").length() > 0);
		} catch (CoreException e) {
			fail("Something went wrong with access to Camel File - camel-context.xml");
		}
		assertFalse("Project with Spring DSL was created with errors", hasErrors());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);

		// Java DSL
		wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("java");
		wiz.next();
		wiz.next();
		wiz.startWithEmptyProject();
		wiz.setProjectType(ProjectType.JAVA);
		wiz.finish();
		assertTrue("Project created with Java DSL do not contain 'CamelRoute.java' file", new ProjectExplorer()
				.getProject("java").containsItem("src/main/java", "com.mycompany", "CamelRoute.java"));
		assertFalse("Project with Java DSL was created with errors", hasErrors());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	private boolean hasErrors() {

		new ProblemsView().open();
		for (TreeItem item : new DefaultTree().getItems()) {
			if (item.getText().toLowerCase().contains("error"))
				return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Verifies that all supported Camel versions are available in New Fuse Project Wizard
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Get all available Camel versions in New Fuse Project Wizard</li>
	 * <li>Get all supported Camel versions</li>
	 * <li>Check availability of supported versions in available versions</li>
	 * </ol>
	 *
	 * @author djelinek
	 */
	@Test
	public void testSupportedCamelVersions() {

		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("test");
		wiz.next();
		List<String> versions = wiz.getCamelVersions();
		Collection<String> supported = SupportedCamelVersions.getCamelVersions();
		List<String> missing = new ArrayList<>();
		for (String sup : supported) {
			if (!versions.contains(sup)) {
				missing.add(sup);
			}
		}

		if (!missing.isEmpty()) {
			StringBuilder build = new StringBuilder();
			build.append("List of missing supported Camel versions:");
			for (String mis : missing) {
				build.append("\n" + mis);
			}
			fail(build.toString());
		}

	}

}
