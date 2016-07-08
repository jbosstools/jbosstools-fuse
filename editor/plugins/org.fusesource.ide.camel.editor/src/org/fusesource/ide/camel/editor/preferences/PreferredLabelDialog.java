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
package org.fusesource.ide.camel.editor.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.internal.CamelService;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class PreferredLabelDialog extends TitleAreaDialog {

	private String component;
	private String parameter;

	private Combo componentCombo;
	private Combo parameterCombo;

	private IInputValidator componentValidator;
	private IInputValidator parameterValidator;

	public PreferredLabelDialog(Shell parentShell) {
		super(parentShell);
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public void setComponentValidator(IInputValidator componentValidator) {
		this.componentValidator = componentValidator;
	}

	public void setParameterValidator(IInputValidator parameterValidator) {
		this.parameterValidator = parameterValidator;
	}

	public void validate() {
		if (componentValidator != null) {
			if (setWarning(componentValidator.isValid(componentCombo.getText()))) {
				return;
			}
		}
		if (parameterValidator != null) {
			setWarning(parameterValidator.isValid(parameterCombo.getText()));
		}
	}

	public boolean setWarning(String msg) {
		if ((msg == null || msg.isEmpty()) && getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
			setMessage(null, IMessageProvider.NONE);
			return false;
		}
		setMessage(msg, IMessageProvider.WARNING);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return true;
	}

	@Override
	public void create() {
		super.create();
		setTitle(UIMessages.preferredLabels_title);
		validate();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createComponentComboBox(container);
		createParameterCombo(container);

		CamelModel model = new CamelService().getCamelModel(CamelCatalogUtils.getLatestCamelVersion(), CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
		List<String> componentNames = new ArrayList<String>();
		model.getEips().stream().map(eip -> eip.getName()).sorted().forEach(componentNames::add);

		componentCombo.setItems(componentNames.toArray(new String[componentNames.size()]));
		componentCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setParameterItems(model);
			}
		});

		return area;
	}

	private void createComponentComboBox(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText(UIMessages.preferredLabels_component);

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		componentCombo = new Combo(container, SWT.BORDER);
		componentCombo.setLayoutData(gd);
		componentCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				validate();
			}
		});

	}

	private void createParameterCombo(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText(UIMessages.preferredLabels_parameter);

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		parameterCombo = new Combo(container, SWT.BORDER);
		parameterCombo.setLayoutData(gd);
		parameterCombo.setEnabled(getComponent() != null);
		parameterCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				validate();
			}
		});
	}

	private void setParameterItems(CamelModel model) {
		String componentName = componentCombo.getText();
		List<String> paramNames = new ArrayList<String>();
		if (!componentName.isEmpty()) {
			List<Parameter> params = model.getEip(componentName).getParameters();
			params.stream().map(p -> p.getName()).sorted().forEach(paramNames::add);
		}
		parameterCombo.setEnabled(!paramNames.isEmpty());
		parameterCombo.setItems(paramNames.toArray(new String[paramNames.size()]));
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (getComponent() != null && !getComponent().isEmpty()) {
			shell.setText(UIMessages.preferredLabels_editDialogTitle);
		} else {
			shell.setText(UIMessages.preferredLabels_newDialogTitle);
		}
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		component = componentCombo.getText();
		parameter = parameterCombo.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

}