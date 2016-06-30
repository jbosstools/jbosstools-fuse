/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.preferences;

import static org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement.USER_LABEL_DELIMETER;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.fusesource.ide.camel.editor.internal.UIMessages;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class UserLabelsListEditor extends ListEditor {

	private Button editButton;

	public UserLabelsListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	protected String[] parseString(String string) {
		if (string == null || string.isEmpty()) {
			return new String[] {};
		}
		return string.split(USER_LABEL_DELIMETER);
	}

	@Override
	protected String getNewInputObject() {
		InputDialog dialog = new InputDialog(getShell(), UIMessages.userLabels_newDialogTitle,
				UIMessages.userLabels_message, null, createValidator());
		if (dialog.open() == Window.OK) {
			return dialog.getValue();
		}
		return null;
	}

	@Override
	protected String createList(String[] items) {
		return String.join(USER_LABEL_DELIMETER, items);
	}

	@Override
	public Composite getButtonBoxControl(Composite parent) {
		Composite buttonBox = super.getButtonBoxControl(parent);
		getUpButton().dispose();
		getDownButton().dispose();
		editButton = createEditButton(buttonBox);
		editButton.moveAbove(getRemoveButton());
		editButton.setEnabled(false);
		return buttonBox;
	}

	private Button createEditButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(UIMessages.userLabels_editButtonText);
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == editButton) {
					editPressed();
				}
			}

		});
		return button;
	}

	private void editPressed() {
		setPresentsDefaultValue(false);
		int index = getList().getSelectionIndex();
		if (index >= 0) {
			String selectedItem = getList().getItem(index);
			InputDialog dialog = new InputDialog(getShell(), UIMessages.userLabels_editDialogTitle,
					UIMessages.userLabels_message, selectedItem, createValidator(selectedItem));
			if (dialog.open() == Window.OK) {
				getList().setItem(index, dialog.getValue());
			}
			getList().select(index);
			selectionChanged();
		}

	}

	@Override
	protected void selectionChanged() {
		int index = getList().getSelectionIndex();
		getRemoveButton().setEnabled(index >= 0);
		if (editButton != null) {
			editButton.setEnabled(index >= 0);
		}
	}

	private IInputValidator createValidator(String... excludedItems) {
		List<String> existingItems = new LinkedList<String>(Arrays.asList(getList().getItems()));
		existingItems.removeAll(Arrays.asList(excludedItems));
		return new UserLabelsListValidator(existingItems.toArray(new String[existingItems.size()]));
	}

}
