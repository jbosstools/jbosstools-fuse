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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.core.matcher.WithTextMatcher;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tab.DefaultTabItem;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests validity of the license agreement used with JBoss Fuse Tooling
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class LicenseTest {

	private static final String LICENSE = "Red Hat, Inc. licenses these features and plugins to you under certain open "
			+ "source licenses (or aggregations of such licenses), which in a particular case may include the Eclipse "
			+ "Public License, the GNU Lesser General Public License, and/or certain other open source licenses. For "
			+ "precise licensing details, consult the corresponding source code, or contact Red Hat, Attn: General "
			+ "Counsel, 100 East Davie St., Raleigh NC 27601 USA.";

	private String plugin;

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Fuse plugins
	 */
	@Parameters
	public static Collection<String> setupData() {
		new ShellMenuItem(new WorkbenchShell(), "Help", "About Red Hat JBoss Developer Studio").select();
		Shell shell = new DefaultShell("About Red Hat JBoss Developer Studio");
		new PushButton(shell, "Installation Details").click();
		shell = new DefaultShell("Red Hat JBoss Developer Studio Installation Details");
		new DefaultTabItem(shell, "Installed Software").activate();
		List<String> fusePlugins = new ArrayList<String>();
		for (TreeItem item : new DefaultTree(shell).getItems()) {
			if (item.getText().startsWith("Red Hat JBoss Fuse Tools")) {
				fusePlugins.add(item.getText());
			}
		}
		new PushButton(shell, "Close").click();
		shell = new DefaultShell("About Red Hat JBoss Developer Studio");
		new PushButton(shell, "Close").click();
		return fusePlugins;
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param JBoss
	 *            Fuse plugin
	 */
	public LicenseTest(String plugin) {
		this.plugin = plugin;
	}

	@Before
	public void setupOpenInstallationDetails() {

		new ShellMenuItem(new WorkbenchShell(), "Help", "About Red Hat JBoss Developer Studio").select();
		Shell shell = new DefaultShell("About Red Hat JBoss Developer Studio");
		new PushButton(shell, "Installation Details").click();
		shell = new DefaultShell("Red Hat JBoss Developer Studio Installation Details");
	}

	@After
	public void setupCloseShells() {
		Shell shell = new DefaultShell(new WithTextMatcher(new RegexMatcher("Properties for.*")));
		new PushButton(shell, "Apply and Close").click();
		shell = new DefaultShell("Red Hat JBoss Developer Studio Installation Details");
		new PushButton(shell, "Close").click();
		shell = new DefaultShell("About Red Hat JBoss Developer Studio");
		new PushButton(shell, "Close").click();
	}

	/**
	 * <p>
	 * Tests validity of the license agreement used with JBoss Fuse Tooling
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open 'Installation Details'</li>
	 * <li>open Properties of all installed Fuse plugins</li>
	 * <li>check validity of the License Agreement</li>
	 * </ol>
	 */
	@Test
	public void testLicense() {

		new DefaultTreeItem(plugin).select();
		new PushButton("Properties").click();
		new DefaultShell(new WithTextMatcher(new RegexMatcher("Properties for.*")));
		new DefaultTreeItem("License Agreement").select();
		String text = new DefaultText(1).getText();
		assertEquals(LICENSE, text);
	}
}
