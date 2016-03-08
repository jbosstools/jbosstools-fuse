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
package org.fusesource.ide.camel.model.service.core.debug.model.exchange;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "backlogTracerEventMessage")
public class BacklogTracerEventMessage {
	
	private String uid;
	private String timestamp;
	private String routeId;
	private String toNode;
	private String exchangeId;
	private Message message;

	/**
	 * @return the uid
	 */
	@XmlElement(name = "uid")
	public String getUid() {
		return this.uid;
	}
	
	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	/**
	 * @return the timestamp
	 */
	@XmlElement(name = "timestamp")
	public String getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the routeId
	 */
	@XmlElement(name = "routeId")
	public String getRouteId() {
		return this.routeId;
	}
	
	/**
	 * @param routeId the routeId to set
	 */
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	/**
	 * @return the toNode
	 */
	@XmlElement(name = "toNode")
	public String getToNode() {
		return this.toNode;
	}
	
	/**
	 * @param toNode the toNode to set
	 */
	public void setToNode(String toNode) {
		this.toNode = toNode;
	}
	
	/**
	 * @return the exchangeId
	 */
	@XmlElement(name = "exchangeId")
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
	 * @return the message
	 */
	@XmlElement(name = "message")
	public Message getMessage() {
		return this.message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}
}
