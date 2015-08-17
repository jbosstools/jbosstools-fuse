
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.commons.util;

public class Strings {
	

    public static String convertCamelCase(String original) {
    	String display = original.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
    	return capitalize(display);
    }
    
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
	
	public static String humanize(String value) {
	    String result = "";
	    
	    String cleanValue = value.trim();
	    boolean lastCharUpperCase = false;
	    for (int i=0; i<cleanValue.length(); i++) {
	        char c = cleanValue.charAt(i);
	        if (Character.isUpperCase(c)) {
	            if (!lastCharUpperCase || result.endsWith(" ID")) {
	                result += " ";
	            }
	            result += c;
	            lastCharUpperCase = true;
	        } else {
	            if (i==0) {
	                result += Character.toUpperCase(c);
	            } else {
	                result += cleanValue.charAt(i);
	            }
	            lastCharUpperCase = false;
	        }
	    }
	    
	    return result;
	}
}
