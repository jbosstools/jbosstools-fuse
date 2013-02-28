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

package org.fusesource.ide.jmx.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class DeleteConnectionJob extends ChainedJob {
	final IConnectionWrapper[] connections;
	public DeleteConnectionJob(IConnectionWrapper[] wrappers) {
		super(JMXCoreMessages.DeleteConnectionJob, JMXActivator.PLUGIN_ID);
		this.connections = wrappers;
	}
	protected IStatus run(IProgressMonitor monitor) {
		for( int i = 0; i < connections.length; i++ )
			connections[i].getProvider().removeConnection(connections[i]);
		return Status.OK_STATUS;
	}
}
