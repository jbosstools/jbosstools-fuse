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


import org.apache.camel.model.FilterDefinition;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.generated.Filter;
import org.fusesource.ide.camel.model.generated.Route;
import org.junit.Test;


public class ModelTest extends ModelTestSupport {

	@Test
	public void testModelSave() throws Exception {
		
		Endpoint ep1 = new Endpoint();
		assertValidNode(ep1, "ep1");
		ep1.setUri("seda:a");

		assertNodeText("ep1", ep1, "", "", "");
		
		Filter filter1 = new Filter();
		assertValidNode(filter1, "filter1");
		// TODO set the filter...
		
		Endpoint ep2 = new Endpoint();
		assertValidNode(ep2, "ep2");
		ep2.setUri("seda:b");
		
		// wire them up...
		ep1.addTargetNode(filter1);
		filter1.addTargetNode(ep2);
		
		RouteSupport route = new Route();
		assertValidNode(route, "route");
		
		route.addChildren(ep1, filter1, ep2);
		
		// now lets turn into the camel model...
		RouteDefinition routeDef = route.createRouteDefinition();
		System.out.println("Created: " + routeDef);

		FromDefinition ed1 = assertSingleInput(routeDef, FromDefinition.class);
		FilterDefinition fd1 = assertSingleOutput(routeDef, FilterDefinition.class);
		ToDefinition ed2 = assertSingleOutput(fd1, ToDefinition.class);

		assertEquals("ed1.uri", "seda:a", ed1.getUri());
		assertEquals("ed2.uri", "seda:b", ed2.getUri());
	}
	
}
