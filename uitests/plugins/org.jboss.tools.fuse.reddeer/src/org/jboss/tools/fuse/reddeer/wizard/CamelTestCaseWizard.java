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

/**
 * Wizard for creating a Camel Test Case.
 * 
 * @author tsedmik
 */
public class CamelTestCaseWizard extends NewMenuWizard {

	public CamelTestCaseWizard() {
		super("New Camel JUnit Test Case", "Red Hat Fuse", "Camel Test Case");
	}

}
