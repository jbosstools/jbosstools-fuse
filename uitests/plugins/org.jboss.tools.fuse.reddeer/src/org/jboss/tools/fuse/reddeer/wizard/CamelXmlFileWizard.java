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
package org.jboss.tools.fuse.reddeer.wizard;

import org.eclipse.reddeer.eclipse.selectionwizard.NewMenuWizard;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * 
 * @author apodhrad
 * 
 */
public class CamelXmlFileWizard extends NewMenuWizard {

	public CamelXmlFileWizard() {
		super("New Camel Context XML File", "Red Hat Fuse", "Camel XML File");
	}

	public CamelXmlFileWizard openWizard() {
		super.open();
		return this;
	}

	public CamelXmlFileWizard setName(String name) {
		new LabeledText("File name:").setText(name);
		return this;
	}

}
