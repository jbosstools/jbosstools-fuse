/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.pages;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;

/**
 * @author lhein
 *
 */
public class DataFormatSelectionPage extends WizardPage {

	private CamelModel model;
	private DataFormat dataFormatSelected;
	private String id;
	private DataBindingContext dbc;
	
	/**
	 * @param pageName
	 */
	public DataFormatSelectionPage(CamelModel model) {
		super("Dataformat selection page"); //$NON-NLS-1$
		setTitle(UIMessages.dataFormatSelectionPageDataFormatSelectionPageTitle);
		setDescription(UIMessages.dataFormatSelectionPageDataFormatSelectionPageDescription);
		this.model = model;
		this.dbc = new DataBindingContext();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		GridLayout gl = new GridLayout(2, false);

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(gl);
		
		createDataFormatSelectionLine(container);
		createIdLine(container);

		setControl(container);
		WizardPageSupport.create(this, dbc);
	}

	/**
	 * @param container
	 */
	private void createDataFormatSelectionLine(Composite container) {
		Label l = new Label(container, SWT.NONE);
		l.setText(UIMessages.dataFormatSelectionPageDataformatLabel);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		
		ComboViewer dataformatComboViewer = new ComboViewer(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		dataformatComboViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		dataformatComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		dataformatComboViewer.setLabelProvider(new DataFormatLabelProvider());
		dataformatComboViewer.setComparator(new ViewerComparator());
		dataformatComboViewer.setInput(model.getDataFormats().toArray());

		dbc.bindValue(ViewerProperties.singleSelection().observe(dataformatComboViewer),
				PojoProperties.value(DataFormatSelectionPage.class, "dataFormatSelected", DataFormat.class).observe(this)); //$NON-NLS-1$
		dataformatComboViewer.setSelection(new StructuredSelection(dataformatComboViewer.getElementAt(0)));
	}

	private void createIdLine(Composite container) {
		Label l_id = new Label(container, SWT.NONE);
		l_id.setText(UIMessages.dataFormatSelectionPageIdLabel);
		l_id.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		
		Text txt_id = new Text(container, SWT.BORDER);
		txt_id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new IValidator() {

			@Override
			public IStatus validate(Object value) {
				String id = (String) value;
				if (id == null || id.isEmpty()) {
					return ValidationStatus.error(UIMessages.globalEndpointWizardPageIdMandatoryMessage);
				}
				// TODO: check unicity of ID
				return ValidationStatus.ok();
			}
		});

		final IObservableValue idObservable = PojoProperties.value(DataFormatSelectionPage.class, "id", String.class).observe(this);//$NON-NLS-1$
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(txt_id), idObservable, strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
	}

	public DataFormat getDataFormatSelected() {
		return dataFormatSelected;
	}

	public void setDataFormatSelected(DataFormat dataFormatSelected) {
		this.dataFormatSelected = dataFormatSelected;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
