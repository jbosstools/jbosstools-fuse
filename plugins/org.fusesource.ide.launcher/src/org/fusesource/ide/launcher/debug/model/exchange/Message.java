/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model.exchange;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "message")
public class Message {
	private String exchangeId;
	private ArrayList<Header> headers;
	private String body;
	
	/**
	 * @return the exchangeId
	 */
	@XmlAttribute(name = "exchangeId")
	public String getExchangeId() {
		return this.exchangeId;
	}
	
	/**
	 * @param exchangeId the exchangeId to set
	 */
	public void setExchangeId(String exchangeId) {
		this.exchangeId = exchangeId;
	}
	
	/**
	 * @return the headers
	 */
	@XmlElementWrapper(name = "headers")
	@XmlElement(name = "header")
	public ArrayList<Header> getHeaders() {
		return this.headers;
	}
	
	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(ArrayList<Header> headers) {
		this.headers = headers;
	}
	
	/**
	 * @return the body
	 */
	@XmlElement(name = "body")
	public String getBody() {
		return this.body;
	}
	
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
}
