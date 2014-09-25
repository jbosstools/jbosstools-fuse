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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.converter.Boolean2StringConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.NonNegativeIntegerValidator;
import org.fusesource.ide.sap.ui.validator.SapRouterStringValidator;

@SuppressWarnings("restriction")
public class OptionalServerPropertySection extends ServerDataPropertySection {

	private Button traceBtn;
	private Text saprouterText;
	private Text workerThreadCountText;
	private Text workerThreadMinCountText;
	private Text maxStartupDelayText;
	private Text repositoryMapText;
	
	
	private Binding workerThreadCountBinding;
	private Binding workerThreadMinCountBinding;
	private Binding maxStartupDelayBinding;
	
	protected ControlDecorationSupport saprouterDecorator;
	protected ControlDecorationSupport workerThreadCountDecorator;
	protected ControlDecorationSupport workerThreadMinCountDecorator;
	protected ControlDecorationSupport maxStartupDelayDecorator;

	private Binding saprouterBinding;

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite optionalContainer = createFlatFormComposite(parent);
		
		traceBtn = getWidgetFactory().createButton(optionalContainer, Messages.OptionalServerPropertySection_TraceLabel, SWT.CHECK); 
		traceBtn.setToolTipText(Messages.OptionalServerPropertySection_TraceToolTip);
		traceBtn.setLayoutData(LayoutUtil.firstEntryLayoutData());

		saprouterText = getWidgetFactory().createText(optionalContainer, "", SWT.BORDER); //$NON-NLS-1$
		saprouterText.setToolTipText(Messages.OptionalServerPropertySection_SaprouterToolTip);
		saprouterText.setLayoutData(LayoutUtil.entryLayoutData(traceBtn));

		CLabel saprouterLbl = getWidgetFactory().createCLabel(optionalContainer, Messages.OptionalServerPropertySection_SaprouterLabel, SWT.NONE);
		saprouterLbl.setLayoutData(LayoutUtil.labelLayoutData(saprouterText));
		saprouterLbl.setAlignment(SWT.RIGHT);
		
		workerThreadCountText = getWidgetFactory().createText(optionalContainer, "", SWT.BORDER); //$NON-NLS-1$
		workerThreadCountText.setToolTipText(Messages.OptionalServerPropertySection_WorkerThreadCountToolTip);
		workerThreadCountText.setLayoutData(LayoutUtil.entryLayoutData(saprouterText));

		CLabel workerThreadCountLbl = getWidgetFactory().createCLabel(optionalContainer, Messages.OptionalServerPropertySection_WorkerThreadCountLabel, SWT.NONE);
		workerThreadCountLbl.setLayoutData(LayoutUtil.labelLayoutData(workerThreadCountText));
		workerThreadCountLbl.setAlignment(SWT.RIGHT);
		
		workerThreadMinCountText = getWidgetFactory().createText(optionalContainer, "", SWT.BORDER); //$NON-NLS-1$
		workerThreadMinCountText.setToolTipText(Messages.OptionalServerPropertySection_WorkerThreadMinCountToolTip);
		workerThreadMinCountText.setLayoutData(LayoutUtil.entryLayoutData(workerThreadCountText));

		CLabel workerThreadMinCountLbl = getWidgetFactory().createCLabel(optionalContainer, Messages.OptionalServerPropertySection_WorkerThreadMinCountLabel, SWT.NONE);
		workerThreadMinCountLbl.setLayoutData(LayoutUtil.labelLayoutData(workerThreadMinCountText));
		workerThreadMinCountLbl.setAlignment(SWT.RIGHT);
		
		maxStartupDelayText = getWidgetFactory().createText(optionalContainer, "", SWT.BORDER); //$NON-NLS-1$
		maxStartupDelayText.setToolTipText(Messages.OptionalServerPropertySection_MaxStartupDelayToolTip);
		maxStartupDelayText.setLayoutData(LayoutUtil.entryLayoutData(workerThreadMinCountText));

		CLabel maxStartupDelayLbl = getWidgetFactory().createCLabel(optionalContainer, Messages.OptionalServerPropertySection_MaxStartupDelayLabel, SWT.NONE);
		maxStartupDelayLbl.setLayoutData(LayoutUtil.labelLayoutData(maxStartupDelayText));
		maxStartupDelayLbl.setAlignment(SWT.RIGHT);
		
		repositoryMapText = getWidgetFactory().createText(optionalContainer, "", SWT.BORDER); //$NON-NLS-1$
		repositoryMapText.setToolTipText(Messages.OptionalServerPropertySection_RepoistoryMapToolTip);
		repositoryMapText.setLayoutData(LayoutUtil.entryLayoutData(maxStartupDelayText));

		CLabel repositoryMapLbl = getWidgetFactory().createCLabel(optionalContainer, Messages.OptionalServerPropertySection_RepoistoryMapLabel, SWT.NONE);
		repositoryMapLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryMapText));
		repositoryMapLbl.setAlignment(SWT.RIGHT);
		
	}
	
	protected DataBindingContext initDataBindings() {
		
		DataBindingContext bindingContext = super.initDataBindings();
		
		//
		IObservableValue observeSelectionTraceBtnObserveWidget = WidgetProperties.selection().observe(traceBtn);
		IObservableValue managedConnectionFactoryTraceObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__TRACE)).observe(serverDataStoreEntry);
		UpdateValueStrategy traceStrategy = new UpdateValueStrategy();
		traceStrategy.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy traceModelStrategy = new UpdateValueStrategy();
		traceModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionTraceBtnObserveWidget, managedConnectionFactoryTraceObserveValue, traceStrategy, traceModelStrategy);
		//
		IObservableValue observeTextSapRouterTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(saprouterText);
		IObservableValue serverDataSapRouterObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SAPROUTER)).observe(serverDataStoreEntry);
		UpdateValueStrategy sapRouterStrategy = new UpdateValueStrategy();
		sapRouterStrategy.setBeforeSetValidator(new SapRouterStringValidator());
		saprouterBinding = bindingContext.bindValue(observeTextSapRouterTextObserveWidget, serverDataSapRouterObserveValue, sapRouterStrategy, null);
		//
		IObservableValue observeTextWorkerThreadCountTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(workerThreadCountText);
		IObservableValue serverDataWorkerThreadCountObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__WORKER_THREAD_COUNT)).observe(serverDataStoreEntry);
		UpdateValueStrategy workerThreadCountStrategy = new UpdateValueStrategy();
		workerThreadCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_WorkerThreadCountValidator));
		workerThreadCountBinding = bindingContext.bindValue(observeTextWorkerThreadCountTextObserveWidget, serverDataWorkerThreadCountObserveValue, workerThreadCountStrategy, null);
		//
		IObservableValue observeTextWorkerThreadMinCountTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(workerThreadMinCountText);
		IObservableValue serverDataWorkerThreadMinCountObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__WORKER_THREAD_MIN_COUNT)).observe(serverDataStoreEntry);
		UpdateValueStrategy workerThreadMinCountStrategy = new UpdateValueStrategy();
		workerThreadMinCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_WorkerThreadMinCountValidator));
		workerThreadMinCountBinding = bindingContext.bindValue(observeTextWorkerThreadMinCountTextObserveWidget, serverDataWorkerThreadMinCountObserveValue, workerThreadMinCountStrategy, null);
		//
		IObservableValue observeTextMaxStartupDelayTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(maxStartupDelayText);
		IObservableValue serverDataMaxStartupDelayObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__MAX_START_UP_DELAY)).observe(serverDataStoreEntry);
		UpdateValueStrategy maxStartupDelayStrategy = new UpdateValueStrategy();
		maxStartupDelayStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_MaxStartupDelayValidator));
		maxStartupDelayBinding = bindingContext.bindValue(observeTextMaxStartupDelayTextObserveWidget, serverDataMaxStartupDelayObserveValue, maxStartupDelayStrategy, null);
		//
		IObservableValue observeRepositoryMapTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryMapText);
		IObservableValue serverRepositoryMapObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__REPOSITORY_MAP)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeRepositoryMapTextObserveWidget, serverRepositoryMapObserveValue, null, null);

		saprouterDecorator = ControlDecorationSupport.create(saprouterBinding, SWT.TOP | SWT.LEFT);		
		workerThreadCountDecorator = ControlDecorationSupport.create(workerThreadCountBinding, SWT.TOP | SWT.LEFT);
		workerThreadMinCountDecorator = ControlDecorationSupport.create(workerThreadMinCountBinding, SWT.TOP | SWT.LEFT);
		maxStartupDelayDecorator = ControlDecorationSupport.create(maxStartupDelayBinding, SWT.TOP | SWT.LEFT);

		return bindingContext;
	}	

}
