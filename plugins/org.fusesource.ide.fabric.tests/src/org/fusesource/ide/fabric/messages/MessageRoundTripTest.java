/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.fusesource.fon.util.messages.Exchange;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IMessage;
import org.fusesource.fon.util.messages.Message;
import org.junit.Test;

public class MessageRoundTripTest {
	Long aLong = new Long(123456790L);
	Integer anInt = 1234;
	String aString = "hello";
	String expectedBody = "Hello World!";
	Boolean aBoolean = true;

	@Test
	public void testRoundTrip() throws Exception {
		Message expectedMessage = new Message();
		Map<String, Object> headers = expectedMessage.getHeaders();
		headers.put("aString", aString);
		headers.put("aLong", aLong);
		headers.put("anInt", anInt);
		headers.put("aBoolean", aBoolean);
		expectedMessage.setBody(expectedBody);

		String xml = Exchanges.marshal(expectedMessage);
		System.out.println("Marshalled to: " + xml);

		
		Exchange exchange = Exchanges.unmarshalXmlString(xml);
		assertNotNull("Should have parsed a valid exchange", exchange);
		IMessage message = exchange.getIn();
		assertNotNull("Should have parsed a valid message", message);
		System.out.println("Parsed message: " + message);

		headers = message.getHeaders();
		assertEquals(aString, headers.get("aString"));
		assertEquals(anInt, headers.get("anInt"));
		assertEquals(aLong, headers.get("aLong"));
		assertEquals(aBoolean, headers.get("aBoolean"));
		assertEquals(expectedBody, message.getBody());
		assertEquals(4, headers.size());
	}

}