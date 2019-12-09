/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.debug.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.launcher.debug.model.variables.CamelHeadersVariable;

/**
 * @author lhein
 *
 */
public class InjectHeaderHander extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelection(event);
		Object o = Selections.getFirstSelection(sel);
		if (o instanceof CamelHeadersVariable) {
			CamelHeadersVariable var = (CamelHeadersVariable)o;
			NewMessageHeaderDialog dlg = new NewMessageHeaderDialog(HandlerUtil.getActiveShell(event));
			if (Window.OK == dlg.open()) {
				String key = dlg.getHeaderKey();
				String val = dlg.getHeaderValue();
				var.addHeader(key, val);
			}			
		}
		return null;
	}
	
	class NewMessageHeaderDialog extends TitleAreaDialog {
		
		private String headerKey;
		private String headerValue;
		
		private Label lbl_headerName;
		private Label lbl_headerValue;
		
		private Text  txt_headerName;
		private Text  txt_headerValue;
		
		/**
		 * 
		 * @param shell
		 */
		public NewMessageHeaderDialog(Shell shell) {
			super(shell);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
		 */
		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("Add new header...");
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.Dialog#create()
		 */
		@Override
		public void create() {
			super.create();
		    setTitle("Add a new message header...");
		    setMessage("Please enter a header name and value for the new message header...", IMessageProvider.INFORMATION);
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected Control createContents(Composite parent) {
			Control c = super.createContents(parent);
			validate();
			return c;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected Control createDialogArea(Composite parent) {
			Composite area = (Composite) super.createDialogArea(parent);
			
			Composite container = new Composite(area, SWT.NONE);
			container.setLayout(new GridLayout(3, true));
			container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			lbl_headerName = new Label(container, SWT.NONE);
			lbl_headerName.setText("Name");
			lbl_headerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			
			txt_headerName = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.NO_SCROLL);
			txt_headerName.setText(Strings.isBlank(this.headerKey) ? "" : this.headerKey);
			txt_headerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			txt_headerName.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					validate();
				}
			});

			lbl_headerValue = new Label(container, SWT.NONE);
			lbl_headerValue.setText("Value");
			lbl_headerValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			
			txt_headerValue = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.NO_SCROLL);
			txt_headerValue.setText(Strings.isBlank(this.headerValue) ? "" : this.headerValue);
			txt_headerValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			txt_headerValue.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					validate();
				}
			});
			
			container.pack();
			
			return area;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
		 */
		@Override
		protected void okPressed() {
			if (validate()) {
				saveInputs();
				super.okPressed();
			}
		}
		
		/**
		 * saves the inputs
		 */
		private void saveInputs() {
			this.headerKey = txt_headerName.getText();
			this.headerValue = txt_headerValue.getText();
		}
		
		/**
		 * validates the inputs
		 */
		private boolean validate() {
			if (Strings.isBlank(txt_headerName.getText().trim())) {
				if (getButton(OK) != null) getButton(OK).setEnabled(false); 
				setErrorMessage("You must enter a valid header name.");
				return false;
			}
			
			if (Strings.isBlank(txt_headerValue.getText().trim())) {
				if (getButton(OK) != null) getButton(OK).setEnabled(false); 
				setErrorMessage("You must enter a valid header value.");
				return false;
			}
			
			if (getButton(OK) != null) getButton(OK).setEnabled(true);
			setErrorMessage(null);
			return true;
		}
		
		/**
		 * @return the headerKey
		 */
		public String getHeaderKey() {
			return this.headerKey;
		}
		
		/**
		 * @param headerKey the headerKey to set
		 */
		public void setHeaderKey(String headerKey) {
			this.headerKey = headerKey;
		}
		
		/**
		 * @return the headerValue
		 */
		public String getHeaderValue() {
			return this.headerValue;
		}
		
		/**
		 * @param headerValue the headerValue to set
		 */
		public void setHeaderValue(String headerValue) {
			this.headerValue = headerValue;
		}
	}
}
