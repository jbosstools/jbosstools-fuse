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

package org.fusesource.ide.camel.editor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.AbstractNodeFacade;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.commons.tree.HasOwner;
import org.fusesource.ide.commons.util.Objects;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class AbstractNodes {

	public static AbstractNode getSelectedNode(ISelection selection) {
		AbstractNode answer = null;
		if (selection instanceof IStructuredSelection) {

			/*
			 * Control oldClient = section.getClient(); if (oldClient != null) {
			 * //section.setClient(null); oldClient.dispose(); }
			 */
			Object input = ((IStructuredSelection) selection).getFirstElement();
			answer = toAbstractNode(input);
		}
		return answer;
	}

	public static AbstractNode toAbstractNode(Object input) {
		AbstractNode answer = null;
		if (input instanceof AbstractNode) {
			return (AbstractNode) input;
		} else if (input instanceof AbstractNodeFacade) {
			AbstractNodeFacade facade = (AbstractNodeFacade) input;
			answer = facade.getAbstractNode();
		} else if (input instanceof ContainerShapeEditPart) {
			ContainerShapeEditPart editPart = (ContainerShapeEditPart) input;
			PictogramElement element = editPart.getPictogramElement();
			if (Activator.getDiagramEditor() != null) {
				if (element != null && element instanceof Diagram) {
					// route selected - this makes properties view work when route is
					// selected in the diagram view
					answer = Activator.getDiagramEditor().getSelectedRoute() != null ? Activator.getDiagramEditor().getSelectedRoute() : Activator.getDiagramEditor().getModel();				
				} else {
					// select the node
					answer = (AbstractNode)Activator.getDiagramEditor().getFeatureProvider().getBusinessObjectForPictogramElement(element);
				}
			}
		} else if (input instanceof AbstractEditPart) {
			AbstractEditPart editPart = (AbstractEditPart) input;
			Object model = editPart.getModel();
			answer = toAbstractNode(model);
		} else if (input instanceof ContainerShape) {
			ContainerShape shape = (ContainerShape) input;
			answer = (AbstractNode)Activator.getDiagramEditor().getFeatureProvider().getBusinessObjectForPictogramElement(shape);
		}
		if (input != null && answer == null) {
			answer = (AbstractNode) Platform.getAdapterManager().getAdapter(input, AbstractNode.class);
		}
		if (answer == null && input instanceof HasOwner) {
			HasOwner ho = (HasOwner) input;
			answer = toAbstractNode(ho.getOwner());
		}
		return answer;
	}

	public static RouteSupport getRoute(AbstractNode node) {
		if (node instanceof RouteSupport) {
			return (RouteSupport) node;
		} else if (node instanceof RouteContainer) {
			return null;
		} else if (node != null) {
			return getRoute(node.getParent());
		}
		return null;
	}

	public static Set<Endpoint> getAllEndpoints(AbstractNode node) {
		if (node != null) {
			RouteSupport route = AbstractNodes.getRoute(node);
			if (route != null) {
				Set<Endpoint> endpoints = route.getEndpoints();
				RouteContainer parent = route.getParent();
				if (parent != null) {
					Set<Endpoint> set = parent.getEndpoints();
					String[] endpointUris = parent.getEndpointUris();
					for (String uri : endpointUris) {
						if (!containsUri(set, uri)) {
							Endpoint endpoint = new Endpoint();
							endpoint.setUri(uri);
							endpoints.add(endpoint);
						}
					}
					endpoints.addAll(set);
				}
				return endpoints;
			}
		}
		return Collections.EMPTY_SET;
	}

	private static boolean containsUri(Set<Endpoint> set, final String uri) {
		return Iterables.any(set, new Predicate<Endpoint>(){

			@Override
			public boolean apply(Endpoint endpoint) {
				return Objects.equal(uri, endpoint.getUri());
			}});
	}

	public static SortedMap<String, String> getAllBeans(AbstractNode node) {
		SortedMap<String,String> answer = new TreeMap<String,String>();
		if (node != null) {
			RouteSupport route = AbstractNodes.getRoute(node);
			if (route != null) {
				Map<String, String> map = route.getBeans();
				if (map != null) {
					answer.putAll(map);
				}
			}
		}
		return answer;
	}

}
