/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.actions.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Test;

public class SwitchCamelVersionWizardIT {

	@Test
	public void testSwitchValidateExistingVersion() throws Exception {
		SwitchCamelVersionWizard switchCamelVersionWizard = new SwitchCamelVersionWizard("2.19.3");
		WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(), switchCamelVersionWizard);
		wizardDialog.setBlockOnOpen(false);
		wizardDialog.open();
		assertThat(switchCamelVersionWizard.performFinish()).isTrue();
		assertThat(switchCamelVersionWizard.getSelectedCamelVersion()).isEqualTo(CamelCatalogUtils.getLatestCamelVersion());
	}
	
}
