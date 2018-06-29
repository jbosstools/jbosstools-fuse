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

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Represents the first page of "Camel Rest DSL from WSDL" wizard
 * 
 * @author djelinek
 *
 */
public class CamelRestDSLFromWSDLFirstPage extends WizardPage {

	public static final String DESTINATION_PROJECT = "Destination Project";
	public static final String WSDL_FILE = "WSDL File";

	public CamelRestDSLFromWSDLFirstPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public LabeledText getDestinationProjectTXT() {
		return new LabeledText(this, DESTINATION_PROJECT);
	}

	public String getTextDestinationProject() {
		return new LabeledText(this, DESTINATION_PROJECT).getText();
	}

	public LabeledText getWSDLFileTXT() {
		return new LabeledText(this, WSDL_FILE);
	}

	public String getTextWSDLFile() {
		return new LabeledText(this, WSDL_FILE).getText();
	}

	public void setTextDestinationProject(String str) {
		new LabeledText(this, DESTINATION_PROJECT).setText(str);
	}

	public void setTextWSDLFile(String str) {
		new LabeledText(this, WSDL_FILE).setText(str);
	}
}
