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

package org.fusesource.ide.fabric8.ui.actions;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.fusesource.ide.commons.ui.config.ConfigurationDetails;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.Fabrics;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public class FabricDetails extends ConfigurationDetails {
	private static WritableList fabricDetailList = WritableList.withElementType(FabricDetails.class);
	private static AtomicBoolean loadedDetalList = new AtomicBoolean(false);
	private static FabricDetails exemplar = new FabricDetails();

	private String name;
	private String urls;
	private String userName = "";
	private String password = "";
	private String zkPassword;

	public static void reloadDetailList() {
		fabricDetailList.clear();
		load(fabricDetailList);

	}

	public static WritableList getDetailList() {
		if (loadedDetalList.compareAndSet(false, true)) {
			load(fabricDetailList);
		}
		return FabricDetails.fabricDetailList;
	}

	public static FabricDetails newInstance(String fabricName, String uris) {
		return new FabricDetails(fabricName, uris);
	}

	public static FabricDetails asFabricDetails(Object element) {
		if (element instanceof FabricDetails) {
			return (FabricDetails) element;
		}
		return null;
	}

	protected static void load(Collection<FabricDetails> cloudDetailList) {
		Preferences node = exemplar.getConfigurationNode();
		try {
			String[] childrenNames = node.childrenNames();
			for (String name : childrenNames) {
				cloudDetailList.add(new FabricDetails(name, node.node(name)));
			}
		} catch (BackingStoreException e) {
			FabricPlugin.showUserError("Failed to load fabric details", e.getMessage(), e);
		}
	}

	public FabricDetails() {
		this("Local Fabric", Fabrics.DEFAULT_FABRIC_URL);
	}

	public FabricDetails(String name, String urls) {
		this.name = name;
		if (urls.startsWith("http://") && urls.endsWith("/jolokia/")) {
			this.urls = urls;	
		} else {
			if (urls.contains(",")) {
				// seems to be more than one public url - take the first best
				this.urls = String.format(Fabrics.DEFAULT_FABRIC_URL_FORMAT, urls.substring(0, urls.indexOf(',')).trim());
			} else {
				this.urls = String.format(Fabrics.DEFAULT_FABRIC_URL_FORMAT, urls.trim());
			}
		}		
	}

	public FabricDetails(String id, Preferences node) {
		super(id);
		this.name = node.get("name", "");
		this.urls = node.get("urls", "");
		this.userName = node.get("userName", "admin");
		this.password = node.get("password", "admin");
		this.zkPassword = node.get("zkPassword", "");
	}

	public static FabricDetails copy(FabricDetails copy) {
		Preferences node = exemplar.getConfigurationNode();
		String id = copy.getId();
		return new FabricDetails(id, node.node(id));
	}

	@Override
	protected void store(Preferences node) {
		node.put("name", name);
		node.put("urls", urls);
		node.put("userName", userName);
		node.put("password", password);
		node.put("zkPassword", zkPassword);
	}

	@Override
	public String toString() {
		return "FabricDetails(" + name + ", " + urls + ")";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrls() {
		return urls;
	}

	public void setUrls(String urls) {
		this.urls = urls;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param zkPassword the zkPassword to set
	 */
	public void setZkPassword(String zkPassword) {
		this.zkPassword = zkPassword;
	}
	
	/**
	 * @return the zkPassword
	 */
	public String getZkPassword() {
		return this.zkPassword;
	}
}
