/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.wizard;

import java.util.List;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;

/**
 * Represents the first page of "Import from Folder or Archive" wizard
 * 
 * @author tsedmik
 */
public class ImportFromArchiveWizardFirstPage extends WizardPage {

	public ImportFromArchiveWizardFirstPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public LabeledCombo getImportSourceCMB() {
		return new LabeledCombo(this, "Import source:");
	}

	public String getTextImportSource() {
		return new LabeledCombo(this, "Import source:").getText();
	}

	public String getSelectionImportSource() {
		return new LabeledCombo(this, "Import source:").getSelection();
	}

	public List<String> getItemsImportSource() {
		return new LabeledCombo(this, "Import source:").getItems();
	}
}
