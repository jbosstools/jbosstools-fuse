package com.mycompany.camel.cxf.code.first.java.incident;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfComponent;
import org.apache.camel.component.cxf.CxfEndpoint;

public class CamelRoute extends RouteBuilder {

	public static final String SERVICE_ADDRESS = "http://localhost:9292/cxf/report";
	
	@Override
	public void configure() throws Exception {

		CxfComponent cxfComponent = new CxfComponent(getContext());
		CxfEndpoint serviceEndpoint = new CxfEndpoint(SERVICE_ADDRESS, cxfComponent);
		serviceEndpoint.setServiceClass(IncidentService.class);
		serviceEndpoint.setBeanId("reportEndpoint");
		// Here we just pass the exception back, don't need to use errorHandler
		errorHandler(noErrorHandler());
		
		from(serviceEndpoint)
		    .recipientList()
		    .simple("direct:${header.operationName}");
		from("direct:reportIncident")
		    .log("reportIncident Call")
		    .process(new ReportIncidentProcessor())
		    .to("log:output");
		from("direct:statusIncident")
                    .log("statusIncident Call")
                    .process(new StatusIncidentProcessor())
                    .to("log:output");
	}

}
