/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.handlers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.AbstractNode;


/**
 * @author lhein
 */
public class DeleteNodeAction extends Action {
	public static final String DELETE_NODE_COMMAND_ID = "org.fusesource.ide.camel.editor.commands.deleteNodeCommand";
	
	private AbstractNode selectedNode;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		RiderDesignEditor editor = Activator.getDiagramEditor();
		DiagramOperations.deleteNode(editor, getSelectedNode());
		DiagramOperations.updateDiagram(editor);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getId()
	 */
	@Override
	public String getId() {
		return DELETE_NODE_COMMAND_ID;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getDescription()
	 */
	@Override
	public String getDescription() {
		return EditorMessages.deleteNodeCommandDescription;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return EditorMessages.deleteNodeCommandLabel;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return EditorMessages.deleteNodeCommandDescription;
	}
	
	/**
	 * @param selectedNode the selectedNode to set
	 */
	public void setSelectedNode(AbstractNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	
	/**
	 * @return the selectedNode
	 */
	public AbstractNode getSelectedNode() {
		return this.selectedNode;
	}
}
