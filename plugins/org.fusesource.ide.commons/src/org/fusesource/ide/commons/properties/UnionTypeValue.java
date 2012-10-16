package org.fusesource.ide.commons.properties;

/**
 * A possible kind of value of a {@link ComplexUnionPropertyDescriptor}
 * 
 */
public class UnionTypeValue {
	private final String id;
	private final Class<?> valueType;

	public UnionTypeValue(String id, Class<?> valueType) {
		this.id = id;
		this.valueType = valueType;
	}

	public String getId() {
		return id;
	}

	public Class<?> getValueType() {
		return valueType;
	}
}
