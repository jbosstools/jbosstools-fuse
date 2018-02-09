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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.jface.dialogs.MessageTypeEnum;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.condition.ControlIsEnabled;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectDependenciesPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectDetailsPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectWizard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests "New Fuse Ignite Extension Project Wizard"<br/>
 * <i>Quick</i> - tests only the wizard, does not finish it
 * 
 * @author tsedmik
 */
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class NewFuseIgniteExtWizardTest {

	private static final String CAMEL_VERSION_ERROR_MSG = "seems to be unavailable";
	private static final String CAMEL_VERSION_OK_MSG = "The specified Apache Camel version is valid.";

	private NewFuseIgniteExtensionProjectWizard wizard;

	@Before
	public void openWizard() {
		wizard = new NewFuseIgniteExtensionProjectWizard();
		wizard.open();
	}

	@After
	public void closeWizard() {
		if (wizard.isOpen()) {
			wizard.cancel();
		}

		LogView errorLog = new LogView();
		errorLog.open();
		errorLog.deleteLog();
	}

	/**
	 * <p>
	 * Tests the first page of <i>New Fuse Ignite Extension Project</i> wizard
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Deselect "Use default Workspace location" option</li>
	 * <li>Check whether "Path" and "Browse" button became enabled</li>
	 * <li>Set a project name</li>
	 * <li>Check whether a user can go to next page (which should be unaccessible)</li>
	 * <li>Select "Use default Workspace location" option/li>
	 * <li>Check whether a user can go to next page (which should be accessible)</li>
	 * </ol>
	 */
	@Test
	public void testLocation() {
		NewFuseIgniteExtensionProjectFirstPage firstPage = new NewFuseIgniteExtensionProjectFirstPage(wizard);

		assertFalse(firstPage.getPathTXT().isEnabled());
		assertFalse(firstPage.getBrowseBTN().isEnabled());
		assertTrue(firstPage.getUseDefaultWorkspaceLocationCHBgroup().isChecked());

		firstPage.toggleUseDefaultWorkspaceLocationGroup(false);

		assertTrue(firstPage.getBrowseBTN().isEnabled());
		assertTrue(firstPage.getPathTXT().isEnabled());
		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		
		firstPage.setTextProjectName("MyNewIgniteExt");

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		firstPage.toggleUseDefaultWorkspaceLocationGroup(true);

		assertTrue(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertNotEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests the second page (dependencies) of <i>New Fuse Ignite Extension Project</i> wizard
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Set a project name</li>
	 * <li>Try to delete each version and check whether user can proceed to another page (UNACCESSIBLE)</li>
	 * <li>Try to set the latest versions</li>
	 * <li>Go to the next page</li>
	 * </ol>
	 */
	@Test
	public void testDependencies() {
		NewFuseIgniteExtensionProjectFirstPage firstPage = new NewFuseIgniteExtensionProjectFirstPage(wizard);
		firstPage.setTextProjectName("MyNewIgniteExt");
		wizard.next();

		assertTrue(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertNotEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		NewFuseIgniteExtensionProjectDependenciesPage secondPage = new NewFuseIgniteExtensionProjectDependenciesPage(wizard);
		secondPage.setTextSpringBootVersion("");

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		secondPage.setTextSpringBootVersion(secondPage.getItemsSpringBootVersion().get(0));
		secondPage.setTextCamelVersion("");

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		secondPage.setTextCamelVersion(secondPage.getItemsCamelVersion().get(0));
		secondPage.setTextFuseIgniteVersion("");

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		secondPage.setTextFuseIgniteVersion(secondPage.getItemsFuseIgniteVersion().get(0));

		assertTrue(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertNotEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests Camel version verify button on the second page (dependencies) of <i>New Fuse Ignite Extension Project</i> wizard
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Set a project name</li>
	 * <li>Set an invalid Camel version and verify it</li>
	 * <li>Check wizard message</li>
	 * <li>Check whether a user can go to the next page (FAIL)</li>
	 * <li>Set a valid Camel version and verify it</li>
	 * <li>Check wizard message</li>
	 * <li>Check whether a user can go to the next page (SUCCESS)</li>
	 * </ol>
	 */
	@Test
	public void testCamelVersionVerify() {
		NewFuseIgniteExtensionProjectFirstPage firstPage = new NewFuseIgniteExtensionProjectFirstPage(wizard);
		firstPage.setTextProjectName("MyNewIgniteExt");
		wizard.next();
		NewFuseIgniteExtensionProjectDependenciesPage secondPage = new NewFuseIgniteExtensionProjectDependenciesPage(wizard);
		secondPage.setTextCamelVersion("2.20.1aaa");
		secondPage.clickVerifyBTN();
		new WaitUntil(new ControlIsEnabled(new PushButton(wizard, secondPage.getVerifyBTN().getText())));

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertTrue(wizard.getMessage().contains(CAMEL_VERSION_ERROR_MSG));

		secondPage.setTextCamelVersion(secondPage.getItemsCamelVersion().get(0));
		secondPage.clickVerifyBTN();
		new WaitUntil(new ControlIsEnabled(new PushButton(wizard, secondPage.getVerifyBTN().getText())));

		assertTrue(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertTrue(wizard.getMessage().contains(CAMEL_VERSION_OK_MSG));
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests Camel the last page (details) of <i>New Fuse Ignite Extension Project</i> wizard
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Set a project name</li>
	 * <li>Go to the last page</li>
	 * <li>Check wizard message - ERROR</li>
	 * <li>Check whether a user can finish the wizard (FAIL)</li>
	 * <li>Set an "Extension ID"</li>
	 * <li>Check wizard message - ERROR</li>
	 * <li>Check whether a user can finish the wizard (FAIL)</li>
	 * <li>Set an "Name"</li>
	 * <li>Check wizard message - ERROR</li>
	 * <li>Check whether a user can finish the wizard (FAIL)</li>
	 * <li>Set an invalid "Version"</li>
	 * <li>Check wizard message - ERROR</li>
	 * <li>Check whether a user can finish the wizard (FAIL)</li>
	 * <li>Set a valid "Version"</li>
	 * <li>Check whether a user can finish the wizard (SUCCESS)</li>
	 * </ol>
	 */
	@Test
	public void testDetails() {
		NewFuseIgniteExtensionProjectFirstPage firstPage = new NewFuseIgniteExtensionProjectFirstPage(wizard);
		firstPage.setTextProjectName("MyNewIgniteExt");
		wizard.next();
		wizard.next();

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		NewFuseIgniteExtensionProjectDetailsPage lastPage = new NewFuseIgniteExtensionProjectDetailsPage(wizard);
		lastPage.setTextExtensionId("my-new-ext-01");
	
		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		lastPage.setTextName("My Super Extension");

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		lastPage.setTextVersion("1.0-SNAPSHOT");

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		lastPage.setTextVersion("1.0-1");

		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		assertFalse(wizard.isFinishEnabled());

		lastPage.setTextVersion("1.0.");

		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		assertFalse(wizard.isFinishEnabled());

		lastPage.setTextVersion("1.0.0");

		assertFalse(wizard.isNextEnabled());
		assertTrue(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertNotEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}
}