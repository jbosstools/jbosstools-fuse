/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.editor.wizards;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.editor.wizards.pages.DataFormatPropertiesPage;
import org.fusesource.ide.camel.editor.wizards.pages.DataFormatSelectionPage;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormatModel;
import org.w3c.dom.Element;

/**
 * @author lhein
 */
public class NewDataFormatWizard extends Wizard implements GlobalConfigurationTypeWizard {

	private DataBindingContext dbc = new DataBindingContext();
    private IObservableMap modelMap = new WritableMap();
	
    private DataFormatModel dfModel;
    
	private Element dataformat;
	
	private DataFormatSelectionPage dataFormatSelectionPage;
	private DataFormatPropertiesPage dataFormatPropertiesPage;

	/**
	 * 
	 */
	public NewDataFormatWizard() {
		dfModel = CamelModelFactory.getModelForVersion(org.fusesource.ide.camel.editor.utils.CamelUtils.getCurrentProjectCamelVersion()).getDataformatModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		setWindowTitle(UIMessages.newGlobalConfigurationTypeDataFormatWizardDialogTitle);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		this.dataFormatSelectionPage = new DataFormatSelectionPage(this, "dataFormatSelectionPage");
		this.dataFormatPropertiesPage = new DataFormatPropertiesPage(this, "dataFormatPropertiesPage");
		super.addPage(dataFormatSelectionPage);
		super.addPage(dataFormatPropertiesPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		this.dataformat = null;
		return super.performCancel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO: create the dataformat out of the information gathered in the wizard pages
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return super.canFinish() && isValid();
	}

	private boolean isValid() {
		return dataFormatSelectionPage.isValid() && dataFormatPropertiesPage.isValid();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard#getGlobalConfigrationElementNode()
	 */
	@Override
	public Element getGlobalConfigurationElementNode() {
		return this.dataformat;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard#setGlobalConfigrationElementNode(org.w3c.dom.Node)
	 */
	@Override
	public void setGlobalConfigurationElementNode(Element node) {
		this.dataformat = node;
	}
	
	public DataBindingContext getDatabindingContext() {
		return this.dbc;
	}
	
	public IObservableMap getObservableMap() {
		return this.modelMap;
	}
	
	public void observeWidget(Combo widget, String propName, IValidator validator) {
		ISWTObservableValue uiObservable = null;
        IObservableValue modelObservable = null;
		
        //initialize the map entry
        modelMap.put(propName, widget.getSelection());
        // create observables for the control
        uiObservable = WidgetProperties.selection().observe(widget);	
        
		// create UpdateValueStrategy and assign to the binding
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(validator);
        
        // create observables for the Map entries
        modelObservable = Observables.observeMapEntry(modelMap, propName);
        // bind the observables
        Binding bindValue = dbc.bindValue(uiObservable, modelObservable, strategy, null);
        ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT); 
	}
	
	public void observeWidget(Text widget, String propName, IValidator validator) {
		ISWTObservableValue uiObservable = null;
        IObservableValue modelObservable = null;
		
        //initialize the map entry
        modelMap.put(propName, widget.getText());
        // create observables for the control
        uiObservable = WidgetProperties.text(SWT.Modify).observe(widget);	
        
		// create UpdateValueStrategy and assign to the binding
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(validator);
        
        // create observables for the Map entries
        modelObservable = Observables.observeMapEntry(modelMap, propName);
        // bind the observables
        Binding bindValue = dbc.bindValue(uiObservable, modelObservable, strategy, null);
        ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT); 
	}
	
	public DataFormatModel getDataFormatModel() {
		return this.dfModel;
	}
}
