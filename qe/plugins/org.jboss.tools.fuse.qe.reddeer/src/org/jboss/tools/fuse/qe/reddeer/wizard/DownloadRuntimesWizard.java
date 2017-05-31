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
package org.jboss.tools.fuse.qe.reddeer.wizard;

import java.util.ArrayList;
import java.util.List;

import org.jboss.reddeer.jface.wizard.WizardDialog;
import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.button.RadioButton;
import org.jboss.reddeer.swt.impl.link.DefaultLink;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.swt.impl.table.DefaultTableItem;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;

/**
 * Represents <i>Download Runtimes</i> dialog that is displayed after click on <i>Download...</i> button in <i>JBoss
 * Runtime Detection</i> preference page.
 * 
 * @author tsedmik
 */
public class DownloadRuntimesWizard extends WizardDialog {

	/**
	 * Accesses names of all available runtimes.
	 * 
	 * @return List with names
	 */
	public List<String> getAllRuntimes() {

		List<String> result = new ArrayList<String>();
		for (TableItem item : new DefaultTable().getItems()) {
			result.add(item.getText());
		}
		return result;
	}

	/**
	 * Select a given runtime
	 * 
	 * @param runtime
	 *            name of the runtime
	 */
	public void selectRuntime(String runtime) {

		new DefaultTableItem(runtime).select();
	}

	/**
	 * Gets project URL (if available)
	 * 
	 * @param runtime
	 *            name of the runtime
	 * @return project URL
	 */
	public String getProjectURL(String runtime) {

		selectRuntime(runtime);
		return new DefaultLink(0).getText();
	}

	/**
	 * Gets download URL (if available)
	 * 
	 * @param runtime
	 *            name of the runtime
	 * @return download URL
	 */
	public String getDownloadURL(String runtime) {

		selectRuntime(runtime);
		return new DefaultLink(1).getText();
	}

	public void acceptTerms() {

		// for those runtimes that need credentials to jboss.org
		try {
			new DefaultText(0).setText("1234sgf");
			new DefaultText(1).setText("1234sgf");
			next();
		} catch (Exception e) {
		}
		new RadioButton(0).toggle(true);
	}

	public void setInstallFolder(String path) {

		new LabeledText("Install folder:").setText(path);
	}

	public void setDownloadFolder(String path) {

		new LabeledText("Download folder:").setText(path);
	}

	public void finish(String runtime) {

		new PushButton("Finish").click();
		new WaitUntil(new ShellWithTextIsAvailable("Download '" + runtime), TimePeriod.getCustom(5));
		new WaitWhile(new ShellWithTextIsAvailable("Download '" + runtime), TimePeriod.getCustom(900));
		new DefaultShell("Preferences");
	}
}
