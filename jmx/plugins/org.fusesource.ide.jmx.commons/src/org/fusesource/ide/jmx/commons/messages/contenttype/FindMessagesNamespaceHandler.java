package org.fusesource.ide.jmx.commons.messages.contenttype;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;


public class FindMessagesNamespaceHandler extends FindNamespaceHandlerSupport {

	private static String[] camelNamesapceArray = new String[]{ "http://fabric.fusesource.org/schema/messages" };
	public static Set<String> camelNamespaces = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(camelNamesapceArray)));
	
	public FindMessagesNamespaceHandler() {
		super(camelNamespaces);
	}
}