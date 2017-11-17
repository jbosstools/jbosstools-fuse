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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.projecttemplates.internal.Messages;

public class SwitchCamelVersionWizardPage extends WizardPage {

	private String initialCamelVersion;
	private String currentSelectedVersion;

	protected SwitchCamelVersionWizardPage(String initialCamelVersion) {
		super(Messages.switchCamelVersionDialogName);
		setTitle(Messages.switchCamelVersionDialogName);
		setDescription(Messages.switchCamelVersionDialogTitle);
		this.initialCamelVersion = initialCamelVersion;
	}

	@Override
	public void createControl(Composite container) {
		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		
		Label lbtVersion = new Label(parent, SWT.NONE);
		lbtVersion.setText(Messages.switchCamelVersionDialogVersionsLabel);

		Combo versionCombo = new Combo(parent, SWT.DROP_DOWN | SWT.RIGHT);
		versionCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		versionCombo.setItems(CamelCatalogUtils.getAllCamelCatalogVersions().stream()
				.sorted((String o1, String o2) -> o2.compareToIgnoreCase(o1))
				.filter(camelVersion -> !initialCamelVersion.equals(camelVersion))
				.toArray(String[]::new));
		versionCombo.addModifyListener(event -> currentSelectedVersion = versionCombo.getText());
		versionCombo.select(0);
		versionCombo.addModifyListener(event -> getWizard().getContainer().updateButtons());
		
		setControl(parent);
	}

	public String getSelectedCamelVersion() {
		return currentSelectedVersion;
	}
	
	@Override
	public boolean isPageComplete() {
		boolean isDifferentVersion = !initialCamelVersion.equals(getSelectedCamelVersion());
		if(!isDifferentVersion) {
			setErrorMessage(Messages.switchCamelVersionDialogSameVersionErrorMessage);
		} else if(Strings.isBlank(getSelectedCamelVersion())) {
			setErrorMessage(Messages.switchCamelVersionDialogTitle);
		} else {
			setErrorMessage(null);
		}
		return isDifferentVersion && !Strings.isBlank(getSelectedCamelVersion());
	}
	
}
