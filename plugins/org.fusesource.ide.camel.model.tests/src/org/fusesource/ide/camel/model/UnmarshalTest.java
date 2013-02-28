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

import static org.junit.Assert.assertEquals;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.UnmarshalDefinition;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.generated.Choice;
import org.fusesource.ide.camel.model.generated.Otherwise;
import org.fusesource.ide.camel.model.generated.Route;
import org.fusesource.ide.camel.model.generated.Unmarshal;
import org.fusesource.ide.camel.model.generated.When;
import org.junit.Test;


public class UnmarshalTest extends ModelTestSupport {

	@Test
	public void testContentBasedRouterModel() throws Exception {
		Endpoint ep1 = new Endpoint();
		ep1.setUri("seda:a");
		Endpoint ep2 = new Endpoint();
		ep2.setUri("seda:b");
		Endpoint ep3 = new Endpoint();
		ep3.setUri("seda:c");
		Endpoint ep4 = new Endpoint();
		ep4.setUri("seda:d");

		
		Unmarshal um = new Unmarshal();
		ep1.addTargetNode(um);
		um.addTargetNode(ep2);

		RouteSupport route = new Route();
		route.addChildren(ep1, um, ep2);

		// now lets turn into the camel model...
		RouteDefinition routeDef = route.createRouteDefinition();
		System.out.println("Created: " + routeDef);

		assertSingleInput(routeDef, FromDefinition.class);
		assertSize(routeDef.getOutputs(), 2);
		assertOutput(routeDef, 0, UnmarshalDefinition.class);
		ToDefinition ed4 = assertOutput(routeDef, 1, ToDefinition.class);
		assertEquals("unmarshal -> to", "seda:b", ed4.getUri());
	}

	protected void assertRoute(RouteSupport route) {
		Endpoint e1 = assertSingleInput(route, Endpoint.class);
		assertEquals("from", "seda:a", e1.getUri());
		
		Choice c1 = assertSingleOutput(e1, Choice.class);
		When w1 = assertOutput(c1, 0, When.class);
		Endpoint e2 = assertSingleOutput(w1, Endpoint.class);
		assertEquals("when -> to", "seda:b", e2.getUri());

		Otherwise o1 = assertOutput(c1, 1, Otherwise.class);
		Endpoint e3 = assertSingleOutput(o1, Endpoint.class);
		assertEquals("otherwise -> to", "seda:c", e3.getUri());

		Endpoint e4 = assertOutput(c1, 2, Endpoint.class);
		assertEquals("choice -> to", "seda:d", e4.getUri());
		
		assertSize(c1.getOutputs(), 3);
	}


}
