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
package org.fusesource.ide.projecttemplates.wizards.pages;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.projecttemplates.internal.Messages;

public class WhereToFindMoreTemplatesMessageDialog extends MessageDialog {
	public WhereToFindMoreTemplatesMessageDialog(Shell parentShell) {
		super(parentShell,
				Messages.newProjectWizardTemplatePageWhereToFindMoreExamples,
				null,
				Messages.newProjectWizardTemplatePageListOfOtherExamplesReposMessage,
				MessageDialog.INFORMATION,
				0,
				IDialogConstants.OK_LABEL);
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		StyledText styledtext = new StyledText(parent, SWT.READ_ONLY);
		Label label = new Label(parent, SWT.NONE);
		styledtext.setCaret(null);
		styledtext.setEditable(false);
		styledtext.setBackground(label.getBackground());
		styledtext.setText(Messages.newProjectWizardTemplatePageListOfOtherExamplesRepos);
		styledtext.setLayoutData(GridDataFactory.fillDefaults().indent(100, SWT.DEFAULT).create());
		return styledtext;
	}
}