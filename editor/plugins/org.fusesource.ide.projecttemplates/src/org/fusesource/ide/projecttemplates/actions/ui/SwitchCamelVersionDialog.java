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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.projecttemplates.internal.Messages;

/**
 * @author lheinema
 *
 */
public class SwitchCamelVersionDialog extends TitleAreaDialog {
	
	private String selectedCamelVersion;
	private Combo versionCombo;
	
	/**
	 * @param parentShell
	 */
	public SwitchCamelVersionDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		super.create();
		setTitle(Messages.switchCamelVersionDialogName);
        setMessage(Messages.switchCamelVersionDialogTitle, IMessageProvider.INFORMATION);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createVersionCombo(container);

        return area;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	@Override
    protected boolean isResizable() {
        return true;
    }
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    private void createVersionCombo(Composite container) {
        Label lbtVersion = new Label(container, SWT.NONE);
        lbtVersion.setText(Messages.switchCamelVersionDialogVersionsLabel);

        GridData dataVersion = new GridData();
        dataVersion.grabExcessHorizontalSpace = true;
        dataVersion.horizontalAlignment = GridData.FILL;

        versionCombo = new Combo(container, SWT.DROP_DOWN | SWT.RIGHT);
        versionCombo.setLayoutData(dataVersion);
        versionCombo.setItems(CamelCatalogUtils.getAllCamelCatalogVersions().stream().sorted( (String o1, String o2) -> o2.compareToIgnoreCase(o1)).toArray(String[]::new));
        if (!Strings.isBlank(selectedCamelVersion)) {
        	versionCombo.setText(selectedCamelVersion);
        }
    }
	
    private void saveInput() {
        this.selectedCamelVersion =this.versionCombo.getText();
    }

	/**
	 * @return the selectedCamelVersion
	 */
	public String getSelectedCamelVersion() {
		return this.selectedCamelVersion;
	}
	
	/**
	 * @param selectedCamelVersion the selectedCamelVersion to set
	 */
	public void setSelectedCamelVersion(String selectedCamelVersion) {
		this.selectedCamelVersion = selectedCamelVersion;
	}
}
