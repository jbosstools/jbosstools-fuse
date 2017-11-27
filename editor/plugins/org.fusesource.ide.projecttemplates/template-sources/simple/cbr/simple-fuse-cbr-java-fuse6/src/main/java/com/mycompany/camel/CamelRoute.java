package com.mycompany.camel;

import org.apache.camel.builder.RouteBuilder;

public class CamelRoute extends RouteBuilder {

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

		from("file:work/cbr/input")
			.log("Receiving order ${file:name}")
			.choice()
				.when().xpath("//order/customer/country[text() = 'UK']")
					.log("Sending order ${file:name} to the UK")
					.to("file:work/cbr/output/uk")
				.when().xpath("//order/customer/country[text() = 'US']")
					.log("Sending order ${file:name} to the US")
					.to("file:work/cbr/output/us")
				.otherwise()
					.log("Sending order ${file:name} to another country")
					.to("file:work/cbr/output/others")
			.log("Done processing ${file:name}");
	}

}
