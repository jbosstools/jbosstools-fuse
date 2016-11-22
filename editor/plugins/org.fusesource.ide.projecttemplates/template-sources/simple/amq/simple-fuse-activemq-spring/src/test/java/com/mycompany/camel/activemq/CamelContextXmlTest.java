package com.mycompany.camel.activemq;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mycompany.templates.camel.activemq.OrderGenerator;

/**
 * A unit test to verify the Camel route works as designed.
 */
public class CamelContextXmlTest extends CamelSpringTestSupport {
	@EndpointInject(uri = "activemq:incomingOrders?username=${broker.userName}&password=${broker.password}")
    private ProducerTemplate in;

    @Test
    public void testCamelRoute() throws Exception {
        // Stop order generator route so it does not interfere with the test
        context().stopRoute("generate-order");

        // intercept 
        context.getRouteDefinition("jms-cbr-route").adviceWith(context, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending and do something else
                interceptSendToEndpoint("file:work/out/uk")
                    .skipSendToOriginalEndpoint()
                    .to("mock:out-uk");
                // intercept sending and do something else
                interceptSendToEndpoint("file:work/out/us")
                    .skipSendToOriginalEndpoint()
                    .to("mock:out-us");
                // intercept sending and do something else
                interceptSendToEndpoint("file:work/out/others")
                    .skipSendToOriginalEndpoint()
                    .to("mock:out-others");
            }
        });

        MockEndpoint uk = getMockEndpoint("mock:out-uk");
        uk.expectedHeaderReceived("CamelFileName", "order-uk.xml");
        uk.expectedMinimumMessageCount(1);

        MockEndpoint us = getMockEndpoint("mock:out-us");
        us.expectedHeaderReceived("CamelFileName", "order-us.xml");
        us.expectedMinimumMessageCount(1);

        MockEndpoint others = getMockEndpoint("mock:out-others");
        others.expectedHeaderReceived("CamelFileName", "order-other.xml");
        others.expectedMinimumMessageCount(1);

        OrderGenerator generator = new OrderGenerator();

        in.sendBodyAndHeader(generator.generateOrderWithFileName(context(), "data/order2.xml"), "CamelFileName", "order-uk.xml");
        in.sendBodyAndHeader(generator.generateOrderWithFileName(context(), "data/order3.xml"), "CamelFileName", "order-us.xml");
        in.sendBodyAndHeader(generator.generateOrderWithFileName(context(), "data/order1.xml"), "CamelFileName", "order-other.xml");

        assertMockEndpointsSatisfied();
    }

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext(
            "META-INF/spring/camel-context.xml", 
            "camel-context-test.xml"
        );
	}
}
