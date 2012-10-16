package org.fusesource.ide.fabric.navigator;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IMessage;
import org.fusesource.fon.util.messages.Message;
import org.fusesource.fon.util.messages.contenttype.MessagesNamespaceXmlContentDescriber;
import org.fusesource.ide.commons.ui.Workbenches;
import org.fusesource.ide.commons.ui.drop.DropHandlerSupport;
import org.fusesource.ide.commons.ui.views.ColumnViewSupport;
import org.fusesource.ide.commons.util.IFiles;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.views.MessagesView;
import org.fusesource.scalate.util.IOUtil;


public class MessageDropHandler extends DropHandlerSupport {
	private final MessageDropTarget target;

	public MessageDropHandler(MessageDropTarget target) {
		this.target = target;
	}

	public void dropIFile(IFile resource) {
		try {
			IMessage message = fileToMessage(resource);
			if (message != null) {
				dropMessage(message);
			}
		} catch (Exception e) {
			FabricPlugin.showUserError("Failed to send message to " + target, "Failed to load resource " + resource
					+ " as a Message. ", e);
		}
	}

	public static IMessage fileToMessage(IFile resource) throws CoreException, JAXBException {
		IContentDescription contentDescription = resource.getContentDescription();
		String fileExtension = resource.getFileExtension();

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
		Object body = null;
		if (binary) {
			// TODO XML encode???
			body = IOUtil.loadBinaryFile(file);
		} else {
			String text = IOUtil.loadTextFile(file, "UTF-8");
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
			FabricPlugin.showUserError("Failed to send message to " + target, "Failed to load file " + file
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
