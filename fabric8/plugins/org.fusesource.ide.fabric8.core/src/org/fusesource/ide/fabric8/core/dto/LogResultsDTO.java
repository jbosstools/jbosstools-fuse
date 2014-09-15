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
package org.fusesource.ide.fabric8.core.dto;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.fabric8.core.utils.JsonHelper;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 */
public class LogResultsDTO {
	// the default value for undefined property values in json
	private static final String PROPERTY_VALUE_UNDEFINED	= "?";
	
	// LogResults JSON
	private static final String PROPERTY_LOGRESULTS_HOST 	= "host";
	private static final String PROPERTY_LOGRESULTS_FROM 	= "fromTimestamp";
	private static final String PROPERTY_LOGRESULTS_TO   	= "toTimestamp";
	private static final String PROPERTY_LOGRESULTS_EVENTS	= "events";

	private String host;
	private Long from;
	private Long to;
	private List<LogEventDTO> logEvents;
	
	public LogResultsDTO() {
		this(null, null, null, new ArrayList<LogEventDTO>());
	}
	
	/**
	 * creates the log results object
	 * 
	 * @param host
	 * @param from
	 * @param to
	 * @param events
	 */
	public LogResultsDTO(String host, Long from, Long to, List<LogEventDTO> events) {
		this.host = host;
		this.from = from;
		this.to = to;
		this.logEvents = events;
	}
	
	/**
	 * @return the from
	 */
	public Long getFrom() {
		return this.from;
	}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return this.host;
	}
	
	/**
	 * @return the logEvents
	 */
	public List<LogEventDTO> getLogEvents() {
		return this.logEvents;
	}
	
	/**
	 * @return the to
	 */
	public Long getTo() {
		return this.to;
	}
	
	/**
	 * @param from the from to set
	 */
	public void setFrom(Long from) {
		this.from = from;
	}
	
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * @param logEvents the logEvents to set
	 */
	public void setLogEvents(List<LogEventDTO> logEvents) {
		this.logEvents = logEvents;
	}
	
	/**
	 * @param to the to to set
	 */
	public void setTo(Long to) {
		this.to = to;
	}
	
	/**
	 * creates a log results object out of the json model node
	 * 
	 * @param rootNode
	 * @return
	 * @throws Exception
	 */
	public static LogResultsDTO fromJson(ModelNode rootNode) throws Exception {
		final String host = JsonHelper.getAsString(rootNode, PROPERTY_LOGRESULTS_HOST);
		final Long from = JsonHelper.getAsLong(rootNode, PROPERTY_LOGRESULTS_FROM);
		final Long to = JsonHelper.getAsLong(rootNode, PROPERTY_LOGRESULTS_TO);
		
		LogResultsDTO res = new LogResultsDTO();
		res.setHost(host);
		res.setFrom(from);
		res.setTo(to);
		
		final List<ModelNode> events = JsonHelper.getAsList(rootNode, PROPERTY_LOGRESULTS_EVENTS);
		for (ModelNode ev : events) {
			res.getLogEvents().add(LogEventDTO.fromJson(res, ev));
		}
		
		return res;
	}	
}
