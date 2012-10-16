package org.fusesource.ide.commons.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.commons.util.ReturnType;


/**
 * @author jstrachan
 */
public class BooleanPropertyDescriptor extends PropertyDescriptor implements ReturnType {

	/**
	 * creates a property descriptor for boolean properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public BooleanPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	@Override
	public Class<?> getReturnType() {
		return Boolean.class;
	}
}
