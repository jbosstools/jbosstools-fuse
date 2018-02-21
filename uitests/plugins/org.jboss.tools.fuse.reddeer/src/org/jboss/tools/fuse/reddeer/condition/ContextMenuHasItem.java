/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.condition;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.swt.api.MenuItem;
import org.eclipse.reddeer.swt.impl.menu.ContextMenu;

/**
 * Checks whether the given ContextMenu has the desired item
 * 
 * @author tsedmik
 */
public class ContextMenuHasItem extends AbstractWaitCondition {

	private ContextMenu menu;
	private String item;

	public ContextMenuHasItem(ContextMenu menu, String item) {
		this.menu = menu;
		this.item = item;
	}

	@Override
	public boolean test() {
		boolean result = false;
		for (MenuItem tmp : menu.getItems()) {
			if (tmp.getText().equals(item)) {
				result = true;
				break;
			}
		}
		return result;
	}

}
