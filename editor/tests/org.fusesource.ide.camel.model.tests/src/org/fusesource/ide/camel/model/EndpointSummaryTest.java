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

import java.util.Map;

import junit.framework.Assert;

import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.EndpointSummary;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.generated.Filter;
import org.fusesource.ide.camel.model.generated.Route;
import org.junit.Test;



public class EndpointSummaryTest {

	@Test
	public void testEndpointSummary() throws Exception {
		RouteContainer routeContainer = new RouteContainer();
		
		// from seda:a -> filter -> seda:b
		RouteSupport route1 = new Route();
		routeContainer.addChild(route1);
		
		Endpoint endpoint1 = new Endpoint("seda:a");
		route1.addChild(endpoint1);
		
		Filter filter1 = new Filter();
		endpoint1.addTargetNode(filter1);
		
		Endpoint endpoint2 = new Endpoint("seda:b");
		filter1.addTargetNode(endpoint2);

		// from seda:b -> filter -> seda:c
		RouteSupport route2 = new Route();
		routeContainer.addChild(route2);
		
		Endpoint endpoint3 = new Endpoint("seda:b");
		route2.addChild(endpoint3);
		
		Filter filter2 = new Filter();
		endpoint3.addTargetNode(filter2);
		
		Endpoint endpoint4 = new Endpoint("seda:c");
		filter2.addTargetNode(endpoint4);

		
		EndpointSummary summary = new EndpointSummary(routeContainer);
		
		Map<String, Endpoint> inputEndpoints = summary.getInputEndpoints();
		Map<String, Endpoint> outputEndpoints = summary.getOutputEndpoints();
		
		
		Assert.assertEquals("Size of input endpoints: " + inputEndpoints, 1, inputEndpoints.size());
		Assert.assertEquals("Size of output endpoints: " + outputEndpoints, 1, outputEndpoints.size());

		assertContains(inputEndpoints, "seda:a");
		assertContains(outputEndpoints, "seda:c");
	}

	protected void assertContains(Map<String, Endpoint> map, String key) {
		Assert.assertTrue("map should contain " + key + " but was " + map, map.containsKey(key));
	}
}
