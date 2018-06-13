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

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.jface.dialogs.MessageTypeEnum;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.condition.ControlIsEnabled;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectSecondPage;
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

	public static final String CAMEL_VERSION_ERROR_MSG = "seems to be unavailable";
	public static final String CAMEL_VERSION_OK_MSG = "The specified Apache Camel version is valid.";

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
		assertTrue(wizard.isBackEnabled());
		assertNotEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Tests the second page of <i>New Fuse Ignite Extension Project</i> wizard
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Set a project name</li>
	 * <li>Try to delete Ignite version and check whether user can finish the wizard (UNACCESSIBLE)</li>
	 * <li>Try to set the latest versions</li>
	 * <li>Check whether a user can finish the wizard</li>
	 * </ol>
	 */
	@Test
	public void testDependencies() {
		NewFuseIgniteExtensionProjectFirstPage firstPage = new NewFuseIgniteExtensionProjectFirstPage(wizard);
		firstPage.setTextProjectName("MyNewIgniteExt");
		wizard.next();

		new WaitUntil(new ControlIsEnabled(new FinishButton(wizard)), TimePeriod.MEDIUM, false);
		assertFalse(wizard.isNextEnabled());
		assertTrue(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertNotEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		NewFuseIgniteExtensionProjectSecondPage secondPage = new NewFuseIgniteExtensionProjectSecondPage(wizard);
		secondPage.setTextFuseIgniteVersion("");

		assertFalse(wizard.isNextEnabled());
		assertFalse(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertEquals(MessageTypeEnum.ERROR, wizard.getMessageType());

		secondPage.setTextFuseIgniteVersion(secondPage.getItemsFuseIgniteVersion().get(0));

		assertFalse(wizard.isNextEnabled());
		assertTrue(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertNotEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Tests <i>Version</i> of <i>New Fuse Ignite Extension Project</i>
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Set a project name</li>
	 * <li>Go to the last page</li>
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

		assertFalse(wizard.isNextEnabled());
		assertTrue(wizard.isBackEnabled());

		NewFuseIgniteExtensionProjectSecondPage lastPage = new NewFuseIgniteExtensionProjectSecondPage(wizard);
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
		new WaitUntil(new ControlIsEnabled(new FinishButton(wizard)), TimePeriod.MEDIUM, false);

		assertFalse(wizard.isNextEnabled());
		assertTrue(wizard.isFinishEnabled());
		assertTrue(wizard.isBackEnabled());
		assertNotEquals(MessageTypeEnum.ERROR, wizard.getMessageType());
		LogChecker.assertNoFuseError();
	}
}