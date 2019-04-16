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
package org.jboss.tools.fuse.reddeer.preference;

import java.util.List;

import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.preference.PreferencePage;
import org.eclipse.reddeer.swt.api.TableItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.button.NextButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.list.DefaultList;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * 
 * @author apodhrad
 *
 */
public class InstalledJREs extends PreferencePage {
	
	public InstalledJREs(ReferencedComposite ref) {
		super(ref, "Java", "Installed JREs");
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
		new WaitWhile(new ShellIsAvailable("Add JRE"));
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
			if (jreItem.getText().replace(" (default)", "").matches(jreName)) {
				return true;
			}
		}
		return false;
	}
}
