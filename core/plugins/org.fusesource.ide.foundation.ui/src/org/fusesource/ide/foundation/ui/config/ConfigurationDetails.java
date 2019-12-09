/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.config;

import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.util.BeanSupport;
import org.fusesource.ide.foundation.ui.util.DialogUtils;
import org.fusesource.ide.foundation.ui.util.PreferencesHelper;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public abstract class ConfigurationDetails extends BeanSupport {
	private String id;

	public ConfigurationDetails() {
	}

	public ConfigurationDetails(String id) {
		this.id = id;
	}

	protected abstract void store(Preferences node);

	public Preferences getConfigurationNode() {
		return PreferencesHelper.configurationNode(getConfigurationNodeId());
	}

	protected String getConfigurationNodeId() {
		return getClass().getName();
	}

	public void flush() {
		try {
			Preferences node = getConfigurationNode();
			if (id == null) {
				int i = node.childrenNames().length;
				while (true) {
					id = getIdPrefix() + (++i);
					if (!node.nodeExists(id)) {
						break;
					}
				}
			}
			Preferences chlld = node.node(id);
			store(chlld);
			PreferencesHelper.flush(node);
		} catch (BackingStoreException e) {
			DialogUtils.showUserError(FoundationUIActivator.PLUGIN_ID, "Unable to store configuration...", "Failed to store configuration: " + this, e);
		}
	}

	public void delete() {
		try {
			Preferences node = getConfigurationNode();
			if (id != null) {
				Preferences child = node.node(id);
				child.removeNode();
			}
			PreferencesHelper.flush(node);
		} catch (BackingStoreException e) {
			DialogUtils.showUserError(FoundationUIActivator.PLUGIN_ID, "Unable to remove configuration...", "Failed to remove + " + this + " from configuration store", e);
		}
	}

	protected String getIdPrefix() {
		return "n";
	}

	public String getId() {
		return id;
	}

}