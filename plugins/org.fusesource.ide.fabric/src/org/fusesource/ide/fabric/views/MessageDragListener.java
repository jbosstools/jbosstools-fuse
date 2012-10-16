package org.fusesource.ide.fabric.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IMessage;
import org.fusesource.ide.fabric.FabricPlugin;


public class MessageDragListener implements DragSourceListener {

	private final TableViewer viewer;

	public MessageDragListener(TableViewer viewer) {
		this.viewer = viewer;
	}

	public void register() {
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { 
				FileTransfer.getInstance(), 
				ResourceTransfer.getInstance(), 
				PluginTransfer.getInstance(),
				//LocalSelectionTransfer.getTransfer(), 
				TextTransfer.getInstance() };
		viewer.addDragSupport(operations, transferTypes, this);
	}

	public void dragFinished(DragSourceEvent event) {
	}

	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
			List<String> fileNames = new ArrayList<String>();
			Iterator iter = selection.iterator();
			try {
				while (iter.hasNext()) {
					Object element = iter.next();
					IMessage message = Exchanges.toMessage(element);
					if (message != null) {
						// TODO - use a temporary directory then make the file
						// name based on the message ID?
						File file = File.createTempFile("message-", ".xml");
						Exchanges.marshal(message, file);
						fileNames.add(file.getAbsolutePath());
					}
				}
				event.data = fileNames.toArray(new String[fileNames.size()]);
			} catch (Exception e) {
				FabricPlugin.getLogger().warning(
						"Failed to create file from message: " + e, e);
			}
		} else if (PluginTransfer.getInstance().isSupportedType(event.dataType) && selection != null) {
			event.data = selection;
		} else if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType) && selection != null) {
			event.data = selection;
		} else {
			IMessage message = Exchanges.toMessage(selection.getFirstElement());
			if (message != null) {
				Object body = message.getBody();
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = body;
				} else {
				}
			}
		}
	}

	public void dragStart(DragSourceEvent event) {
	}

}
