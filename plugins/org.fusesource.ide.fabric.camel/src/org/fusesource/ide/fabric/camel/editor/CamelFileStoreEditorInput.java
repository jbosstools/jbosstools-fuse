package org.fusesource.ide.fabric.camel.editor;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.fusesource.ide.camel.model.io.IRemoteCamelEditorInput;
import org.fusesource.ide.fabric.camel.navigator.CamelContextNode;


public class CamelFileStoreEditorInput extends FileStoreEditorInput implements IRemoteCamelEditorInput {

	private final CamelContextNode contextNode;

	public CamelFileStoreEditorInput(IFileStore fileStore, CamelContextNode contextNode) {
		super(fileStore);
		this.contextNode = contextNode;
	}

	@Override
	public String getXml() {
		return contextNode.getXmlString();
	}

	@Override
	public String getUriText() {
		return getURI().toString();
	}

}
