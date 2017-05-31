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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tab.DefaultTabItem;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
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
		new ShellMenu("Help", "Installation Details").select();
		new DefaultShell("Red Hat JBoss Developer Studio Installation Details");
		new DefaultTabItem("Installed Software").activate();
		List<String> fusePlugins = new ArrayList<String>();
		for (TreeItem item : new DefaultTree().getItems()) {
			if (item.getText().startsWith("JBoss Fuse Tooling")) {
				fusePlugins.add(item.getText());
			}
		}
		new PushButton("Close").click();
		;
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

		new ShellMenu("Help", "Installation Details").select();
		new DefaultShell("Red Hat JBoss Developer Studio Installation Details");
	}

	@After
	public void setupCloseShells() {

		new PushButton("OK").click();
		new DefaultShell("Red Hat JBoss Developer Studio Installation Details");
		new PushButton("Close").click();
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
		AbstractWait.sleep(TimePeriod.SHORT);
		new DefaultTreeItem("License Agreement").select();
		String text = new DefaultText(1).getText();
		assertEquals(LICENSE, text);
	}
}
