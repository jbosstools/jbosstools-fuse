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

package org.fusesource.ide.foundation.ui.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class TextFilters {

	public static boolean matches(String searchText, String text) {
		return text != null && text.contains(searchText);
	}

	/**
	 * Returns true if the text matches the given filter or false if the filter
	 * is null or it doesn't match
	 */
	public static boolean matches(String searchText, TextFilter filter) {
		return filter != null && filter.matches(searchText);
	}

	/**
	 * Returns true if the search text matches the given object; which is either
	 * a {@link TextFilter} or its converted to a String
	 */
	public static boolean matches(String searchText, Object object) {
		if (searchText == null || searchText.trim().length() == 0) {
			return true;
		} else if (object instanceof TextFilter) {
			return matches(searchText, (TextFilter) object);
		} else if (object instanceof Map) {
			return matches(searchText, (Map<?,?>) object);
		} else if (object instanceof Collection) {
			return matches(searchText, (Collection<?>) object);
		} else if (object instanceof IPropertySource) {
			return matches(searchText, (IPropertySource)object);
		} else if (object instanceof Object[]) {
			return matches(searchText, Arrays.asList((Object[]) object));
		} else {
			if (object != null) {
				return matches(searchText, object.toString());
			}
		}
		return false;
	}

	public static boolean matches(String searchText, IPropertySource property) {
		IPropertyDescriptor[] descriptors = property.getPropertyDescriptors();
		for (IPropertyDescriptor descriptor : descriptors) {
			Object value = property.getPropertyValue(descriptor.getId());
			if (value != null) {
				if (matches(searchText, value)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean matches(String searchText, Map map) {
		Set<Map.Entry> entrySet = map.entrySet();
		for (Map.Entry entry : entrySet) {
			if (matches(searchText, entry.getKey()) || matches(searchText, entry.getValue())) {
				return true;
			}
		}
		return false;
	}

	public static boolean matches(String searchText, Collection<?> collection) {
		for (Object element : collection) {
			if (matches(searchText, element)) {
				return true;
			}
		}
		return false;
	}
}
