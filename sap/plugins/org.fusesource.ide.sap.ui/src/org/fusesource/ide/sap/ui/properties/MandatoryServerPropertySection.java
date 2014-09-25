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
import org.fusesource.ide.sap.ui.validator.NonNegativeIntegerValidator;

@SuppressWarnings("restriction")
public class MandatoryServerPropertySection extends ServerDataPropertySection {

	private Text gwhostText;
	private Text gwservText;
	private Text progidText;
	private Text repositoryDestinationText;
	private Text connectionCountText;
	
	private Binding connectionCountBinding;

	protected ControlDecorationSupport connectionCountDecorator;

	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite mandatoryContainer = createFlatFormComposite(parent);
		
		gwhostText = getWidgetFactory().createText(mandatoryContainer, null, SWT.NONE);
		gwhostText.setToolTipText(Messages.MandatoryServerPropertySection_GwhostToolTip);
		gwhostText.setLayoutData(LayoutUtil.firstEntryLayoutData());
		
		CLabel gwhostLbl = getWidgetFactory().createCLabel(mandatoryContainer, Messages.MandatoryServerPropertySection_GwhostLabel, SWT.NONE);
		gwhostLbl.setLayoutData(LayoutUtil.labelLayoutData(gwhostText));
		gwhostLbl.setAlignment(SWT.RIGHT);
		
		gwservText = getWidgetFactory().createText(mandatoryContainer, null, SWT.NONE);
		gwservText.setToolTipText(Messages.MandatoryServerPropertySection_GwservToolTip);
		gwservText.setLayoutData(LayoutUtil.entryLayoutData(gwhostText));
		
		CLabel gwservLbl = getWidgetFactory().createCLabel(mandatoryContainer, Messages.MandatoryServerPropertySection_GwservLabel, SWT.NONE);
		gwservLbl.setLayoutData(LayoutUtil.labelLayoutData(gwservText));
		gwservLbl.setAlignment(SWT.RIGHT);
		
		progidText = getWidgetFactory().createText(mandatoryContainer, null, SWT.NONE);
		progidText.setToolTipText(Messages.MandatoryServerPropertySection_ProgidToolTip);
		progidText.setLayoutData(LayoutUtil.entryLayoutData(gwservText));
		
		CLabel progidLbl = getWidgetFactory().createCLabel(mandatoryContainer, Messages.MandatoryServerPropertySection_ProgidLabel, SWT.NONE);
		progidLbl.setLayoutData(LayoutUtil.labelLayoutData(progidText));
		progidLbl.setAlignment(SWT.RIGHT);
		
		repositoryDestinationText = getWidgetFactory().createText(mandatoryContainer, "", SWT.BORDER); //$NON-NLS-1$
		repositoryDestinationText.setToolTipText(Messages.OptionalServerPropertySection_RepositoryDestinationToolTip);
		repositoryDestinationText.setLayoutData(LayoutUtil.entryLayoutData(progidText));

		CLabel repositoryDestinationLbl = getWidgetFactory().createCLabel(mandatoryContainer, Messages.OptionalServerPropertySection_RepositoryDestinationLabel, SWT.NONE);
		repositoryDestinationLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryDestinationText));
		repositoryDestinationLbl.setAlignment(SWT.RIGHT);
		
		connectionCountText = getWidgetFactory().createText(mandatoryContainer, null, SWT.NONE);
		connectionCountText.setToolTipText(Messages.MandatoryServerPropertySection_ConnectionCountToolTip);
		connectionCountText.setLayoutData(LayoutUtil.entryLayoutData(repositoryDestinationText));
		new Label(mandatoryContainer, SWT.NONE);
		new Label(mandatoryContainer, SWT.NONE);
	
		CLabel connectionCountLbl = getWidgetFactory().createCLabel(mandatoryContainer, Messages.MandatoryServerPropertySection_ConnectionCountLabel, SWT.NONE);
		connectionCountLbl.setLayoutData(LayoutUtil.labelLayoutData(connectionCountText));
		connectionCountLbl.setAlignment(SWT.RIGHT);
		
	}
	
	protected DataBindingContext initDataBindings() {
		
		DataBindingContext bindingContext = super.initDataBindings();
		//
		IObservableValue observeTextAshostTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(gwhostText);
		IObservableValue destinationAshostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__GWHOST)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextAshostTextObserveWidget, destinationAshostObserveValue, null, null);
		//
		IObservableValue observeTextSysnrTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(gwservText);
		IObservableValue destinationSysnrObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__GWSERV)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextSysnrTextObserveWidget, destinationSysnrObserveValue, null, null);
		//
		IObservableValue observeTextClientTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(progidText);
		IObservableValue destinationClientObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__PROGID)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextClientTextObserveWidget, destinationClientObserveValue, null, null);
		//
		IObservableValue observeRepositoryDestinationTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryDestinationText);
		IObservableValue serverRepositoryDestinationObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__REPOSITORY_DESTINATION)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeRepositoryDestinationTextObserveWidget, serverRepositoryDestinationObserveValue, null, null);
		//
		IObservableValue observeTextLanguageTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(connectionCountText);
		IObservableValue destinationLangObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__CONNECTION_COUNT)).observe(serverDataStoreEntry);
		UpdateValueStrategy connectionCountStrategy = new UpdateValueStrategy();
		connectionCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.MandatoryServerPropertySection_ConnectionCountValidator));
		connectionCountBinding = bindingContext.bindValue(observeTextLanguageTextObserveWidget, destinationLangObserveValue, connectionCountStrategy, null);
		
		connectionCountDecorator = ControlDecorationSupport.create(connectionCountBinding, SWT.TOP | SWT.LEFT);
		
		return bindingContext;
	}
	
}
