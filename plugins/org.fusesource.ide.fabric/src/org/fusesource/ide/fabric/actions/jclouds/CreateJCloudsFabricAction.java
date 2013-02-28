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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.fusesource.ide.commons.ui.Wizards;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.Messages;
import org.fusesource.ide.fabric.navigator.Fabrics;


public class CreateJCloudsFabricAction extends Action  {
	public static final boolean createLocalAgents = true;
	private final Fabrics fabrics;

	public CreateJCloudsFabricAction(Fabrics fabrics) {
		super(Messages.createJCloudsFabricMenuLabel, SWT.CHECK);
		this.fabrics = fabrics;

		setToolTipText(Messages.createJCloudsFabricToolTip);
		setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("new_cloud_fabric.png"));
	}

	@Override
	public void run() {
		showDialog();
	}

	protected void showDialog() {
		CreateJCloudsFabricWizard wizard = new CreateJCloudsFabricWizard(fabrics, "Registry");

		/*
		IWorkbenchPartSite site = Workbenches.getActiveWorkbenchPartSite();
		wizard.init(Workbenches.getActiveWorkbench(), Selections.getStructuredSelection(site));
		 */

		Wizards.openWizardDialog(wizard);
	}



}
