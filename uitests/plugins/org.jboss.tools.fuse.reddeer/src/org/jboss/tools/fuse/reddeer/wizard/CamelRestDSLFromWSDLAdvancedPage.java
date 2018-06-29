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

import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.core.reference.ReferencedComposite;

/**
 * Represents the second page with Advanced options of "Camel Rest DSL from WSDL" wizard
 * 
 * @author djelinek
 *
 */
public class CamelRestDSLFromWSDLAdvancedPage extends WizardPage {

	public static final String TARGET_REST_SERVICE_ADDRESS = "Target REST Service Address";
	public static final String TARGET_SERVICE_ADDRESS = "Target Service Address";
	public static final String DESTINATION_CAMEL_FOLDER = "Destination Camel Folder";
	public static final String DESTINATION_JAVA_FOLDER = "Destination Java Folder";

	public CamelRestDSLFromWSDLAdvancedPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public LabeledText getTargetRESTServiceAddressTXT() {
		return new LabeledText(this, TARGET_REST_SERVICE_ADDRESS);
	}

	public String getTextTargetRESTServiceAddress() {
		return new LabeledText(this, TARGET_REST_SERVICE_ADDRESS).getText();
	}

	public LabeledText getTargetServiceAddressTXT() {
		return new LabeledText(this, TARGET_SERVICE_ADDRESS);
	}

	public String getTextTargetServiceAddress() {
		return new LabeledText(this, TARGET_SERVICE_ADDRESS).getText();
	}

	public LabeledText getDestinationCamelFolderTXT() {
		return new LabeledText(this, DESTINATION_CAMEL_FOLDER);
	}

	public String getTextDestinationCamelFolder() {
		return new LabeledText(this, DESTINATION_CAMEL_FOLDER).getText();
	}

	public LabeledText getDestinationJavaFolderTXT() {
		return new LabeledText(this, DESTINATION_JAVA_FOLDER);
	}

	public String getTextDestinationJavaFolder() {
		return new LabeledText(this, DESTINATION_JAVA_FOLDER).getText();
	}

	public void setTextTargetRESTServiceAddress(String str) {
		new LabeledText(this, TARGET_REST_SERVICE_ADDRESS).setText(str);
	}

	public void setTextTargetServiceAddress(String str) {
		new LabeledText(this, TARGET_SERVICE_ADDRESS).setText(str);
	}

	public void setTextDestinationCamelFolder(String str) {
		new LabeledText(this, DESTINATION_CAMEL_FOLDER).setText(str);
	}

	public void setTextDestinationJavaFolder(String str) {
		new LabeledText(this, DESTINATION_JAVA_FOLDER).setText(str);
	}
}
