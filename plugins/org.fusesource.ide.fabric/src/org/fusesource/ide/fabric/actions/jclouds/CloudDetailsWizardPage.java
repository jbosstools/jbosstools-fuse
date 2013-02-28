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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.fabric.actions.Messages;


public class CloudDetailsWizardPage extends WizardPage {

	private CloudDetailsTable table = new CloudDetailsTable();

	public CloudDetailsWizardPage() {
		super(Messages.jclouds_chooseCloudTitle);
		setDescription(Messages.jclouds_chooseCloudDescription);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());

		table.createPartControl(composite);

		setControl(composite);

		table.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectionUpdated();
			}
		});

	}

	@Override
	public boolean isPageComplete() {
		return getSelectedCloud() != null;
	}

	public CloudDetails getSelectedCloud() {
		return table.getSelectedCloud();
	}


	protected void selectionUpdated() {
		boolean selected = getSelectedCloud() != null;
		setPageComplete(selected);
	}


}
