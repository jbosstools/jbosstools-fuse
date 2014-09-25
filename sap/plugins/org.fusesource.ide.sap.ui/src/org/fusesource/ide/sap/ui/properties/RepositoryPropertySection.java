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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.converter.Boolean2StringConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.util.LayoutUtil;

@SuppressWarnings("restriction")
public class RepositoryPropertySection extends DestinationDataPropertySection {

	private Text repositoryDestinationText;
	private Text repositoryUserText;
	private Text repositoryPasswordText;
	private Button respositorySncBtn;
	private Button repositoryRoundtripOptimizationBtn;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite repositoryContainer = createFlatFormComposite(parent);
		
		repositoryDestinationText = getWidgetFactory().createText(repositoryContainer, "", SWT.BORDER); //$NON-NLS-1$
		repositoryDestinationText.setToolTipText(Messages.RepositoryPropertySection_RepositoryDestinationToolTip);
		repositoryDestinationText.setLayoutData(LayoutUtil.firstEntryLayoutData());
		
		CLabel repositoryDestinationLbl = getWidgetFactory().createCLabel(repositoryContainer, Messages.RepositoryPropertySection_RepositoryDestinationLabel, SWT.NONE);
		repositoryDestinationLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryDestinationText));
		repositoryDestinationLbl.setAlignment(SWT.RIGHT);

		repositoryUserText = getWidgetFactory().createText(repositoryContainer, "", SWT.BORDER); //$NON-NLS-1$
		repositoryUserText.setToolTipText(Messages.RepositoryPropertySection_RepositoryUserToolTip);
		repositoryUserText.setLayoutData(LayoutUtil.entryLayoutData(repositoryDestinationText));
		
		CLabel repositoryUserLbl = getWidgetFactory().createCLabel(repositoryContainer, Messages.RepositoryPropertySection_RepositoryUserLabel, SWT.NONE);
		repositoryUserLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryUserText));
		repositoryUserLbl.setAlignment(SWT.RIGHT);

		repositoryPasswordText = getWidgetFactory().createText(repositoryContainer, "", SWT.BORDER | SWT.PASSWORD); //$NON-NLS-1$
		repositoryPasswordText.setToolTipText(Messages.RepositoryPropertySection_RepositoryPasswordToolTip);
		repositoryPasswordText.setLayoutData(LayoutUtil.entryLayoutData(repositoryUserText));
		
		CLabel repositoryPasswordLbl = getWidgetFactory().createCLabel(repositoryContainer, Messages.RepositoryPropertySection_RepositoryPasswordLabel, SWT.NONE);
		repositoryPasswordLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryPasswordText));
		repositoryPasswordLbl.setAlignment(SWT.RIGHT);

		respositorySncBtn = getWidgetFactory().createButton(repositoryContainer, Messages.RepositoryPropertySection_RepositorySncLabel, SWT.CHECK);
		respositorySncBtn.setToolTipText(Messages.RepositoryPropertySection_RepositorySncToolTip);
		respositorySncBtn.setLayoutData(LayoutUtil.entryLayoutData(repositoryPasswordText));
		
		repositoryRoundtripOptimizationBtn = getWidgetFactory().createButton(repositoryContainer, Messages.RepositoryPropertySection_RepositoryRoundtripOptimizationLabel, SWT.CHECK);
		repositoryRoundtripOptimizationBtn.setToolTipText(Messages.RepositoryPropertySection_RepositoryRoundtripOptimizationToolTip);
		repositoryRoundtripOptimizationBtn.setLayoutData(LayoutUtil.entryLayoutData(respositorySncBtn));
		
	}
	
	protected DataBindingContext initDataBindings() {
		
		DataBindingContext bindingContext = super.initDataBindings();
		
		//
		IObservableValue observeTextRepositoryDestinationTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryDestinationText);
		IObservableValue managedConnectionFactoryRepositoryDestObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_DEST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextRepositoryDestinationTextObserveWidget, managedConnectionFactoryRepositoryDestObserveValue, null, null);
		//
		IObservableValue observeTextRepositoryUserTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryUserText);
		IObservableValue managedConnectionFactoryRepositoryUserObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_USER)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextRepositoryUserTextObserveWidget, managedConnectionFactoryRepositoryUserObserveValue, null, null);
		//
		IObservableValue observeTextRepositoryPasswordTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryPasswordText);
		IObservableValue managedConnectionFactoryRepositoryPasswdObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_PASSWD)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextRepositoryPasswordTextObserveWidget, managedConnectionFactoryRepositoryPasswdObserveValue, null, null);
		//
		IObservableValue observeSelectionRespositorySncBtnObserveWidget = WidgetProperties.selection().observe(respositorySncBtn);
		IObservableValue managedConnectionFactoryRepositorySncObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_SNC)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_15 = new UpdateValueStrategy();
		strategy_15.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy repositorySncModelStrategy = new UpdateValueStrategy();
		repositorySncModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionRespositorySncBtnObserveWidget, managedConnectionFactoryRepositorySncObserveValue, strategy_15, repositorySncModelStrategy);
		//
		IObservableValue observeSelectionRepositoryRoundtripOptimizationBtnObserveWidget = WidgetProperties.selection().observe(repositoryRoundtripOptimizationBtn);
		IObservableValue managedConnectionFactoryRepositoryRoundtripOptimizationObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_ROUNDTRIP_OPTIMIZATION)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_16 = new UpdateValueStrategy();
		strategy_16.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy repositoryRoundtripOptimizationModelStrategy = new UpdateValueStrategy();
		repositoryRoundtripOptimizationModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionRepositoryRoundtripOptimizationBtnObserveWidget, managedConnectionFactoryRepositoryRoundtripOptimizationObserveValue, strategy_16, repositoryRoundtripOptimizationModelStrategy);

		return bindingContext;
	}	

}
