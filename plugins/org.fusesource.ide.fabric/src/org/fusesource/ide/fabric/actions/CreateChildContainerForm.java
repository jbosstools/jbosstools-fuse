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

package org.fusesource.ide.fabric.actions;

import org.fusesource.fabric.api.Profile;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric.navigator.ContainerNode;


/**
 * Form for creating child agents
 */
public class CreateChildContainerForm extends CreateContainerFormSupport {
	private ContainerNode agentNode;

	public CreateChildContainerForm(ICanValidate validator, ContainerNode agentNode, String defaultAgentName) {
		super(validator, agentNode.getVersionNode(), defaultAgentName);
		this.agentNode = agentNode;
	}

	@Override
	public void setFocus() {
		// if we've a selected focus auto-defauled lets use the container name instead
		if (getSelectedProfileList().isEmpty()) {
			getProfilesViewer().getControl().setFocus();
		} else {
			super.setFocus();
		}
	}


	@Override
	public void okPressed() {
		String agentName = getNewAgentName();
		Profile[] profiles = getSelectedProfileArray();

		if (agentName.length() > 0 && profiles.length > 0) {
			agentNode.createContainer(agentName, profiles);
		}
	}

}
