package org.fusesource.ide.commons.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.commons.util.ReturnType;


/**
 * @author jstrachan
 */
public class EnumPropertyDescriptor extends PropertyDescriptor implements ReturnType {

	private final Class<? extends Enum> enumType;

	/**
	 * creates a property descriptor for enum properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public EnumPropertyDescriptor(Object id, String displayName, Class<? extends Enum> enumType) {
		super(id, displayName);
		this.enumType = enumType;
	}

	public Class<? extends Enum> getEnumType() {
		return enumType;
	}

	@Override
	public Class<?> getReturnType() {
		return enumType;
	}}
