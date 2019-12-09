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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.xml.XmlEscapeUtility;
import org.fusesource.ide.foundation.ui.util.TextFilter;
import org.fusesource.ide.foundation.ui.util.TextFilters;


@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class Message implements IMessage, TextFilter, PreMarshalHook {

	@XmlElementWrapper(name = "headers", required = false)
	@XmlElement(name = "header", required = false)
	private List<Header> headerList;
	@XmlElement(name = "body", required = false)
	private Body textBodyMarkup;
	@XmlElement(name = "binaryBody", required = false)
	private BinaryBody binaryBodyMarkup;
	@XmlAttribute(required = false)
	private String id;
	@XmlAttribute(required = false)
	private Long uuid;

	// tracing information
	@XmlAttribute(required = false)
	private String toNode;
	@XmlAttribute(required = false)
	private Date timestamp;
	@XmlAttribute(required = false)
	private String endpointUri;
	@XmlAttribute(required = false)
	private Long relativeTime;
	@XmlAttribute(required = false)
	private Long elapsedTime;
	@XmlAttribute(required = false)
	private Integer exchangeIndex;

	@XmlTransient
	private Map<String, Object> headers;
	@XmlTransient
	private Object bodyValue;
	@XmlTransient
	private BodyType bodyMarkup;

	public Message() {
	}

	public Message(Object body) {
		this.bodyValue = body;
	}

	@Override
	public String toString() {
		return "Message[headers=" + (headers != null ? headers : (headerList != null ? headerList : "")) 
				+ ", body=" + ((bodyValue != null ? bodyValue : (textBodyMarkup != null ? textBodyMarkup : binaryBodyMarkup))) + "]";
	}

	@Override
	public Map<String, Object> getHeaders() {
		if (headers == null) {
			headers = new HashMap<String, Object>();
			if (headerList != null) {
				for (Header header : headerList) {
					String name = header.getName();
					Object value = header.getValue();
					if (name != null && value != null) {
						headers.put(name, value);
					}
				}
			}
		}
		return headers;
	}

	@Override
	public String getId() {
		if (id == null) {
			id = Strings.getOrElse(getHeaders().get("JMSMessageId"), null);
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Long getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	@Override
	public Object getBody() {
		if (bodyValue == null) {
			if (textBodyMarkup != null) {
				String text = textBodyMarkup.getValue();
				if (text != null) {
					// TODO should we always XML unescape when extracting from the body
					text = XmlEscapeUtility.unescape(text);
				}
				bodyValue = text;
			} else if (binaryBodyMarkup != null) {
				bodyValue = binaryBodyMarkup.getValue();
			}
		}
		return bodyValue;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	/**
	 * Allow the cached body value to be cleared so that it is lazily recalculated from the textBodyMarkup or binaryBodyMarkup
	 */
	public void clearCachedBody() {
		this.bodyValue = null;
	}
	
	public void setBody(Object body) {
		this.bodyValue = body;
		if (body instanceof byte[]) {
			this.binaryBodyMarkup = new BinaryBody((byte[]) body);
		} else {
			// lets encode the text 
			this.textBodyMarkup = new Body(body);
		}
	}

	public List<Header> getHeaderList() {
		if (headerList == null) {
			headerList = new ArrayList<Header>();
			Set<Entry<String, Object>> entrySet = getHeaders().entrySet();
			for (Entry<String, Object> entry : entrySet) {
				Object value = entry.getValue();
				if (value != null) {
					headerList.add(new Header(entry.getKey(), value));
				}
			}
		}
		return headerList;
	}

	public void setHeaderList(List<Header> headerList) {
		this.headerList = headerList;
	}

	public BodyType getBodyMarkup() {
		return bodyMarkup;
	}

	public void setBodyMarkup(Body bodyMarkup) {
		this.bodyMarkup = bodyMarkup;
	}

	@Override
	public String getToNode() {
		return toNode;
	}

	@Override
	public void setToNode(String toNode) {
		this.toNode = toNode;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the relative time from the first exchange at which this trace message occurred
	 */
	@Override
	public Long getRelativeTime() {
		return relativeTime;
	}

	@Override
	public void setRelativeTime(Long elapsedTime) {
		this.relativeTime = elapsedTime;
	}

	@Override
	public Long getElapsedTime() {
		return elapsedTime;
	}

	@Override
	public void setElapsedTime(Long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	@Override
	public Integer getExchangeIndex() {
		return exchangeIndex;
	}

	@Override
	public void setExchangeIndex(Integer exchangeIndex) {
		this.exchangeIndex = exchangeIndex;
	}

	@Override
	public String getEndpointUri() {
		return endpointUri;
	}

	@Override
	public void setEndpointUri(String endpointUri) {
		this.endpointUri = endpointUri;
	}

	@Override
	public boolean matches(String searchText) {
		return TextFilters.matches(searchText, getBody()) || TextFilters.matches(searchText, getToNode())
				|| TextFilters.matches(searchText, getHeaders());
	}

	@Override
	public void preMarshal() {
		if (headers != null) {
			headerList = null;
			getHeaderList();
		}
	}

	public void setHeader(String name, String value) {
		addHeader(new Header(name, value));
	}

	public void addHeader(Header header) {
		getHeaderList().add(header);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.model.service.core.jmx.camel.
	 * IBacklogTracerMessage#setBody(java.lang.String)
	 */
	void setBody(String body) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.model.service.core.jmx.camel.
	 * IBacklogTracerMessage#setHeaders(java.util.ArrayList)
	 */
	void setHeaders(ArrayList<Header> headers) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.model.service.core.jmx.camel.
	 * IBacklogTracerMessage#setExchangeId(java.lang.String)
	 */
	void setExchangeId(String exchangeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.model.service.core.jmx.camel.
	 * IBacklogTracerMessage#getExchangeId()
	 */
	String getExchangeId() {
		return null;
	}

}
