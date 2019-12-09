/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.restconfiguration.wizards.pages;

import java.util.UUID;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigUtil;
import org.fusesource.ide.camel.editor.restconfiguration.wizards.AddRestOperationWizard;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 *
 */
public class RestVerbDefinitionPage extends BaseRestWizardPage {

	private String verbTypeToCreate = RestVerbElement.GET_VERB; // default to GET
	private String id = UUID.randomUUID().toString(); // default ID
	private String uriValue = null;
	private RestConfigUtil util = new RestConfigUtil();
	private String toURISelected = null;

	public RestVerbDefinitionPage(String pageName) {
		super(pageName);
		setTitle(UIMessages.restVerbDefinitionPageTitle);
		setMessage(UIMessages.restVerbDefinitionPageMessage);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
		Text idControl = createLabelAndText(composite, UIMessages.restVerbDefinitionPageIDField, 2);
		idControl.setText(id);
		idControl.addModifyListener(input -> { 
			id = idControl.getText();
			validate();
		});

		Text uriControl = createLabelAndText(composite, UIMessages.restVerbDefinitionPageURIField, 2);
		uriControl.addModifyListener(input -> { 
			uriValue = uriControl.getText();
			validate();
		});
		
		Combo verbCombo = createComboAndText(composite, UIMessages.restVerbDefinitionPageVerbField, 2);
		verbCombo.add(RestVerbElement.CONNECT_VERB);
		verbCombo.add(RestVerbElement.DELETE_VERB);
		verbCombo.add(RestVerbElement.GET_VERB);
		verbCombo.add(RestVerbElement.HEAD_VERB);
		verbCombo.add(RestVerbElement.OPTIONS_VERB);
		verbCombo.add(RestVerbElement.PATCH_VERB);
		verbCombo.add(RestVerbElement.POST_VERB);
		verbCombo.add(RestVerbElement.PUT_VERB);
		verbCombo.add(RestVerbElement.TRACE_VERB);
		verbCombo.setText(verbTypeToCreate);
		verbCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				verbTypeToCreate = verbCombo.getText();
				validate();
			}
		});
		
		Combo toUriCombo = createComboAndText(composite, UIMessages.restVerbDefinitionPageToRouteLabel, 2);
		AddRestOperationWizard addWiz = (AddRestOperationWizard) getWizard();
		String[] routeRefs = util.getRoutes(addWiz.getCameFileFromEditor(), ""); //$NON-NLS-1$
		if (routeRefs.length == 1) {
			toUriCombo.add(UIMessages.restVerbDefinitionPageNoRouteToURIsAvailableMessage);
			toUriCombo.setText(UIMessages.restVerbDefinitionPageNoRouteToURIsAvailableMessage); 
			toUriCombo.setEnabled(false);
		} else {
			toUriCombo.setItems(routeRefs);
		}
		toUriCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toURISelected = toUriCombo.getText();
				validate();
			}
		});

		setControl(composite);
		validate();
		setErrorMessage(null); // clear any error messages at first
	}

	public String getVerbTypeToCreate() {
		return verbTypeToCreate;
	}

	public String getId() {
		return id;
	}

	public String getUriValue() {
		return uriValue;
	}
	
	public String getToUriSelected() {
		return toURISelected;
	}

	private boolean validate() {
		if (Strings.isEmpty(getId())) {
			setErrorMessage(UIMessages.restVerbDefinitionPageIDCannotBeNullError);
			setPageComplete(false);
			return false;
		} else {
			setErrorMessage(null);
		}

		if (Strings.isEmpty(getUriValue())) {
			setErrorMessage(UIMessages.restVerbDefinitionPageURICannotBeNull);
			setPageComplete(false);
			return false;
		} else {
			setErrorMessage(null);
		}
		
		if (getRestConfigEditor() != null) {
			CamelContextElement ctx = getRestConfigEditor().getCtx();
			if(!ctx.findAllNodesWithId(id).isEmpty()){
				setErrorMessage(UIMessages.restVerbDefinitionPageInvalidOperationIDError);
				setPageComplete(false);
				return false;
			}
		}

		setPageComplete(true);
		return true;
	}
}
