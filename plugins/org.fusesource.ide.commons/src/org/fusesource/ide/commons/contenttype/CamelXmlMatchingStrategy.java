package org.fusesource.ide.commons.contenttype;


/**
 * Detects the Camel namespace in an XML document to determine if we should open
 * with Rider
 */
public class CamelXmlMatchingStrategy extends XmlMatchingStrategySupport  {

	protected FindCamelNamespaceHandler createNamespaceFinder() {
		return new FindCamelNamespaceHandler();
	}
}
