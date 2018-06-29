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

import org.eclipse.reddeer.eclipse.selectionwizard.NewMenuWizard;

/**
 * Represents "Camel Rest DSL from WSDL" wizard
 * 
 * @author djelinek
 * 
 */
public class CamelRestDSLFromWSDLWizard extends NewMenuWizard {

	public static final String SHELL_NAME = "Camel Rest DSL from WSDL";
	public static final String WIZARD_CATEGORY = "Red Hat Fuse";
	public static final String WIZARD_NAME = "Camel Rest DSL from WSDL";

	public CamelRestDSLFromWSDLWizard() {
		super(SHELL_NAME, WIZARD_CATEGORY, WIZARD_NAME);
	}

}