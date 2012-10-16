/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.fabric.actions;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.fusesource.fabric.api.Container;
import org.fusesource.fabric.api.Profile;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.ide.fabric.navigator.ProfileNode;
import org.fusesource.ide.fabric.navigator.VersionNode;


/**
 * The form for creating agents via ssh
 */
public class CreateSshContainerForm extends CreateContainerFormSupport {
	private final ContainerNode selectedAgent;
	private final ProfileNode selectedProfile;

	private Text hostField;
	private CreateSshContainerArgumentsBean args = new CreateSshContainerArgumentsBean();

	public CreateSshContainerForm(ICanValidate validator, VersionNode versionNode, ContainerNode selectedAgent, String defaultAgentName, ProfileNode selectedProfile) {
		super(validator, versionNode, defaultAgentName);
		this.selectedAgent = selectedAgent;
		this.selectedProfile = selectedProfile;
		addMandatoryPropertyNames("host", "path", "username", "password");
	}


	@Override
	public void setFocus() {
		hostField.setFocus();
	}

	@Override
	public void okPressed() {
		if (isValid()) {
			String agentName = getNewAgentName();
			Profile[] profiles = getSelectedProfileArray();

			Container agent = null;
			if (selectedAgent != null) {
				agent = selectedAgent.getContainer();
			}
			args.setName(agentName);
			getFabric().createContainer(agent, profiles, args.delegate());
		}
	}


	@Override
	protected void loadPreference() {
		if (selectedProfile != null) {
			Profile profile = selectedProfile.getProfile();
			if (profile != null) {
				setCheckedProfiles(profile);
			}
		}
		super.loadPreference();
	}

	@Override
	protected void createTextFields(Composite inner) {
		super.createTextFields(inner);

		hostField = createBeanPropertyTextField(inner, args, "host", Messages.agentHostLabel, Messages.agentHostLabel);
		createBeanPropertyTextField(inner, args, "username", Messages.agentUserLabel, Messages.agentUserLabel);
		createBeanPropertyTextField(inner, args, "password", Messages.agentPasswordLabel, Messages.agentPasswordLabel, SWT.PASSWORD);
		createBeanPropertyTextField(inner, args, "path", Messages.agentPathLabel, Messages.agentPathTooltip);
		createBeanPropertyTextField(inner, args, "port", Messages.agentPortLabel, Messages.agentPortLabel);
		createBeanPropertyTextField(inner, args, "sshRetries", Messages.agentSshRetriesLabel, Messages.agentSshRetriesTooltip);
		createBeanPropertyTextField(inner, args, "retryDelay", Messages.agentRetryDelayLabel, Messages.agentRetryDelayTooltip);
	}


}