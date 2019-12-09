/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.actions.ui;

import java.util.Comparator;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelVersionLabelProvider;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Widgets;
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
		ComboViewer versionComboViewer = new ComboViewer(versionCombo);
		versionComboViewer.setLabelProvider(new CamelVersionLabelProvider());
		versionComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		versionComboViewer.setComparator(new ViewerComparator(Comparator.reverseOrder()));
		versionComboViewer.setInput(CamelCatalogUtils.getAllCamelCatalogVersions().stream()
				.filter(camelVersion -> !initialCamelVersion.equals(camelVersion))
				.toArray(String[]::new));
		versionCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		versionCombo.addModifyListener(event -> {
			if (!Widgets.isDisposed(versionComboViewer)) {
				String selectedElement = (String) ((IStructuredSelection)versionComboViewer.getSelection()).getFirstElement();
				if (selectedElement != null) {
					currentSelectedVersion =  selectedElement;
				} else {
					// the Camel version has been entered manually
					currentSelectedVersion = versionComboViewer.getCombo().getText();
				}
			}		
		});
		if(CamelCatalogUtils.DEFAULT_CAMEL_VERSION.equals(initialCamelVersion)) {
			versionCombo.select(0);
		} else {
			versionComboViewer.setSelection(new StructuredSelection(CamelCatalogUtils.DEFAULT_CAMEL_VERSION));
		}
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
			setErrorMessage(NLS.bind(Messages.switchCamelVersionDialogSameVersionErrorMessage, initialCamelVersion));
		} else if(Strings.isBlank(getSelectedCamelVersion())) {
			setErrorMessage(Messages.switchCamelVersionDialogTitle);
		} else {
			setErrorMessage(null);
		}
		return isDifferentVersion && !Strings.isBlank(getSelectedCamelVersion());
	}
	
}
