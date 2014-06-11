/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/
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

package org.fusesource.ide.jmx.core.providers;

import org.eclipse.jface.viewers.StructuredViewer;
import org.fusesource.ide.jmx.core.ConnectJob;
import org.fusesource.ide.jmx.core.IConnectionProviderListener;
import org.fusesource.ide.jmx.core.IConnectionWrapper;


/**
 * Ensures that DefaultConnectionWrapper type connections
 * automatically attempt to start
 */
public class AutomaticStarter implements IConnectionProviderListener {
	// TODO how to find this???
	private StructuredViewer viewer;
	
	public void connectionAdded(IConnectionWrapper connection) {
		if( connection instanceof DefaultConnectionWrapper ) {
			new ConnectJob(viewer, new IConnectionWrapper[] { connection }).schedule();
		}
	}

	public void connectionChanged(IConnectionWrapper connection) {
	}
	public void connectionRemoved(IConnectionWrapper connection) {
	}

}
