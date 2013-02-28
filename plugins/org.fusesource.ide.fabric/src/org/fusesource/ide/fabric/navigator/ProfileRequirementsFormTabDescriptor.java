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

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.ui.form.FormPage;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


/**
 * @author lhein
 */
public class ProfileRequirementsFormTabDescriptor extends PageTabDescriptor {

	private final ProfileNode node;

	public ProfileRequirementsFormTabDescriptor(String label, ProfileNode node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new FormPage(new ProfileRequirementsForm(node));
	}
}
