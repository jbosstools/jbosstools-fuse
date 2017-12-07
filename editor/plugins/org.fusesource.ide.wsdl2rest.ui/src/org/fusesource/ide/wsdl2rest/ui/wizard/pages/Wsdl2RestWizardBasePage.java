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
package org.fusesource.ide.wsdl2rest.ui.wizard.pages;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.wsdl2rest.ui.wizard.Wsdl2RestOptions;
import org.fusesource.ide.wsdl2rest.ui.wizard.Wsdl2RestWizard;

/**
 * @author brianf
 *
 */
public abstract class Wsdl2RestWizardBasePage extends WizardPage {

	protected final DataBindingContext dbc = new DataBindingContext(DisplayRealm.getRealm(Display.getCurrent()));

    protected Wsdl2RestWizardBasePage(String pageName) {
		this(pageName, null, null);
	}

    protected Wsdl2RestWizardBasePage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	protected Text createLabelAndText(Composite composite, String labelText) {
		return createLabelAndText(composite, labelText, 3);
	}

	protected Text createLabelAndText(Composite composite, String labelText, int span) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		Text textControl = new Text(composite, SWT.BORDER);
		textControl.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).span(span, 1).create());
		return textControl;
	}

	protected Button createButton(Composite composite, String labelText) {
		Button buttonControl = new Button(composite, SWT.PUSH);
		buttonControl.setText(labelText);
		return buttonControl;
	}

	protected Wsdl2RestOptions getOptionsFromWizard() {
		return ((Wsdl2RestWizard)getWizard()).getOptions();
	}
}
