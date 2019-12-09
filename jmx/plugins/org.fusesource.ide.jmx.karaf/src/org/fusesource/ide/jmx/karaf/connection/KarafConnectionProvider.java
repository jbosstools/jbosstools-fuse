/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.karaf.connection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegate;
import org.jboss.tools.jmx.core.IConnectionWrapper;

public class KarafConnectionProvider extends AbstractKarafJMXConnectionProvider {

	public static final String ID = "org.fusesource.ide.jmx.karaf.connection.KarafConnectionProvider";
	
	@Override
	protected boolean belongsHere(IServer server) {
		IKarafServerDelegate del = (IKarafServerDelegate)server.loadAdapter(IKarafServerDelegate.class, new NullProgressMonitor());
		if( del != null ) {
			return true;
		}
		return false;
	}

	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getName(IConnectionWrapper wrapper) {
		if( wrapper instanceof KarafServerConnection) {
			return ((KarafServerConnection)wrapper).getName();
		}
		return null;
	}
	
	@Override
	protected IConnectionWrapper createConnection(IServer server) {
		return new KarafServerConnection(server);
	}
}
