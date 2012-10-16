package org.fusesource.ide.fabric.camel.editor;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.io.ICamelEditorInput;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.camel.Messages;
import org.fusesource.ide.fabric.camel.navigator.CamelContextNode;
import org.fusesource.scalate.util.IOUtil;


public class CamelContextNodeEditorInput implements ICamelEditorInput {
	public static final String ID = CamelContextNodeEditorInput.class.getName();

	private final CamelContextNode contextNode;

	private IFileEditorInput fileEditorInput;
	private File tempFile;
	private IFile lastSaveAsFile;

	public CamelContextNodeEditorInput(CamelContextNode contextNode) {
		this.contextNode = contextNode;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return FabricPlugin.getDefault().getImageDescriptor("camel.png");
	}

	@Override
	public String getName() {
		return "CamelContext: " + contextNode.getContextId();
	}

	@Override
	public IPersistableElement getPersistable() {
		return new IPersistableElement() {

			@Override
			public void saveState(IMemento memento) {
				// TODO how to get the document input...
			}

			@Override
			public String getFactoryId() {
				return ID;
			}
		};
	}

	@Override
	public String getToolTipText() {
		return Messages.camelContextNodeEditRouteToolTip;
	}

	@Override
	public RouteContainer loadModel() {
		return contextNode.getModelContainer();
	}


	@Override
	public void save(String xml) {
		contextNode.updateXml(xml);
	}

	@Override
	public IEditorInput getFileEditorInput() {
		if (lastSaveAsFile != null) {
			return new FileEditorInput(lastSaveAsFile);
		}
		if (fileEditorInput == null) {
			try {
				// lets create a temporary file...
				File tempDir = File.createTempFile("FuseIDE-camel-context-" + contextNode.getContextId() + "-", "");
				tempDir.delete();
				tempDir.mkdirs();
				if (tempFile == null) {
					tempFile = File.createTempFile("camelContext-", ".xml", tempDir);
				}
				// lets create the IFile from it...
				IFileSystem fileSystem = EFS.getLocalFileSystem();
				IFileStore fileStore = fileSystem.fromLocalFile(tempFile);

				String xml = contextNode.getXmlString();
				IOUtil.writeText(tempFile, xml);

				return new CamelFileStoreEditorInput(fileStore, contextNode);
			} catch (Exception e) {
				Activator.getLogger().warning("Failed to create temporary file: " + e, e);
			}
		}
		return fileEditorInput;
	}

	@Override
	public IFile getLastSaveAsFile() {
		return lastSaveAsFile;
	}

	@Override
	public void setLastSaveAsFile(IFile lastSaveAsFile) {
		this.lastSaveAsFile = lastSaveAsFile;
	}

}
