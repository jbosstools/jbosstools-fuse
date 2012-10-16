package org.fusesource.ide.camel.model;

import static org.junit.Assert.*;


import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.generated.Filter;
import org.fusesource.ide.camel.model.generated.Route;
import org.junit.Test;


public class DisplayTextTest extends ModelTestSupport {

	@Test
	public void testModelSave() throws Exception {
		
		assertDisplayText(new Endpoint());
		assertDisplayText(new Filter());
		assertDisplayText(new Route());
	}

	private void assertDisplayText(AbstractNode node) {
		String displayText = node.getDisplayText();
		assertTrue("Display text should not be null for node: " + node, displayText != null);
		assertTrue("Display text length should be greater than zero: " + node, displayText.trim().length() > 0);
		
		System.out.println("Node " + node + " has displayText: " + displayText);
	}
	
}
