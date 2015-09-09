package org.example;

import java.io.FileInputStream;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TransformationTest extends CamelSpringTestSupport {

	private String accountTag = "org.xyz.Account";
	
    @EndpointInject(uri = "mock:xml2csv-test-output")
    private MockEndpoint resultEndpoint;

    @Produce(uri = "direct:xml2csv-test-input")
    private ProducerTemplate startEndpoint;

    @Test
    public void transform() throws Exception {
        // setup expectations
        resultEndpoint.expectedMessageCount(1);

        // grab the expected result
        String result = readFile("src/data/acme-cust.csv");
        
        resultEndpoint.expectedBodiesReceived(result);

        // run test
        startEndpoint.sendBody(readFile("src/data/accounts.xml"));

        // verify results
        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:xml2csv-test-input")
                    .log("Input message:\n ${body}")
                    .split(body().tokenizeXML(accountTag, "*"), new AggregateAccounts())
                    .log("Before transformation:\n ${body}")
                    .to("xml2csv")
                    .log("After transformation:\n ${body}")
                    .end()
                    .log("Output message:\n ${body}")
                    .to("mock:xml2csv-test-output");
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
    
    
    
    /**
     * Aggregator implementation which wraps all <account> fragments into a single
     * CSV document with each <account> appearing on a separate line.
     */
    private class AggregateAccounts implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }
            String originalBody = oldExchange.getIn().getBody(String.class);
            String bodyToAdd = newExchange.getIn().getBody(String.class);
            oldExchange.getIn().setBody(originalBody + bodyToAdd);
            return oldExchange;
        }
        
    }
}
