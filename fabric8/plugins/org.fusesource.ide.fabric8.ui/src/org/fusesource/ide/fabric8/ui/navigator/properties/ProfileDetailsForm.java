/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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

package org.fusesource.ide.fabric8.ui.navigator.properties;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.ui.actions.Messages;
import org.fusesource.ide.fabric8.ui.actions.ProfileTreeSelectionFormSupport;
import org.fusesource.ide.fabric8.ui.navigator.ProfileNode;
import org.fusesource.ide.fabric8.ui.navigator.ProfileParentsContentProvider;


/**
 * The form for adding or editing {@link Profile}
 */
public class ProfileDetailsForm extends ProfilesFormSupport {
	private final ProfileNode node;
	// lets add the parent selection...
	ProfileTreeSelectionFormSupport parentsForm = new ProfileTreeSelectionFormSupport() {

		@Override
		public void setFocus() {
			getProfilesViewer().getControl().setFocus();
		}

		@Override
		protected void createTextFields(Composite parent) {
			// TODO Auto-generated method stub
		}

		@Override
		protected void onProfileSelectionChanged() {
			super.onProfileSelectionChanged();
			okPressed();
		}

		@Override
		public void okPressed() {
			ProfileDTO[] profiles = getSelectedProfileArray();
			node.getProfile().setParents(Arrays.asList(profiles));
			// TODO we should reload the parent nodes now!
		}

		@Override
		public FormToolkit getToolkit() {
			return ProfileDetailsForm.this.getToolkit();
		}
	};

	public ProfileDetailsForm(ICanValidate validator, ProfileNode node) {
		super(validator);
		this.node = node;
	}

	@Override
	public void createTextFields(Composite parent) {
		this.featuresList = null;
		ProfileDTO profile = node.getProfile();
		String sectionTitle = profile.getId();

		Composite inner = createSectionComposite(sectionTitle, new GridData(GridData.FILL_BOTH));
		createProfileForm(profile, inner);

		createLabel(inner, Messages.profileParentsLabel);
		parentsForm.createColumnsViewer(inner);
		parentsForm.setProfilesViewerInput(node.getVersionNode());
		parentsForm.getProfilesViewer().setContentProvider(new ProfileParentsContentProvider(node));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		parentsForm.getProfilesViewer().getTree().setLayoutData(gridData);
		List<ProfileDTO> parents = profile.getParents();
		parentsForm.setCheckedProfiles(parents);
	}

	@Override
	protected Label createLabel(Composite inner, String text) {
		Label label = super.createLabel(inner, text);
		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		label.setLayoutData(data);
		return label;
	}

	@Override
	public void setFocus() {
		parentsForm.setFocus();
	}


}