package org.fusesource.ide.fabric.navigator.osgi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric.FabricPlugin;


public class TabularDataHelper {

	/**
	 * Converts a TabularData to a list of IPropertySource
	 */
	public static List<IPropertySource> toPropertySources(TabularData tabularData) {
		List<IPropertySource> answer = new ArrayList<IPropertySource>();
		try {
			if (tabularData != null) {
				final Collection<?> rows = tabularData.values();
				for (Object row : rows) {
					if (row instanceof CompositeData) {
						CompositeData cd = (CompositeData) row;
						answer.add(new CompositeDataPropertySource(cd));
					} else {
						System.out.println("===== uknown row type: " + row + " of type " + Objects.typeName(row));
					}
				}
			}
		} catch (Exception e) {
			FabricPlugin.getLogger().error("Failed to convert TabularData to List<IPropertySource>: " + e, e);
		}
		return answer;
	}
}
