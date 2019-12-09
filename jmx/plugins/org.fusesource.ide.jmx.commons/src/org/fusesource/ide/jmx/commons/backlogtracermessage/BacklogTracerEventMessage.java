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

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fusesource.ide.camel.model.service.core.jmx.camel.IBacklogTracerEventMessageMBean;

/**
 * @author lhein
 */
@XmlRootElement(name = "backlogTracerEventMessage")
public class BacklogTracerEventMessage implements IBacklogTracerEventMessageMBean {
	
	private long uid;
	private Date timestamp;
	private String routeId;
	private String toNode;
	private String exchangeId;
	private Message message;

	/**
	 * @return the uid
	 */
	@Override
	@XmlElement(name = "uid")
	public long getUid() {
		return this.uid;
	}
	
	/**
	 * @param uid the uid to set
	 */
	@Override
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	/**
	 * @return the timestamp
	 */
	@Override
	@XmlElement(name = "timestamp")
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @param timestamp the timestamp to set
	 */
	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the routeId
	 */
	@Override
	@XmlElement(name = "routeId")
	public String getRouteId() {
		return this.routeId;
	}
	
	/**
	 * @param routeId the routeId to set
	 */
	@Override
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	/**
	 * @return the toNode
	 */
	@Override
	@XmlElement(name = "toNode")
	public String getToNode() {
		return this.toNode;
	}
	
	/**
	 * @param toNode the toNode to set
	 */
	@Override
	public void setToNode(String toNode) {
		this.toNode = toNode;
	}
	
	/**
	 * @return the exchangeId
	 */
	@Override
	@XmlElement(name = "exchangeId")
	public String getExchangeId() {
		return this.exchangeId;
	}
	
	/**
	 * @param exchangeId the exchangeId to set
	 */
	@Override
	public void setExchangeId(String exchangeId) {
		this.exchangeId = exchangeId;
	}
	
	/**
	 * @return the message
	 */
	@Override
	@XmlElement(name = "message")
	public Message getMessage() {
		return this.message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.message = (Message) message;
	}

	/**
	 * @return
	 */
	@Override
	public String getMessageAsXml() {
		throw new RuntimeException("Not implemented");
	}
}
