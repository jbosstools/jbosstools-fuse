package org.fusesource.ide.jmx.commons.messages.contenttype;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.foundation.core.util.ResourceModelUtils;
import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;
import org.fusesource.ide.foundation.core.xml.namespace.NamespaceXmlContentDescriberSupport;


/**
 * A content describer for detecting the messages namespace in an XML document
 */
public final class MessagesNamespaceXmlContentDescriber extends NamespaceXmlContentDescriberSupport {
	
	public static final String ID = "org.fusesource.ide.commmons.messagesContentType";
	
	protected FindNamespaceHandlerSupport createNamespaceFinder() {
		return new FindMessagesNamespaceHandler();
	}

	public static boolean isXmlFormat(IFile file) throws CoreException {
		return ResourceModelUtils.isContentTypeId(file, ID);
	}
}
