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
import static org.jboss.tools.fuse.qe.reddeer.ProjectTemplate.CBR;
import static org.jboss.tools.fuse.qe.reddeer.ProjectType.BLUEPRINT;
import static org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630254;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Fuse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.condition.FuseLogContainsText;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.preference.FuseServerRuntimePreferencePage;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Class contains test cases verifying resolved issues
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@Fuse(server = @Server(type = Fuse, state = PRESENT))
public class RegressionFuseTest extends DefaultTest {

	@InjectRequirement
	private FuseRequirement serverRequirement;

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupClean() {

		String server = serverRequirement.getConfig().getName();
		if (FuseServerManipulator.isServerStarted(server)) {
			FuseServerManipulator.stopServer(server);
		}
		new ProjectExplorer().deleteAllProjects();
	}

	/**
	 * <p>
	 * New Server Runtime Wizard - Cancel/Finish button error
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1067">https://issues.jboss.org/browse/FUSETOOLS-1067</a>
	 */
	@Test
	public void issue_1067() {

		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage();
		dialog.open();
		dialog.select(serverRuntime);

		new PushButton("Add...").click();
		new DefaultShell("New Server Runtime Environment").setFocus();
		new DefaultTreeItem("JBoss Fuse").expand();

		// tests the _Finish_ button
		for (TreeItem item : new DefaultTreeItem("JBoss Fuse").getItems()) {
			if (!item.getText().startsWith("JBoss"))
				continue;
			AbstractWait.sleep(TimePeriod.SHORT);
			item.select();
			try {

				assertFalse(new PushButton("Finish").isEnabled());
			} catch (AssertionError ex) {

				new DefaultTreeItem("JBoss Fuse").select();
				AbstractWait.sleep(TimePeriod.SHORT);
				new PushButton("Cancel").click();
				AbstractWait.sleep(TimePeriod.NORMAL);
				new DefaultShell().close();
				throw ex;
			}
		}

		// tests the _Cancel_ button
		AbstractWait.sleep(TimePeriod.SHORT);
		new DefaultTreeItem("JBoss Fuse", "JBoss Fuse " + serverRequirement.getConfig().getServerBase().getVersion())
				.select();
		AbstractWait.sleep(TimePeriod.SHORT);
		new PushButton("Cancel").click();
		AbstractWait.sleep(TimePeriod.SHORT);
		try {
			assertTrue(new DefaultShell().getText().equals("Preferences"));
		} catch (AssertionError ex) {

			new DefaultShell().close();
			new DefaultTreeItem("JBoss Fuse").select();
			AbstractWait.sleep(TimePeriod.SHORT);
			new PushButton("Cancel").click();
			AbstractWait.sleep(TimePeriod.NORMAL);
			new DefaultShell().close();
			throw ex;
		}
	}

	/**
	 * <p>
	 * uninstall of bundles from servers broken
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1152">https://issues.jboss.org/browse/FUSETOOLS-1152</a>
	 */
	@Test
	public void issue_1152() {

		ProjectFactory.newProject("cbr-blueprint").version(CAMEL_2_17_0_REDHAT_630254).template(CBR).type(BLUEPRINT).create();
		String server = serverRequirement.getConfig().getName();
		FuseServerManipulator.startServer(server);
		FuseServerManipulator.addModule(server, "cbr-blueprint");
		AbstractWait.sleep(TimePeriod.NORMAL);
		FuseServerManipulator.removeAllModules(server);
		new WaitUntil(new FuseLogContainsText("(CamelContext: cbr-example-context) is shutdown"), TimePeriod.VERY_LONG);
	}

	/**
	 * <p>
	 * Problem occurred during restart JBoss Fuse
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1252">https://issues.jboss.org/browse/FUSETOOLS-1252</a>
	 */
	@Test
	public void issue_1252() {

		ProjectFactory.newProject("camel-blueprint").template(ProjectTemplate.CBR).type(ProjectType.BLUEPRINT).create();
		String server = serverRequirement.getConfig().getName();
		FuseServerManipulator.addModule(server, "camel-blueprint");
		FuseServerManipulator.startServer(server);
		FuseServerManipulator.restartInDebug(server);
		try {
			new WaitUntil(new ShellWithTextIsAvailable("Problem Occurred"));
			new DefaultShell("Problem Occurred");
			new PushButton("OK");
		} catch (Exception e) {
			// OK no shell "Problem Occurred" was found
			return;
		}
		fail();
	}
}
