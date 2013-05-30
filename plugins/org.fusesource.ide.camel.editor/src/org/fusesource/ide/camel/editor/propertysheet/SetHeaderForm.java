/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.propertysheet;

import org.apache.camel.model.SetHeaderDefinition;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.fusesource.camel.tooling.util.Languages;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.ui.form.FormSupport;


public class SetHeaderForm extends FormSupport {
	private final SetHeaderDefinition definition;
	private Text headerNameField;
	private Text expressionField;
	private ComboViewer languageCombo;
	private SetHeaderBeanView beanView;
	private Refreshable refreshable;

	public SetHeaderForm(SetHeaderDefinition definition, Refreshable refreshable) {
		this.definition = definition;
		this.refreshable = refreshable;
		this.beanView = new SetHeaderBeanView(definition);
	}

	public SetHeaderDefinition getDefinition() {
		return definition;
	}

	@Override
	public void setFocus() {
		if (headerNameField != null) {
			headerNameField.setFocus();
		}
	}

	@Override
	protected void createTextFields(Composite parent) {
		Composite panel = getForm().getBody();
		panel.setLayout(new GridLayout(2, false));

		headerNameField = createBeanPropertyTextField(panel, beanView, "headerName", "Header", "Name of the header to set");
		expressionField = createBeanPropertyTextField(panel, beanView, "expression", "Expression", "Expression used to set the header value");

		languageCombo = createBeanPropertyCombo(panel, beanView, "language", "Language", "Expression language to use", SWT.READ_ONLY);
		languageCombo.setContentProvider(new ArrayContentProvider());
		languageCombo.setInput(new Languages().languageArray());
		languageCombo.setSelection(new StructuredSelection(beanView.getLanguage()));
	}

	@Override
	public void okPressed() {
		beanView.update();
		if (refreshable != null) {
			refreshable.refresh();
		}
	}

}
