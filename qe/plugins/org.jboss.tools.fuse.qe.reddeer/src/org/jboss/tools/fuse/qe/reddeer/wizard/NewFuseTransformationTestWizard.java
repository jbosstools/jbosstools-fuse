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
package org.jboss.tools.fuse.qe.reddeer.wizard;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.jface.wizard.NewWizardDialog;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.text.LabeledText;

/**
 * Represents 'New Fuse Transformation Test Wizard'
 * 
 * @author tsedmik
 */
public class NewFuseTransformationTestWizard extends NewWizardDialog {

	private Logger log = Logger.getLogger(NewFuseTransformationTestWizard.class);

	public NewFuseTransformationTestWizard() {
		super("JBoss Fuse", "Fuse Transformation Test");
	}

	public void selectTransformationID(String name) {
		log.debug("Set 'Transformation ID' to '" + name + "'");
		new DefaultCombo().setSelection(name);
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	public void setPackage(String name) {
		log.debug("Set 'Package' to '" + name + "'");
		new LabeledText("Package:").setText(name);
		AbstractWait.sleep(TimePeriod.SHORT);
	}
}
