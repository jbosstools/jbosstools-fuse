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

import java.util.Date;
import java.util.List;

import org.fusesource.fon.util.messages.Exchange;
import org.fusesource.fon.util.messages.ExchangeList;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.Message;
import org.junit.Test;

public class ExchangeListRoundTripTest {
	protected int numberOfExchanges = 2;

	@Test
	public void testRoundTrip() throws Exception {
		ExchangeList exchanges = new ExchangeList();
		for (int i = 0; i < numberOfExchanges; i++) {
			Exchange exchange = new Exchange();
			exchange.setId("" + i);

			Message in = new Message();
			in.getHeaders().put("foo", (i + 4) * 2);
			in.setBody("In of Message " + i);
			in.setToNode("node" + i);
			in.setTimestamp(new Date());
			exchange.setIn(in);

			exchanges.add(exchange);
			/*
			 * Message out = new Message(); in.getHeaders().put("foo", (i + 5) *
			 * 2); in.setBody("Out of Message " + i);
			 */
		}

		String xml = Exchanges.marshal(exchanges);
		System.out.println("Marshalled to: " + xml);

		ExchangeList actual = Exchanges.unmarshalExchangesXmlString(xml);
		List<Exchange> list = actual.getExchanges();
		for (Exchange exchange : list) {
			System.out.println("Received: " + exchange);
		}
	}

}