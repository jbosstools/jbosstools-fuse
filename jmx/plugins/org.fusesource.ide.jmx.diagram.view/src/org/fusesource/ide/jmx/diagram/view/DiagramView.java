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

package org.fusesource.ide.jmx.diagram.view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.graph.GraphFilter;
import org.fusesource.ide.graph.GraphLabelProviderSupport;
import org.fusesource.ide.graph.GraphViewSupport;
import org.fusesource.ide.jmx.camel.navigator.CamelContextNode;
import org.fusesource.ide.jmx.camel.navigator.EndpointNode;
import org.fusesource.ide.jmx.camel.navigator.ProcessorNode;
import org.fusesource.ide.jmx.camel.navigator.RouteNode;
import org.fusesource.ide.jmx.commons.messages.Exchanges;
import org.fusesource.ide.jmx.commons.messages.HasNodeStatisticsContainer;
import org.fusesource.ide.jmx.commons.messages.IMessage;
import org.fusesource.ide.jmx.commons.messages.NodeStatisticsContainer;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.tree.Node;

/**
 * Shows the selected CamelContexts in a graph
 */
public class DiagramView extends GraphViewSupport {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.fusesource.ide.jmx.views.DiagramView";

	private Node selectedNode;
	AbstractCamelModelElement node;
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

					if (part.getClass().getName().equals("org.fusesource.ide.jmx.commons.views.messages.MessagesView")) {
						// special handling for message view selections

						if (firstSelection != null) {
							IMessage in = Exchanges.asMessage(firstSelection);
							if (in != null) {
								String toNode = in.getToNode();
								if (toNode != null) {
									if (selectNodeId(toNode, in.getEndpointUri())) {
										return;
									}
								}
							}
						}
					}

					if (firstSelection != null) {
						updateselection(firstSelection);
					}

					// if (firstSelection == null) {
					// setSelectedObjectOnly(null);
					// }

					AbstractCamelModelElement node = org.fusesource.ide.camel.editor.utils.NodeUtils.getSelectedNode(selection);
					if (node != null && !(part instanceof CamelEditor)) {
						// Activator.getLogger().debug("Part is: " + part + " of
						// type : " + part.getClass());
						if (node != DiagramView.this.node) {
							updateGraph(node, part);
						}
					} else {
						if (firstSelection instanceof Node) {
							updateGraph((Node) firstSelection, part);
							// this clause is needed because ConnectionHandlers
							// are not descendants of Node
						} else if (firstSelection instanceof IConnectionWrapper) {
							viewer.setContentProvider(new NodeGraphContentProvider());
							setSelectedObjectOnly(firstSelection);
						} else if (firstSelection != null) {
							IMessage in = Exchanges.asMessage(firstSelection);
							if (in != null) {
								String toNode = in.getToNode();
								if (toNode != null) {
									if (selectNodeId(toNode, in.getEndpointUri())) {
										return;
									}
								}
							}
						}
					}
					viewer.setSelection(selection);
				}

			});
		}
	}

	private boolean isRelevantSelectionSource(IWorkbenchPart part, ISelection selection) {
		boolean process = false;
		
		// we filter for specific selection sources...
		final String partClassname = part.getClass().getName();
		//@formatter:off
		if (partClassname.equals("org.jboss.tools.jmx.ui.internal.views.navigator.JMXNavigator")
				|| partClassname.equals("org.fusesource.ide.jmx.commons.views.messages.MessagesView")
				|| partClassname.equals("org.eclipse.ui.views.properties.PropertySheet")
				|| partClassname.equals("org.eclipse.wst.server.ui.internal.view.servers.ServersView")
				|| partClassname.equals("org.eclipse.wst.server.ui.internal.cnf.ServersView2")
		   ) {
			//@formatter:on
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
	
	public void updateGraph(AbstractCamelModelElement node, IWorkbenchPart part) {
		this.node = node;
		this.selectionPart = part;
		RouteGraphContentProvider contentProvider = new RouteGraphContentProvider();
		viewer.setContentProvider(contentProvider);
		setInputAndSelection(node, node);
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
			AbstractCamelModelElement parent = getParentContainer();
			if (parent != null) {
				AbstractCamelModelElement newSelection = parent.findNode(toNode);
				if (newSelection != null) {
					if (newSelection instanceof CamelRouteElement) {
						// okay its the route we want to select, but we dont display the route node itself
						// so instead select the endpoint uri which is the source node of the route
						if (endpointUri != null) {
							return selectEndpointUri(endpointUri);
						} else {
							// okay we dont have the endpoint uri, then select the first source of the route
							CamelRouteElement route = (CamelRouteElement) newSelection;
							if (!route.getInputs().isEmpty()) {
								newSelection = route.getInputs().get(0);
							}
						}
					}
					if (newSelection != null) {
						viewer.setSelection(new StructuredSelection(newSelection));
						return true;
					}
				}
			}
		} else {
			for (Object nodeElement : getViewer().getNodeElements()) {
				if (isCorrespondingElement(toNode, nodeElement)) {
					viewer.setSelection(new StructuredSelection(nodeElement));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param toNode
	 * @param nodeElement
	 * @return
	 */
	private boolean isCorrespondingElement(String toNode, Object nodeElement) {
		return isCorrespondingNodeText(toNode, nodeElement)
				|| isCorrespondingCamelContext(toNode, nodeElement)
				|| isCorrespondingCamelRoute(toNode, nodeElement)
				|| isCorrespondingCamelEndpoint(toNode, nodeElement)
				|| isCorrespondingProcessor(toNode, nodeElement);
	}

	/**
	 * @param toNode
	 * @param nodeElement
	 * @return
	 */
	private boolean isCorrespondingNodeText(String toNode, Object nodeElement) {
		return nodeElement instanceof Node && toNode.equals(Strings.getOrElse(nodeElement));
	}

	/**
	 * @param toNode
	 * @param nodeElement
	 * @return
	 */
	private boolean isCorrespondingProcessor(String toNode, Object nodeElement) {
		return nodeElement instanceof ProcessorNode && toNode.equals(((ProcessorNode) nodeElement).getNodeId());
	}

	/**
	 * @param toNode
	 * @param nodeElement
	 * @return
	 */
	private boolean isCorrespondingCamelEndpoint(String toNode, Object nodeElement) {
		return nodeElement instanceof EndpointNode && toNode.equals(((EndpointNode) nodeElement).getEndpointUri());
	}

	/**
	 * @param toNode
	 * @param nodeElement
	 * @return
	 */
	private boolean isCorrespondingCamelRoute(String toNode, Object nodeElement) {
		return nodeElement instanceof RouteNode && toNode.equals(((RouteNode) nodeElement).getNodeId());
	}

	/**
	 * @param toNode
	 * @param nodeElement
	 * @return
	 */
	private boolean isCorrespondingCamelContext(String toNode, Object nodeElement) {
		return nodeElement instanceof CamelContextNode && toNode.equals(((CamelContextNode) nodeElement).getContextId());
	}

	protected boolean selectEndpointUri(String uri) {
		if (node != null) {
			AbstractCamelModelElement parent = getParentContainer();
			if (parent instanceof CamelRouteElement) {
				CamelRouteElement route = (CamelRouteElement) parent;
				AbstractCamelModelElement newSelection = route.findEndpoint(uri);
				if (newSelection != null) {
					viewer.setSelection(new StructuredSelection(newSelection));
					return true;
				}
			}
		}
		return false;
	}


	protected AbstractCamelModelElement getParentContainer() {
		return node.getParent();
	}

	@Override
	protected void doubleClickSelection(ISelection selection) {
		ISelectionProvider selectionProvider = Selections.getSelectionProvider(selectionPart);
		if (selectionProvider != null) {
			selectionProvider.setSelection(selection);
		}
	}
}