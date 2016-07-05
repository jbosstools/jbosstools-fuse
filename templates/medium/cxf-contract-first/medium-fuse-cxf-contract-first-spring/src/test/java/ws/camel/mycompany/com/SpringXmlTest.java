package ws.camel.mycompany.com;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SpringXmlTest extends CamelSpringTestSupport {

	private static final String BASE_URL = "http://localhost:8181/cxf/order/";
    private CloseableHttpClient httpClient;	
	
	// TODO Create test message bodies that work for the route(s) being tested
	// Expected message bodies
	protected Object[] expectedBodies = { "<something id='1'>expectedBody1</something>",
			"<something id='2'>expectedBody2</something>" };
	// Templates to send to input endpoints
	@Produce(uri = "cxf:bean:orderEndpoint")
	protected ProducerTemplate inputEndpoint;
	@Produce(uri = "seda:ukOrders")
	protected ProducerTemplate input2Endpoint;
	@Produce(uri = "seda:usOrders")
	protected ProducerTemplate input3Endpoint;
	@Produce(uri = "seda:otherOrders")
	protected ProducerTemplate input4Endpoint;
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

	@Test
	public void testCamelRoute() throws Exception {
//		// Create routes from the output endpoints to our mock endpoints so we can assert expectations
//		context.addRoutes(new RouteBuilder() {
//			@Override
//			public void configure() throws Exception {
//				from("seda:ukOrders").to(outputEndpoint);
//				from("seda:otherOrders").to(output3Endpoint);
//				from("seda:usOrders").to(output2Endpoint);
//			}
//		});

		// Define some expectations
		// Define some input data based on the files we have to test against
		String value1 = getFileContents("src/test/resources/generated.xml");

		expectedBodies = new Object[] { value1 };

		// TODO Ensure expectations make sense for the route(s) we're testing
		outputEndpoint.expectedBodiesReceivedInAnyOrder(expectedBodies);

		// Send some messages to input endpoints
		for (Object expectedBody : expectedBodies) {
			inputEndpoint.sendBody(expectedBody);
		}

		// Validate our expectations
		assertMockEndpointsSatisfied();
	}

	@Override
	protected ClassPathXmlApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
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
