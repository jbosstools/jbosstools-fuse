package org.fusesource.ide.fabric.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import javax.xml.bind.JAXBException;

import org.fusesource.fon.util.messages.Exchange;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IMessage;
import org.fusesource.fon.util.messages.Message;
import org.junit.Test;

public class MarshalTest {

	private static final String messageBodyXml = "<headers>"
			+ "<header key=\"foo\" type=\"java.lang.String\">bar</header>"
			+ "<header key=\"xyz\" type=\"java.lang.String\">1234</header>"
			+ "</headers>"
	        + "<body type=\"java.lang.String\">Hello World</body>\n"
	        + "</message>";
	
	protected String messageXml = "<message xmlns=\"http://fabric.fusesource.org/schema/messages\">\n" + messageBodyXml;

	protected String noNamespaceMessageXml = "<message>\n" + messageBodyXml;

	@Test
	public void testUnmarshal() throws Exception {
		Exchange exchange = Exchanges.unmarshalXmlString(messageXml);
		assertUnmarshals(exchange);
	}

	@Test
	public void testUnmarshalNoNamespaceXml() throws Exception {
		Exchange exchange = Exchanges.unmarshalNoNamespaceXmlString(noNamespaceMessageXml);
		assertUnmarshals(exchange);
	}

	protected void assertUnmarshals(Exchange exchange) throws JAXBException {
		assertNotNull("Should have parsed a valid exchange", exchange);
		IMessage message = exchange.getIn();
		assertNotNull("Should have parsed a valid message", message);
		assertEquals("Hello World", message.getBody());
		Map<String, Object> headers = message.getHeaders();
		assertEquals("bar", headers.get("foo"));
		assertEquals("1234", headers.get("xyz"));
		assertEquals(2, headers.size());
		System.out.println("Found object: " + message);
		
		String newXml = Exchanges.marshal(message);
		System.out.println("Marshalled to: " + newXml);
	}

	@Test
	public void testMarshal() throws Exception {
		Message message = new Message();
		Map<String, Object> headers = message.getHeaders();
		headers.put("foo", "bar");
		headers.put("xyz", 1234);
		message.setBody("hello world!");
		String newXml = Exchanges.marshal(message);
		System.out.println("Marshalled to: " + newXml);
	}

}