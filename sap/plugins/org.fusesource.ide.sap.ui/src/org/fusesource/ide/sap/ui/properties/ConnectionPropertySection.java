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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.SapRouterStringValidator;
import org.fusesource.ide.sap.ui.validator.SystemNumberValidator;

@SuppressWarnings("restriction")
public class ConnectionPropertySection extends DestinationDataPropertySection {

	private Binding saprouterBinding;
	private Binding sysnrBinding2;

	protected ControlDecorationSupport saprouterDecorator;
	protected ControlDecorationSupport sysnrDecorator;

	private Text sysnrText2;
	private Text saprouterText;
	private Text ashostText2;
	private Text mshostText;
	private Text msservText;
	private Text gwhostText;
	private Text gwservText;
	private Text r3nameText;
	private Text groupText;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite connectionContainer = createFlatFormComposite(parent);

		sysnrText2 = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		sysnrText2.setToolTipText(Messages.ConnectionPropertySection_SysnrToolTip);
		sysnrText2.setLayoutData(LayoutUtil.firstEntryLayoutData());

		CLabel sysnrLbl2 = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_SysnrLabel, SWT.NONE);
		sysnrLbl2.setLayoutData(LayoutUtil.labelLayoutData(sysnrText2));
		sysnrLbl2.setAlignment(SWT.RIGHT);

		saprouterText = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		saprouterText.setToolTipText(Messages.ConnectionPropertySection_SaprouterToolTip);
		saprouterText.setLayoutData(LayoutUtil.entryLayoutData(sysnrText2));

		CLabel saprouterLbl = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_SaprouterLabel, SWT.NONE);
		saprouterLbl.setLayoutData(LayoutUtil.labelLayoutData(saprouterText));
		saprouterLbl.setAlignment(SWT.RIGHT);

		ashostText2 = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		ashostText2.setToolTipText(Messages.ConnectionPropertySection_AshostToolTip);
		ashostText2.setLayoutData(LayoutUtil.entryLayoutData(saprouterText));

		CLabel ashostLbl2 = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_AshostLabel, SWT.NONE);
		ashostLbl2.setLayoutData(LayoutUtil.labelLayoutData(ashostText2));
		ashostLbl2.setAlignment(SWT.RIGHT);

		mshostText = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		mshostText.setToolTipText(Messages.ConnectionPropertySection_MshostToolTip);
		mshostText.setLayoutData(LayoutUtil.entryLayoutData(ashostText2));

		CLabel mshostLbl = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_MshostLabel, SWT.NONE);
		mshostLbl.setLayoutData(LayoutUtil.labelLayoutData(mshostText));
		mshostLbl.setAlignment(SWT.RIGHT);

		msservText = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		msservText.setToolTipText(Messages.ConnectionPropertySection_MsservToolTip);
		msservText.setLayoutData(LayoutUtil.entryLayoutData(mshostText));

		CLabel msgservLbl = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_MsservLabel, SWT.NONE);
		msgservLbl.setLayoutData(LayoutUtil.labelLayoutData(msservText));
		msgservLbl.setAlignment(SWT.RIGHT);

		gwhostText = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		gwhostText.setToolTipText(Messages.ConnectionPropertySection_GwhostToolTip);
		gwhostText.setLayoutData(LayoutUtil.entryLayoutData(msservText));

		CLabel gwhostLbl = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_GwhostLabel, SWT.NONE);
		gwhostLbl.setLayoutData(LayoutUtil.labelLayoutData(gwhostText));
		gwhostLbl.setAlignment(SWT.RIGHT);

		gwservText = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		gwservText.setToolTipText(Messages.ConnectionPropertySection_GwservToolTip);
		gwservText.setLayoutData(LayoutUtil.entryLayoutData(gwhostText));

		CLabel gwservLbl = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_GwservLabel, SWT.NONE);
		gwservLbl.setLayoutData(LayoutUtil.labelLayoutData(gwservText));
		gwservLbl.setAlignment(SWT.RIGHT);

		r3nameText = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		r3nameText.setToolTipText(Messages.ConnectionPropertySection_R3nameToolTip);
		r3nameText.setLayoutData(LayoutUtil.entryLayoutData(gwservText));

		CLabel r3nameLbl = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_R3nameLabel, SWT.NONE);
		r3nameLbl.setLayoutData(LayoutUtil.labelLayoutData(r3nameText));
		r3nameLbl.setAlignment(SWT.RIGHT);

		groupText = getWidgetFactory().createText(connectionContainer, "", SWT.BORDER); //$NON-NLS-1$
		groupText.setToolTipText(Messages.ConnectionPropertySection_GroupToolTip);
		groupText.setLayoutData(LayoutUtil.entryLayoutData(r3nameText));

		CLabel groupLbl = getWidgetFactory().createCLabel(connectionContainer, Messages.ConnectionPropertySection_GroupLabel, SWT.NONE);
		groupLbl.setLayoutData(LayoutUtil.labelLayoutData(groupText));
		groupLbl.setAlignment(SWT.RIGHT);
		
	}
	
	protected DataBindingContext initDataBindings() {

		DataBindingContext bindingContext = super.initDataBindings();

		//
		IObservableValue observeTextSysnrText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sysnrText2);
		IObservableValue managedConnectionFactorySysnrObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SYSNR)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setBeforeSetValidator(new SystemNumberValidator());
		sysnrBinding2 = bindingContext.bindValue(observeTextSysnrText2ObserveWidget, managedConnectionFactorySysnrObserveValue, strategy_2, null);
		//
		IObservableValue observeTextSaprouterTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(saprouterText);
		IObservableValue managedConnectionFactorySaprouterObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SAPROUTER)).observe(destinationDataStoreEntry);
		UpdateValueStrategy saprouterStrategy = new UpdateValueStrategy();
		saprouterStrategy.setBeforeSetValidator(new SapRouterStringValidator());
		saprouterBinding = bindingContext.bindValue(observeTextSaprouterTextObserveWidget, managedConnectionFactorySaprouterObserveValue, saprouterStrategy, null);
		//
		IObservableValue observeTextAshostText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(ashostText2);
		IObservableValue managedConnectionFactoryAshostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__ASHOST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextAshostText2ObserveWidget, managedConnectionFactoryAshostObserveValue, null, null);
		//
		IObservableValue observeTextMshostTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(mshostText);
		IObservableValue managedConnectionFactoryMshostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MSHOST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextMshostTextObserveWidget, managedConnectionFactoryMshostObserveValue, null, null);
		//
		IObservableValue observeTextMsservTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(msservText);
		IObservableValue managedConnectionFactoryMsservObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MSSERV)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextMsservTextObserveWidget, managedConnectionFactoryMsservObserveValue, null, null);
		//
		IObservableValue observeTextGwhostTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(gwhostText);
		IObservableValue managedConnectionFactoryGwhostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GWHOST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextGwhostTextObserveWidget, managedConnectionFactoryGwhostObserveValue, null, null);
		//
		IObservableValue observeTextGwservTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(gwservText);
		IObservableValue managedConnectionFactoryGwservObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GWSERV)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextGwservTextObserveWidget, managedConnectionFactoryGwservObserveValue, null, null);
		//
		IObservableValue observeTextR3nameTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(r3nameText);
		IObservableValue managedConnectionFactoryR3nameObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__R3NAME)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextR3nameTextObserveWidget, managedConnectionFactoryR3nameObserveValue, null, null);
		//
		IObservableValue observeTextGroupTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(groupText);
		IObservableValue managedConnectionFactoryGroupObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GROUP)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextGroupTextObserveWidget, managedConnectionFactoryGroupObserveValue, null, null);

		saprouterDecorator = ControlDecorationSupport.create(saprouterBinding, SWT.TOP | SWT.LEFT);
		sysnrDecorator = ControlDecorationSupport.create(sysnrBinding2, SWT.TOP | SWT.LEFT);

		return bindingContext;
	}	

}
