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

import static org.eclipse.reddeer.requirements.server.ServerRequirementState.PRESENT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.SPRINGBOOT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimePage;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
@Fuse(state = PRESENT)
public class NewFuseProjectWizardWithFuseRequirementsTest {

	@InjectRequirement
	private FuseRequirement serverRequirement;

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
	 * <li>Select 'Target Runtime' to 'None selected'</li>
	 * <li>Check whether user can change Camel Version</li>
	 * <li>Cancel the wizard</li>
	 * </ol>
	 */
	@Test
	public void testRuntime() {
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName("test");
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		secondPage.setDeploymentType(STANDALONE);
		secondPage.setRuntimeType(KARAF);
		assertEquals("There is something wrong in 'Target Runtime' Combo box!", 2,
				secondPage.getKarafRuntimes().size());
		for (String temp : secondPage.getKarafRuntimes()) {
			if (!(temp.equals("None selected")
					|| temp.equals(serverRequirement.getConfiguration().getServer().getRuntimeName()))) {
				fail("'Target Runtime' Combo box contains something wrong!");
			}
		}
		secondPage.selectKarafRuntime(serverRequirement.getConfiguration().getServer().getRuntimeName());
		assertFalse("Path should not be editable!. The runtime is set.", secondPage.isCamelVersionComboEditable());
		assertEquals("Camel versions are different (runtime vs wizard)!",
				serverRequirement.getConfiguration().getCamelVersion(), secondPage.getSelectedCamelVersion());
		wiz.cancel();
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests switching of runtime types and checking whether Camel version is editable. For more details:
	 * https://issues.jboss.org/browse/FUSETOOLS-2877
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>setup a JBoss Fuse runtime</li>
	 * <li>Invoke <i>File --> New --> Fuse Integration Project</i> wizard</li>
	 * <li>Set project name</li>
	 * <li>Hit 'Next'</li>
	 * <li>Select the configured runtime and check whether Camel version is set properly and user cannot change it</li>
	 * <li>Select Runtime type to "SpringBoot" and check whether Camel version is editable</li>
	 * <li>Select Runtime type back to Karaf. There should be a runtime selected in appropriate combobox. Check whether
	 * Camel version combobox is disabled</li>
	 * <li>Cancel the wizard</li>
	 * </ol>
	 */
	@Test
	public void testRuntimeTypesSwitching() {
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName("test");
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		secondPage.setDeploymentType(STANDALONE);
		secondPage.setRuntimeType(KARAF);
		assertTrue("No runtime is selected => Camel version should be editable",
				secondPage.isCamelVersionComboEditable());
		secondPage.selectKarafRuntime(serverRequirement.getConfiguration().getServer().getRuntimeName());
		assertFalse("A runtime is selected => Camel version should not be editable",
				secondPage.isCamelVersionComboEditable());
		secondPage.setRuntimeType(SPRINGBOOT);
		assertTrue("SpringBoot runtime is selected => Camel version should be editable",
				secondPage.isCamelVersionComboEditable());
		secondPage.setRuntimeType(KARAF);
		assertFalse("A runtime is selected => Camel version should not be editable",
				secondPage.isCamelVersionComboEditable());
		wiz.cancel();
	}
}
