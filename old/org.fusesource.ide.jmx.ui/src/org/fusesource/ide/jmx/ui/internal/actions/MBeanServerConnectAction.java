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

package org.fusesource.ide.jmx.ui.internal.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;
import org.fusesource.ide.jmx.core.ConnectJob;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.JMXImages;


/**
 * The connect action
 */
public class MBeanServerConnectAction extends Action {
	private IConnectionWrapper[] connection;
	private final StructuredViewer viewer;
	
    public MBeanServerConnectAction(StructuredViewer viewer, IConnectionWrapper[] wrapper) {
        super(Messages.MBeanServerConnectAction_text, AS_PUSH_BUTTON);
		this.viewer = viewer;
        JMXImages.setLocalImageDescriptors(this, "attachAgent.gif"); //$NON-NLS-1$
        this.connection = wrapper;
    }

	public void run() {
		if( connection != null ) {
			new ConnectJob(viewer, connection).schedule();
		}
    }
}
