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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.ui.navigator.ContainerNode;

/**
 * The form for adding or editing {@link Profile}
 */
public class ProfilesForm extends ProfilesFormSupport {
	private final ContainerNode node;
	public ProfilesForm(ICanValidate validator, ContainerNode node) {
		super(validator);
		this.node = node;
	}

	@Override
	public void createTextFields(Composite parent) {
		this.featuresList = null;
		ProfileDTO[] profiles = node.getContainer().getProfiles().toArray(new ProfileDTO[node.getContainer().getProfileIDs().size()]);
		for (final ProfileDTO profile : profiles) {
			String sectionTitle = profile.getId();

			Composite inner = createSectionComposite(sectionTitle, new GridData(GridData.FILL_BOTH));
			
			createProfileForm(profile, inner);
		}
	}

}