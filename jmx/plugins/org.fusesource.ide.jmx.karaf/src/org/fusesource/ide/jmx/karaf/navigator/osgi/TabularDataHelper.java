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

package org.fusesource.ide.jmx.karaf.navigator.osgi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;


public class TabularDataHelper {
	
	private TabularDataHelper() {
		//static access only
	}

	/**
	 * Converts a TabularData to a list of IPropertySource
	 */
	public static List<IPropertySource> toPropertySources(TabularData tabularData) {
		List<IPropertySource> answer = new ArrayList<>();
		try {
			if (tabularData != null) {
				final Collection<?> rows = tabularData.values();
				for (Object row : rows) {
					if (row instanceof CompositeData) {
						CompositeData cd = (CompositeData) row;
						answer.add(new CompositeDataPropertySource(cd));
					} else {
						KarafJMXPlugin.getLogger().debug("===== unknown row type: " + row + " of type " + Objects.typeName(row));
					}
				}
			}
		} catch (Exception e) {
			KarafJMXPlugin.getLogger().error("Failed to convert TabularData to List<IPropertySource>: " + e, e);
		}
		return answer;
	}
}
