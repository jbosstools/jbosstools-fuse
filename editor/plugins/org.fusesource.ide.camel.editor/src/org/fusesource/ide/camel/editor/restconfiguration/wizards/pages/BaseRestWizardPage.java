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
package org.fusesource.ide.camel.editor.restconfiguration.wizards.pages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author brianf
 *
 */
public abstract class BaseRestWizardPage extends WizardPage {

	protected BaseRestWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * Creates a label control beside a Text control with a default column span.
	 * @param composite
	 * @param labelText
	 * @return
	 */
	protected Text createLabelAndText(Composite composite, String labelText) {
		return createLabelAndText(composite, labelText, 3);
	}

	/**
	 * Creates a label control beside a Text control with a specific column span.
	 * @param composite
	 * @param labelText
	 * @param span
	 * @return
	 */
	protected Text createLabelAndText(Composite composite, String labelText, int span) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		Text textControl = new Text(composite, SWT.BORDER);
		textControl.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(10, 0).grab(true, false).span(span, 1).create());
		return textControl;
	}

	/**
	 * Creates a button control.
	 * @param composite
	 * @param labelText
	 * @return
	 */
	protected Button createButton(Composite composite, String labelText) {
		Button buttonControl = new Button(composite, SWT.PUSH);
		buttonControl.setText(labelText);
		return buttonControl;
	}

	/**
	 * Creates a label control beside a Combo control with a specific column span.
	 * @param composite
	 * @param labelText
	 * @param span
	 * @return
	 */
	protected Combo createComboAndText(Composite composite, String labelText, int span) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		Combo comboControl = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		comboControl.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(10, 0).grab(true, false).span(span, 1).create());
		return comboControl;
	}

}
