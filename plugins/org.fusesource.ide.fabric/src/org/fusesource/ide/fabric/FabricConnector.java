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

package org.fusesource.ide.fabric;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.ui.statushandlers.StatusManager;
import io.fabric8.api.Container;
import io.fabric8.api.FabricService;
import io.fabric8.api.FabricStatus;
import io.fabric8.api.Version;
import io.fabric8.jolokia.facade.JolokiaFabricConnector;
import org.fusesource.ide.fabric.actions.FabricDetails;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.osgi.framework.BundleContext;


public class FabricConnector {
	private final Fabric fabric;
	private final FabricDetails details;
	private final String url;
	private FabricService fabricService;
	private AtomicBoolean initialised = new AtomicBoolean(false);
	private AtomicBoolean connected = new AtomicBoolean(false);
	private JolokiaFabricConnector connector;
	
	
	public FabricConnector(Fabric fabric) {
		this.fabric = fabric;
		this.details = fabric.getDetails();
		this.url = details.getUrls();
	}

	protected void initialised() throws Exception {
		FabricPlugin.getLogger().debug("Starting to connect Fabric on: " + url);

		this.connector = JolokiaFabricConnector.getFabricConnector(this.details.getUserName(), this.details.getPassword(), this.details.getUrls());
		
		if (fabricService == null) {
			this.fabricService = this.connector.getFabricServiceFacade();
		}
		
		// Trigger a FabricNotConnectedException if the connection failed.
		checkConnected();
	}

	public Container[] getAgents() {
		FabricService service = getFabricService();
		if (service == null) {
			return null;
		}
		return service.getContainers();
	}

	public Version[] getVersions() {
		FabricService service = getFabricService();
		if (service == null) {
			return null;
		}
		return service.getVersions();
	}

	public BundleContext getBundleContext() {
		return FabricPlugin.getDefault().getBundle().getBundleContext();
	}

	public String getName() {
		return details.getName();
	}

	public String getUrl() {
		return url;
	}

	public String getDefaultVersionId() {
		FabricService service = getFabricService();
		if (service != null) {
			Version defaultVersion = service.getDefaultVersion();
			if (defaultVersion != null) {
				return defaultVersion.getId();
			}
		}
		return null;
	}

	public JolokiaFabricConnector getConnector() {
		return this.connector;
	}
	
	public FabricService getFabricService() {
		checkConnected();
		return fabricService;
	}
	
	public void checkConnected() {
		if (initialised.compareAndSet(false, true)) {
			try {
				initialised();
			} catch (Exception e) {
				FabricNotConnectedException fnce = new FabricNotConnectedException(this, e);
				final String PID = FabricPlugin.PLUGIN_ID;
				MultiStatus status = new MultiStatus(PID, 1, "Unable to connect to Fabric. Please make sure Fabric is running on the specified host, the used ports are not blocked and the ZooKeeper password is correct.", fnce);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
				fabric.onDisconnect();
				throw fnce;
			}
		} else {
			try {
				FabricStatus status = this.fabricService.getFabricStatus();
				connected.set(status != null);
			} catch (Exception ex) {
				connected.set(false);
				throw new FabricNotConnectedException(this, ex);
			}
		}
	}

	public void setFabricService(FabricService fabricService) {
		this.fabricService = fabricService;
	}

	public boolean isConnected() {
		try {
			checkConnected();
		} catch (Exception e) {
			FabricPlugin.getLogger().debug("Fabric " + this + " is not connected  " + e);
			fabric.onDisconnect();
			return false;
		}
		return connected.get();
	}
	
	public void dispose() {
		if (this.connector != null) this.connector.disconnect();
		this.connector = null;
		this.fabricService = null;
	}
}
