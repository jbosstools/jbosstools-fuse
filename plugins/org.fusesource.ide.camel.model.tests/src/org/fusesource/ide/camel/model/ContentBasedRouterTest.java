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
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OtherwiseDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.WhenDefinition;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.generated.Choice;
import org.fusesource.ide.camel.model.generated.Otherwise;
import org.fusesource.ide.camel.model.generated.Route;
import org.fusesource.ide.camel.model.generated.When;
import org.junit.Test;


public class ContentBasedRouterTest extends ModelTestSupport {

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

		Choice choice = new Choice();
		ep1.addTargetNode(choice);
		// adding out of order to check we add the otherwise first before the non-when/otherwise
		choice.addTargetNode(ep4);

		When when = new When();
		choice.addTargetNode(when);
		when.addTargetNode(ep2);

		Otherwise otherwise = new Otherwise();
		choice.addTargetNode(otherwise);
		otherwise.addTargetNode(ep3);

		
		RouteSupport route = new Route();
		route.addChildren(ep1, choice, when, otherwise, ep2, ep3, ep4);

		// now lets turn into the camel model...
		RouteDefinition routeDef = route.createRouteDefinition();
		System.out.println("Created: " + routeDef);

		assertSingleInput(routeDef, FromDefinition.class);
		assertSize(routeDef.getOutputs(), 2);
		ChoiceDefinition c1 = assertOutput(routeDef, 0, ChoiceDefinition.class);
		ToDefinition ed4 = assertOutput(routeDef, 1, ToDefinition.class);
		assertEquals("choice -> to", "seda:d", ed4.getUri());

		List<WhenDefinition> whenClauses = c1.getWhenClauses();
		assertSize(whenClauses, 1);
		WhenDefinition wd1 = whenClauses.get(0);
		ToDefinition ed2 = assertSingleOutput(wd1, ToDefinition.class);
		assertEquals("when -> to", "seda:b", ed2.getUri());
		
		OtherwiseDefinition od1 = c1.getOtherwise();
		assertNotNull("Should have Otherwise", od1);
		ToDefinition ed3 = assertSingleOutput(od1, ToDefinition.class);
		assertEquals("otherwise -> to", "seda:c", ed3.getUri());

		RouteContainer routeContainer = new RouteContainer();
		RouteSupport route2 = new Route(routeDef, routeContainer);
		System.out.println("Created route: " + route2);
		assertRoute(route2);
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
