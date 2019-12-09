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

package org.fusesource.ide.jmx.karaf.navigator.osgi;

import org.eclipse.ui.views.properties.IPropertySource;

public class BundleStateFacade {
	private final IPropertySource source;

	public BundleStateFacade(IPropertySource source) {
		this.source = source;
	}

	public Long getId() {
		return getProperty("Identifier", Long.class);
	}

	public String getState() {
		return getProperty("State", String.class);
	}

	protected <T> T getProperty(String name, Class<T> aClass) {
		Object value = source.getPropertyValue(name);
		if (value != null && aClass.isInstance(value)) {
			return aClass.cast(value);
		}
		return null;
	}

	public String getLocation() {
		return getProperty("Location", String.class);
	}
}
