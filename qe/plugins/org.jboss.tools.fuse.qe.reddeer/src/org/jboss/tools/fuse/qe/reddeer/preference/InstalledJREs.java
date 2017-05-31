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
package org.jboss.tools.fuse.qe.reddeer.preference;

import java.util.List;

import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.jface.preference.PreferencePage;
import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.swt.impl.button.FinishButton;
import org.jboss.reddeer.swt.impl.button.NextButton;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.list.DefaultList;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.swt.impl.text.LabeledText;

/**
 * 
 * @author apodhrad
 *
 */
public class InstalledJREs extends PreferencePage {

	public InstalledJREs() {
		super("Java", "Installed JREs");
	}

	/**
	 * Adds new jre with a given name and path. If the jre with such name already exists, nothing is added.
	 * 
	 * @param jrePath
	 *            Jre path
	 * @param jreName
	 *            Jre name
	 */
	public void addJre(String jrePath, String jreName) {
		if (containsJreWithName(jreName)) {
			return;
		}

		new PushButton("Add...").click();
		new DefaultShell("Add JRE");
		new DefaultList("Installed JRE Types:").select("Standard VM");
		new NextButton().click();
		new LabeledText("JRE home:").setText(jrePath);
		new LabeledText("JRE name:").setText(jreName);
		new FinishButton().click();
		new WaitWhile(new ShellWithTextIsAvailable("Add JRE"));
	}

	/**
	 * Returns whether the jre with a given name already exists.
	 * 
	 * @param jreName
	 *            Jre name
	 * @return whether the jre with a gicen name already exists
	 */
	public boolean containsJreWithName(String jreName) {
		List<TableItem> jreItems = new DefaultTable().getItems();
		for (TableItem jreItem : jreItems) {
			if (jreItem.getText().replace(" (default)", "").equals(jreName)) {
				return true;
			}
		}
		return false;
	}
}
