package org.fusesource.ide.commons.properties;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.ui.propsrc.BeanPropertySource;


public class PropertySources {

	public static IPropertySource asPropertySource(Object object) {
		if (object instanceof IPropertySource) {
			return (IPropertySource) object;
		}
		IPropertySource answer = null;
		if (object instanceof IPropertySourceProvider) {
			IPropertySourceProvider provider = (IPropertySourceProvider) object;
			answer = provider.getPropertySource(object);
		}
		if (answer == null && object != null) {
			try {
				return new BeanPropertySource(object);
			} catch (IntrospectionException e) {
				Activator.getLogger().warning("Failed to create BeanPropertySource on " + object + ". " + e, e);
			}
		}
		return answer;
	}

	/**
	 * Converts the list of objects into a list of IPropertySource
	 */
	public static <T> List<IPropertySource> toPropertySourceList(List<T> list) {
		List<IPropertySource> answer = new ArrayList<IPropertySource>();
		if (list != null) {
			for (T object : list) {
				IPropertySource propertySource = PropertySources.asPropertySource(object);
				if (propertySource != null) {
					answer.add(propertySource);
				}
			}
		}
		return answer;
	}

}
