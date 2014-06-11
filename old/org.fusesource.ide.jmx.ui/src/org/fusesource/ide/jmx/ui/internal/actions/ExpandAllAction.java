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

package org.fusesource.ide.jmx.ui.internal.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author lhein
 */
public abstract class ExpandAllAction extends Action implements IWorkbenchWindowActionDelegate {

	/**
	 * creates the refresh action
	 */
	public ExpandAllAction() {
		/*
		setText(Messages.ExpandAllAction_text);
		setDescription(Messages.ExpandAll_description);
		setToolTipText(Messages.RExpandAll_tooltip);
		JMXImages.setLocalImageDescriptors(this, "refresh.gif");
		 */
	}

}
