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

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EMPTY_BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.SPRINGBOOT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.direct.project.Project;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.jboss.tools.fuse.reddeer.FileUtils;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.reddeer.dialog.WhereToFindMoreTemplatesMessageDialog;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardAdvancedPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimePage;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests wizard for creating a new Fuse Integration Project
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
public class NewFuseProjectWizardTest {

	/**
	 * Prepares test environment
	 */
	@After
	public void setupDeleteProjects() {
		ProjectFactory.deleteAllProjects();
		LogView log = new LogView();
		log.open();
		log.deleteLog();
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
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
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		assertFalse("The path is editable, but 'Use default workspace location' is selected!",
				firstPage.isPathEditable());
		firstPage.setProjectName("test");
		firstPage.useDefaultLocation(false);
		firstPage.setLocation(targetLocation.getAbsolutePath());
		wiz.next();
		wiz.next();
		NewFuseIntegrationProjectWizardAdvancedPage lastPage = new NewFuseIntegrationProjectWizardAdvancedPage(wiz);
		lastPage.selectTemplate(SPRINGBOOT);
		wiz.finish();
		File actualLocation = new File(Project.getLocation("test"));
		assertEquals("Location of a project is different!", targetLocation, actualLocation);
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
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName("test");
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		secondPage.setDeploymentType(STANDALONE);
		secondPage.setRuntimeType(KARAF);
		secondPage.typeCamelVersion("2.17.3");
		wiz.next();
		NewFuseIntegrationProjectWizardAdvancedPage lastPage = new NewFuseIntegrationProjectWizardAdvancedPage(wiz);
		lastPage.selectTemplate(EMPTY_BLUEPRINT);
		wiz.finish();
		assertFalse("Project was created with errors", hasErrors());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
		try {
			String pom = FileUtils.getFileContent(Project.getLocation("test") + "/pom.xml");
			assertTrue(pom.contains("<version>2.17.3</version>"));
		} catch (IOException e) {
			fail("Cannot access project's pom.xml file!");
		}
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
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName("test");
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		List<String> versions = secondPage.getAllAvailableCamelVersions();
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

	/**
	 * <p>
	 * Verifies that More examples are announced to users
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Hit 'Next'</li>
	 * <li>Click link to see more examples</li>
	 * <li>Check the text contains the Github repositories containing more examples</li>
	 * </ol>
	 *
	 * @author apupier
	 */
	@Test
	public void testMoreExamplesAvailable() {
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName("test");
		wiz.next();
		wiz.next();
		NewFuseIntegrationProjectWizardAdvancedPage lastPage = new NewFuseIntegrationProjectWizardAdvancedPage(wiz);
		lastPage.selectMoreExamplesLink();
		WhereToFindMoreTemplatesMessageDialog moreExamplesDialog = new WhereToFindMoreTemplatesMessageDialog();
		assertThat(moreExamplesDialog.getMessage()).contains("https://github.com/apache/camel/tree/master/examples",
				"https://github.com/fabric8-quickstarts");
		wiz.cancel();
		LogChecker.assertNoFuseError();
	}

}
