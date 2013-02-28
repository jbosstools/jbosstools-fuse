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

package org.fusesource.ide.fabric.navigator;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.fabric.api.Container;
import org.fusesource.fabric.api.Profile;
import org.fusesource.ide.fabric.Messages;
import org.fusesource.ide.fabric.actions.ProfileTreeSelectionFormSupport;

import scala.actors.threadpool.Arrays;


public class ProfileTreeForm extends ProfileTreeSelectionFormSupport {
	private final ContainerNode node;

	public ProfileTreeForm(ContainerNode node) {
		this.node = node;
	}

	@Override
	public void setFocus() {
		getProfilesViewer().getControl().setFocus();
	}

	@Override
	protected void createTextFields(Composite parent) {
		Composite outer = createSectionComposite(Messages.profilesForm_header, new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		outer.setLayout(layout);

		createColumnsViewer(outer);
		createButtons(parent);

		setProfilesViewerInput(node.getVersionNode());
		Profile[] profiles = node.getContainer().getProfiles();
		setCheckedProfiles(profiles);
	}


	@Override
	protected void onProfileSelectionChanged() {
		super.onProfileSelectionChanged();

		if (isValid()) {
			okPressed();
		}
	}

	@Override
	public void okPressed() {
		Profile[] profiles = getSelectedProfileArray();
		Container agent = node.getContainer();
		System.out.println("Updating the profiles of " + agent + " to: " + Arrays.asList(profiles));
		agent.setProfiles(profiles);
		node.refresh();
	}

	@Override
	protected Composite createButtonComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		/*
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = SWT.CENTER;
		 */
		composite.setLayout(layout);
		//composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		return composite;
	}

	@Override
	protected void setButtonLayoutData(Button button) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		//gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		//gridData.horizontalAlignment = SWT.CENTER;
		button.setLayoutData(gridData);
	}

}
