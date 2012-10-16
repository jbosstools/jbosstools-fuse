package org.fusesource.ide.commons.contenttype;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lhein
 */
public class SpringNamespaceHandler extends FindNamespaceHandlerSupport {
	
	private static String[] springNamespaces = new String[]{ "http://camel.apache.org/schema/spring" };
	public static Set<String> sprNamespaces = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(springNamespaces)));
	
	public SpringNamespaceHandler() {
		super(sprNamespaces);
	}
}
