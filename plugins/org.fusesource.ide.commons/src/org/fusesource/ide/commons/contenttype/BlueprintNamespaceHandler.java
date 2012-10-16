package org.fusesource.ide.commons.contenttype;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lhein
 */
public class BlueprintNamespaceHandler extends FindNamespaceHandlerSupport {
	
	private static String[] blueprintNamespaces = new String[]{ "http://camel.apache.org/schema/blueprint" };
	public static Set<String> bpNamespaces = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(blueprintNamespaces)));
	
	public BlueprintNamespaceHandler() {
		super(bpNamespaces);
	}
}
