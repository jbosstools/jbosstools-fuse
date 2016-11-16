package org.fusesource.ide.jmx.commons.messages.contenttype;

import org.fusesource.ide.foundation.core.contenttype.*;

/**
 * Detects the messages namespace in an XML document
 */
public class MessagesXmlMatchingStrategy extends XmlMatchingStrategySupport  {

	protected FindMessagesNamespaceHandler createNamespaceFinder() {
		return new FindMessagesNamespaceHandler();
	}
}
