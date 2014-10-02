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

package org.fusesource.ide.camel.editor.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.fusesource.ide.camel.editor.handlers.AutoLayoutAction;
import org.fusesource.ide.camel.editor.handlers.DeleteNodeAction;
import org.fusesource.ide.camel.editor.handlers.DeleteRouteAction;
import org.fusesource.ide.camel.editor.provider.ToolBehaviourProvider;
import org.fusesource.ide.camel.editor.provider.generated.AddNodeMenuFactory;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.generated.Route;


/**
 * @author lhein
 */
public class RiderEditorContextMenuProvider extends ContextMenuProvider {

	/** The editor's action registry. */
	private ActionRegistry actionRegistry;
	private final RiderDesignEditor editor;
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
	public RiderEditorContextMenuProvider(RiderDesignEditor editor, INodeViewer nodeViewer, EditPartViewer viewer, ActionRegistry registry) {
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

	public RiderDesignEditor getEditor() {
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
		// add the delete route if a route is selected
		AbstractNode node = nodeViewer.getSelectedNode();
		if (node == null) {
			// lets choose the root container
			node = editor.getModel();
		}

		if (node != null && node instanceof Route) {
			DeleteRouteAction deleteRouteAction = new DeleteRouteAction();
			deleteRouteAction.setSelectedRoute((RouteSupport)node);
			menu.add(deleteRouteAction);
		}

		// add a separator
		menu.add(new Separator());

		final AddNodeMenuFactory factory = new AddNodeMenuFactory();
		final MenuManager subMenu = new MenuManager("Add", factory.getImageDescriptor("add.png"), "org.fusesource.ide.actions.add");
		menu.add(subMenu);

		subMenu.setRemoveAllWhenShown(true);
		subMenu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				AbstractNode node = nodeViewer.getSelectedNode();
				if (node == null) {
					// lets choose the root container
					node = editor.getModel();
				}
				boolean enabled = node != null;
				subMenu.setVisible(enabled);

				ToolBehaviourProvider tbp = (ToolBehaviourProvider)editor.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
				ArrayList<IToolEntry> additionalEndpoints = new ArrayList<IToolEntry>();
				additionalEndpoints.addAll(tbp.getConnectorsToolEntries());
				additionalEndpoints.addAll(tbp.getExtensionPointToolEntries());
				
				// sort the palette entries
		        Collections.sort(additionalEndpoints, new Comparator<IToolEntry>() {
		            /* (non-Javadoc)
		             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		             */
		            @Override
		            public int compare(IToolEntry o1, IToolEntry o2) {
		                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
		            }
		        });
				
				factory.fillMenu(editor, subMenu, node, additionalEndpoints);
			}
		});
		// add the delete item
		if (node != null && node instanceof Route == false) {
			DeleteNodeAction deleteNodeAction = new DeleteNodeAction();
			deleteNodeAction.setSelectedNode(node);
			menu.add(deleteNodeAction);
		}

		// add a separator
		menu.add(new Separator());
		// auto layout action
		menu.add(new AutoLayoutAction());
	}

	/**
	 * retrieves an action for a given id
	 * 
	 * @param actionId	the action id
	 * @return	the action or null if not found
	 */
	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}
}
