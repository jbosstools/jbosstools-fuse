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
import org.fusesource.ide.sap.ui.validator.NonNegativeIntegerValidator;

@SuppressWarnings("restriction")
public class PoolPropertySection extends DestinationDataPropertySection {
	
	protected ControlDecorationSupport peakLimitDecorator;
	protected ControlDecorationSupport poolCapacityDecorator;
	protected ControlDecorationSupport expirationTimeDecorator;
	protected ControlDecorationSupport expirationPeriodDecorator;
	protected ControlDecorationSupport maxGetTimeDecorator;

	private Binding peakLimitBinding;
	private Binding poolCapacityBinding;
	private Binding expirationTimeBinding;
	private Binding expirationPeriodBinding;
	private Binding maxGetTimeBinding;

	private Text peakLimitText;
	private Text poolCapacityText;
	private Text expirationTimeText;
	private Text expirationCheckPeriodText;
	private Text maxGetClientTimeText;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite poolContainer = createFlatFormComposite(parent);

		peakLimitText = getWidgetFactory().createText(poolContainer, "", SWT.NONE); //$NON-NLS-1$
		peakLimitText.setToolTipText(Messages.PoolPropertySection_PeakLimitToolTip);
		peakLimitText.setLayoutData(LayoutUtil.firstEntryLayoutData());

		CLabel peakLimitLbl = getWidgetFactory().createCLabel(poolContainer, Messages.PoolPropertySection_PeakLimitLabel, SWT.NONE);
		peakLimitLbl.setLayoutData(LayoutUtil.labelLayoutData(peakLimitText));
		peakLimitLbl.setAlignment(SWT.RIGHT);
		
		poolCapacityText = getWidgetFactory().createText(poolContainer, "",	SWT.NONE); //$NON-NLS-1$
		poolCapacityText.setToolTipText(Messages.PoolPropertySection_PoolCapacityToolTip);
		poolCapacityText.setLayoutData(LayoutUtil.entryLayoutData(peakLimitText));

		CLabel poolCapacityLbl = getWidgetFactory().createCLabel(poolContainer, Messages.PoolPropertySection_PoolCapacityLabel, SWT.NONE);
		poolCapacityLbl.setLayoutData(LayoutUtil.labelLayoutData(poolCapacityText));
		poolCapacityLbl.setAlignment(SWT.RIGHT);
		
		expirationTimeText = getWidgetFactory().createText(poolContainer, "", SWT.NONE); //$NON-NLS-1$
		expirationTimeText.setToolTipText(Messages.PoolPropertySection_ExpirationTimeToolTip);
		expirationTimeText.setLayoutData(LayoutUtil.entryLayoutData(poolCapacityText));

		CLabel expirationTimeLbl = getWidgetFactory().createCLabel(poolContainer, Messages.PoolPropertySection_ExpirationTimeLabel, SWT.NONE);
		expirationTimeLbl.setLayoutData(LayoutUtil.labelLayoutData(expirationTimeText));
		expirationTimeLbl.setAlignment(SWT.RIGHT);
		
		expirationCheckPeriodText = getWidgetFactory().createText(poolContainer, "", SWT.NONE); //$NON-NLS-1$
		expirationCheckPeriodText.setToolTipText(Messages.PoolPropertySection_ExpirationCheckPeriodToolTip);
		expirationCheckPeriodText.setLayoutData(LayoutUtil.entryLayoutData(expirationTimeText));

		CLabel expirationCheckPeriodLbl = getWidgetFactory().createCLabel(poolContainer, Messages.PoolPropertySection_ExpirationCheckPeriodLabel, SWT.NONE);
		expirationCheckPeriodLbl.setLayoutData(LayoutUtil.labelLayoutData(expirationCheckPeriodText));
		expirationCheckPeriodLbl.setAlignment(SWT.RIGHT);
		
		maxGetClientTimeText = getWidgetFactory().createText(poolContainer, "",	SWT.NONE); //$NON-NLS-1$
		maxGetClientTimeText.setToolTipText(Messages.PoolPropertySection_MaxGetClientTimeToolTip);
		maxGetClientTimeText.setLayoutData(LayoutUtil.entryLayoutData(expirationCheckPeriodText));

		CLabel maxGetClientTimeLbl = getWidgetFactory().createCLabel(poolContainer, Messages.PoolPropertySection_MaxGetClientTimeLabel, SWT.NONE);
		maxGetClientTimeLbl.setLayoutData(LayoutUtil.labelLayoutData(maxGetClientTimeText));
		maxGetClientTimeLbl.setAlignment(SWT.RIGHT);
		
	}

	protected DataBindingContext initDataBindings() {
		
		DataBindingContext bindingContext = super.initDataBindings();

		//
		IObservableValue observeTextPeakLimitTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(peakLimitText);
		IObservableValue managedConnectionFactoryPeakLimitObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__PEAK_LIMIT)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
		strategy_8.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_PeakLimitValidator));
		peakLimitBinding = bindingContext.bindValue(observeTextPeakLimitTextObserveWidget, managedConnectionFactoryPeakLimitObserveValue, strategy_8, null);
		//
		IObservableValue observeTextPoolCapacityTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(poolCapacityText);
		IObservableValue managedConnectionFactoryPoolCapacityObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__POOL_CAPACITY)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
		strategy_9.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_PoolCapacityValidator));
		poolCapacityBinding = bindingContext.bindValue(observeTextPoolCapacityTextObserveWidget, managedConnectionFactoryPoolCapacityObserveValue, strategy_9, null);
		//
		IObservableValue observeTextExpirationTimeTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(expirationTimeText);
		IObservableValue managedConnectionFactoryExpirationTimeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__EXPIRATION_TIME)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
		strategy_10.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_ExpirationTimeValidator));
		expirationTimeBinding = bindingContext.bindValue(observeTextExpirationTimeTextObserveWidget, managedConnectionFactoryExpirationTimeObserveValue, strategy_10, null);
		//
		IObservableValue observeTextExpirationCheckPeriodTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(expirationCheckPeriodText);
		IObservableValue managedConnectionFactoryExpirationPeriodObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__EXPIRATION_PERIOD)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
		strategy_11.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_ExpirationCheckPeriodValidator));
		expirationPeriodBinding = bindingContext.bindValue(observeTextExpirationCheckPeriodTextObserveWidget, managedConnectionFactoryExpirationPeriodObserveValue, strategy_11, null);
		//
		IObservableValue observeTextMaxGetClientTimeTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(maxGetClientTimeText);
		IObservableValue managedConnectionFactoryMaxGetTimeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MAX_GET_TIME)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_12 = new UpdateValueStrategy();
		strategy_12.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_MaxGetClientTimeValidator));
		maxGetTimeBinding = bindingContext.bindValue(observeTextMaxGetClientTimeTextObserveWidget, managedConnectionFactoryMaxGetTimeObserveValue, strategy_12, null);
		
		peakLimitDecorator = ControlDecorationSupport.create(peakLimitBinding, SWT.TOP | SWT.LEFT);
		poolCapacityDecorator = ControlDecorationSupport.create(poolCapacityBinding, SWT.TOP | SWT.LEFT);
		expirationTimeDecorator = ControlDecorationSupport.create(expirationTimeBinding, SWT.TOP | SWT.LEFT);
		expirationPeriodDecorator = ControlDecorationSupport.create(expirationPeriodBinding, SWT.TOP | SWT.LEFT);
		maxGetTimeDecorator = ControlDecorationSupport.create(maxGetTimeBinding, SWT.TOP | SWT.LEFT);

		return bindingContext;
	}	
	
}
