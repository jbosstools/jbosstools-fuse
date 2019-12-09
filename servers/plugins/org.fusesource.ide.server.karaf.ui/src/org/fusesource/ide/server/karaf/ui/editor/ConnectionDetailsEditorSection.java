/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.ui.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegateWorkingCopy;
import org.fusesource.ide.server.karaf.ui.Messages;
import org.jboss.ide.eclipse.as.wtp.ui.editor.ServerWorkingCopyPropertyCommand;


public class ConnectionDetailsEditorSection extends ServerEditorSection {

	IKarafServerDelegateWorkingCopy configuration = null;
	// a dummy string to represent that we did not look up the password
	// in secure storage yet.  This is done to avoid secure storage 
	// popping up as soon as a user opens eclipse.
	private static final String PASSWORD_NOT_LOADED = "***fuseide****"; //$NON-NLS-1$
	private String passwordString = PASSWORD_NOT_LOADED;
	private boolean passwordChanged = false;
	private Text sshPasswordText;
	private ModifyListener sshPasswordListener;
	
	
	public ConnectionDetailsEditorSection() {
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		configuration = (IKarafServerDelegateWorkingCopy) server.loadAdapter(IKarafServerDelegateWorkingCopy.class, null);
	}
	
	@Override
	public void createSection(Composite parent) {
		super.createSection(parent);
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());

		Section section = toolkit.createSection(parent,ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(Messages.ConnectionDetailsEditorSection_section_name);
		section.setDescription(Messages.ConnectionDetailsEditorSection_section_desc);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(2,false);
		composite.setLayout(layout);
		GridData filldata = new GridData(GridData.FILL_BOTH);
		GridData leftData = new GridData(SWT.LEFT);
		leftData.widthHint = 100;
		composite.setLayoutData(filldata);
		section.setClient(composite);
		
		if (configuration == null) {
			section.setDescription(Messages.ConnectionDetailsEditorSection_no_srv_conn);
			return;
		}
		
		Label portNumber = toolkit.createLabel(composite, Messages.ConnectionDetailsEditorSection_port_num_label);
		portNumber.setLayoutData(leftData);
		final Text portNumberText = toolkit.createText(composite, Integer.toString(configuration.getPortNumber()),SWT.BORDER);
		portNumberText.setLayoutData(filldata);
		portNumberText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				try {
				int parseInt = Integer.parseInt(portNumberText.getText().trim());
				execute( new PortNumberChangeOperation(configuration,parseInt,Messages.ConnectionDetailsEditorSection_port_num_op));
				}catch(NumberFormatException ne){
					//ignore
				}
			}
		});
		
		Label sshUserNameLabel = toolkit.createLabel(composite, Messages.ConnectionDetailsEditorSection_user_name_label);
		sshUserNameLabel.setLayoutData(leftData);
		final Text sshUserNameText = toolkit.createText(composite, ""+configuration.getUserName(),SWT.BORDER);
		sshUserNameText.setLayoutData(filldata);
		sshUserNameText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				execute(new UserNameChangeOperation(configuration,sshUserNameText.getText(),Messages.ConnectionDetailsEditorSection_user_name_op));
			}
		});
		
		Label sshPasswordLabel = toolkit.createLabel(composite, Messages.ConnectionDetailsEditorSection_password_label);
		sshPasswordLabel.setLayoutData(leftData);
		sshPasswordText = toolkit.createText(composite, PASSWORD_NOT_LOADED,SWT.BORDER|SWT.PASSWORD); //$NON-NLS-1$
		sshPasswordText.setLayoutData(filldata);
		sshPasswordListener = new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				execute(new SetPassCommand(server));
			}
		};
		sshPasswordText.addModifyListener(sshPasswordListener);
	}
	
	/**
	 * Because the implementation of setPassword accesses the secure storage,
	 * we should only do this on a save of the editor, not on every change
	 * to the text control.  For this reason, we cache changes to 
	 * this field, and only persist them on a doSave() call. 
	 */
	public class SetPassCommand extends ServerWorkingCopyPropertyCommand {
		public SetPassCommand(IServerWorkingCopy server) {
			super(server, Messages.ConnectionDetailsEditorSection_password_op,
					sshPasswordText, sshPasswordText.getText(), 
					null, sshPasswordListener);
			oldVal = passwordString;
		}
		
		@Override
		public void execute() {
			passwordString = newVal;
			passwordChanged = !PASSWORD_NOT_LOADED.equals(passwordString);
		}
		
		@Override
		public void undo() {
			passwordString = oldVal;
			text.removeModifyListener(listener);
			text.setText(oldVal);
			text.addModifyListener(listener);
			passwordChanged = !PASSWORD_NOT_LOADED.equals(passwordString);
		}
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable adapt) {
			execute();
			return Status.OK_STATUS;
		}
	}

	/**
	 * Allow a section an opportunity to respond to a doSave request on the editor.
	 * @param monitor the progress monitor for the save operation.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if( passwordChanged ) {
			configuration.setPassword(passwordString);
			monitor.worked(100);
			passwordChanged = false;
		}
	}
}
