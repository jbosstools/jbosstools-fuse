package org.fusesource.ide.commons.contenttype;

/**
 * @author lhein
 */
public class BlueprintXmlMatchingStrategy extends XmlMatchingStrategySupport {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport#createNamespaceFinder()
	 */
	@Override
	protected FindNamespaceHandlerSupport createNamespaceFinder() {
		return new BlueprintNamespaceHandler();
	}
}
