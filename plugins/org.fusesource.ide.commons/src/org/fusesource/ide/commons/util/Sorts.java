package org.fusesource.ide.commons.util;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Sorts {
	
	public static String[] toSortedStringArray(Collection<String> collection) { 
		Set<String> keys = new TreeSet<String>(collection);
		return keys.toArray(new String[keys.size()]);

	}

}
