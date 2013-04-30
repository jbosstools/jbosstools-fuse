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

import java.io.File;
import java.lang.reflect.Array;

import junit.framework.Assert;

import org.fusesource.fon.util.messages.Exchange;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IMessage;
import org.fusesource.fon.util.messages.Message;
import org.fusesource.ide.fabric.navigator.MessageDropHandler;


public class MessagesFilesTestSupport {

	public MessagesFilesTestSupport() {
		super();
	}

	protected void assertFile(String name, boolean text) throws Exception {
		// TODO Huge shame its so damn hard to get an IFile from a File!!!!
		
		File file = new File("files", name);
		System.out.println("File exists: " + file.exists());
		Assert.assertTrue(file.exists());
	
		// round trip and assert that contents is the same...
		Message message = new Message();
		MessageDropHandler.setMessageBody(message , file, !text);
		
		Object expectedBody = message.getBody();
		
		System.out.println("Generating message; textMode: " + text + " body type: " + expectedBody.getClass().getCanonicalName());
	
		String xml = Exchanges.marshal(message);
		System.out.println(xml);
		
		Exchange exchange = Exchanges.unmarshalXmlString(xml);
		IMessage actual = exchange.getIn();
		Object actualBody = actual.getBody();
		
		System.out.println("Actual body after round trip: " + actualBody + " of type: " + actualBody.getClass().getCanonicalName());
		
		if (expectedBody != null && expectedBody.getClass().isArray()) {
			assertArrayEquals(expectedBody, actualBody);
		} else {
			Assert.assertEquals(expectedBody, actualBody);
		}
	}

	protected void assertArrayEquals(Object expectedBody, Object actualBody) {
		Assert.assertNotNull("expected should not be null", expectedBody);
		Assert.assertNotNull("actual should not be null", actualBody);
	
		int expectedLength = Array.getLength(expectedBody);
		int actualLength = Array.getLength(actualBody);
		
		Assert.assertEquals("Sizes of arrays not the same", expectedLength, actualLength);
	
		for (int i = 0, size = expectedLength; i < size; i++) {
			Assert.assertEquals("value[" + i + "]", Array.get(expectedBody, i), Array.get(actualBody, i));
		}
	}

}