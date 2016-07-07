package com.mycompany.camel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mycompany.camel.CamelRoute;

/**
 * A unit test to verify the Camel route works as designed.
 */
public class RouteJavaTest extends CamelTestSupport {

//	private static final String BASE_URL = "http://localhost:8181/cxf/order/";
    private CloseableHttpClient httpClient;	

    // Expected message bodies
	protected Object[] expectedBodies = {}; // empty to start

	// Templates to send to input endpoints
	@Produce(uri = "cxf:bean:orderEndpoint")
	protected ProducerTemplate inputEndpoint;

	@Produce(uri = "seda:ukOrders")
	protected MockEndpoint outputEndpointUK;
	@Produce(uri = "seda:usOrders")
	protected MockEndpoint outputEndpointUS;
	@Produce(uri = "seda:otherOrders")
	protected MockEndpoint outputEndpointOthers;
	
	// Mock endpoints used to consume messages from the output endpoints and then perform assertions
	@EndpointInject(uri = "mock:output")
	protected MockEndpoint outputEndpoint;
	@EndpointInject(uri = "mock:output2")
	protected MockEndpoint output2Endpoint;
	@EndpointInject(uri = "mock:output3")
	protected MockEndpoint output3Endpoint;
	@EndpointInject(uri = "mock:end")
	protected MockEndpoint output4Endpoint;
	@EndpointInject(uri = "mock:end")
	protected MockEndpoint output5Endpoint;
	@EndpointInject(uri = "mock:end")
	protected MockEndpoint output6Endpoint;


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
		String value1 = getFileContents("src/test/resources/generated.xml");

		expectedBodies = new Object[] { value1 };

		outputEndpoint.expectedBodiesReceivedInAnyOrder(expectedBodies);
		
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

	@Before
    public void setUpTests() {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
    }

    @After
    public void closeHttpClient() throws IOException {
        httpClient.close();
        httpClient = null;
    }
}
