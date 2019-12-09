/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.backlogtracermessage;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.service.core.jmx.camel.IBacklogTracerMessage;

/**
 * @author lhein
 */
@XmlRootElement(name = "message")
public class Message implements IBacklogTracerMessage {
	private String exchangeId;
	private List<Header> headers;
	private String body;

	/**
	 * @return the exchangeId
	 */
	@Override
	@XmlAttribute(name = "exchangeId")
	public String getExchangeId() {
		return this.exchangeId;
	}

	/**
	 * @param exchangeId
	 *            the exchangeId to set
	 */
	@Override
	public void setExchangeId(String exchangeId) {
		this.exchangeId = exchangeId;
	}

	/**
	 * @return the headers
	 */
	@Override
	@XmlElementWrapper(name = "headers")
	@XmlElement(name = "header")
	public List<Header> getHeaders() {
		return this.headers;
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	/**
	 * @return the body
	 */
	@Override
	@XmlElement(name = "body")
	public String getBody() {
		return this.body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	@Override
	public void setBody(String body) {
		this.body = body;
	}
}
