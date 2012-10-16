package org.fusesource.ide.commons.properties;

import java.util.List;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.commons.util.ReturnType;


/**
 * @author jstrachan
 */
public class ListPropertyDescriptor extends PropertyDescriptor implements ReturnType {

	/**
	 * creates a property descriptor for list properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public ListPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public Class<?> getElementType() {
		return String.class;
	}


	@Override
	public Class<?> getReturnType() {
		return List.class;
	}
}
