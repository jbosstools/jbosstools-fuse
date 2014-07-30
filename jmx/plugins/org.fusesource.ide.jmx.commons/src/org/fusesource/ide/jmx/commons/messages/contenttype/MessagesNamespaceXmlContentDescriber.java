package org.fusesource.ide.jmx.commons.messages.contenttype;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.commons.contenttype.FindNamespaceHandlerSupport;
import org.fusesource.ide.commons.contenttype.NamespaceXmlContentDescriberSupport;
import org.fusesource.ide.commons.util.IFiles;


/**
 * A content describer for detecting the messages namespace in an XML document
 */
public final class MessagesNamespaceXmlContentDescriber extends NamespaceXmlContentDescriberSupport {
	
	public static final String ID = "org.fusesource.ide.commmons.messagesContentType";
	
	protected FindNamespaceHandlerSupport createNamespaceFinder() {
		return new FindMessagesNamespaceHandler();
	}

	public static boolean isXmlFormat(IFile file) throws CoreException {
		return IFiles.isContentTypeId(file, ID);
	}
}
