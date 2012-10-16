package org.fusesource.ide.camel.model;

/**
 * Strategy to determine if an object has a value
 * <p/>
 * This is used to determine for example any UI property descriptors has any value assigned
 * by the end user.
 */
public interface HasValue {

	/**
	 * Is there a value.
	 */
	boolean hasValue();

}
