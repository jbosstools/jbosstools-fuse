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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a summary of the input and output endpoints of a RouteContainer
 */
public class EndpointSummary {
	
	private Map<String,Endpoint> inputEndpoints = new LinkedHashMap<String,Endpoint>();
	private Map<String,Endpoint> outputEndpoints = new LinkedHashMap<String,Endpoint>();

	public EndpointSummary(RouteContainer routeContainer) {
		Set<Endpoint> endpoints = routeContainer.getEndpoints();

		for (Endpoint endpoint : endpoints) {
			String key = endpoint.getUri();
			if (key != null) {
				if (endpoint.isInputEndpoint()) {
					inputEndpoints.put(key, endpoint);
				}
				if (endpoint.isOutputEndpoint()) {
					outputEndpoints.put(key, endpoint);
				}
			}
		}
		
		// different routes may have routed from one endpoint to another so lets filter out URIs which appear in both maps
		ArrayList<String> outputKeys = new ArrayList<String>(outputEndpoints.keySet());
		ArrayList<String> inputKeys = new ArrayList<String>(inputEndpoints.keySet());
		removeAll(inputEndpoints, outputKeys);
		removeAll(outputEndpoints, inputKeys);
	}

	public static <K,V> void removeAll(Map<K, V> map, Collection<K> keys) {
		for (K key : keys) {
			map.remove(key);
		}
	}

	public Map<String, Endpoint> getInputEndpoints() {
		return inputEndpoints;
	}

	public Map<String, Endpoint> getOutputEndpoints() {
		return outputEndpoints;
	}

	
}
