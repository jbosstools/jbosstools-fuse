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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;

public class ModelTestSupport {

	public ModelTestSupport() {
		super();
	}

	protected void assertNodeText(String name, AbstractNode node, String string2,
			String string3, String string4) {
			
				System.out.println("node " +  node + " displayText='" + node.getDisplayText() 
						+ "' tooltip='" + node.getDisplayToolTip() + "' description='" + node.getDescription() + "'");
			
				// TODO add test case!!!
				//assertEquals(name + ".getDisplayText()", "", node.getDisplayText());
				//assertEquals(name + ".getDescription()", "", node.getDescription());
				
			}

	protected void assertValidNode(AbstractNode node, String name) {
		//assertEquals(name + ".id", "", node.getId());
		// we don't default a description now by default!
		//assertNotNull(name + ".description", node.getDescription());
	}

	protected <T> T assertIsInstance(Object object, Class<T> aClass) {
		assertTrue("Should be an instance of " + aClass.getName(), aClass.isInstance(object));
		return aClass.cast(object);
	}

	protected <T> T assertSingleOutput(ProcessorDefinition<?> node, Class<T> aClass) {
		return assertSingletonList(node.getOutputs(), aClass);
	}

	protected <T> T assertSingleOutput(AbstractNode node, Class<T> aClass) {
		return assertSingletonList(node.getOutputs(), aClass);
	}

	protected <T> T assertSingleInput(RouteDefinition node, Class<T> aClass) {
		return assertSingletonList(node.getInputs(), aClass);
	}

	protected <T> T assertSingleInput(RouteSupport route, Class<T> aClass) {
		return assertSingletonList(route.getRootNodes(), aClass);
	}

	protected <T> T assertOutput(ProcessorDefinition<?> node, int index, Class<T> aClass) {
		return assertListElement(node.getOutputs(), index, aClass);
	}

	protected <T> T assertOutput(AbstractNode node, int index, Class<T> aClass) {
		return assertListElement(node.getOutputs(), index, aClass);
	}

	protected <T> T assertListElement(List<?> list, int index, Class<T> aClass) {
		int actual = list.size();
		assertTrue("Size of outputs must be bigger than " + index + " but was " + actual, actual > index);
		Object object = list.get(index);
		return assertIsInstance(object, aClass);
	}
	
	protected <T> T assertSingletonList(List<?> list, Class<T> aClass) {
		assertSize(list, 1);
		Object object = list.get(0);
		return assertIsInstance(object, aClass);
	}

	protected void assertSize(List<?> list, int size) {
		assertEquals("Size of " + list, size, list.size());
	}

}