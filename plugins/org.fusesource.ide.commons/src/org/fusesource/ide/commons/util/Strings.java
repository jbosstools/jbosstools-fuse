
package org.fusesource.ide.commons.util;

public class Strings {
	public static String getOrElse(Object text) {
		return getOrElse(text, "");
	}

	public static String getOrElse(Object text, String defaultValue) {
		if (text == null) {
			return defaultValue;
		} else {
			return text.toString();
		}
	}

	public static String capitalize(String name) {
		if (name.length() > 0) {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		return name;
	}
	
	public static boolean isBlank(String text) {
		return text == null || text.trim().length() == 0;
	}

	public static String join(String[] names, String separator) {
		StringBuilder buffer = new StringBuilder();
		boolean first = true;
		for (String name : names) {
			buffer.append(name);
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
		}
		return buffer.toString();
	}

	/**
	 * Joins a list of nullable values together with the given separator between non-null values
	 */
	public static String join(String separator, Object... values) {
		StringBuilder buffer = new StringBuilder();
		boolean first = true;
		for (Object value : values) {
			String text = getOrElse(value);
			if (!isBlank(text)) {
				if (first) {
					first = false;
				} else {
					buffer.append(separator);
				}
				buffer.append(text);
			}
		}
		return buffer.toString();
	}

	/**
	 * Returns true if any of the given strings contain the given filter
	 */
	public static boolean contains(String filter, String... values) {
		for (String value : values) {
			if (value.contains(filter)) {
				return true;
			}
		}
		return false;
	}
}
