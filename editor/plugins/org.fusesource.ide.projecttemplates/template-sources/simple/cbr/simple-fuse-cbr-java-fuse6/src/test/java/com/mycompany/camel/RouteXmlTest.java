package com.mycompany.camel;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.camel.Consume;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import com.mycompany.camel.CamelRoute;

/**
 * A unit test to verify the Camel route works as designed.
 */
public class RouteXmlTest extends CamelTestSupport {

	// Expected message bodies
	protected Object[] expectedBodies = {}; // empty to start

	// Templates to send to input endpoints
	@Produce(uri = "file:work/cbr/input")
	protected ProducerTemplate inputEndpoint;

	@Consume(uri = "file:work/cbr/output/uk")
	protected MockEndpoint outputEndpointUK;
	@Consume(uri = "file:work/cbr/output/us")
	protected MockEndpoint outputEndpointUS;
	@Consume(uri = "file:work/cbr/output/others")
	protected MockEndpoint outputEndpointOthers;

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
		// Define some input data based on the files we have to test against
		String value1 = getFileContents("src/test/resources/data/order1.xml");
		String value2 = getFileContents("src/test/resources/data/order2.xml");
		String value3 = getFileContents("src/test/resources/data/order3.xml");
		String value4 = getFileContents("src/test/resources/data/order4.xml");
		String value5 = getFileContents("src/test/resources/data/order5.xml");

		expectedBodies = new Object[] { value1, value2, value3, value4, value5 };

		// Define some expectations
		getMockEndpoint("mock:file:work/cbr/output/uk").expectedMinimumMessageCount(2);
		getMockEndpoint("mock:file:work/cbr/output/others").expectedMinimumMessageCount(1);
		getMockEndpoint("mock:file:work/cbr/output/us").expectedMinimumMessageCount(2);

		// Send some messages to input endpoints
		for (Object expectedBody : expectedBodies) {
			inputEndpoint.sendBody(expectedBody);
		}

		// Validate our expectations
		assertMockEndpointsSatisfied();
	}

	/*
	 * Pull source from a text file.
	 * 
	 * @param path of the file.
	 * 
	 * @return string matching the contents of the file
	 * 
	 * @throws Exception any exception encountered
	 */
	private String getFileContents(String path) throws Exception {
		Path filePath = new File(path).toPath();
		return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
	}
}
