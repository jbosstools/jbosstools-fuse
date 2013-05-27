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

package org.fusesource.ide.camel.editor.views;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.HasNodeStatisticsContainer;
import org.fusesource.fon.util.messages.IMessage;
import org.fusesource.fon.util.messages.NodeStatisticsContainer;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.editor.RiderEditor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.generated.Route;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.graph.GraphFilter;
import org.fusesource.ide.graph.GraphLabelProviderSupport;
import org.fusesource.ide.graph.GraphViewSupport;
import org.fusesource.ide.jmx.core.IConnectionWrapper;


/**
 * Shows the selected CamelContexts in a graph
 */

public class DiagramView extends GraphViewSupport {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.fusesource.ide.camel.editor.views.RouteView";

	private Node selectedNode;
	AbstractNode node;
	private NodeStatisticsContainer nodeStatisticsContainer;

	private IWorkbenchPart selectionPart;

	public DiagramView() {
	}

	@Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        getSite().setSelectionProvider(getViewer());
    }

    @Override
	protected GraphLabelProviderSupport createGraphLabelProvider() {
		return new DiagramGraphLabelProvider(this);
	}

	@Override
	protected NodeGraphContentProvider createGraphContentProvider() {
		return new NodeGraphContentProvider();
	}


	public NodeStatisticsContainer getNodeStatisticsContainer() {
		return nodeStatisticsContainer;
	}

	@Override
	protected GraphFilter createGraphFilter() {
		return new GraphFilter(this) {

			@Override
			protected boolean canFilterNode(Object element) {
				return !Objects.equal(element, node) && !Objects.equal(element, selectedNode);
			}
		};
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		if (site != null) {
			site.getWorkbenchWindow().getSelectionService().addSelectionListener(new ISelectionListener() {

				@Override
				public void selectionChanged(IWorkbenchPart part, ISelection selection) {
					// we only want to process selection change events from few selected sources...so filtering here
					if (!isRelevantSelectionSource(part, selection)) {
						return;
					}
					
					Object firstSelection = Selections.getFirstSelection(selection);
					
					// we don't want to process empty selections
					if (selection.isEmpty()) {
						return;
					}
					
					if (part.getClass().getName().equals("org.fusesource.ide.fabric.views.MessagesView")) {
						// special handling for message view selections
						if (firstSelection != null) {
							IMessage in = Exchanges.asMessage(firstSelection);
							if (in != null) {
								String toNode = in.getToNode();
								if (toNode != null) {
									selectNodeId(toNode, in.getEndpointUri());
								}
							}
						}
						return;
					}
					
					if (firstSelection != null) {
						updateselection(firstSelection);
					}
					
//					if (firstSelection == null) {
//						setSelectedObjectOnly(null);
//					}
					
					AbstractNode node = AbstractNodes.getSelectedNode(selection);
					if (node != null && !(part instanceof RiderEditor)) {
						//System.out.println("Part is: " + part + " of type : " + part.getClass());
						if (node != DiagramView.this.node) {
							updateGraph(node, part);
						}
					} else {
						if (firstSelection instanceof Node) {
							updateGraph((Node) firstSelection, part);
						// this clause is needed because ConnectionHandlers are not descendants of Node 
						} else if (firstSelection instanceof IConnectionWrapper) {
							viewer.setContentProvider(new NodeGraphContentProvider());
							setSelectedObjectOnly(firstSelection);
						} else if (firstSelection != null) {
							IMessage in = Exchanges.asMessage(firstSelection);
							if (in != null) {
								String toNode = in.getToNode();
								if (toNode != null) {
									selectNodeId(toNode, in.getEndpointUri());
								}

							}
						}
					}
				}

			});
		}
	}

	private boolean isRelevantSelectionSource(IWorkbenchPart part, ISelection selection) {
		boolean process = false;
		
		// we filter for specific selection sources...
		if (part.getClass().getName().equals("org.fusesource.ide.jmx.ui.internal.views.navigator.Navigator") ||
			part.getClass().getName().equals("org.fusesource.ide.fabric.navigator.FabricNavigator") || 
			part.getClass().getName().equals("org.fusesource.ide.fabric.views.MessagesView") ||
			part.getClass().getName().equals("org.eclipse.ui.views.properties.PropertySheet")
		   ) {
			process = true;
		}
		
		return process;
	}

	protected void updateselection(Object firstSelection) {
		if (firstSelection != null) {
			nodeStatisticsContainer = null;
			if (firstSelection instanceof HasNodeStatisticsContainer) {
				HasNodeStatisticsContainer hasNodeStatisticsContainer = (HasNodeStatisticsContainer) firstSelection;
				nodeStatisticsContainer = hasNodeStatisticsContainer.getNodeStatisticsContainer();
			} else if (firstSelection instanceof NodeStatisticsContainer) {
				nodeStatisticsContainer = (NodeStatisticsContainer) firstSelection;
			}
		} else {
			this.selectedNode = null;
			this.node = null;
			this.nodeStatisticsContainer = null;
			clearDiagramGraph();
		}
	}
	
	protected void clearDiagramGraph() {
		setSelectedObjectOnly(null);
		this.viewer.refresh();
	}
	
	public void updateGraph(AbstractNode node, IWorkbenchPart part) {
		this.node = node;
		this.selectionPart = part;
		RouteGraphContentProvider contentProvider = new RouteGraphContentProvider();
		viewer.setContentProvider(contentProvider);
		if (true) {
			//Object[] input = contentProvider.getElements(node);
			setInputAndSelection(node, node);
		} else {
			setSelectedObject(node);
		}
	}

	public void updateGraph(Node node, IWorkbenchPart part) {
		this.selectedNode = node;
		this.selectionPart = part;
		this.node = null;
		viewer.setContentProvider(new NodeGraphContentProvider());
		setSelectedObjectOnly(node);
	}

	protected boolean selectNodeId(String toNode, String endpointUri) {
		if (node != null) {
			RouteContainer parent = getParentContainer();
			if (parent != null) {
				AbstractNode newSelection = parent.getNode(toNode);
				if (newSelection != null) {
					if (newSelection instanceof Route) {
						// okay its the route we want to select, but we dont display the route node itself
						// so instead select the endpoint uri which is the source node of the route
						if (endpointUri != null) {
							return selectEndpointUri(endpointUri);
						} else {
							// okay we dont have the endpoint uri, then select the first source of the route
							Route route = (Route) newSelection;
							if (!route.getSourceNodes().isEmpty()) {
								newSelection = route.getSourceNodes().get(0);
							}
						}
					}
					if (newSelection != null) {
						viewer.setSelection(new StructuredSelection(newSelection));
						return true;
					}
				}
			}
		}
		return false;
	}

	protected boolean selectEndpointUri(String uri) {
		if (node != null) {
			RouteContainer parent = getParentContainer();
			if (parent instanceof RouteSupport) {
				RouteSupport route = (RouteSupport) parent;
				AbstractNode newSelection = route.findEndpoint(uri);
				if (newSelection == null) {
					// lets try iterate through any children
					List<AbstractNode> children = parent.getChildren();
					for (AbstractNode child : children) {
						if (child instanceof RouteSupport) {
							route = (RouteSupport) child;
							newSelection = route.findEndpoint(uri);
							if (newSelection != null) {
								break;
							}
						}
					}
				}
				if (newSelection != null) {
					viewer.setSelection(new StructuredSelection(newSelection));
					return true;
				}
			}
		}
		return false;
	}


	protected RouteContainer getParentContainer() {
		RouteContainer parent = node.getParent();
		if (parent == null && node instanceof RouteContainer) {
			parent = (RouteContainer) node;
		}
		return parent;
	}

	@Override
	protected void doubleClickSelection(ISelection selection) {
		ISelectionProvider selectionProvider = Selections.getSelectionProvider(selectionPart);
		if (selectionProvider != null) {
			selectionProvider.setSelection(selection);
		}
	}
}