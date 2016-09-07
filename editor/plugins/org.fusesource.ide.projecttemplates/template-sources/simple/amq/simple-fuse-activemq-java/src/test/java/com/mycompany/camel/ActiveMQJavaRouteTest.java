package com.mycompany.camel;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * A unit test to verify the Camel route works as designed.
 */
public class ActiveMQJavaRouteTest extends CamelTestSupport {

	// Expected message bodies
	protected Object[] expectedBodies = { 
			"<person><city>Raleigh</city></person>", 
			"<person><city>London</city></person>" };
	
	// Templates to send to input endpoints
	@Produce(uri = "file:src/data?noop=true")
	protected ProducerTemplate inputEndpoint;
	
	// Mock endpoints used to consume messages from the output endpoints and then perform assertions
	@EndpointInject(uri = "mock:file:target/messages/uk")
	protected MockEndpoint ukOutputEndpoint;
	@EndpointInject(uri = "mock:file:target/messages/other")
	protected MockEndpoint othersOutputEndpoint;

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new CamelRoute();
	}

	@Override
	public String isMockEndpoints() {
		return "*";
	}

	@Test
	public void testCamelRoute() throws Exception {
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("file:target/messages/others").to(othersOutputEndpoint);
				from("file:target/messages/uk").to(ukOutputEndpoint);
			}
		});

		// Define some expectations
		getMockEndpoint("mock:file:target/messages/uk").expectedMinimumMessageCount(1);
		getMockEndpoint("mock:file:target/messages/other").expectedMinimumMessageCount(1);

		// Send some messages to input endpoints
		for (Object expectedBody : expectedBodies) {
			inputEndpoint.sendBody(expectedBody);
		}

		// Validate our expectations
		assertMockEndpointsSatisfied();
	}
}
