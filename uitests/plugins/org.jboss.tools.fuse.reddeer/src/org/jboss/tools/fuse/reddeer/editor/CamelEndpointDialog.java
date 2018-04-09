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
package org.jboss.tools.fuse.reddeer.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.api.Button;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * Dialog (wizard) for creating a new Camel Endpoint
 * 
 * @author djelinek
 */
public class CamelEndpointDialog extends WizardDialog {

	public static final String TITLE = "Choose Global Camel endpoint";
	public static final String ID = "Id *";
	public static final String SELECTION = "Camel component selection";

	public CamelEndpointDialog() {
		super(TITLE);
	}

	public void activate() {
		setShell(new DefaultShell(TITLE));
	}

	public void setCamelComponent(String... path) {
		new DefaultTreeItem(path).select();
	}

	public LabeledText getId() {
		return new LabeledText(this, ID);
	}

	public String getIdText() {
		return new LabeledText(this, ID).getText();
	}

	public void setId(String id) {
		new LabeledText(this, ID).setText(id);
	}

	public LabeledText getFilter() {
		return new LabeledText(new DefaultGroup(this, SELECTION), ID);
	}

	public String getFilterText() {
		return new LabeledText(new DefaultGroup(this, SELECTION), ID).getText();
	}

	public void setFilter(String filter) {
		new LabeledText(new DefaultGroup(this, SELECTION), ID).setText(filter);
	}

	public Button getShowOnlyPaletteComponents() {
		return new CheckBox(new DefaultGroup(this, SELECTION), "Show only palette components");
	}

	public void setShowOnlyPaletteComponents() {
		getShowOnlyPaletteComponents().click();
	}

	public Button getGroupedByCategories() {
		return new CheckBox(new DefaultGroup(this, SELECTION), "Grouped by categories");
	}

	public void setGroupedByCategories() {
		getGroupedByCategories().click();
	}

	public List<String> getEndpoints() {
		List<String> endpoints = new ArrayList<>();
		for (TreeItem item : new DefaultTree(this).getAllItems()) {
			endpoints.add(item.getText());
		}
		return endpoints;
	}

}
