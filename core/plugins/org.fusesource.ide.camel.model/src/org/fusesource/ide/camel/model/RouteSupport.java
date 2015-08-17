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

package org.fusesource.ide.camel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.eclipse.draw2d.geometry.Rectangle;
import org.fusesource.ide.camel.model.generated.NodeFactory;
import org.fusesource.ide.commons.camel.tools.BeanDef;

/**
 * @author lhein
 */
public abstract class RouteSupport extends RouteContainer {

	public static final int DEFAULT_ROUTE_HEIGHT = 250;
	public static final int DEFAULT_ROUTE_WIDTH = 400;

	private Map<String, Endpoint> endpoints = new HashMap<String, Endpoint>();

	private static final String ICON = "route.png";

	/**
	 * default constructor
	 */
	public RouteSupport() {
		init();
	}

	public RouteSupport(RouteDefinition definition, RouteContainer parent) {
		super(parent);
		init();
		loadPropertiesFromCamelDefinition(definition);
		loadChildrenFromCamelDefinition(definition);
	}

	@Override
	public String[] getEndpointUris() {
		RouteContainer parent = getParent();
		if (parent != null) {
			return parent.getEndpointUris();
		}
		return super.getEndpointUris();
	}

	@Override
	public Map<String, BeanDef> getBeans() {
		RouteContainer parent = getParent();
		if (parent != null) {
			return parent.getBeans();
		}
		return super.getBeans();
	}

	private void init() {
		setLayout(new Rectangle(0, 0, DEFAULT_ROUTE_WIDTH, DEFAULT_ROUTE_HEIGHT));
	}

	@Override
	public String getDocumentationFileName() {
		return "route";
	}

	@Override
	public String getIconName() {
		return ICON;
	}

	@Override
	public Class<?> getCamelDefinitionClass() {
		return RouteDefinition.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.RouteContainer#createCamelDefinition()
	 */
	@Override
	public ProcessorDefinition createCamelDefinition() {
		RouteDefinition answer = new RouteDefinition();
		super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	/**
	 * creates a route definition
	 * @return
	 */
	public RouteDefinition createRouteDefinition() {
		ArrayList<AbstractNode> processedNodes = new ArrayList<AbstractNode>();
		RouteDefinition answer = (RouteDefinition) createCamelDefinition();

		// lets find all the children which have nothing routing into them
		List<AbstractNode> list = getRootNodes();
		for (AbstractNode node : list) {
			if (node instanceof Endpoint) {
				Endpoint endpoint = (Endpoint) node;
				FromDefinition from = new FromDefinition();
				CamelModelHelper.setUri(from, endpoint);
				CamelModelHelper.setId(from, endpoint);
				CamelModelHelper.setDescription(from, endpoint);
				resetCustomId(from);

				answer.getInputs().add(from);

				// now lets populate the ID and description
				endpoint.populateCamelDefinition(from);

				if (!processedNodes.contains(node)) {
					// add all of the children of node as outputs of the route
					endpoint.saveChildrenToCamelDefinitions(answer, processedNodes);
					// remember that node is processed
					processedNodes.add(node);
				}
			} else {
				Activator.getLogger().warning(node + " is not connected to anything!");
				// throw new IllegalStateException(node +
				// " is not connected to anything!");
			}
		}
		return answer;
	}

	/**
	 * Returns the root nodes in this route - basically the Endpoint nodes which are the sources of flows
	 */
	public List<AbstractNode> getRootNodes() {
		List<AbstractNode> answer = new ArrayList<AbstractNode>();
		List<AbstractNode> list = getChildren();
		for (AbstractNode node : list) {
			if (node.isNotTarget()) {
				answer.add(node);
			}
		}
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadChildrenFromCamelDefinition(ProcessorDefinition processor) {
		RouteDefinition routeDef = (RouteDefinition) processor;
		List<FromDefinition> inputs = routeDef.getInputs();
		List<ProcessorDefinition> outputs = processor.getOutputs();

		AbstractNode parent = null;
		AbstractNode lastNode = null;
		for (ProcessorDefinition childProcessor : outputs) {
			AbstractNode node = NodeFactory.createNode(childProcessor, this);
			if (parent == null) {
				if (lastNode == null) {
					for (FromDefinition input : inputs) {
						Endpoint endpoint = getOrCreateEndpoint(input);
						endpoint.addTargetNode(node);
					}
				} else {
					// lets find all the last nodes added
					// such as the When / Otherwise nodes in a Choice...
					lastNode.addTargetNode(node);
				}
			} else {
				if (lastNode != null && "choice".equals(lastNode.getNodeTypeId())) {
					lastNode.addTargetNode(node);
				} else {
					parent.addTargetNode(node);
				}
			}
			if (!isMulticastNode(parent, node) && node.isNextSiblingStepAddedAsNodeChild()) {
				parent = node;
			}
			lastNode = node;
		}
	}

	/**
	 * generates a key out of uri and id of an endpoint
	 * 
	 * @param uri
	 * @param id
	 * @return
	 */
	private String generateEndpointKey(String uri, String id) {
		String part1 = (uri == null || uri.trim().length()<1) ? UUID.randomUUID().toString() : uri;
		String part2 = (id  == null || id.trim().length()<1)  ? UUID.randomUUID().toString() : id;
		return String.format("%s$%s", part1, part2);
	}

	/**
	 * Gets the endpoint for the given from or creates a new one if there is not one yet
	 */
	protected Endpoint getOrCreateEndpoint(FromDefinition input) {
		String key = generateEndpointKey(CamelModelHelper.getUri(input), input.getId());
		Endpoint answer = findEndpoint(key);
		if (answer == null) {
			answer = new Endpoint(input, this);
			endpoints.put(key, answer);
		}
		return answer;
	}

	/**
	 * Returns the endpoint for the given URI or null if there is not one available
	 */
	public Endpoint findEndpoint(String uri) {
		return endpoints.get(uri);
	}

	/**
	 * Gets the endpoint for the given to or creates a new one if there is not one yet
	 */
	public Endpoint getOrCreateEndpoint(ToDefinition definition, RouteContainer parent) {
		String key = generateEndpointKey(definition.getUri(), definition.getId());
		Endpoint answer = findEndpoint(key);
		if (answer != null && answer.getOutputs().size() > 0) {
			// lets avoid endpoints which are already in the middle of a route pipeline to avoid loops
			answer = null;
		}
		if (answer == null) {
			answer = new Endpoint(definition, parent);
			endpoints.put(key, answer);
		}
		return answer;
	}

	@Override
	public void clearResources() {
		super.clearResources();
		List<AbstractNode> children = getRootNodes();
		for (AbstractNode node : children) {
			node.clearResources();
		}
	}

}
