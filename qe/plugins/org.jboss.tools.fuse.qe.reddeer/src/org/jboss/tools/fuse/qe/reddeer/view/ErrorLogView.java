/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.view;

import org.jboss.reddeer.eclipse.ui.views.log.LogView;
import org.jboss.reddeer.swt.impl.menu.ViewMenu;

/**
 * Represents <i>Error Log</i> view
 * 
 * @author tsedmik
 */
public class ErrorLogView extends LogView {

	public void selectActivateOnNewEvents(boolean value) {
		open();
		ViewMenu menu = new ViewMenu("View Menu", "Activate on new events");
		if ((value && !menu.isSelected()) || (!value && menu.isSelected())) {
			menu.select();
		}
	}
}
