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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.generated.Choice;
import org.fusesource.ide.camel.model.generated.Filter;
import org.fusesource.ide.camel.model.generated.Otherwise;
import org.fusesource.ide.camel.model.generated.When;
import org.fusesource.ide.camel.model.io.ContainerMarshaler;
import org.fusesource.ide.camel.model.io.XmlContainerMarshaller;
import org.junit.Test;


public class XmlMarshalTest {
	
	ContainerMarshaler marshaller = new XmlContainerMarshaller();
	
	protected File sourceDir = new File("src");
	protected File outputDir = new File("target/testData");
	
	@Test
	public void testLoadAndSaveOfSimpleModel() throws Exception {
		assertModelRoundTrip("sample.xml", 1);
	}
	
	@Test
	public void testLoadAndSaveOfFilter() throws Exception {
		RouteSupport route = assertModelRoundTrip("filterSample.xml", 1);
		List<AbstractNode> routeChildren = route.getChildren();

		Endpoint endpoint = assertChildIsInstance(route.getRootNodes(), 0, Endpoint.class);
		assertEquals("endpoint uri", "seda:someWhere", endpoint.getUri());

		Filter filter = assertChildIsInstance(endpoint.getOutputs(), 0, Filter.class);
		assertEquals("filter expression", "/foo/bar", filter.getExpression().getExpression());
		assertEquals("filter language", "xpath", filter.getExpression().getLanguage());
		
		Endpoint endpoint2 = assertChildIsInstance(filter.getOutputs(), 0, Endpoint.class);
		assertEquals("endpoint2 uri", "seda:anotherPlace2", endpoint2.getUri());
		
		assertContains(routeChildren, endpoint, filter, endpoint2);
	}
	
	@Test
	public void testLoadAndSaveOfFilterWithBlueprint() throws Exception {
		RouteSupport route = assertModelRoundTrip("filterSampleBlueprint.xml", 1);
		List<AbstractNode> routeChildren = route.getChildren();

		Endpoint endpoint = assertChildIsInstance(route.getRootNodes(), 0, Endpoint.class);
		assertEquals("endpoint uri", "seda:someWhere", endpoint.getUri());

		Filter filter = assertChildIsInstance(endpoint.getOutputs(), 0, Filter.class);
		assertEquals("filter expression", "/foo/bar", filter.getExpression().getExpression());
		assertEquals("filter language", "xpath", filter.getExpression().getLanguage());
		
		Endpoint endpoint2 = assertChildIsInstance(filter.getOutputs(), 0, Endpoint.class);
		assertEquals("endpoint2 uri", "seda:anotherPlace2", endpoint2.getUri());
		
		assertContains(routeChildren, endpoint, filter, endpoint2);
	}

	@Test
	public void testLoadAndSaveOfContentBasedRouter() throws Exception {
		RouteSupport route = assertModelRoundTrip("cbrSample.xml", 1);
		List<AbstractNode> routeChildren = route.getChildren();

		Endpoint endpoint = assertChildIsInstance(route.getRootNodes(), 0, Endpoint.class);
		assertEquals("endpoint uri", "seda:choiceInput", endpoint.getUri());

		System.out.println("Endpoint outputs: " + endpoint.getOutputs());
		Choice choice = assertChildIsInstance(endpoint.getOutputs(), 0, Choice.class);

		System.out.println("Choice outputs: " + choice.getOutputs());
		When filter = assertChildIsInstance(choice.getOutputs(), 0, When.class);
		Otherwise otherwise = assertChildIsInstance(choice.getOutputs(), 1, Otherwise.class);
		
		assertEquals("when expression", "/foo/bar", filter.getExpression().getExpression());
		assertEquals("when language", "xpath", filter.getExpression().getLanguage());
		
		Endpoint endpoint2 = assertChildIsInstance(filter.getOutputs(), 0, Endpoint.class);
		assertEquals("endpoint2 uri", "seda:choiceWhen", endpoint2.getUri());

		Endpoint endpoint3 = assertChildIsInstance(otherwise.getOutputs(), 0, Endpoint.class);
		assertEquals("endpoint3 uri", "seda:choiceOtherwise", endpoint3.getUri());

		assertContains(routeChildren, endpoint, filter, endpoint2);
	}

	protected RouteSupport assertModelRoundTrip(String name, int outputCount) {
		outputDir.mkdirs();

		File inFile = new File(sourceDir, name);
		RouteContainer model1 = marshaller.loadRoutes(inFile);
		
		File outFile = new File(outputDir, name);
		marshaller.save(outFile, model1);
		
		RouteContainer model2 = marshaller.loadRoutes(outFile);
		
		List<AbstractNode> children1 = model1.getChildren();
		List<AbstractNode> children2 = model2.getChildren();
		assertEquals("Should have the same outputs " + model1 + " and " + model2, model1.getOutputs().size(), model2.getOutputs().size());
		assertEquals("Should have the same children " + model1 + " and " + model2, children1.size(), children2.size());
		assertEquals("Child count", outputCount, children1.size());

		RouteSupport route1 = assertIsInstance(children1.get(0), RouteSupport.class);
		RouteSupport route2 = assertIsInstance(children2.get(0), RouteSupport.class);

		assertEquals("Should have the same outputs " + route1 + " and " + route2, route1.getOutputs().size(), route2.getOutputs().size());
		assertEquals("Should have the same children " + route1 + " and " + route2, route1.getChildren().size(), route2.getChildren().size());
		
		return route2;
	}

	protected <T> void assertContains(Collection<T> collection, T... items) {
		for (T item : items) {
			assertTrue("collection should contain " + item, collection.contains(item));
		}
	}
	
	protected <T> T assertChildIsInstance(List<AbstractNode> list, int index, Class<T> aClass) {
		int minSize = index + 1;
		assertTrue("List should have at least " + minSize + " elements. But was: " + list, list.size() >= minSize);
		return assertIsInstance(list.get(index), aClass);
	}
	
	protected <T> T assertIsInstance(Object object, Class<T> aClass) {
		assertTrue("Should be an instance of " + aClass.getName() + " but was: " + object, aClass.isInstance(object));
		return aClass.cast(object);
	}



}
