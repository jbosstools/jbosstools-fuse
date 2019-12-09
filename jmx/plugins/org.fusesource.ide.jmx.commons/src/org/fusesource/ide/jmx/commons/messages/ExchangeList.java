/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.messages;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "exchanges")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeList implements IExchangeBrowser, PreMarshalHook  {
	@XmlElement(name = "exchange", required = false)
	private List<Exchange> exchanges = new ArrayList<Exchange>();

	public ExchangeList() {
	}

	public ExchangeList(List<Exchange> exchanges) {
		this.exchanges = exchanges;
	}

	public List<Exchange> getExchanges() {
		return exchanges;
	}

	public void setExchanges(List<Exchange> exchanges) {
		this.exchanges = exchanges;
	}

	@Override
	public List<IExchange> browseExchanges() {
		List<IExchange> answer = new ArrayList<IExchange>(exchanges.size());
		for (IExchange exchange : exchanges) {
			answer.add(exchange);
		}
		return answer;
	}
	
	@Override
	public void preMarshal() {
		for (Exchange exchange : exchanges) {
			exchange.preMarshal();
		}
	}

	public void add(Exchange exchange) {
		exchanges.add(exchange);
	}

}
