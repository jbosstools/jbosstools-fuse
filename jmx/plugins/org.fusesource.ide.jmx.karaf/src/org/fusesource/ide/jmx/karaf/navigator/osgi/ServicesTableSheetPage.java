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

package org.fusesource.ide.jmx.karaf.navigator.osgi;


import org.eclipse.ui.IActionBars;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableSheetPage;


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
	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		actionBars.getMenuManager();

		/** TODO add start/stop stuff */

	}

}