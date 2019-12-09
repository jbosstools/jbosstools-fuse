/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

/**
 * Represents an action which can change dynamically what it represents such as an action based on the currently selected items state
 * such as start/stop type actions.
 */
public class ToggleAction extends Action {
	private Action currentAction;

	@Override
	public void run() {
		if (currentAction != null) {
			currentAction.run();
		}
	}


	@Override
	public void runWithEvent(Event event) {
		if (currentAction != null) {
			currentAction.runWithEvent(event);
		}
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;

		setText(currentAction.getText());
		setToolTipText(currentAction.getToolTipText());
		setDescription(currentAction.getDescription());
		setHoverImageDescriptor(currentAction.getHoverImageDescriptor());
		setImageDescriptor(currentAction.getImageDescriptor());
		setDisabledImageDescriptor(currentAction.getDisabledImageDescriptor());
		setEnabled(currentAction.isEnabled());
		setChecked(currentAction.isChecked());
	}

}
