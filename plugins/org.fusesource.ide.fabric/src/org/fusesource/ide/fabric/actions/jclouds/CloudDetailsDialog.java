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

package org.fusesource.ide.fabric.actions.jclouds;

import org.fusesource.ide.commons.ui.form.FormDialogSupport;
import org.fusesource.ide.fabric.actions.Messages;


public class CloudDetailsDialog extends FormDialogSupport {

	public CloudDetailsDialog() {
		super(Messages.jclouds_cloudDetails);
		setForm(new CloudDetailsForm(this));
	}

	@Override
	public CloudDetailsForm getForm() {
		return (CloudDetailsForm) super.getForm();
	}

	public CloudDetails getCloudDetails() {
		return getForm().getDetails();
	}


}
