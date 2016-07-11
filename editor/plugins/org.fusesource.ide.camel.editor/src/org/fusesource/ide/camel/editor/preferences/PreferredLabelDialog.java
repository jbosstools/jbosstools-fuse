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

import static org.fusesource.ide.camel.editor.provider.ProviderHelper.getCategoryFromEip;
import static org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils.RUNTIME_PROVIDER_KARAF;
import static org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils.getLatestCamelVersion;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
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

	private ComboViewer componentCombo;
	private ComboViewer parameterCombo;

	private IInputValidator componentValidator;
	private IInputValidator parameterValidator;

	private CamelModel camelModel;

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

	protected CamelModel getCamelModel() {
		if (camelModel == null) {
			camelModel = new CamelService().getCamelModel(getLatestCamelVersion(), RUNTIME_PROVIDER_KARAF);
		}
		return camelModel;
	}

	protected List<Eip> getComponents() {
		return getCamelModel().getEips().stream().filter(eip -> !"NONE".equals(getCategoryFromEip(eip)))
				.collect(Collectors.toList());
	}

	public void validate() {
		if (componentValidator != null) {
			if (setWarning(componentValidator.isValid(componentCombo.getCombo().getText()))) {
				return;
			}
		}
		if (parameterValidator != null) {
			setWarning(parameterValidator.isValid(parameterCombo.getCombo().getText()));
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

		componentCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof StructuredSelection) {
					Object selectedObject = ((StructuredSelection) selection).getFirstElement();
					if (selectedObject instanceof Eip) {
						parameterCombo.setInput(((Eip) selectedObject).getParameters());
					}
				}
			}
		});

		if (component != null && !component.isEmpty()) {
			Eip eip = getCamelModel().getEip(component);
			componentCombo.getCombo().setEnabled(false);
			componentCombo.setInput(new Eip[] { eip });
			componentCombo.setSelection(new StructuredSelection(eip));
			if (parameter != null && !parameter.isEmpty()) {
				parameterCombo.setSelection(new StructuredSelection(eip.getParameter(parameter)));
			}
		} else {
			componentCombo.setInput(getComponents());
		}

		validate();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		componentCombo = createComboViewer(container, UIMessages.preferredLabels_component);
		parameterCombo = createComboViewer(container, UIMessages.preferredLabels_parameter);

		return area;
	}

	private ComboViewer createComboViewer(Composite container, String label) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText(label);

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		ComboViewer comboViewer = new ComboViewer(container, SWT.BORDER | SWT.READ_ONLY);
		comboViewer.getCombo().setLayoutData(gd);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setSorter(new ViewerSorter());
		comboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Eip) {
					return ((Eip) element).getName();
				}
				if (element instanceof Parameter) {
					return ((Parameter) element).getName();
				}
				return element.toString();
			}
		});
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				validate();
			}
		});
		return comboViewer;
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
		component = componentCombo.getCombo().getText();
		parameter = parameterCombo.getCombo().getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

}