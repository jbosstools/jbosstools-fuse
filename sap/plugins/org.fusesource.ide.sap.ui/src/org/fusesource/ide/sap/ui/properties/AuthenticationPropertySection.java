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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.ClientNumberValidator;
import org.fusesource.ide.sap.ui.validator.LanguageValidator;

@SuppressWarnings("restriction")
public class AuthenticationPropertySection extends DestinationDataPropertySection {

	private Binding clientBinding2;
	private Binding langBinding2;

	protected ControlDecorationSupport langDecorator;
	protected ControlDecorationSupport clientDecorator;

	private CCombo authTypeCombo;
	private Text clientText2;
	private Text userText2;
	private Text userAlias;
	private Text passwordText2;
	private Text mysapsso2Text;
	private Text x509certText;
	private Text languageText2;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite authContainer = createFlatFormComposite(parent);

		authTypeCombo = getWidgetFactory().createCCombo(authContainer, SWT.READ_ONLY);
		authTypeCombo.setToolTipText(Messages.AuthenticationPropertySection_AuthTypeToolTip);
		authTypeCombo.setItems(new String[] {"CONFIGURED_USER", "CURRENT_USER"}); //$NON-NLS-1$ //$NON-NLS-2$
		authTypeCombo.select(0);
		authTypeCombo.setLayoutData(LayoutUtil.firstEntryLayoutData());
		
		CLabel authTypeLbl = getWidgetFactory().createCLabel(authContainer, Messages.AuthenticationPropertySection_AuthTypeLable, SWT.NONE);
		authTypeLbl.setLayoutData(LayoutUtil.labelLayoutData(authTypeCombo));
		authTypeLbl.setAlignment(SWT.RIGHT);
	
		clientText2 = getWidgetFactory().createText(authContainer, "", SWT.BORDER); //$NON-NLS-1$
		clientText2.setToolTipText(Messages.AuthenticationPropertySection_ClientToolTip);
		clientText2.setLayoutData(LayoutUtil.entryLayoutData(authTypeCombo));
		
		CLabel clientLbl2 = getWidgetFactory().createCLabel(authContainer, Messages.AuthenticationPropertySection_ClientLable, SWT.NONE);
		clientLbl2.setLayoutData(LayoutUtil.labelLayoutData(clientText2));
		clientLbl2.setAlignment(SWT.RIGHT);
		
		
		userText2 = getWidgetFactory().createText(authContainer, "", SWT.BORDER); //$NON-NLS-1$
		userText2.setToolTipText(Messages.AuthenticationPropertySection_UserToolTip);
		userText2.setLayoutData(LayoutUtil.entryLayoutData(clientText2));
		
		CLabel userLbl2 = getWidgetFactory().createCLabel(authContainer, Messages.AuthenticationPropertySection_UserLabel, SWT.NONE);
		userLbl2.setLayoutData(LayoutUtil.labelLayoutData(userText2));
		userLbl2.setAlignment(SWT.RIGHT);
		
		userAlias = getWidgetFactory().createText(authContainer, "", SWT.BORDER); //$NON-NLS-1$
		userAlias.setToolTipText(Messages.AuthenticationPropertySection_UserAliasToolTip);
		userAlias.setLayoutData(LayoutUtil.entryLayoutData(userText2));
		
		CLabel userAliasLbl = getWidgetFactory().createCLabel(authContainer, Messages.AuthenticationPropertySection_UserAliasLabel, SWT.NONE);
		userAliasLbl.setLayoutData(LayoutUtil.labelLayoutData(userAlias));
		userAliasLbl.setAlignment(SWT.RIGHT);

		passwordText2 = getWidgetFactory().createText(authContainer, "", SWT.BORDER | SWT.PASSWORD); //$NON-NLS-1$
		passwordText2.setToolTipText(Messages.AuthenticationPropertySection_PasswordToolTip);
		passwordText2.setLayoutData(LayoutUtil.entryLayoutData(userAlias));
		
		CLabel passwordLbl2 = getWidgetFactory().createCLabel(authContainer, Messages.AuthenticationPropertySection_PasswordLabel, SWT.NONE);
		passwordLbl2.setLayoutData(LayoutUtil.labelLayoutData(passwordText2));
		passwordLbl2.setAlignment(SWT.RIGHT);
		
		mysapsso2Text = getWidgetFactory().createText(authContainer, "", SWT.BORDER); //$NON-NLS-1$
		mysapsso2Text.setToolTipText(Messages.AuthenticationPropertySection_Mysapsso2ToolTip);
		mysapsso2Text.setLayoutData(LayoutUtil.entryLayoutData(passwordText2));
		
		CLabel mysapsso2Lbl = getWidgetFactory().createCLabel(authContainer, Messages.AuthenticationPropertySection_Mysapsso2Label, SWT.NONE);
		mysapsso2Lbl.setLayoutData(LayoutUtil.labelLayoutData(mysapsso2Text));
		mysapsso2Lbl.setAlignment(SWT.RIGHT);
		
		x509certText = getWidgetFactory().createText(authContainer, "", SWT.BORDER); //$NON-NLS-1$
		x509certText.setToolTipText(Messages.AuthenticationPropertySection_X509certToolTip);
		x509certText.setLayoutData(LayoutUtil.entryLayoutData(mysapsso2Text));
		
		CLabel x509certLbl = getWidgetFactory().createCLabel(authContainer, Messages.AuthenticationPropertySection_X509certLabel, SWT.NONE);
		x509certLbl.setLayoutData(LayoutUtil.labelLayoutData(x509certText));
		x509certLbl.setAlignment(SWT.RIGHT);
		
		languageText2 = getWidgetFactory().createText(authContainer, "", SWT.BORDER); //$NON-NLS-1$
		languageText2.setToolTipText(Messages.AuthenticationPropertySection_LanguageToolTip);
		languageText2.setLayoutData(LayoutUtil.entryLayoutData(x509certText));
		
		CLabel languageLbl2 = getWidgetFactory().createCLabel(authContainer, Messages.AuthenticationPropertySection_LanguageLabel, SWT.NONE);
		languageLbl2.setLayoutData(LayoutUtil.labelLayoutData(languageText2));
		languageLbl2.setAlignment(SWT.RIGHT);
		
	}

	protected DataBindingContext initDataBindings() {
		
		DataBindingContext bindingContext = super.initDataBindings();

		//
		IObservableValue observeTextAuthTypeComboObserveWidget = WidgetProperties.text().observe(authTypeCombo);
		IObservableValue managedConnectionFactoryAuthTypeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__AUTH_TYPE)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextAuthTypeComboObserveWidget, managedConnectionFactoryAuthTypeObserveValue, null, null);
		//
		IObservableValue observeTextClientText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(clientText2);
		IObservableValue managedConnectionFactoryClientObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CLIENT)).observe(destinationDataStoreEntry);
		UpdateValueStrategy clientStrategy2 = new UpdateValueStrategy();
		clientStrategy2.setBeforeSetValidator(new ClientNumberValidator());
		clientBinding2 = bindingContext.bindValue(observeTextClientText2ObserveWidget, managedConnectionFactoryClientObserveValue, clientStrategy2, null);
		//
		IObservableValue observeTextUserText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(userText2);
		IObservableValue managedConnectionFactoryUserObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__USER)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextUserText2ObserveWidget, managedConnectionFactoryUserObserveValue, null, null);
		//
		IObservableValue observeTextUserAliasObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(userAlias);
		IObservableValue managedConnectionFactoryAliasUserObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__ALIAS_USER)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextUserAliasObserveWidget, managedConnectionFactoryAliasUserObserveValue, null, null);
		//
		IObservableValue observeTextPasswordText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(passwordText2);
		IObservableValue managedConnectionFactoryPasswordObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__PASSWORD)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextPasswordText2ObserveWidget, managedConnectionFactoryPasswordObserveValue, null, null);
		//
		IObservableValue observeTextMysapsso2TextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(mysapsso2Text);
		IObservableValue managedConnectionFactoryMysapsso2ObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MYSAPSSO2)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextMysapsso2TextObserveWidget, managedConnectionFactoryMysapsso2ObserveValue, null, null);
		//
		IObservableValue observeTextX509certTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(x509certText);
		IObservableValue managedConnectionFactoryX509certObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__X509CERT)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextX509certTextObserveWidget, managedConnectionFactoryX509certObserveValue, null, null);
		//
		IObservableValue observeTextLanguageText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(languageText2);
		IObservableValue managedConnectionFactoryLangObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__LANG)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setBeforeSetValidator(new LanguageValidator());
		langBinding2 = bindingContext.bindValue(observeTextLanguageText2ObserveWidget, managedConnectionFactoryLangObserveValue, strategy_3, null);

		clientDecorator = ControlDecorationSupport.create(clientBinding2, SWT.TOP | SWT.LEFT);
		langDecorator = ControlDecorationSupport.create(langBinding2, SWT.TOP | SWT.LEFT);

		return bindingContext;
	}	
		
}
