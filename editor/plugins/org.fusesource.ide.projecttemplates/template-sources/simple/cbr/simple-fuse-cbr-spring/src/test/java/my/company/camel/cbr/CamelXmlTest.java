package my.company.camel.cbr;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A unit test to verify the Camel route works as designed.
 */
public class CamelXmlTest extends CamelSpringTestSupport {

	// Expected message bodies
	protected Object[] expectedBodies = {}; // empty to start

	// Templates to send to input endpoints
	@Produce(uri = "file:work/cbr/input")
	protected ProducerTemplate inputEndpoint;

	// Mock endpoints used to consume messages from the output endpoints and
	// then perform assertions
	@EndpointInject(uri = "mock:outputUK")
	protected MockEndpoint outputEndpointUK;
	@EndpointInject(uri = "mock:outputUS")
	protected MockEndpoint outputEndpointUS;
	@EndpointInject(uri = "mock:outputOthers")
	protected MockEndpoint outputEndpointOthers;

	@Test
	public void testCamelRoute() throws Exception {
		// Create routes from the output endpoints to our mock endpoints so we
		// can assert expectations
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("file:work/cbr/output/uk").to(outputEndpointUK);
				from("file:work/cbr/output/others").to(outputEndpointOthers);
				from("file:work/cbr/output/us").to(outputEndpointUS);
			}
		});

		// Define some input data based on the files we have to test against
		String value1 = getFileContents("src/test/resources/data/order1.xml");
		String value2 = getFileContents("src/test/resources/data/order2.xml");
		String value3 = getFileContents("src/test/resources/data/order3.xml");
		String value4 = getFileContents("src/test/resources/data/order4.xml");
		String value5 = getFileContents("src/test/resources/data/order5.xml");

		expectedBodies = new Object[] { value1, value2, value3, value4, value5 };

		// Define some expectations
		outputEndpointUK.expectedMessageCount(2);
		outputEndpointOthers.expectedMessageCount(1);
		outputEndpointUS.expectedMessageCount(2);

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
}
