/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.jmx;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.jmx.internal.KarafJVMFacadeUtility;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.core.JMXException;
import org.jboss.tools.jmx.core.tree.Root;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.local.JVMConnectionUtility;

/**
 * This class is primarily a stub to demonstrate proof of concept
 * for enabling the JMX integration for Karaf. 
 * 
 * This class currently does not create custom connection types,
 * but will provide either a dummy JMX connection (if the server is stopped)
 * that cannot be connected to, or, a JVMConnectionWrapper if a matching one is found.
 */
@Deprecated
public class KarafJMXModel {
	public static KarafJMXModel instance;
	public synchronized static KarafJMXModel getDefault() {
		if( instance == null ) {
			instance = new KarafJMXModel();
		}
		return instance;
	}
	
	private HashMap<IServer, IConnectionWrapper> dummies;
	protected KarafJMXModel() {
		dummies = new HashMap<IServer, IConnectionWrapper>();
	}
	
	public IConnectionWrapper findConnectionWrapper(IServer server) {
		IConnectionWrapper found = findJVMConnectionWrapper(server);
		if( found == null ) {
			IConnectionWrapper dummy = dummies.get(server);
			if( dummy == null ) {
				dummy = new DummyConnectionWrapper();
				dummies.put(server, dummy);
			}
			return dummy;
		}
		return found;
	}
	
	protected IConnectionWrapper findJVMConnectionWrapper(IServer server) {
		// TODO look through the list of JVM connections to find the one 
		// we think belongs to us right now
		IActiveJvm jvm = KarafJVMFacadeUtility.findJvmForServer(server);
		if( jvm != null )
			return JVMConnectionUtility.findConnectionForJvm(jvm);
		return null;
	}
	
	
	/**
	 * This class is a dummy and is meant to take no action, and indicate
	 * the user also can not take any action, to control this connection.
	 * 
	 * @author rob
	 *
	 */
	private static class DummyConnectionWrapper implements IConnectionWrapper {

		@Override
		public IConnectionProvider getProvider() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isConnected() {
			return false;
		}

		@Override
		public boolean canControl() {
			return false;
		}

		@Override
		public void connect() throws IOException {
		}

		@Override
		public void disconnect() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void loadRoot(IProgressMonitor monitor) throws CoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Root getRoot() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void run(IJMXRunnable runnable) throws JMXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void run(IJMXRunnable runnable, HashMap<String, String> prefs)
				throws JMXException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
