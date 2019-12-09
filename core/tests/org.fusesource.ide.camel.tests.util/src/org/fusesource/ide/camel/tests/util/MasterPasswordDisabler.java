/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.tests.util;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.equinox.internal.security.storage.friends.IStorageConstants;

public class MasterPasswordDisabler {

	private String initialValue;

	public void setup() {
		//it avoids to have Master Security Password popup
		IEclipsePreferences node = ConfigurationScope.INSTANCE.getNode("org.eclipse.equinox.security");
		initialValue = node.get(IStorageConstants.DISABLED_PROVIDERS_KEY, "");
		node.put(IStorageConstants.DISABLED_PROVIDERS_KEY, "org.eclipse.equinox.security.ui.defaultpasswordprovider");
	}

	public void tearDown() {
		IEclipsePreferences node = ConfigurationScope.INSTANCE.getNode("org.eclipse.equinox.security");
		if(initialValue.isEmpty()){
			node.remove(IStorageConstants.DISABLED_PROVIDERS_KEY);
		} else {
			node.put(IStorageConstants.DISABLED_PROVIDERS_KEY, initialValue);
		}
	}

}
