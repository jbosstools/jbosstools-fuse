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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 *
 */
public class Wsdl2RestWizardPage extends WizardPage {
	
	private String wsdlURL;
	private String outputPathURL;

	public Wsdl2RestWizardPage(String pageName) {
		super(pageName);
		setMessage("Provide a URL to your WSDL and select the project and folder to put the generated artifacts into.");
	}

	public Wsdl2RestWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage("Provide a URL to your WSDL and select the project and folder to put the generated artifacts into.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
		
		// add controls
		Text urlTextControl = createLabelAndText(composite, "WSDL File URL:", 2);
		createButton(composite, "...");
		
		Text outputPathControl = createLabelAndText(composite, "Output Path:", 2);
		createButton(composite, "...");
		
		if (!Strings.isEmpty(wsdlURL)) {
			urlTextControl.setText(wsdlURL);
		}
		if (!Strings.isEmpty(outputPathURL)) {
			outputPathControl.setText(outputPathURL);
		}

		setControl(composite);
	}

	/**
	 * @param composite
	 */
	protected Text createLabelAndText(Composite composite, String labelText) {
		return createLabelAndText(composite, labelText, 3);
	}
	
	protected Text createLabelAndText(Composite composite, String labelText, int span) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		Text textControl = new Text(composite, SWT.BORDER);
		textControl.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).span(2, 1).create());
		return textControl;
	}
	
	protected Button createButton(Composite composite, String labelText) {
		Button buttonControl = new Button(composite, SWT.PUSH);
		buttonControl.setText(labelText);
		return buttonControl;
	}

	public String getWsdlURL() {
		return wsdlURL;
	}

	public void setWsdlURL(String wsdlURL) {
		this.wsdlURL = wsdlURL;
	}

	public String getOutputPathURL() {
		return outputPathURL;
	}

	public void setOutputPathURL(String outputPathURL) {
		this.outputPathURL = outputPathURL;
	}
	
}
