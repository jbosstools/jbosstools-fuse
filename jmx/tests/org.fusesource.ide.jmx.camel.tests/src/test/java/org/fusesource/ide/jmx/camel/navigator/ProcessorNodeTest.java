/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.camel.navigator;


import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.tree.Root;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorNodeTest {
	
	@Mock
	IConnectionWrapper connectionWrapper;
	
	@Mock
	IConnectionProvider connectionProvider;
		
	@Test
	public void testHashCodeDifferentForSameIdInDifferentRoute() throws Exception {
		CamelRouteElement route1 = new CamelRouteElement(null, null);
		route1.setId("Route1");
		CamelBasicModelElement basicElement = new CamelBasicModelElement(null, null);
		basicElement.setId("sameId");
		RouteNode routeNode1 = new RouteNode(new RoutesNode(new CamelContextNode(new CamelContextsNode(new Root(connectionWrapper), null), null, null)), route1);
		ProcessorNode processorNode1 = new ProcessorNode(routeNode1, routeNode1, basicElement);
		
		
		CamelRouteElement route2 = new CamelRouteElement(null, null);
		route2.setId("Route2");
		CamelBasicModelElement basicElement2 = new CamelBasicModelElement(null, null);
		basicElement2.setId("sameId");
		RouteNode routeNode2 = new RouteNode(new RoutesNode(new CamelContextNode(new CamelContextsNode(new Root(connectionWrapper), null), null, null)), route2);
		ProcessorNode processorNode2 = new ProcessorNode(routeNode2, routeNode2, basicElement2);

		assertThat(processorNode2.hashCode()).isNotEqualTo(processorNode1.hashCode());
	}

}
