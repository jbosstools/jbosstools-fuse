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

package org.fusesource.ide.jmx.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredViewer;

public class ConnectJob extends ChainedJob {
	private IConnectionWrapper[] connection;

	public ConnectJob(StructuredViewer viewer, IConnectionWrapper[] connection) {
		super(JMXCoreMessages.ConnectJob, JMXActivator.PLUGIN_ID);
		this.connection = connection;
	}

	protected IStatus run(IProgressMonitor monitor) {
		try {
			for (int i = 0; i < connection.length; i++) {
				IConnectionWrapper wrapper = connection[i];
				wrapper.connect();
			}
			return Status.OK_STATUS;
		} catch (Exception ioe) {
			return new Status(IStatus.ERROR, JMXActivator.PLUGIN_ID, JMXCoreMessages.ConnectJobFailed, ioe);
		}
	}
}
