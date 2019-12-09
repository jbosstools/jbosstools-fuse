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

package org.fusesource.ide.foundation.ui.properties;

import java.beans.PropertyDescriptor;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.foundation.core.functions.ReturnType;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;


public class PropertyDescriptors {

	/**
	 * Returns a readable property descriptor, converting camelCase to more readable words if there is no description configured
	 */
	public static String getReadablePropertyName(final IPropertyDescriptor descriptor) {
		String name = descriptor.getDisplayName();
		Object id = descriptor.getId();
		if (id instanceof String && Objects.equal(name, id)) {
			// lets split any camel case to make it more readable
			name = capitalizeAndSplitCamelCase(name);
		}
		return name;
	}

	public static String getReadablePropertyName(PropertyDescriptor descriptor) {
		String name = descriptor.getDisplayName();
		String id = descriptor.getName();
		// TODO use shortName???
		if (Objects.equal(name, id)) {
			name = capitalizeAndSplitCamelCase(name);
		}
		return name;
	}

	protected static String capitalizeAndSplitCamelCase(String name) {
		String name2 = Strings.splitCamelCase(name);
		name = Strings.capitalize(name2);
		return name;
	}

	public static Class<?> getPropertyType(IPropertyDescriptor descriptor) {
		if (descriptor instanceof ReturnType) {
			ReturnType rt = (ReturnType) descriptor;
			return rt.getReturnType();
		} else if (descriptor instanceof TextPropertyDescriptor) {
			return String.class;
		} else {
			FoundationUIActivator.pluginLog().logInfo("Unknown property type for " + descriptor + " of class: "
					+ descriptor.getClass().getName() + " " + descriptor.getId());
			return String.class;
		}
	}

}
