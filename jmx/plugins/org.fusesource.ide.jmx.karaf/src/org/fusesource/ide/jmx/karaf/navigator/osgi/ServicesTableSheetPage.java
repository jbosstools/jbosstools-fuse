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

package org.fusesource.ide.jmx.karaf.navigator.osgi;


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.foundation.ui.views.IViewPage;


public class ServicesTableSheetPage extends PropertySourceTableSheetPage {
	private final ServicesNode osgiNode;

	public ServicesTableSheetPage(ServicesNode osgiNode) {
		super(osgiNode, ServicesTableSheetPage.class.getName());
		this.osgiNode = osgiNode;
	}


	public ServicesNode getNode() {
		return osgiNode;
	}



	@Override
	public void setView(IViewPage view) {
		super.setView(view);

		/*
		Action setVersionAction = new ActionSupport("Set Version") {};

		getTableView().addLocalMenuActions(setVersionAction);
		 */
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		IMenuManager menu = actionBars.getMenuManager();

		/** TODO add start/stop stuff */

	}

}