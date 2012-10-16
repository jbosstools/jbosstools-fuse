package org.fusesource.ide.server.karaf.ui.editor;

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
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
import org.fusesource.ide.server.karaf.core.internal.server.IServerConfigurationWorkingCopy;
import org.fusesource.ide.server.karaf.ui.Messages;


public class ConnectionDetailsEditorSection extends ServerEditorSection {

	IServerConfigurationWorkingCopy configuration = null;
	
	public ConnectionDetailsEditorSection() {
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		configuration = (IServerConfigurationWorkingCopy) server.loadAdapter(IServerConfigurationWorkingCopy.class, null);
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
		
		Label hostName = toolkit.createLabel(composite, Messages.ConnectionDetailsEditorSection_hostname_label);
		hostName.setLayoutData(leftData);
		final Text hostNameText = toolkit.createText(composite, configuration.getHostName(), SWT.BORDER);
		hostNameText.setLayoutData(filldata);
		hostNameText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				String hostNameValue = hostNameText.getText().trim();
				execute( new HostNameChangeOperation(configuration, hostNameValue, Messages.ConnectionDetailsEditorSection_hostname_op));
			}
		});
		
		Label portNumber = toolkit.createLabel(composite, Messages.ConnectionDetailsEditorSection_port_num_label);
		portNumber.setLayoutData(leftData);
		final Text portNumberText = toolkit.createText(composite, ""+configuration.getPortNumber(),SWT.BORDER);
		portNumberText.setLayoutData(filldata);
		portNumberText.addModifyListener(new ModifyListener(){
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
			public void modifyText(ModifyEvent e) {
				execute(new UserNameChangeOperation(configuration,sshUserNameText.getText(),Messages.ConnectionDetailsEditorSection_user_name_op));
			}
		});
		
		Label sshPasswordLabel = toolkit.createLabel(composite, Messages.ConnectionDetailsEditorSection_password_label);
		sshPasswordLabel.setLayoutData(leftData);
		final Text sshPasswordText = toolkit.createText(composite, ""+configuration.getPassword(),SWT.BORDER|SWT.PASSWORD); //$NON-NLS-1$
		sshPasswordText.setLayoutData(filldata);
		sshPasswordText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				execute(new PasswordChangeOperation(configuration,sshPasswordText.getText(),Messages.ConnectionDetailsEditorSection_password_op));
			}
		});
	}
	
}
