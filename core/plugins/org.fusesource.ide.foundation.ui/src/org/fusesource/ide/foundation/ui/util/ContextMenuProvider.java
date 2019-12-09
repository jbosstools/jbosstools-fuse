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
package org.fusesource.ide.foundation.ui.util;

import org.eclipse.jface.action.IMenuManager;


/**
 * This is a workaround interface to replace a deprecated and
 * removed interface, org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;
 * 
 * This interface allows elements that are model objects presented
 * in a common navigator to make additions to the given view's context
 * menu. 
 * 
 * This interface is here for backwards compatibility, but is
 * not intended to be used by new clients.
 */
public interface ContextMenuProvider {
	/**
	 * Add actions to the given menu
	 * @param menu
	 */
	public void provideContextMenu(IMenuManager menu);
}
