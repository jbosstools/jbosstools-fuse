/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.ui.wizards.pages;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.dialog.IValidationMessageProvider;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.ControlDecorationHelper;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;
import org.fusesource.ide.syndesis.extensions.core.util.IgniteVersionMapper;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;
import org.fusesource.ide.syndesis.extensions.ui.templates.CustomConnectorProjectTemplate;
import org.fusesource.ide.syndesis.extensions.ui.templates.CustomStepAsCamelRouteProjectTemplate;
import org.fusesource.ide.syndesis.extensions.ui.templates.CustomStepAsJavaBeanProjectTemplate;
import org.fusesource.ide.syndesis.extensions.ui.util.SyndesisVersionChecker;
import org.fusesource.ide.syndesis.extensions.ui.wizards.SyndesisExtensionProjectWizard;
import org.fusesource.ide.syndesis.extensions.ui.wizards.validation.SyndesisExtensionIdValidator;
import org.fusesource.ide.syndesis.extensions.ui.wizards.validation.SyndesisExtensionNameValidator;
import org.fusesource.ide.syndesis.extensions.ui.wizards.validation.SyndesisExtensionVersionValidator;

/**
 * @author lheinema
 */
public class SyndesisExtensionProjectWizardExtensionDetailsPage extends WizardPage {

	private static final String DEFAULT_EXTENSION_ID = "ignite.extension.example";
	private static final String DEFAULT_EXTENSION_NAME = "Example Ignite Extension";
	private static final String DEFAULT_EXTENSION_VERSION = "1.0.0";
	
	private SelectionListener btnGroupSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			customConnector = customConnectorRadio.getSelection();
			stepButtonGroup.setVisible(customStepRadio.getSelection());
		}
	};
	
	private SelectionListener stepSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			camelRoute = stepCamelRouteRadio.getSelection();
		}
	};
	
	private IValidationMessageProvider dummyMessageProvider = new IValidationMessageProvider() {
		
		@Override
		public int getMessageType(ValidationStatusProvider statusProvider) {
			IStatus s = getStatus(statusProvider);
			if (!s.isOK()) {
				return IMessageProvider.ERROR;
			}
			return IMessageProvider.NONE;
		}
		
		@Override
		public String getMessage(ValidationStatusProvider statusProvider) {
			IStatus s = getStatus(statusProvider);
			if (!s.isOK()) {
				return Messages.newProjectWizardExtensionDetailsPageErrorValidationError;
			}
			return null;
		}
		
		private IStatus getStatus(ValidationStatusProvider statusProvider) {
			if (statusProvider != null) {
				IObservableValue<IStatus> s = statusProvider.getValidationStatus();
				return s.getValue();
			}
			return Status.OK_STATUS;
		}
	};
	
	private ComboViewer syndesisVersionCombo;
	private Button customStepRadio;
	private Button customConnectorRadio;
	private Button stepCamelRouteRadio;
	private Composite stepButtonGroup;
	
	private ControlDecorationHelper controlDecorationHelper =  new ControlDecorationHelper();
	private SyndesisExtensionProjectWizard wizard;
	private Map<String, String> syndesisVersionMap = new HashMap<>();
	private boolean customConnector;
	private boolean camelRoute;
	
	public SyndesisExtensionProjectWizardExtensionDetailsPage() {
		super(Messages.newProjectWizardExtensionDetailsPageName);
		setTitle(Messages.newProjectWizardExtensionDetailsPageTitle);
		setDescription(Messages.newProjectWizardExtensionDetailsPageDescription);
		setImageDescriptor(SyndesisExtensionsUIActivator.imageDescriptorFromPlugin(SyndesisExtensionsUIActivator.PLUGIN_ID, SyndesisExtensionsUIActivator.SYNDESIS_EXTENSION_PROJECT_ICON));
		setPageComplete(false);
	}
	
	@Override
	public void createControl(Composite parent) {
		DataBindingContext dbc = new DataBindingContext();
		WizardPageSupport wps = WizardPageSupport.create(this, dbc);
		wps.setValidationMessageProvider(dummyMessageProvider);
		wizard = (SyndesisExtensionProjectWizard)getWizard();
				
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(4, false));

		createSyndesisVersionControls(container, dbc);
		
		Label spacer = new Label(container, SWT.NONE);
		GridData gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		spacer.setLayoutData(gridData);
		
		Label extensionDetailsLabel = new Label(container, SWT.NONE);
		extensionDetailsLabel.setText(Messages.newProjectWizardExtensionDetailsPageExtensionDetailsLabel);
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		extensionDetailsLabel.setLayoutData(gridData);
		
		Text extensionIdText = createField(container, Messages.newProjectWizardExtensionDetailsPageExtensionIdLabel, null, Messages.newProjectWizardExtensionDetailsPageExtensionIdTooltip);
		UpdateValueStrategy updateStrategy = UpdateValueStrategy.create(null);
		updateStrategy.setBeforeSetValidator(new SyndesisExtensionIdValidator());		
		createBinding(dbc, extensionIdText, "extensionId", updateStrategy);
		// set a default value
		extensionIdText.setText(DEFAULT_EXTENSION_ID);
		
		Text extensionNameText = createField(container, Messages.newProjectWizardExtensionDetailsPageNameLabel, null, Messages.newProjectWizardExtensionDetailsPageNameTooltip);
		updateStrategy = UpdateValueStrategy.create(null);
		updateStrategy.setBeforeSetValidator(new SyndesisExtensionNameValidator());
		createBinding(dbc, extensionNameText, "name", updateStrategy);
		// set a default value
		extensionNameText.setText(DEFAULT_EXTENSION_NAME);
		
		Text extensionDescriptionText = createField(container, Messages.newProjectWizardExtensionDetailsPageDescriptionLabel, Messages.newProjectWizardExtensionDetailsPageOptionalDescriptionFieldHint, Messages.newProjectWizardExtensionDetailsPageDescriptionTooltip);
		createBinding(dbc, extensionDescriptionText, "description");

		Text extensionVersionText = createField(container, Messages.newProjectWizardExtensionDetailsPageVersionLabel, null, Messages.newProjectWizardExtensionDetailsPageVersionTooltip);
		updateStrategy = UpdateValueStrategy.create(null);
		updateStrategy.setBeforeSetValidator(new SyndesisExtensionVersionValidator());
		createBinding(dbc, extensionVersionText, "version", updateStrategy);
		// set a default value
		extensionVersionText.setText(DEFAULT_EXTENSION_VERSION);
		
		spacer = new Label(container, SWT.NONE);
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		spacer.setLayoutData(gridData);
		
		createExtensionTypeRadioGroup(container);
				
		spacer = new Label(container, SWT.NONE);
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		spacer.setLayoutData(gridData);
		
		createStepTypeRadioGroup(container);
		
		setControl(container);
		
		extensionIdText.setFocus();
	}
	
	private Binding createBinding(DataBindingContext dbc, Widget control, String property) {
		return createBinding(dbc, control, property, null);
	}
	
	private Binding createBinding(DataBindingContext dbc, Widget control, String property, UpdateValueStrategy updateStrategy) {
		IObservableValue target = null;
		if (control instanceof Combo) {
			target = WidgetProperties.text().observeDelayed(200, control);		
		} else if (control instanceof Text) {
			target = WidgetProperties.text(SWT.Modify).observeDelayed(200, control);
		} else {
			// not supported
		}
		IObservableValue model= BeanProperties.value(SyndesisExtension.class, property).observe(wizard.getSyndesisExtension());
		if (model != null && target != null) {
			Binding b = dbc.bindValue(target, model, updateStrategy, null);
			ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);
			return b;
		}
		return null;
	}

	private void createSyndesisVersionControls(Composite container, DataBindingContext dbc) {
		Label syndesisVersionLabel = new Label(container, SWT.NONE);
		syndesisVersionLabel.setText(Messages.newProjectWizardExtensionDetailsPageSyndesisVersionLabel);
		GridData gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		syndesisVersionLabel.setLayoutData(gridData);
		
		Label spacer = new Label(container, SWT.NONE);
		gridData = GridDataFactory.fillDefaults().grab(false, false).span(1, 1).indent(8, 0).create();
		gridData.minimumWidth = 10;
		gridData.widthHint = 15;
		spacer.setLayoutData(gridData);
		
		syndesisVersionCombo = new ComboViewer(container, SWT.BORDER | SWT.DROP_DOWN);
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(2, 1).indent(8, 0).create();
		syndesisVersionCombo.getCombo().setLayoutData(gridData);
		syndesisVersionCombo.getCombo().setToolTipText(Messages.newProjectWizardExtensionDetailsPageSyndesisVersionTooltip);
		new ControlDecorationHelper().addInformationOnFocus(syndesisVersionCombo.getCombo(), Messages.newProjectWizardExtensionDetailsPageSyndesisVersionTooltip);
		syndesisVersionCombo.setLabelProvider(new SyndesisVersionLabelProvider());
		syndesisVersionCombo.setComparator(new ViewerComparator(Comparator.reverseOrder()));
		syndesisVersionCombo.setContentProvider(ArrayContentProvider.getInstance());
		UpdateValueStrategy updateStrategy = UpdateValueStrategy.create(null);
		updateStrategy.setConverter(IConverter.create(String.class, String.class, o1 -> translateDisplayTextToVersion((String) o1)));
		updateStrategy.setBeforeSetValidator(new SyndesisExtensionVersionValidator());
		createBinding(dbc, syndesisVersionCombo.getCombo(), "syndesisVersion", updateStrategy);
		
		Display.getDefault().asyncExec( () -> {
			syndesisVersionMap = new IgniteVersionMapper().getMapping();
			syndesisVersionCombo.setInput(getSyndesisVersions());
			syndesisVersionCombo.getCombo().select(0);
		} );
		
		Button syndesisVersionValidationBtn = new Button(container, SWT.PUSH);
		GridData verifyVersionButtonData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		syndesisVersionValidationBtn.setLayoutData(verifyVersionButtonData);
		syndesisVersionValidationBtn.setText(Messages.newProjectWizardExtensionDetailsPageSyndesisVersionValidationLabel);
		syndesisVersionValidationBtn.setToolTipText(Messages.newProjectWizardExtensionDetailsPageSyndesisVersionValidationTooltip);
		syndesisVersionValidationBtn.addSelectionListener(new VersionValidationHandler());
	}
	
	private void createStepTypeRadioGroup(Composite container) {
		GridData gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		stepButtonGroup = new Composite(container, SWT.NONE);
		stepButtonGroup.setLayoutData(gridData);
		stepButtonGroup.setLayout(new GridLayout(4, false));

		Label stepTypeLabel = new Label(stepButtonGroup, SWT.NONE);
		stepTypeLabel.setText(Messages.newProjectWizardExtensionDetailsPageStepTypeSelectionLabel);
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		stepTypeLabel.setLayoutData(gridData);

		Label spacer = new Label(stepButtonGroup, SWT.NONE);
		gridData = GridDataFactory.fillDefaults().grab(false, false).span(1, 1).indent(8, 0).create();
		gridData.minimumWidth = 10;
		gridData.widthHint = 15;
		spacer.setLayoutData(gridData);
		
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(3, 1).indent(8, 0).create();
		stepCamelRouteRadio = new Button(stepButtonGroup, SWT.RADIO);
		stepCamelRouteRadio.setText(Messages.newProjectWizardExtensionDetailsPageStepTypeSelectionCamelRouteLabel);
		stepCamelRouteRadio.setToolTipText(Messages.newProjectWizardExtensionDetailsPageStepTypeSelectionCamelRouteHint);
		stepCamelRouteRadio.setLayoutData(gridData);
		stepCamelRouteRadio.addSelectionListener(stepSelectionListener);
		
		spacer = new Label(stepButtonGroup, SWT.NONE);
		gridData = GridDataFactory.fillDefaults().grab(false, false).span(1, 1).indent(8, 0).create();
		gridData.minimumWidth = 10;
		gridData.widthHint = 15;
		spacer.setLayoutData(gridData);
		
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(3, 1).indent(8, 0).create();
		Button stepJavaBeanRadio = new Button(stepButtonGroup, SWT.RADIO);
		stepJavaBeanRadio.setText(Messages.newProjectWizardExtensionDetailsPageStepTypeSelectionJavaBeanLabel);
		stepJavaBeanRadio.setToolTipText(Messages.newProjectWizardExtensionDetailsPageStepTypeSelectionJavaBeanHint);
		stepJavaBeanRadio.setLayoutData(gridData);
		stepJavaBeanRadio.addSelectionListener(stepSelectionListener);
	
		stepCamelRouteRadio.setSelection(true);
		camelRoute = true;
	}
	
	private void createExtensionTypeRadioGroup(Composite container) {
		GridData gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		Composite typeButtonGroup = new Composite(container, SWT.NONE);
		typeButtonGroup.setLayoutData(gridData);
		typeButtonGroup.setLayout(new GridLayout(4, false));
				
		Label extensionTypeLabel = new Label(typeButtonGroup, SWT.NONE);
		extensionTypeLabel.setText(Messages.newProjectWizardExtensionDetailsPageTypeSelectionLabel);
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(4, 1).indent(8, 0).create();
		extensionTypeLabel.setLayoutData(gridData);

		Label spacer = new Label(typeButtonGroup, SWT.NONE);
		gridData = GridDataFactory.fillDefaults().grab(false, false).span(1, 1).indent(8, 0).create();
		gridData.minimumWidth = 10;
		gridData.widthHint = 15;
		spacer.setLayoutData(gridData);
		
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(3, 1).indent(8, 0).create();
		customStepRadio = new Button(typeButtonGroup, SWT.RADIO);
		customStepRadio.setText(Messages.newProjectWizardExtensionDetailsPageTypeSelectionStepLabel);
		customStepRadio.setToolTipText(Messages.newProjectWizardExtensionDetailsPageTypeSelectionStepHint);
		customStepRadio.setLayoutData(gridData);
		customStepRadio.addSelectionListener(btnGroupSelectionListener);
		
		spacer = new Label(typeButtonGroup, SWT.NONE);
		gridData = GridDataFactory.fillDefaults().grab(false, false).span(1, 1).indent(8, 0).create();
		gridData.minimumWidth = 10;
		gridData.widthHint = 15;
		spacer.setLayoutData(gridData);
		
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(3, 1).indent(8, 0).create();
		customConnectorRadio = new Button(typeButtonGroup, SWT.RADIO);
		customConnectorRadio.setText(Messages.newProjectWizardExtensionDetailsPageTypeSelectionConnectorLabel);
		customConnectorRadio.setToolTipText(Messages.newProjectWizardExtensionDetailsPageTypeSelectionConnectorHint);
		customConnectorRadio.setLayoutData(gridData);
		customConnectorRadio.addSelectionListener(btnGroupSelectionListener);
		
		customStepRadio.setSelection(true);
		customConnector = customConnectorRadio.getSelection();
	}
	
	private Text createField(Composite container, String label, String message, String toolTip) {
		// create the label
		Label spacer = new Label(container, SWT.NONE);
		GridData gridData = GridDataFactory.fillDefaults().grab(false, false).span(1, 1).indent(8, 0).create();
		gridData.minimumWidth = 10;
		gridData.widthHint = 15;
		spacer.setLayoutData(gridData);
		
		Label l = new Label(container, SWT.NONE);
		l.setText(label);
		
		gridData = GridDataFactory.fillDefaults().grab(true, false).span(1, 1).indent(8, 0).create();
		
		// create the control
		Text textField = new Text(container, SWT.BORDER);
		textField.setLayoutData(gridData);
		textField.setToolTipText(toolTip);
		
		controlDecorationHelper.addInformationOnFocus(textField, toolTip);
		
		if (!Strings.isBlank(message)) {
			textField.setMessage(message);
		}
		
		spacer = new Label(container, SWT.NONE);
		gridData = GridDataFactory.fillDefaults().grab(false, false).span(1, 1).indent(8, 0).create();
		spacer.setLayoutData(gridData);
		
		return textField;
	}
	
	private Set<String> getSyndesisVersions() {
		return syndesisVersionMap.keySet();
	}
	
	private String translateDisplayTextToVersion(String displayText) {
		for (Entry<String, String> e : syndesisVersionMap.entrySet()) {
			if (e.getValue().equals(displayText)) {
				return e.getKey();
			}
		}
		return displayText;
	}
	
	/**
	 * @return the camelRoute
	 */
	private boolean isCamelRoute() {
		return this.camelRoute;
	}
	
	/**
	 * @return the customConnector
	 */
	private boolean isCustomConnector() {
		return this.customConnector;
	}
	
	public AbstractProjectTemplate getTemplate() {
		AbstractProjectTemplate template;
		
		if (isCustomConnector()) {
			template = new CustomConnectorProjectTemplate();
		} else if (isCamelRoute()) {
			template = new CustomStepAsCamelRouteProjectTemplate();
		} else {
			template = new CustomStepAsJavaBeanProjectTemplate();
		}
		
		return template;
	}
	
	class VersionValidationHandler extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			String syndesisVersion = translateDisplayTextToVersion(syndesisVersionCombo.getCombo().getText());
			SyndesisVersionChecker versionChecker = new SyndesisVersionChecker(syndesisVersion);
			try {
				getWizard().getContainer().run(true, true, versionChecker);
			} catch (InterruptedException iex) {
				versionChecker.cancel();
				Thread.currentThread().interrupt();
			} catch (Exception ex) {
				SyndesisExtensionsUIActivator.pluginLog().logError(ex);
			}
			while (!versionChecker.isDone()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
			updateSyndesisValidation(syndesisVersion, versionChecker);
		}
		
		private void updateSyndesisValidation(String syndesisVersion, SyndesisVersionChecker versionChecker) {
			boolean valid = versionChecker.isValid();
			if (versionChecker.isCanceled()) {
				setErrorMessage(null);
			} else if (!valid) {
				setMessage(null);
				setErrorMessage(NLS.bind(Messages.newProjectWizardExtensionDetailsPageErrorInvalidSyndesisVersion, syndesisVersion));
			} else {
				setErrorMessage(null);
				setMessage(Messages.newProjectWizardExtensionDetailsPageSyndesisVersionValid, INFORMATION);
			}
			setPageComplete(valid);
		}
	}
}
