package org.example;

import java.io.FileInputStream;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TransformationTest extends CamelSpringTestSupport {

    @EndpointInject(uri = "mock:xml2xml-test-output")
    private MockEndpoint resultEndpoint;

    @Produce(uri = "direct:xml2xml-test-input")
    private ProducerTemplate startEndpoint;

    @Test
    public void transform() throws Exception {
        // setup expectations
        resultEndpoint.expectedMessageCount(1);

        // grab the expected result
        String result = readFile("src/data/xyz-order.xml");
        
        // scrub the hard return/line feeds for actual generated results
        result = result.replaceAll("[\r\n]+", "\n");
        
        resultEndpoint.expectedBodiesReceived(result);

        // run test
        startEndpoint.sendBodyAndHeader(readFile("src/data/abc-order.xml"), "approval", "AUTO");

        // verify results
        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:xml2xml-test-input")
                    .log("Before transformation:\n ${body}")
                    .to("ref:xml2xml")
                    .log("After transformation:\n ${body}")
                    .to("mock:xml2xml-test-output");
            }
        };
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    }

    private String readFile(String filePath) throws Exception {
        String content;
        FileInputStream fis = new FileInputStream(filePath);
        try {
            content = createCamelContext().getTypeConverter().convertTo(String.class, fis);
        } finally {
            fis.close();
        }
        return content;
    }
}
