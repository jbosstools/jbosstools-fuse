package org.fusesource.ide.commons.contenttype;


/**
 * A content describer for detecting the camel namespace in an XML document
 */
public final class CamelNamespaceXmlContentDescriber extends NamespaceXmlContentDescriberSupport {
	
	protected FindNamespaceHandlerSupport createNamespaceFinder() {
		return new FindCamelNamespaceHandler();
	}
}
