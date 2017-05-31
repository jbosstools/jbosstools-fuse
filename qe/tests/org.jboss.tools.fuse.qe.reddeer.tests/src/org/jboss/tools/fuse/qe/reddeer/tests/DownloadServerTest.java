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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.button.RadioButton;
import org.jboss.reddeer.swt.impl.link.DefaultLink;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.tools.fuse.qe.reddeer.ResourceHelper;
import org.jboss.tools.fuse.qe.reddeer.preference.FuseServerRuntimePreferencePage;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests a download server runtime feature
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
public class DownloadServerTest extends DefaultTest {

	private static final String ADD_BUTTON = "Add...";
	private static final String NEXT_BUTTON = "Next >";
	private static final String BACK_BUTTON = "< Back";
	private static final String CANCEL_BUTTON = "Cancel";
	private static final String FINISH_BUTTON = "Finish";
	private static final String NEW_WINDOW = "New Server Runtime Environment";
	private static final String SERVER_SECTION = "JBoss Fuse";
	private static final String SERVER_TYPE = "Apache Karaf 3.0";
	private static final String DOWNLOAD_LINK = "Download and install runtime...";
	private static final String DOWNLOAD_FOLDER = "Download folder:";
	private static final String DOWNLOAD_TITLE = "Download Runtimes";
	private static final String INSTALL_FOLDER = "Install folder:";

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
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage();
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

		new WaitUntil(new ShellWithTextIsAvailable(DOWNLOAD_TITLE));
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
