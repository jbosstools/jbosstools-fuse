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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Ignore;
import org.junit.Test;

public class SwitchCamelVersionWizardIT {

	@Test
	public void testSwitchValidateExistingVersion() throws Exception {
		testSwitchFromExistingSelectByDefault("2.19.3", CamelCatalogUtils.DEFAULT_CAMEL_VERSION);
	}
	
	@Test
	@Ignore("The automated test cannot work for now because the Camel version is not looking for early access repository, neither the configured repositories in the pom of the project."
			+ "Working locally if you already have the artefact locally or configured your local settings.xml. see FUSETOOLS-2865")
	public void testSwitchValidateExistingVersionWhenAlreadyDefaultVersionChooseLatestVersionByDefault() throws Exception {
		testSwitchFromExistingSelectByDefault(CamelCatalogUtils.DEFAULT_CAMEL_VERSION, getLatestCamelVersion());
	}

	private void testSwitchFromExistingSelectByDefault(String existingVersion, String expectedDefaultVersion) {
		SwitchCamelVersionWizard switchCamelVersionWizard = new SwitchCamelVersionWizard(existingVersion);
		WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(), switchCamelVersionWizard);
		wizardDialog.setBlockOnOpen(false);
		wizardDialog.open();
		assertThat(switchCamelVersionWizard.performFinish()).isTrue();
		assertThat(switchCamelVersionWizard.getSelectedCamelVersion()).isEqualTo(expectedDefaultVersion);
	}

	private String getLatestCamelVersion() {
		List<String> allCamelCatalogVersions = new ArrayList<>(CamelCatalogUtils.getAllCamelCatalogVersions());
		allCamelCatalogVersions.sort(Comparator.reverseOrder());
		return allCamelCatalogVersions.get(0);
	}
	
}
