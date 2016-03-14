/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.editor.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.camel.editor.wizards.NewDataFormatWizard;

/**
 * @author lhein
 *
 */
public class DataFormatPropertiesPage extends WizardPage {

	private NewDataFormatWizard wizard;
	
	/**
	 * @param pageName
	 */
	public DataFormatPropertiesPage(NewDataFormatWizard wizard, String pageName) {
		super(pageName);
		this.wizard = wizard;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
	}
	
	public boolean isValid() {
		return false;
	}
}
