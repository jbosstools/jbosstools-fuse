package org.fusesource.ide.camel.editor.editor;

import org.fusesource.ide.camel.model.AbstractNode;

public interface INodeViewer {

	AbstractNode getSelectedNode();

	void setSelectedNode(AbstractNode newSelection);
}
