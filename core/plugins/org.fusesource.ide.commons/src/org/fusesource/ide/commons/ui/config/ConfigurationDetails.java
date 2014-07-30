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

package org.fusesource.ide.commons.ui.config;

import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.PreferencesHelper;
import org.fusesource.ide.commons.util.BeanSupport;
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
			Activator.showUserError("Failed to store configuration: " + this, e.getMessage(), e);
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
			Activator.showUserError("Failed to remove + " + this + " from configuration store", e.getMessage(), e);
		}
	}

	protected String getIdPrefix() {
		return "n";
	}

	public String getId() {
		return id;
	}

}