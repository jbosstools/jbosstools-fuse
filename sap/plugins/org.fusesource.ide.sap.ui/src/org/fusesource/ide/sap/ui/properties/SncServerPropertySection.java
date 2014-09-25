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
import org.fusesource.ide.sap.ui.converter.SncQos2SncQosComboSelectionConverter;
import org.fusesource.ide.sap.ui.converter.SncQosComboSelection2SncQosConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.util.LayoutUtil;

@SuppressWarnings("restriction")
public class SncServerPropertySection extends ServerDataPropertySection {

	private Button sncModeBtn;
	private CCombo sncQopCombo;
	private Text sncMynameText;
	private Text sncLibraryText;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite sncContainer = createFlatFormComposite(parent);

		sncModeBtn = getWidgetFactory().createButton(sncContainer, Messages.SncServerPropertySection_SncModeLabel, SWT.CHECK);
		sncModeBtn.setToolTipText(Messages.SncServerPropertySection_SncModeToolTip);
		sncModeBtn.setLayoutData(LayoutUtil.firstEntryLayoutData());
		
		sncQopCombo = getWidgetFactory().createCCombo(sncContainer, SWT.READ_ONLY);
		sncQopCombo.setToolTipText(Messages.SncServerPropertySection_SncQopToolTip);
		sncQopCombo.setItems(new String[] {"", Messages.SncServerPropertySection_SncSecurityLevel1Label, Messages.SncServerPropertySection_SncSecurityLevel2Label, Messages.SncServerPropertySection_SncSecurityLevel3Label, Messages.SncServerPropertySection_SncSecurityLevel8Label, Messages.SncServerPropertySection_SncSecurityLevel9Label}); //$NON-NLS-1$
		sncQopCombo.setLayoutData(LayoutUtil.entryLayoutData(sncModeBtn));
		sncQopCombo.select(0);
		
		CLabel sncQopLbl = getWidgetFactory().createCLabel(sncContainer, Messages.SncServerPropertySection_SncQopLabel, SWT.NONE);
		sncQopLbl.setLayoutData(LayoutUtil.labelLayoutData(sncQopCombo));
		sncQopLbl.setAlignment(SWT.RIGHT);

		sncMynameText = getWidgetFactory().createText(sncContainer, "", SWT.BORDER); //$NON-NLS-1$
		sncMynameText.setToolTipText(Messages.SncServerPropertySection_SncMynameToolTip);
		sncMynameText.setLayoutData(LayoutUtil.entryLayoutData(sncQopCombo));
		
		CLabel sncMynameLbl = getWidgetFactory().createCLabel(sncContainer, Messages.SncServerPropertySection_SncMynameLabel, SWT.NONE);
		sncMynameLbl.setLayoutData(LayoutUtil.labelLayoutData(sncMynameText));
		sncMynameLbl.setAlignment(SWT.RIGHT);

		sncLibraryText = getWidgetFactory().createText(sncContainer, "", SWT.BORDER); //$NON-NLS-1$
		sncLibraryText.setToolTipText(Messages.SncServerPropertySection_SncLibraryToolTip);
		sncLibraryText.setLayoutData(LayoutUtil.entryLayoutData(sncMynameText));

		CLabel sncLibraryLbl = getWidgetFactory().createCLabel(sncContainer, Messages.SncServerPropertySection_SncLibraryLabel, SWT.NONE);
		sncLibraryLbl.setLayoutData(LayoutUtil.labelLayoutData(sncLibraryText));
		sncLibraryLbl.setAlignment(SWT.RIGHT);

	}
	
	protected DataBindingContext initDataBindings() {
		
		DataBindingContext bindingContext = super.initDataBindings();
		
		//
		IObservableValue observeSelectionSncModeBtnObserveWidget = WidgetProperties.selection().observe(sncModeBtn);
		IObservableValue managedConnectionFactorySncModeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_MODE)).observe(serverDataStoreEntry);
		UpdateValueStrategy strategy_13 = new UpdateValueStrategy();
		strategy_13.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy sncModeModelStrategy = new UpdateValueStrategy();
		sncModeModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionSncModeBtnObserveWidget, managedConnectionFactorySncModeObserveValue, strategy_13, sncModeModelStrategy);
		//
		IObservableValue observeSingleSelectionIndexSncQopComboObserveWidget = WidgetProperties.singleSelectionIndex().observe(sncQopCombo);
		IObservableValue managedConnectionFactorySncQopObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_QOP)).observe(serverDataStoreEntry);
		UpdateValueStrategy sncQopStategy = new UpdateValueStrategy();
		sncQopStategy.setConverter(new SncQosComboSelection2SncQosConverter());
		UpdateValueStrategy sncQopModelStrategy = new UpdateValueStrategy();
		sncQopModelStrategy.setConverter(new SncQos2SncQosComboSelectionConverter());
		bindingContext.bindValue(observeSingleSelectionIndexSncQopComboObserveWidget, managedConnectionFactorySncQopObserveValue, sncQopStategy, sncQopModelStrategy);
		//
		IObservableValue observeTextSncMynameTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sncMynameText);
		IObservableValue managedConnectionFactorySncMynameObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_MYNAME)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextSncMynameTextObserveWidget, managedConnectionFactorySncMynameObserveValue, null, null);
		//
		IObservableValue observeTextSncLibraryTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sncLibraryText);
		IObservableValue managedConnectionFactorySncLibraryObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_LIB)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextSncLibraryTextObserveWidget, managedConnectionFactorySncLibraryObserveValue, null, null);

		return bindingContext;
	}	

}
