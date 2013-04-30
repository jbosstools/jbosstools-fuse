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

import javax.xml.bind.JAXBException;

import org.fusesource.fon.util.messages.Exchange;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IMessage;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XmlEscapeBadXmlFromCamelTest {

	@Test
	public void testBadMessage() throws Exception {
		String xml = "<message>\n<body type=\"java.lang.String\"><?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<person user=\"james\">hey</person>\n</body></message>";
		assertParses(xml);
	}

	@Test
	public void testValidMessage() throws Exception {
		String xml = "<message>\n<body type=\"java.lang.String\">&lt;person user=\"james\"&gt;hey&lt;/person&gt;\n</body></message>";
		assertParses(xml);
	}

	protected void assertParses(String xml) throws JAXBException, SAXException {
		Exchange exchange = Exchanges.unmarshalNoNamespaceXmlString(xml);
		
		IMessage message = exchange.getIn();
		
		Object body = message.getBody();
		System.out.println("Got message: " + body);
	}
}
