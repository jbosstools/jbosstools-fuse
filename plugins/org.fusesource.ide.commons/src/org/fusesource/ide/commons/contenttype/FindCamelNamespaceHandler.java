package org.fusesource.ide.commons.contenttype;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;



public class FindCamelNamespaceHandler extends FindNamespaceHandlerSupport {

	private static String[] camelNamesapceArray = new String[]{ "http://camel.apache.org/schema/spring", "http://camel.apache.org/schema/blueprint" };
	public static Set<String> camelNamespaces = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(camelNamesapceArray)));
	
	public FindCamelNamespaceHandler() {
		super(camelNamespaces);
	}
}