package org.fusesource.ide.commons.util;

/**
 * Represents an object that can be searched for a given text string
 */
public interface TextFilter {
	/**
	 * Returns true if this object matches the given search text
	 */
	boolean matches(String searchText);
}
