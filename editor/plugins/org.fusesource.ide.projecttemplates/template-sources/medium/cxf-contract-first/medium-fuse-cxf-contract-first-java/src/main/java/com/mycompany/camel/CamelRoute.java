package com.mycompany.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.cxf.CxfComponent;
import org.apache.camel.component.cxf.CxfEndpoint;

public class CamelRoute extends RouteBuilder {

	public static final String SERVICE_ADDRESS = "http://localhost:12345/cxf/order";
	
	@Override
	public void configure() throws Exception {

		CxfComponent cxfComponent = new CxfComponent(getContext());
		CxfEndpoint serviceEndpoint = new CxfEndpoint(SERVICE_ADDRESS, cxfComponent);
		serviceEndpoint.setBeanId("orderEndpoint");
		serviceEndpoint.setServiceClass(ws.camel.mycompany.com.OrderEndpoint.class);
		// Here we just pass the exception back, don't need to use errorHandler
		errorHandler(noErrorHandler());
		
		Namespaces ns = new Namespaces("order", "http://com.mycompany.camel.ws");

		from(serviceEndpoint)
			.setBody(simple("${in.body[0]}"))
			.log("Got this WSDL payload: ${body}")
			.choice()
				.when(ns.xpath("/order:order/order:customer/country[text() = 'UK']"))
					.log("Sending order to the UK")
					.to("seda:ukOrders")
				.when(ns.xpath("/order:order/order:customer/country[text() = 'US']"))
					.log("Sending order to the US")
					.to("seda:usOrders")
				.otherwise()
					.log("Sending order to another country")
					.to("seda:otherOrders")
			.end()
			.transform(constant("OK"))
			.log("Done processing order");
		
		from("seda:ukOrders").to("mock:end");
		from("seda:usOrders").to("mock:end");
		from("seda:otherOrders").to("mock:end");
	}

}
