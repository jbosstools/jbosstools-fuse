package com.mycompany.camel;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;


public class CamelRoute extends RouteBuilder {
	
	private static String BROKER_URL = "vm://localhost?broker.persistent=false&broker.useJmx=false&broker.useShutdownHook=false"; 
	private static String BROKER_URL2 = "failover:(tcp://localhost:61616,tcp://localhost:61616)?persistent=false&useJmx=false"; 
	
	@Override
	public void configure() throws Exception {
		/* When this route is started, it will automatically create the work/cbr/input directory 
		 * where you can drop the file that need to be processed.

        The <log/> elements are used to add human-friendly business logging statements. They 
        make it easier to see what the route is doing.

        The <choice/> element contains the content based router. The two <when/> clauses use 
        XPath to define the criteria for entering that part of the route. When the country in 
        the XML message is set to UK or US, the file will be moved to a directory for that country. 
        The <otherwise/> element ensures that any file that does not meet the requirements for 
        either of the <when/> elements will be moved to the work/cbr/output/others directory.
        
		 */
		ActiveMQComponent activeMQComponent = new ActiveMQComponent();
		activeMQComponent.setBrokerURL(BROKER_URL);
		getContext().addComponent("activemq", activeMQComponent);		

		from("file:src/data?noop=true")
			.to("activemq:personnel.records");
		
		from("activemq:personnel.records")
			.log("Receiving record ${file:name}")
			.choice()
				.when()
					.xpath("/person/city = 'London'")
					.to("file:target/messages/uk")
					.log("Sending record ${file:name} to the UK")
				.otherwise()
					.to("file:target/messages/others")
					.log("Sending record ${file:name} to Other")
			.log("Done processing ${file:name}");
	}

}
