/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.jmx.karaf.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerEvent;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;
import org.fusesource.ide.server.karaf.core.server.KarafServerDelegate;
import org.jboss.ide.eclipse.as.core.server.UnitedServerListener;
import org.jboss.ide.eclipse.as.core.server.UnitedServerListenerManager;
import org.jboss.tools.jmx.core.AbstractConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionCategory;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionProviderEventEmitter;
import org.jboss.tools.jmx.core.IConnectionWrapper;

public abstract class AbstractKarafJMXConnectionProvider extends AbstractConnectionProvider 
	implements IConnectionProvider, IConnectionProviderEventEmitter, IConnectionCategory {

	private HashMap<String, IConnectionWrapper> idToConnection;
	public AbstractKarafJMXConnectionProvider() {
		UnitedServerListener listener = createUnitedListener();
		UnitedServerListenerManager.getDefault().addListener(listener);
	}
	
	private UnitedServerListener createUnitedListener() {
		UnitedServerListener listener = new UnitedServerListener() {
			public boolean canHandleServer(IServer server) {
				if (server.loadAdapter(KarafServerDelegate.class, new NullProgressMonitor()) != null)
					return true;
				return false;
			}

			public void serverChanged(ServerEvent event) {
				IConnectionWrapper con = idToConnection.get(event.getServer().getId());
				if( con != null ) {
					if( serverSwitchesToState(event, IServer.STATE_STARTED)) {
						fireAdded(con);
					} else if( serverSwitchesToState(event, IServer.STATE_STOPPED)) {
						fireRemoved(con);
					}
				}
			}

			public void serverAdded(IServer server) {
				if( belongsHere(server)) {
					getConnections();
					if( !idToConnection.containsKey(server.getId())) {
						IConnectionWrapper connection = createConnection(server);
						idToConnection.put(server.getId(), connection);
						if( connection != null && server.getServerState() == IServer.STATE_STARTED )
							fireAdded(idToConnection.get(server.getId()));
					}
				}
			}

			public void serverChanged(IServer server) {
				if( belongsHere(server)) {
					getConnections();
					Object o = idToConnection.get(server.getId());
					if( o == null ) {
						IConnectionWrapper connection = createConnection(server);
						idToConnection.put(server.getId(), connection);
						if( connection != null && server.getServerState() == IServer.STATE_STARTED )
							fireAdded(idToConnection.get(server.getId()));
					}
				}
			}

			public void serverRemoved(IServer server) {
				if( belongsHere(server)) {
					IConnectionWrapper connection;
					if( idToConnection != null ) {
						connection = idToConnection.get(server.getId());
						if( connection != null ) {
							idToConnection.remove(server.getId());
							fireRemoved(connection);
						}
					} else {
						// hasn't been initialized yet
						getConnections();
						
						// but now its missing from the collection, so make one up
						IConnectionWrapper dummy = createConnection(server);
						
						// Make sure we don't fire a removal for a connection that doesn't exist
						if( dummy != null )
							fireRemoved(dummy);
					}
				}
			}
		};
		return listener;
	}
	
	protected abstract boolean belongsHere(IServer server);
	public abstract String getId();
	protected abstract IConnectionWrapper createConnection(IServer server);
	public abstract String getName(IConnectionWrapper wrapper);


	public IConnectionWrapper findConnection(IServer s) {
		getConnections();
		return idToConnection.get(s.getId());
	}
	
	public IConnectionWrapper[] getConnections() {
		// do it all on demand right now
		if( idToConnection == null ) {
			// load them all
			idToConnection = new HashMap<String, IConnectionWrapper>();
			IServer[] allServers = ServerCore.getServers();
			IConnectionWrapper c;
			for( int i = 0; i < allServers.length; i++ ) {
				if( belongsHere(allServers[i])) {
					c = createConnection(allServers[i]);
					if( c != null ) 
						idToConnection.put(allServers[i].getId(), c);
				}
			}
		} 
		ArrayList<IConnectionWrapper> list = new ArrayList<IConnectionWrapper>();
		Set<String> serverIds = idToConnection.keySet();
		Iterator<String> it = serverIds.iterator();
		while(it.hasNext()) {
			String id = it.next();
			if( isServerStarted(id)) {
				list.add(idToConnection.get(id));
			}
		}
		return list.toArray(new IConnectionWrapper[list.size()]);
	}
	
	private boolean isServerStarted(String id) {
		IServer s = ServerCore.findServer(id);
		if( s != null ) {
			return s.getServerState() == IServer.STATE_STARTED;
		}
		return false;
	}
	
	public boolean canCreate() {
		return false;
	}

	@SuppressWarnings(value={"unchecked"})
	public IConnectionWrapper createConnection(Map map) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, KarafJMXPlugin.PLUGIN_ID, "", null));
	}

	public void addConnection(IConnectionWrapper connection) {
		// Not Supported
	}
	public void removeConnection(IConnectionWrapper connection) {
		// Not Supported
	}
	public boolean canDelete(IConnectionWrapper wrapper) {
		return false;
	}
	public void connectionChanged(IConnectionWrapper connection) {
		// do nothing
	}

	@Override
	public String getCategoryId() {
		return IConnectionCategory.SERVER_CATEGORY;
	}
}
