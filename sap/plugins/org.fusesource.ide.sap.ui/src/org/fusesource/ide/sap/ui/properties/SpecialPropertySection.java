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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.converter.Boolean2StringConverter;
import org.fusesource.ide.sap.ui.converter.CpicTraceComboSelection2TraceLevelConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.converter.TraceLevel2CpicTraceComboSelectionConverter;
import org.fusesource.ide.sap.ui.util.LayoutUtil;

@SuppressWarnings("restriction")
public class SpecialPropertySection extends DestinationDataPropertySection {

	private Button traceBtn;
	private CCombo cpicTraceCombo;
	private Button lcheckBtn;
	private Text codepageText;
	private Button getsso2Btn;
	private Button denyInitialPasswordBtn;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite specialContainer = createFlatFormComposite(parent);
		
		traceBtn = getWidgetFactory().createButton(specialContainer, Messages.SpecialPropertySection_TraceLabel, SWT.CHECK); 
		traceBtn.setToolTipText(Messages.SpecialPropertySection_TraceToolTip);
		traceBtn.setLayoutData(LayoutUtil.firstEntryLayoutData());

		cpicTraceCombo = getWidgetFactory().createCCombo(specialContainer, SWT.READ_ONLY);
		cpicTraceCombo.setItems(new String[] {"", Messages.SpecialPropertySection_CpicTraceLevel0Label, Messages.SpecialPropertySection_CpicTraceLevel1Label, Messages.SpecialPropertySection_CpicTraceLevel2Label, Messages.SpecialPropertySection_CpicTraceLevel3Label}); //$NON-NLS-1$
		cpicTraceCombo.setToolTipText(Messages.SpecialPropertySection_CpicTraceToolTip);
		cpicTraceCombo.setLayoutData(LayoutUtil.entryLayoutData(traceBtn));
		cpicTraceCombo.select(0);

		CLabel sysnrLbl2 = getWidgetFactory().createCLabel(specialContainer, Messages.SpecialPropertySection_SysnrLabel, SWT.NONE);
		sysnrLbl2.setLayoutData(LayoutUtil.labelLayoutData(cpicTraceCombo));
		sysnrLbl2.setAlignment(SWT.RIGHT);
		
		lcheckBtn = getWidgetFactory().createButton(specialContainer, Messages.SpecialPropertySection_LcheckLabel, SWT.CHECK);
		lcheckBtn.setToolTipText(Messages.SpecialPropertySection_LcheckToolTip);
		lcheckBtn.setLayoutData(LayoutUtil.entryLayoutData(cpicTraceCombo));
		lcheckBtn.setText(Messages.SpecialPropertySection_LcheckLabel);
		
		codepageText = getWidgetFactory().createText(specialContainer, "", SWT.BORDER); //$NON-NLS-1$
		codepageText.setToolTipText(Messages.SpecialPropertySection_CodepageToolTip);
		codepageText.setLayoutData(LayoutUtil.entryLayoutData(lcheckBtn));
		
		CLabel codepageLbl = getWidgetFactory().createCLabel(specialContainer, Messages.SpecialPropertySection_CodepageLabel, SWT.NONE);
		codepageLbl.setLayoutData(LayoutUtil.labelLayoutData(codepageText));
		codepageLbl.setAlignment(SWT.RIGHT);
		
		getsso2Btn = getWidgetFactory().createButton(specialContainer, Messages.SpecialPropertySection_Getsso2Label, SWT.CHECK);
		getsso2Btn.setToolTipText(Messages.SpecialPropertySection_Getsso2ToolTip);
		getsso2Btn.setLayoutData(LayoutUtil.entryLayoutData(codepageText));
		
		denyInitialPasswordBtn = getWidgetFactory().createButton(specialContainer, Messages.SpecialPropertySection_DenyInitialPasswordLabel, SWT.CHECK);
		denyInitialPasswordBtn.setToolTipText(Messages.SpecialPropertySection_DenyInitialPasswordToolTip);
		denyInitialPasswordBtn.setLayoutData(LayoutUtil.entryLayoutData(codepageText));
		
	}
	
	protected DataBindingContext initDataBindings() {
		
		DataBindingContext bindingContext = super.initDataBindings();
		
		//
		IObservableValue observeSelectionTraceBtnObserveWidget = WidgetProperties.selection().observe(traceBtn);
		IObservableValue managedConnectionFactoryTraceObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__TRACE)).observe(destinationDataStoreEntry);
		UpdateValueStrategy traceStrategy = new UpdateValueStrategy();
		traceStrategy.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy traceModelStrategy = new UpdateValueStrategy();
		traceModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionTraceBtnObserveWidget, managedConnectionFactoryTraceObserveValue, traceStrategy, traceModelStrategy);
		//
		IObservableValue observeSelectionCpicTraceComboObserveWidget = WidgetProperties.singleSelectionIndex().observe(cpicTraceCombo);
		IObservableValue managedConnectionFactoryCpicTraceObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CPIC_TRACE)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new CpicTraceComboSelection2TraceLevelConverter());
		UpdateValueStrategy cpicTraceStrategy = new UpdateValueStrategy();
		cpicTraceStrategy.setConverter(new TraceLevel2CpicTraceComboSelectionConverter());
		bindingContext.bindValue(observeSelectionCpicTraceComboObserveWidget, managedConnectionFactoryCpicTraceObserveValue, strategy_4, cpicTraceStrategy);
		//
		IObservableValue observeSelectionLcheckBtnObserveWidget = WidgetProperties.selection().observe(lcheckBtn);
		IObservableValue managedConnectionFactoryLcheckObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__LCHECK)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy lcheckModelStrategy = new UpdateValueStrategy();
		lcheckModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionLcheckBtnObserveWidget, managedConnectionFactoryLcheckObserveValue, strategy_5, lcheckModelStrategy);
		//
		IObservableValue observeTextCodepageTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(codepageText);
		IObservableValue managedConnectionFactoryCodepageObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CODEPAGE)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextCodepageTextObserveWidget, managedConnectionFactoryCodepageObserveValue, null, null);
		//
		IObservableValue observeSelectionGetsso2BtnObserveWidget = WidgetProperties.selection().observe(getsso2Btn);
		IObservableValue managedConnectionFactoryGetsso2ObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GETSSO2)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy getssoModelStrategy = new UpdateValueStrategy();
		getssoModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionGetsso2BtnObserveWidget, managedConnectionFactoryGetsso2ObserveValue, strategy_6, getssoModelStrategy);
		//
		IObservableValue observeSelectionDenyInitialPasswordBtnObserveWidget = WidgetProperties.selection().observe(denyInitialPasswordBtn);
		IObservableValue managedConnectionFactoryDenyInitialPasswordObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__DENY_INITIAL_PASSWORD)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy denyInitialPasswordModelStrategy = new UpdateValueStrategy();
		denyInitialPasswordModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionDenyInitialPasswordBtnObserveWidget, managedConnectionFactoryDenyInitialPasswordObserveValue, strategy_7, denyInitialPasswordModelStrategy);

		return bindingContext;
	}	
}
