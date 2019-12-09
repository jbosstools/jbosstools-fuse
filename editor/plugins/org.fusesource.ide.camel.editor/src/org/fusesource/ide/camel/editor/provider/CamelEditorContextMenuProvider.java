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

package org.fusesource.ide.camel.editor.provider;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.handler.AutoLayoutAction;
import org.fusesource.ide.camel.editor.utils.INodeViewer;

/**
 * @author lhein
 */
public class CamelEditorContextMenuProvider extends ContextMenuProvider {

	/** The editor's action registry. */
	private ActionRegistry actionRegistry;
	private final CamelDesignEditor editor;
	private final INodeViewer nodeViewer;

	/**
	 * Instantiate a new menu context provider for the specified EditPartViewer
	 * and ActionRegistry.
	 * 
	 * @param editor
	 * @param viewer	the editor's graphical viewer
	 * @param registry	the editor's action registry
	 * @throws IllegalArgumentException if registry is <tt>null</tt>.
	 */
	public CamelEditorContextMenuProvider(CamelDesignEditor editor, INodeViewer nodeViewer, EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		this.editor = editor;
		this.nodeViewer = nodeViewer;
		if (registry == null) {
			throw new IllegalArgumentException();
		}
		actionRegistry = registry;
	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public CamelDesignEditor getEditor() {
		return editor;
	}

	public INodeViewer getNodeViewer() {
		return nodeViewer;
	}

	/**
	 * Called when the context menu is about to show. Actions,
	 * whose state is enabled, will appear in the context menu.
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void buildContextMenu(IMenuManager menu) {
		// add a separator
		menu.add(new Separator());

//		final AddNodeMenuFactory factory = new AddNodeMenuFactory();
//		final MenuManager subMenu = new MenuManager("Add", factory.getImageDescriptor("add.png"), "org.fusesource.ide.actions.add");
//		menu.add(subMenu);
//
//		subMenu.setRemoveAllWhenShown(true);
//		subMenu.addMenuListener(new IMenuListener() {
//
//			@Override
//			public void menuAboutToShow(IMenuManager manager) {
//				AbstractNode node = nodeViewer.getSelectedNode();
//				if (node == null) {
//					// lets choose the root container
//					node = editor.getModel();
//				}
//				boolean enabled = node != null;
//				subMenu.setVisible(enabled);
//
//				ToolBehaviourProvider tbp = (ToolBehaviourProvider)editor.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
//				ArrayList<IToolEntry> additionalEndpoints = new ArrayList<IToolEntry>();
//				additionalEndpoints.addAll(tbp.getConnectorsToolEntries());
//				additionalEndpoints.addAll(tbp.getExtensionPointToolEntries());
//				
//				// sort the palette entries
//		        Collections.sort(additionalEndpoints, new Comparator<IToolEntry>() {
//		            /* (non-Javadoc)
//		             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
//		             */
//		            @Override
//		            public int compare(IToolEntry o1, IToolEntry o2) {
//		                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
//		            }
//		        });
//				
//				factory.fillMenu(editor, subMenu, node, additionalEndpoints);
//			}
//		});

		// add a separator
		menu.add(new Separator());
		// auto layout action
		menu.add(new AutoLayoutAction());
	}
}
