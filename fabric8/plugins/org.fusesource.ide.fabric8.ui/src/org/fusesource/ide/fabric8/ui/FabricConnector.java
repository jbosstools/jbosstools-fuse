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

package org.fusesource.ide.fabric8.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.ide.fabric8.core.connector.Fabric8Connector;
import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.connector.JolokiaFabric8Connector;
import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.ui.actions.FabricDetails;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;

/**
 * @author lhein
 */
public class FabricConnector {
	private final Fabric fabric;
	private final FabricDetails details;
	private Fabric8Facade fabricService;
	private Fabric8Connector con;
	private final String url;
	private AtomicBoolean connected = new AtomicBoolean(false);
	private ArrayList<FabricConnectionListener> connectionListeners = new ArrayList<FabricConnectionListener>();
	
	/**
	 * creates the fabric connector
	 * 
	 * @param fabric
	 */
	public FabricConnector(Fabric fabric) {
		this.fabric = fabric;
		this.details = fabric.getDetails();
		this.url = details.getUrls();
		initialize();
	}

	/**
	 * does the initialization
	 */
	protected void initialize() {
		FabricPlugin.getLogger().debug("Starting to connect Fabric on: " + url);
		this.con = new Fabric8Connector(JolokiaFabric8Connector.getFabric8Connector(details.getUserName(), details.getPassword(), url));
	}

	/**
	 * returns the list of available containers
	 * 
	 * @return
	 */
	public List<ContainerDTO> getAgents() {
		Fabric8Facade service = getFabricService();
		if (service != null) {
			return service.getContainers();
		}
		return new ArrayList<ContainerDTO>();
	}

	/**
	 * returns the list of available versions
	 * 
	 * @return
	 */
	public List<VersionDTO> getVersions() {
		Fabric8Facade service = getFabricService();
		if (service != null) {
			return service.getVersions();
		}
		return new ArrayList<VersionDTO>();
	}

	/**
	 * returns the name of the fabric node
	 * 
	 * @return
	 */
	public String getName() {
		return details.getName();
	}

	/**
	 * returns the url of the fabric
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * returns the default version
	 * 
	 * @return
	 */
	public String getDefaultVersionId() {
		Fabric8Facade service = getFabricService();
		if (service != null) {
			VersionDTO defaultVersion = service.getDefaultVersion();
			if (defaultVersion != null) {
				return defaultVersion.getId();
			}
		}
		return null;
	}

	/**
	 * returns the fabric service instance
	 * 
	 * @return
	 */
	public Fabric8Facade getFabricService() {
		isConnected();
		return fabricService;
	}

	/**
	 * connects to the fabric
	 */
	public synchronized void connect() throws IOException {
		initialize();
		this.con.connect();
		if (this.con.isConnected()) {
			this.connected.set(true);
			this.fabricService = con.getConnection().getFabricFacade();
			notifyListeners(FabricConnectionListener.EVENT_TYPE_CONNECT);
		} else {
			notifyListeners(FabricConnectionListener.EVENT_TYPE_DISCONNECT);
			// connection attempt failed...throw Exception
			throw new IOException("Unable to connect to Fabric. Please make sure Fabric is running on the specified host, the used ports are not blocked and the ZooKeeper password is correct. Also check if the Fabric8 version is supported (version >= 1.2).");
		}
	}
	
	/**
	 * disconnects from fabric
	 */
	public synchronized void disconnect() {
		dispose();
		this.connected.set(false);
		notifyListeners(FabricConnectionListener.EVENT_TYPE_DISCONNECT);
	}
	
	/**
	 * checks if we are connected to the fabric8 instance
	 * 
	 * @return
	 */
	public boolean isConnected() {
		
		if (this.connected.get() == false) return false;
		
		boolean c = this.con != null && this.con.isConnected();
		if (c == true) {
			this.connected.set(c);
			return this.connected.get();
		}
		
		if (this.connected.get() == true && c == false) {
			// seems we lost the connection - retry
			try {
				connect();
			} catch (IOException ex) {
				FabricPlugin.getLogger().error(ex);
				disconnect();
				FabricPlugin.showUserError("Connection failed...", "Unable to connect to Fabric. Please make sure Fabric is running on the specified host, the used ports are not blocked and the ZooKeeper password is correct. Also check if the Fabric8 version is supported (version >= 1.2).", ex);
				return false;
			}

		}
		return connected.get();
	}

	/**
	 * discards connection
	 */
	public void dispose() {
		if (this.con != null) this.con.disconnect();
		this.fabricService = null;
	}
	
	/**
	 * adds a connection listener
	 * 
	 * @param listener
	 */
	public synchronized void addFabricConnectionListener(FabricConnectionListener listener) {
		if (!this.connectionListeners.contains(listener)) {
			this.connectionListeners.add(listener);
		}
	}
	
	/**
	 * removes the listener
	 * 
	 * @param listener
	 */
	public synchronized void removeFabricConnectionListener(FabricConnectionListener listener) {
		if (this.connectionListeners.contains(listener)) {
			this.connectionListeners.remove(listener);
		}
	}
	
	/**
	 * notifies the listeners
	 * 
	 * @param eventType
	 */
	private void notifyListeners(String eventType) {
		final HashSet<FabricConnectionListener> copy = new HashSet<FabricConnectionListener>(this.connectionListeners);
		for (FabricConnectionListener l : copy) {
			if (eventType.equals(FabricConnectionListener.EVENT_TYPE_CONNECT)) {
				l.onFabricConnected();
			} else {
				l.onFabricDisconnected();
			}
		}
	}
}
