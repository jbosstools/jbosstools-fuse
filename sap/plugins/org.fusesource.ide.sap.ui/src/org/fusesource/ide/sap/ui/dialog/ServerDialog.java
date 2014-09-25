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
package org.fusesource.ide.sap.ui.dialog;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;

public class ServerDialog extends TitleAreaDialog {
	
	public enum Type {
		CREATE, PASTE
	}

    private Type type;
	
	private EditingDomain editingDomain;
	private Text serverNameText;
	private ServerDataStore serverDataStore;
	private ServerDataStoreEntryImpl serverDataStoreEntry;

	private Button okButton;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ServerDialog(Shell parentShell, Type type, EditingDomain editingDomain, ServerDataStore serverDataStore, ServerDataStoreEntryImpl serverDataStoreEntry) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.APPLICATION_MODAL);
		this.type = type;
		this.editingDomain = editingDomain;
		this.serverDataStore = serverDataStore;
		this.serverDataStoreEntry = serverDataStoreEntry;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (type == Type.CREATE) {
			newShell.setText(Messages.ServerDialog_shellCreateTitle);
		} else if (type == Type.PASTE) {
			newShell.setText(Messages.ServerDialog_shellPasteTitle);
		}
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		if (type == Type.CREATE) {
			setTitle(Messages.ServerDialog_dialogCreateTitle);
		} else if (type == Type.PASTE) {
			setTitle(Messages.ServerDialog_dialogPasteTitle);
			setMessage(""); //$NON-NLS-1$
		}
		setTitleImage(Activator.getDefault().getImageRegistry().get(Activator.FUSE_RS_IMAGE));

		return contents;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Composite entryNameContainer = new Composite(container, SWT.None);
		entryNameContainer.setLayout(new GridLayout(2, false));
		GridData gd_entryNameContainer = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_entryNameContainer.widthHint = 300;
		entryNameContainer.setLayoutData(gd_entryNameContainer);
		
		Label lblDestinationName = new Label(entryNameContainer, SWT.NONE);
		lblDestinationName.setAlignment(SWT.RIGHT);
		lblDestinationName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDestinationName.setText(Messages.ServerDialog_destinationNameLabel);
		
		serverNameText = new Text(entryNameContainer, SWT.BORDER);
		serverNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		serverNameText.setBounds(0, 0, 64, 19);
		serverNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setErrorMessage(null);
				if (serverNameText.getText().isEmpty()) {
					okButton.setEnabled(false);
				} else {
					String name = serverNameText.getText();
					if (serverDataStore.getEntries().get(name) != null) {
						// named entry already exists
						setErrorMessage(NLS.bind(Messages.ServerDialog_serverAlreadyExists, name));
						okButton.setEnabled(false);
					} else {
						okButton.setEnabled(true);
					}
				}
			}
		});
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		initDataBindings();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(550, 189);
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextServerNameTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(serverNameText);
		IObservableValue serverDataServerNameObserveValue = EMFEditProperties.value(editingDomain, Literals.SERVER_DATA_STORE_ENTRY__KEY).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextServerNameTextObserveWidget, serverDataServerNameObserveValue, null, null);
		//
		return bindingContext;
	}
}
