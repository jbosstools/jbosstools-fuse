/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.link.DefaultLink;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.preference.FuseServerRuntimePreferencePage;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests a download server runtime feature
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
public class DownloadServerTest extends DefaultTest {

	public static final String ADD_BUTTON = "Add...";
	public static final String NEXT_BUTTON = "Next >";
	public static final String BACK_BUTTON = "< Back";
	public static final String CANCEL_BUTTON = "Cancel";
	public static final String FINISH_BUTTON = "Finish";
	public static final String NEW_WINDOW = "New Server Runtime Environment";
	public static final String SERVER_SECTION = "Apache";
	public static final String SERVER_TYPE = "Karaf 3.0";
	public static final String DOWNLOAD_LINK = "Download and install runtime...";
	public static final String DOWNLOAD_FOLDER = "Download folder:";
	public static final String DOWNLOAD_TITLE = "Download Runtimes";
	public static final String INSTALL_FOLDER = "Install folder:";

	/**
	 * <p>
	 * Tries to download a server runtime.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>open Server Runtimes Preference page</li>
	 * <li>select Karaf 3.0.3 runtime</li>
	 * <li>check download link</li>
	 * <li>download the server runtime</li>
	 * <li>check the server runtime</li>
	 * </ol>
	 */
	@Test
	public void testDownloadServerRuntimeTest() {

		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage(dialog);
		dialog.open();
		dialog.select(serverRuntime);
		new PushButton(ADD_BUTTON).click();
		new DefaultShell(NEW_WINDOW).setFocus();
		new DefaultTreeItem(SERVER_SECTION, SERVER_TYPE).select();
		new PushButton(NEXT_BUTTON).click();

		DefaultLink downloadLink = new DefaultLink(DOWNLOAD_LINK);
		for (int i = 0; i < 10; i++) {
			if (downloadLink.isEnabled())
				break;
			AbstractWait.sleep(TimePeriod.SHORT);
		}
		new DefaultLink(DOWNLOAD_LINK).click();

		new WaitUntil(new ShellIsAvailable(DOWNLOAD_TITLE));
		new DefaultShell(DOWNLOAD_TITLE);

		PushButton buttonNext = new PushButton(NEXT_BUTTON);
		PushButton buttonBack = new PushButton(BACK_BUTTON);
		PushButton buttonCancel = new PushButton(CANCEL_BUTTON);
		PushButton buttonFinish = new PushButton(FINISH_BUTTON);

		assertTrue(buttonNext.isEnabled());
		assertFalse(buttonBack.isEnabled());
		assertFalse(buttonFinish.isEnabled());
		assertTrue(buttonCancel.isEnabled());

		buttonNext.click();

		assertFalse(buttonNext.isEnabled());
		assertTrue(buttonBack.isEnabled());
		assertFalse(buttonFinish.isEnabled());
		assertTrue(buttonCancel.isEnabled());

		new RadioButton(0).toggle(true);

		assertTrue(buttonNext.isEnabled());
		assertTrue(buttonBack.isEnabled());
		assertFalse(buttonFinish.isEnabled());
		assertTrue(buttonCancel.isEnabled());

		buttonNext.click();
		new LabeledText(DOWNLOAD_FOLDER).setText(ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "target"));
		new LabeledText(INSTALL_FOLDER).setText(ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "target"));
		buttonFinish.click();

		AbstractWait.sleep(TimePeriod.getCustom(2));
		new WaitWhile(new JobIsRunning(), TimePeriod.getCustom(3600));
		AbstractWait.sleep(TimePeriod.getCustom(2));
		new PushButton(FINISH_BUTTON).click();
		assertEquals(1, FuseServerManipulator.getServerRuntimes().size());
	}
}
