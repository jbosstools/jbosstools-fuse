/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.commons.tree;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.fusesource.ide.foundation.core.util.IFiles;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.ui.drop.DropHandlerSupport;
import org.fusesource.ide.foundation.ui.util.Workbenches;
import org.fusesource.ide.foundation.ui.views.ColumnViewSupport;
import org.fusesource.ide.jmx.commons.Activator;
import org.fusesource.ide.jmx.commons.messages.Exchanges;
import org.fusesource.ide.jmx.commons.messages.IMessage;
import org.fusesource.ide.jmx.commons.messages.Message;
import org.fusesource.ide.jmx.commons.messages.contenttype.MessagesNamespaceXmlContentDescriber;
import org.fusesource.ide.jmx.commons.views.messages.MessagesView;


public class MessageDropHandler extends DropHandlerSupport {
	private final MessageDropTarget target;

	public MessageDropHandler(MessageDropTarget target) {
		this.target = target;
	}

	@Override
	public void dropIFile(IFile resource) {
		try {
			IMessage message = fileToMessage(resource);
			if (message != null) {
				dropMessage(message);
			}
		} catch (Exception e) {
			Activator.showUserError("Failed to send message to " + target, "Failed to load resource " + resource
					+ " as a Message. ", e);
		}
	}

	public static IMessage fileToMessage(IFile resource) throws CoreException, JAXBException {
		IContentDescription contentDescription = resource.getContentDescription();

		IMessage message;
		if (MessagesNamespaceXmlContentDescriber.isXmlFormat(resource)) {
			message = Exchanges.loadMessage(resource, resource.getContents());
		} else {

			// TODO deal with binary....
			File file = IFiles.toFile(resource);
			if (file != null) {
				Message m = new Message();
				message = m;

				setMessageBody(m, file, !IFiles.isTextContentType(resource));
				// TODO should we add MIME type and whatnot headers...

				
				if (contentDescription != null) {
					IContentType contentType = contentDescription.getContentType();
					if (contentType != null) {
						m.setHeader("EclipseContentType", contentType.getId());
					}
				}
			} else {
				message = null;
			}
			
		}
		return message;
	}

	public static void setMessageBody(Message m, File file, boolean binary) {
		Object body;
		if (binary) {
			// TODO XML encode???
			body = IOUtils.loadBinaryFile(file);
		} else {
			String text = IOUtils.loadTextFile(file, "UTF-8");
			// TODO no need to escape as we do it in the Body class!
			//text = XmlHelper.escape(text);
			body = text;
		}
		
		// TODO lets XML encode it...
		m.setBody(body);
	}

	@Override
	public void dropFile(File file) {
		try {
			IMessage message = Exchanges.loadMessage(file);
			dropMessage(message);
		} catch (Exception e) {
			Activator.showUserError("Failed to send message to " + target, "Failed to load file " + file
					+ " as a Message. ", e);
		}

	}

	public void dropMessage(IMessage message) {
		if (message != null) {
			target.dropMessage(message);

			// lets try refresh the related views
			IWorkbenchPage activeWorkbenchPage = Workbenches.getActiveWorkbenchPage();
			if (activeWorkbenchPage != null) {
				IViewPart view = activeWorkbenchPage.findView(MessagesView.ID);
				if (view instanceof MessagesView) {
					ColumnViewSupport messageView = (ColumnViewSupport) view;
					messageView.refresh();
				}
			}
		}
	}
}
