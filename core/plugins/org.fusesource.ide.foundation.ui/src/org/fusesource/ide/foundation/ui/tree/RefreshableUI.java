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

package org.fusesource.ide.foundation.ui.tree;

/**
 * Represents an object which can cause UIs to be notified of a refresh being required due to model changes
 */
public interface RefreshableUI {

	public abstract void fireRefresh();

	public abstract void fireRefresh(final Object node, final boolean full);

}
