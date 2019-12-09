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

import java.util.Date;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.fusesource.ide.foundation.ui.label.TimeThenDateLabelProvider;


public class LastModifiedLabelProvider extends TimeThenDateLabelProvider {

	@Override
	protected Object convertValue(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof CompositeDataPropertySource) {
			int idx = cell.getColumnIndex();
			CompositeDataPropertySource cdps = (CompositeDataPropertySource) element;
			IPropertyDescriptor[] propertyDescriptors = cdps.getPropertyDescriptors();
			if (propertyDescriptors != null && propertyDescriptors.length > idx && idx >= 0) {
				IPropertyDescriptor descriptor = propertyDescriptors[idx];
				if (descriptor != null) {
					Object value = cdps.getPropertyValue(descriptor.getId());
					if (value instanceof Long) {
						Long l = (Long) value;
						long timestamp = l.longValue();
						if (timestamp <= 0) {
							return null;
						}
						return new Date(timestamp);
					}
					return value;
				}
			}
		}
		return null;
	}

}
