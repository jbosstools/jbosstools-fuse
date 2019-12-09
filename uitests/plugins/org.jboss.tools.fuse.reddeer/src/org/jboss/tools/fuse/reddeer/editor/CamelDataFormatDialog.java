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
package org.jboss.tools.fuse.reddeer.editor;

import java.util.List;

import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Dialog (wizard) for creating a new Camel Data Format
 * 
 * @author djelinek
 */
public class CamelDataFormatDialog extends WizardDialog {

	public static final String TITLE = "Create a new Data Format...";
	public static final String DATAFORMAT = "Data Format:";
	public static final String ID = "Id: *";

	public CamelDataFormatDialog() {
		super(TITLE);
	}

	public void activate() {
		setShell(new DefaultShell(TITLE));
	}

	public LabeledText getId() {
		return new LabeledText(this, ID);
	}

	public String getIdText() {
		return new LabeledText(this, ID).getText();
	}

	public void setIdText(String id) {
		new LabeledText(this, ID).setText(id);
	}

	public LabeledCombo getDataFormat() {
		return new LabeledCombo(this, DATAFORMAT);
	}

	public void setDataFormat(String title) {
		for (String dataformat : getDataFormats()) {
			if (dataformat.startsWith(title)) {
				new LabeledCombo(this, DATAFORMAT).setSelection(dataformat);
			}
		}
	}

	public List<String> getDataFormats() {
		return new LabeledCombo(this, DATAFORMAT).getItems();
	}

}
