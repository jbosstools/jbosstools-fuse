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

package org.fusesource.ide.camel.editor.wizards.pages;

import java.util.ArrayList;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.wizards.NewDataFormatWizard;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author lhein
 *
 */
public class DataFormatSelectionPage extends WizardPage {

	private NewDataFormatWizard wizard;
	
	/**
	 * @param pageName
	 */
	public DataFormatSelectionPage(NewDataFormatWizard wizard, String pageName) {
		super(pageName);
		this.wizard = wizard;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		GridLayout gl = new GridLayout(2, false);

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(gl);
		
		Label l = new Label(container, SWT.NONE);
		l.setText(UIMessages.dataFormatSelectionPage_dataformatLabel);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		
		Combo cmb_format = new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmb_format.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ArrayList<String> supportedDataFormats = new ArrayList<String>();
		for (DataFormat df : wizard.getDataFormatModel().getSupportedDataFormats()) {
			if (supportedDataFormats.contains(df.getModelName()) == false) supportedDataFormats.add(df.getModelName());
		}
		cmb_format.setItems(supportedDataFormats.toArray(new String[supportedDataFormats.size()]));
		wizard.observeWidget(cmb_format, "dataformat", null);
		cmb_format.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		cmb_format.select(0);
		
		Label l_id = new Label(container, SWT.NONE);
		l_id.setText(UIMessages.dataFormatSelectionPage_idLabel);
		l_id.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		
		Text txt_id = new Text(container, SWT.BORDER);
		txt_id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txt_id.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		wizard.observeWidget(txt_id, "id", new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value != null && value instanceof String && value.toString().trim().length()>0) {
					return ValidationStatus.ok();
				}
				return ValidationStatus.error("Parameter id is a mandatory field and cannot be empty.");
			}
		});
		
		Label l_desc = new Label(container, SWT.NONE);
		l_desc.setText(UIMessages.dataFormatSelectionPage_descriptionLabel);
		l_desc.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		
		Text txt_desc = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.BORDER);
		txt_desc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		txt_desc.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		
		wizard.observeWidget(txt_desc, "description", null);
		
		setControl(container);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return isValid();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return isValid();
	}
	
	public void validate() {
		setPageComplete(isValid());
	}
	
	/**
	 * returns true if the page is valid
	 * 
	 * @return
	 */
	public boolean isValid() {
		return !Strings.isBlank((String)wizard.getObservableMap().get("id")) &&
			   !Strings.isBlank((String)wizard.getObservableMap().get("dataformat"));
	}
}
