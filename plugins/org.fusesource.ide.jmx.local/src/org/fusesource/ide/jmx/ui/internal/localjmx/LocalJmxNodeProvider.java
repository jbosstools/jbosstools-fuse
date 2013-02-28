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

package org.fusesource.ide.jmx.ui.internal.localjmx;

import java.util.List;

import org.fusesource.ide.commons.tree.PartialRefreshableNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.jmx.ui.RootJmxNodeProvider;



public class LocalJmxNodeProvider implements RootJmxNodeProvider{

	public void provideRootJmxNodes(RefreshableUI contentProvider, List list) {
		PartialRefreshableNode connections = new JvmConnectionsNode(null, contentProvider);
		list.add(connections);
	}

}
