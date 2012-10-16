package org.fusesource.ide.camel.model;

import junit.framework.Assert;

import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.generated.Bean;
import org.fusesource.ide.camel.model.generated.Choice;
import org.fusesource.ide.camel.model.generated.Filter;
import org.fusesource.ide.camel.model.generated.Otherwise;
import org.fusesource.ide.camel.model.generated.When;
import org.junit.Test;



public class CanConnectTest {

	@Test
	public void testCanConnect() throws Exception {
		Endpoint endpoint = new Endpoint();
		Filter filter = new Filter();
		Choice choice = new Choice();
		When when = new When();
		Otherwise otherwise = new Otherwise();
		Bean bean = new Bean();
		
		assertCanConnect(true, endpoint, choice);
		assertCanConnect(true, choice, when);
		assertCanConnect(true, choice, otherwise);
		
		assertCanConnect(true, when, endpoint);
		assertCanConnect(true, when, filter);
		assertCanConnect(true, otherwise, endpoint);
		assertCanConnect(true, otherwise, filter);

		assertCanConnect(true, choice, endpoint);
		assertCanConnect(true, choice, filter);
		
		assertCanConnect(false, endpoint, when);
		assertCanConnect(false, filter, when);
		assertCanConnect(false, endpoint, otherwise);
		assertCanConnect(false, filter, otherwise);
		
		// now lets check we can't connect to something we've already connected in the other direction 
		assertCanConnect(true, endpoint, filter);
		assertCanConnect(true, filter, endpoint);
		endpoint.addTargetNode(filter);
		assertCanConnect(false, endpoint, filter);
		assertCanConnect(false, filter, endpoint);

		// we should still be able to connect this filter to another output
		assertCanConnect(true, filter, bean);

		// already has one otherwise, so can't add another
		choice.addTargetNode(otherwise);
		assertCanConnect(false, choice, new Otherwise());
	}

	protected void assertCanConnect(boolean expected, AbstractNode source, AbstractNode target) throws Exception { 
		boolean actual = source.canConnectTo(target);
		Assert.assertEquals("" + source + " -> " + target + " canConnect: ", expected, actual);
	}
}
