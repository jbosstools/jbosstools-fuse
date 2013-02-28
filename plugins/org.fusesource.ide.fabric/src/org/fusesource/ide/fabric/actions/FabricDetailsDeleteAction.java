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

import org.eclipse.jface.action.Action;
import org.fusesource.ide.fabric.FabricPlugin;


public abstract class FabricDetailsDeleteAction extends Action {

	public FabricDetailsDeleteAction() {
		super(Messages.fabricDeleteButton);
		setToolTipText(Messages.fabricDeleteButtonTooktip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("delete.gif"));
	}

	protected abstract FabricDetails getSelectedFabricDetails();

	@Override
	public void run() {
		FabricDetails details = getSelectedFabricDetails();
		if (details != null) {
			details.delete();
			FabricDetails.getDetailList().remove(details);
		}
	}

}
