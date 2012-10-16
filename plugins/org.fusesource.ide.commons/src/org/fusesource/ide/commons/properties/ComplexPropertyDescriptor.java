package org.fusesource.ide.commons.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.commons.util.ReturnType;


/**
 * @author jstrachan
 */
public class ComplexPropertyDescriptor extends PropertyDescriptor implements ReturnType {

	private final Class<?> propertyType;

	/**
	 * creates a property descriptor for complex properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public ComplexPropertyDescriptor(Object id, String displayName, Class<?> propertyType) {
		super(id, displayName);
		this.propertyType = propertyType;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}


	@Override
	public Class<?> getReturnType() {
		return propertyType;
	}
}
