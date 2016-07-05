package com.mycompany.activemq;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

/**
 * A unit test to verify the Camel route works as designed.
 */
public class ActiveMQBlueprintTest extends CamelBlueprintTestSupport {

	// Expected message bodies
	protected Object[] expectedBodies = { 
			"<person><city>Raleigh</city></person>", 
			"<person><city>London</city></person>" };
	
	// Templates to send to input endpoints
	@Produce(uri = "file:src/data?noop=true")
	protected ProducerTemplate inputEndpoint;
	@Produce(uri = "activemq:personnel.records")
	protected ProducerTemplate recordsInputEndpoint;
	
	// Mock endpoints used to consume messages from the output endpoints and then perform assertions
	@EndpointInject(uri = "mock:output")
	protected MockEndpoint recordsOutputEndpoint;
	@EndpointInject(uri = "mock:output2")
	protected MockEndpoint ukOutputEndpoint;
	@EndpointInject(uri = "mock:output3")
	protected MockEndpoint othersOutputEndpoint;

	@Test
	public void testCamelRoute() throws Exception {
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("activemq:personnel.records").to(recordsOutputEndpoint);
				from("file:target/messages/others").to(othersOutputEndpoint);
				from("file:target/messages/uk").to(ukOutputEndpoint);
			}
		});

		// Define some expectations
		recordsOutputEndpoint.expectedMessageCount(2);
		othersOutputEndpoint.expectedMessageCount(1);
		ukOutputEndpoint.expectedMessageCount(0);

		// Send some messages to input endpoints
		for (Object expectedBody : expectedBodies) {
			inputEndpoint.sendBody(expectedBody);
		}

		// Validate our expectations
		assertMockEndpointsSatisfied();
	}

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/blueprint.xml";
	}

}
