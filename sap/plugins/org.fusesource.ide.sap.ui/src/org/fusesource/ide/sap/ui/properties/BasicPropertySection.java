/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.properties;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.ClientNumberValidator;
import org.fusesource.ide.sap.ui.validator.LanguageValidator;
import org.fusesource.ide.sap.ui.validator.SystemNumberValidator;

@SuppressWarnings("restriction")
public class BasicPropertySection extends DestinationDataPropertySection {

	private Text ashostText;
	private Text sysnrText;
	private Text clientText;
	private Text passwordText;
	private Text languageText;
	private Text userText;

	private Binding sysnrBinding;
	private Binding clientBinding;
	private Binding langBinding;
	protected ControlDecorationSupport langDecorator;
	protected ControlDecorationSupport sysnrDecorator;
	protected ControlDecorationSupport clientDecorator;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite basicContainer = createFlatFormComposite(parent);
		
		ashostText = getWidgetFactory().createText(basicContainer, null, SWT.NONE);
		ashostText.setToolTipText(Messages.BasicPropertySection_AshostToolTip);
		ashostText.setLayoutData(LayoutUtil.firstEntryLayoutData());
		
		CLabel ashostLbl = getWidgetFactory().createCLabel(basicContainer, Messages.BasicPropertySection_AshostLabel, SWT.NONE);
		ashostLbl.setLayoutData(LayoutUtil.labelLayoutData(ashostText));
		ashostLbl.setAlignment(SWT.RIGHT);
		
		sysnrText = getWidgetFactory().createText(basicContainer, null, SWT.NONE);
		sysnrText.setToolTipText(Messages.BasicPropertySection_SysnrToolTip);
		sysnrText.setLayoutData(LayoutUtil.entryLayoutData(ashostText));
		
		CLabel systemNumberLbl = getWidgetFactory().createCLabel(basicContainer, Messages.BasicPropertySection_SysnrLabel, SWT.NONE);
		systemNumberLbl.setLayoutData(LayoutUtil.labelLayoutData(sysnrText));
		systemNumberLbl.setAlignment(SWT.RIGHT);
		
		clientText = getWidgetFactory().createText(basicContainer, null, SWT.NONE);
		clientText.setToolTipText(Messages.BasicPropertySection_ClientToolTip);
		clientText.setLayoutData(LayoutUtil.entryLayoutData(sysnrText));
		
		CLabel clientLbl = getWidgetFactory().createCLabel(basicContainer, Messages.BasicPropertySection_ClientLabel, SWT.NONE);
		clientLbl.setLayoutData(LayoutUtil.labelLayoutData(clientText));
		clientLbl.setAlignment(SWT.RIGHT);
		
		userText = getWidgetFactory().createText(basicContainer, null, SWT.NONE);
		userText.setToolTipText(Messages.BasicPropertySection_UserToolTip);
		userText.setLayoutData(LayoutUtil.entryLayoutData(clientText));
		
		CLabel userLbl = getWidgetFactory().createCLabel(basicContainer, Messages.BasicPropertySection_UserLabel, SWT.NONE);
		userLbl.setLayoutData(LayoutUtil.labelLayoutData(userText));
		userLbl.setAlignment(SWT.RIGHT);
		
		passwordText = getWidgetFactory().createText(basicContainer, null, SWT.PASSWORD);
		passwordText.setToolTipText(Messages.BasicPropertySection_PasswordToolTip);
		passwordText.setLayoutData(LayoutUtil.entryLayoutData(userText));
		
		CLabel passwordLbl = getWidgetFactory().createCLabel(basicContainer, Messages.BasicPropertySection_PasswordLabel, SWT.NONE);
		passwordLbl.setLayoutData(LayoutUtil.labelLayoutData(passwordText));
		passwordLbl.setAlignment(SWT.RIGHT);
		
		languageText = getWidgetFactory().createText(basicContainer, null, SWT.NONE);
		languageText.setToolTipText(Messages.BasicPropertySection_LanguageToolTip);
		languageText.setLayoutData(LayoutUtil.entryLayoutData(passwordText));
		new Label(basicContainer, SWT.NONE);
		new Label(basicContainer, SWT.NONE);
	
		CLabel languageLbl = getWidgetFactory().createCLabel(basicContainer, Messages.BasicPropertySection_LanguageLabel, SWT.NONE);
		languageLbl.setLayoutData(LayoutUtil.labelLayoutData(languageText));
		languageLbl.setAlignment(SWT.RIGHT);
		
	}
		
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = super.initDataBindings();

		//
		IObservableValue observeTextAshostTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(ashostText);
		IObservableValue destinationAshostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__ASHOST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextAshostTextObserveWidget, destinationAshostObserveValue, null, null);
		//
		IObservableValue observeTextSysnrTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sysnrText);
		IObservableValue destinationSysnrObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SYSNR)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setBeforeSetValidator(new SystemNumberValidator());
		sysnrBinding = bindingContext.bindValue(observeTextSysnrTextObserveWidget, destinationSysnrObserveValue, strategy_1, null);
		//
		IObservableValue observeTextClientTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(clientText);
		IObservableValue destinationClientObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CLIENT)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new ClientNumberValidator());
		clientBinding = bindingContext.bindValue(observeTextClientTextObserveWidget, destinationClientObserveValue, strategy, null);
		//
		IObservableValue observeTextUserTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(userText);
		IObservableValue destinationUserNameObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__USER_NAME)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextUserTextObserveWidget, destinationUserNameObserveValue, null, null);
		//
		IObservableValue observeTextPasswordTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(passwordText);
		IObservableValue destinationPasswordObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__PASSWORD)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextPasswordTextObserveWidget, destinationPasswordObserveValue, null, null);
		//
		IObservableValue observeTextLanguageTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(languageText);
		IObservableValue destinationLangObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__LANG)).observe(destinationDataStoreEntry);
		UpdateValueStrategy langStrategy = new UpdateValueStrategy();
		langStrategy.setBeforeSetValidator(new LanguageValidator());
		langBinding = bindingContext.bindValue(observeTextLanguageTextObserveWidget, destinationLangObserveValue, langStrategy, null);
		
		sysnrDecorator = ControlDecorationSupport.create(sysnrBinding, SWT.TOP | SWT.LEFT);
		clientDecorator = ControlDecorationSupport.create(clientBinding, SWT.TOP | SWT.LEFT);
		langDecorator = ControlDecorationSupport.create(langBinding, SWT.TOP | SWT.LEFT);

		return bindingContext;
	}
	
}
