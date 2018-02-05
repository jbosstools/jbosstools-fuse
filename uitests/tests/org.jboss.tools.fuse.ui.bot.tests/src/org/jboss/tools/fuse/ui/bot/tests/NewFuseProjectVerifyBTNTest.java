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

import static org.junit.Assert.assertFalse;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement.CleanErrorLog;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.condition.ControlIsEnabled;
import org.eclipse.reddeer.swt.impl.button.CancelButton;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.label.DefaultLabel;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.workbench.handler.EditorHandler;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.JiraIssue;
import org.jboss.tools.fuse.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.reddeer.condition.LabelContainsText;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.ConsolePreferenceUtil;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.jboss.tools.fuse.ui.bot.tests.utils.LogChecker;
import org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
	
/**
 * Verifies functionality of 'Verify' button on New Fuse Integration Project wizard
 * 
 * @author djelinek
 */
@CleanWorkspace
@CleanErrorLog
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class NewFuseProjectVerifyBTNTest {

	protected Logger log = Logger.getLogger(NewFuseProjectVerifyBTNTest.class);
	
	private static final String PROJECT_NAME = "test";
	
	private static final String EXISTING_CAMEL_VERSION = SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187;
	
	private static final String NON_EXISTING_CAMEL_VERSION = "0.0.0";
	
	private static final String INTERRUPT_CAMEL_VERSION = "0.0.1";
	
	private static final String COMMUNITY_CAMEL_VERSION = "2.20.2";
	
	private NewFuseIntegrationProjectWizard wizard;
	
	@BeforeClass
	public static void initSetup() {
		new WorkbenchShell().maximize();
		ConsolePreferenceUtil.setConsoleOpenOnError(false);
		ConsolePreferenceUtil.setConsoleOpenOnOutput(false);
	}
	
	@Before
	public void prepareTestEnvironment() {
		wizard = new NewFuseIntegrationProjectWizard();
		wizard.open();
		wizard.setProjectName(PROJECT_NAME);
		wizard.next();
	} 
	
	@After
	public void cleanSetup() {
		LogView view = new LogView(); 
		view.open();
		view.deleteLog();
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
		EditorHandler.getInstance().closeAll(true);
		ProjectFactory.deleteAllProjects();
	}
	
	/**
	 * Test tries to verify existing supported Camel version
	 */
	@Test
	public void testExistingCamelVersion() {	
		wizard.setCamelVersion(EXISTING_CAMEL_VERSION);
		wizard.clickVerifyButton();
		
		waiting();
		finish();
		LogChecker.assertNoFuseError();
	}
	
	/**
	 * Test tries to verify non-existing Camel version
	 */
	@Test
	public void testNonExistingCamelVersion() {
		wizard.setCamelVersion(NON_EXISTING_CAMEL_VERSION);
		wizard.clickVerifyButton();
		
		waiting();
		assertButtons();
		cancel();
		LogChecker.assertNoFuseError();
	}
	
	/**
	 * Test tries to verify community Camel version
	 */
	@Test
	public void testCommunityCamelVersion() {
		wizard.setCamelVersion(COMMUNITY_CAMEL_VERSION);
		wizard.clickVerifyButton();
		
		waiting();
		finish();
		LogChecker.assertNoFuseError();
	}
	
	/**
	 * Test tries to interrupt verifying of project Camel version 
	 */
	@Test
	public void testInterruptVerifying() {
		wizard.setCamelVersion(INTERRUPT_CAMEL_VERSION);
		wizard.clickVerifyButton();
		
		AbstractWait.sleep(TimePeriod.MEDIUM);
		new DefaultToolItem(wizard, 0).click(); // click on 'Cancel Operation'
		waiting();
		testIssue2840();
		assertButtons();
		cancel();
		LogChecker.assertNoFuseError();
	}
	
	/**
	 * Verifies 'Finish' and 'Next' buttons availability  
	 */
	private void assertButtons() {
		assertFalse("Button 'Next' should be disabled", wizard.isNextEnabled());
		assertFalse("Button 'Finish' should be disabled", wizard.isFinishEnabled());
	}
	
	/**
	 * Wait until 'Finish' button is enabled and click
	 */
	private void finish() {
		new WaitUntil(new ControlIsEnabled(new FinishButton(wizard)), TimePeriod.MEDIUM);
	}
	
	/**
	 * Wait until 'Cancel' button is enabled and click
	 */
	private void cancel() {
		new WaitUntil(new ControlIsEnabled(new CancelButton(wizard)), TimePeriod.MEDIUM);
	}
	
	private void waiting() {
		String label = "Checking availability for Camel version " + wizard.getCamelVersion() + ". Please wait...";
		new WaitWhile(new LabelContainsText(new DefaultLabel(wizard, 5), label), TimePeriod.VERY_LONG);
	}
	
	/**
	 * https://issues.jboss.org/browse/FUSETOOLS-2840
	 */
	private void testIssue2840() {
		if(wizard.isNextEnabled() || wizard.isFinishEnabled())
			throw new JiraIssue("FUSETOOLS-2840");
		else 
			return;
	}
}
