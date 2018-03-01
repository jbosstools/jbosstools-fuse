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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersView2;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.FileUtils;
import org.jboss.tools.fuse.reddeer.condition.RadioButtonIsAvailable;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.FuseServerRuntimePreferencePage;
import org.jboss.tools.fuse.reddeer.preference.JBossRuntimeDetection;
import org.jboss.tools.fuse.reddeer.wizard.DownloadRuntimesWizard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Class contains test cases on a variety of server feature requests
 * 
 * @author djelinek
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class FeaturesServerTest extends DefaultTest {

	protected Logger log = Logger.getLogger(CamelEditorTest.class);

	@Before
	public void clean() {
		// clean previous runtime installations
		FileUtils.deleteDir(new File(getPath() + "target/temp_install/jboss-fuse-6.3.0.redhat-187"));
		// close all shells
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
	}

	/**
	 * <p>
	 * Fuse runtime detection never makes a server
	 * <p>
	 * <ul>
	 * <li>Download and install new runtime in JBoss Runtime Detection</li>
	 * <li>Verify if new server was created</li>
	 * <li>Open Fuse Runtime Preference Page</li>
	 * <li>Verify if new runtime was created</li>
	 * </ul>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-2176">https://issues.jboss.org/browse/FUSETOOLS-2176</a>
	 */
	@Test
	public void testFuseJbossRuntimeDetection() {
		downloadRuntime("Red Hat Fuse 6.3.0");
		assertTrue("Server was not created in view", new ServersView2().getServers().size() == 1);
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage(dialog);
		dialog.open();
		dialog.select(serverRuntime);
		assertTrue("Server runtime was not created", serverRuntime.getServerRuntimes().size() == 1);
	}

	/**
	 * Downloads/installs new runtime into temp/temp_install folder
	 * 
	 * @param name
	 *            String name of runtime
	 */
	private void downloadRuntime(String name) {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		JBossRuntimeDetection page = new JBossRuntimeDetection(dialog);
		dialog.open();
		dialog.select(page);
		DownloadRuntimesWizard runtimeWiz = page.downloadRuntime();
		runtimeWiz.selectRuntime(name);
		runtimeWiz.next();
		new PushButton("Add...").click();
		/**
		 * temporary solution ... credentials should not be in public repository -> use system parameters or something
		 * else solution
		 */
		new LabeledText("Username:").setText("aqtwrdnr");
		new LabeledText("Password:").setText("aqtwrdnr");
		new PushButton("OK").click();
		runtimeWiz.next();
		new WaitUntil(new ShellIsAvailable("Download Runtimes"));
		new WaitUntil(new RadioButtonIsAvailable("I accept the terms of the license agreement"));
		runtimeWiz.next();
		runtimeWiz.setInstallFolder(getPath() + "target/temp_install");
		runtimeWiz.setDownloadFolder(getPath() + "target/temp");
		runtimeWiz.finish(name);
		new WaitWhile(new ShellIsAvailable("Download 'Red Hat Fuse 6.3.0'"), TimePeriod.getCustom(900));
		dialog.ok();
	}

	/**
	 * Returns absolute path to the project
	 * 
	 * @return absolute path to the project
	 */
	private static String getPath() {
		File currentDirFile = new File(".");
		String temp = currentDirFile.getAbsolutePath();
		return temp.substring(0, temp.length() - 1);
	}

}
