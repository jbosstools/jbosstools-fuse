package org.fusesource.ide.jmx.commons.messages.contenttype;

import org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport;

/**
 * Detects the messages namespace in an XML document
 */
public class MessagesXmlMatchingStrategy extends XmlMatchingStrategySupport  {

	protected FindMessagesNamespaceHandler createNamespaceFinder() {
		return new FindMessagesNamespaceHandler();
	}
}
