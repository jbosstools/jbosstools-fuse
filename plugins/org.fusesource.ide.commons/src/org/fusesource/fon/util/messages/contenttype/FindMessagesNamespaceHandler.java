package org.fusesource.fon.util.messages.contenttype;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.fusesource.ide.commons.contenttype.FindNamespaceHandlerSupport;


public class FindMessagesNamespaceHandler extends FindNamespaceHandlerSupport {

	private static String[] camelNamesapceArray = new String[]{ "http://fabric.fusesource.org/schema/messages" };
	public static Set<String> camelNamespaces = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(camelNamesapceArray)));
	
	public FindMessagesNamespaceHandler() {
		super(camelNamespaces);
	}
}