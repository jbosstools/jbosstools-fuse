/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.link.DefaultLink;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.condition.ButtonIsAvailable;
import org.jboss.tools.fuse.reddeer.condition.RadioButtonIsAvailable;
import org.jboss.tools.fuse.reddeer.preference.FuseServerRuntimePreferencePage;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * 
 * @author tsedmik, fpospisi
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class DownloadServerTest extends DefaultTest {

	public static final String OK_BUTTON = "OK";
	public static final String ADD_BUTTON = "Add...";
	public static final String NEXT_BUTTON = "Next >";
	public static final String BACK_BUTTON = "< Back";
	public static final String CANCEL_BUTTON = "Cancel";
	public static final String FINISH_BUTTON = "Finish";
	public static final String REMOVE_BUTTON = "Remove";
	public static final String NEW_WINDOW = "New Server Runtime Environment";
	public static final String DOWNLOAD_LINK = "Download and install runtime...";
	public static final String DOWNLOAD_FOLDER = "Download folder:";
	public static final String DOWNLOAD_TITLE = "Download Runtimes";
	public static final String INSTALL_FOLDER = "Install folder:";
	public static final String ACCEPT_TERMS = "I accept the terms of the license agreement";

	public static final String USERNAME = System.getenv("JBOSS_USERNAME");
	public static final String PASSWORD = System.getenv("JBOSS_PASSWORD");

	@Parameter
	public String parSection;

	@Parameter(1)
	public String parType;

	@Parameter(2)
	public boolean parLoginRequired;

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// Karaf
			{ "Apache", "Karaf 4.1+", false },
			// Fuse on Karaf
			{ "Red Hat JBoss Middleware", "Red Hat Fuse 7+", true },
			{ "JBoss Community", "JBoss 6.x Runtime", false },
			// Fuse on EAP
			{ "Red Hat JBoss Middleware", "Red Hat JBoss Enterprise Application Platform 7.3 Runtime", true }, });
	}

	/*
	 * Remove downloaded Runtime.
	 */
	@After
	public void cleanWorkspace() {
		FuseServerManipulator.deleteAllServerRuntimes();
	}

	/**
	 * <p>
	 * Tries to download a server runtime.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>open Server Runtimes Preference page</li>
	 * <li>select server from parameter</li>
	 * <li>check download link</li>
	 * <li>download the server runtime</li>
	 * <li>check the server runtime</li>
	 * </ol>
	 */
	@Test
	public void testDownloadServerRuntimeTest() {

		/*
		 * Open Server Runtime Environments.
		 */
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage(dialog);
		dialog.open();
		dialog.select(serverRuntime);

		/*
		 * Add new Server Runtime Environment.
		 */
		new PushButton(ADD_BUTTON).click();
		new DefaultShell(NEW_WINDOW).setFocus();
		new DefaultTreeItem(parSection, parType).select();
		new PushButton(NEXT_BUTTON).click();

		/*
		 * Open list of available versions.
		 */
		DefaultLink downloadLink = new DefaultLink(DOWNLOAD_LINK);
		for (int i = 0; i < 30; i++) {
			if (downloadLink.isEnabled())
				break;
			AbstractWait.sleep(TimePeriod.SHORT);
		}
		new DefaultLink(DOWNLOAD_LINK).click();
		new WaitUntil(new ShellIsAvailable(DOWNLOAD_TITLE));
		new DefaultShell(DOWNLOAD_TITLE);

		/*
		 * Select latest available.
		 */
		DefaultTable selection = new DefaultTable();
		selection.select((selection.rowCount() - 1));
		PushButton buttonNext = new PushButton(NEXT_BUTTON);
		assertTrue(buttonNext.isEnabled());
		buttonNext.click();

		/*
		 * Handle login, if necessary.
		 */
		if (parLoginRequired) {
			new PushButton(ADD_BUTTON).click();
			new LabeledText("Username:").setText(USERNAME);
			new LabeledText("Password:").setText(PASSWORD);
			
			new WaitUntil(new ButtonIsAvailable(OK_BUTTON));
			PushButton buttonOk = new PushButton(OK_BUTTON);
			
			/*
			 * If login is already in list.
			 */
			if (buttonOk.isEnabled()) {
				buttonOk.click();
			} else {
				new PushButton(CANCEL_BUTTON).click();
			}

			AbstractWait.sleep(TimePeriod.getCustom(2));
			new WaitUntil(new ButtonIsAvailable(NEXT_BUTTON));
			assertTrue(buttonNext.isEnabled());
			buttonNext.click();

			new WaitUntil(new RadioButtonIsAvailable(ACCEPT_TERMS));
		}

		/*
		 * Accept licence agreement.
		 */
		new RadioButton(0).toggle(true);
		assertTrue(buttonNext.isEnabled());
		buttonNext.click();

		/*
		 * Handle download and check runtime.
		 */
		new LabeledText(DOWNLOAD_FOLDER).setText(ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "target"));
		new LabeledText(INSTALL_FOLDER).setText(ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "target"));
		new PushButton(FINISH_BUTTON).click();

		AbstractWait.sleep(TimePeriod.getCustom(2));
		new WaitWhile(new JobIsRunning(), TimePeriod.getCustom(3600));
		AbstractWait.sleep(TimePeriod.getCustom(2));
		new WaitUntil(new ButtonIsAvailable(FINISH_BUTTON));
		new PushButton(FINISH_BUTTON).click();
		assertEquals(1, FuseServerManipulator.getServerRuntimes().size());
	}
}
