/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.fusesource.ide.foundation.core.util;

import java.beans.Introspector;

public class Strings {

    public static String convertCamelCase(String original) {
    	String display = original.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
    	return capitalize(display);
    }

    public static boolean isBlank(String text) {
    	return isEmpty(text);
    }
    
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }
    
	public static boolean isNonEmptyAndNotOnlySpaces(Object value) {
		return value != null && value.toString().trim().length() > 0;
	}
    
    public static String splitCamelCase(String text) {
        StringBuilder buffer = new StringBuilder();
        char last = 'A';
        for (char c: text.toCharArray()) {
            if (Character.isLowerCase(last) && Character.isUpperCase(c)) {
                buffer.append(" ");
            }
            buffer.append(c);
            last = c;
        }
        return buffer.toString();
    }

    public static String capitalize(String text) {
        if (!isEmpty(text)) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    public static String decapitalize(String text) {
        return Introspector.decapitalize(text);
    }

    public static String toJson(Object n) {
        if (n == null) {
            return "null";
        }
        if (n instanceof Number) {
            return n.toString();
        }
        if (n instanceof String) {
            return "\"" + ((String) n).replaceAll("\\n", "\\\\n") + "\"";
        }
        return "\"" + n.toString() + "\"";
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
	            result += Character.toString(c);
	            lastCharUpperCase = true;
	        } else {
	            if (i==0) {
	                result += Character.toString(Character.toUpperCase(c));
	            } else {
	                result += Character.toString(cleanValue.charAt(i));
	            }
	            lastCharUpperCase = false;
	        }
	    }
	    
	    return result;
	}
	
	/**
	 * Invokes toString() on the object if it is not null
	 * @param input 
	 * @return input.toString() or null otherwise
	 */
	public static String nullSafeToString(Object input){
		return input!=null?input.toString():null;
	}
    
}
