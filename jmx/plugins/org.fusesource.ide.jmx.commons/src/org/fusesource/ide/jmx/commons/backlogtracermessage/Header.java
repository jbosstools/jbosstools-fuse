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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.fusesource.ide.camel.model.service.core.jmx.camel.IBacklogTracerHeader;

/**
 * @author lhein
 */
@XmlRootElement(name = "header")
public class Header implements IBacklogTracerHeader {
	private String key;
	private String type;
	private String value;

	public Header() {
	}

	public Header(String key, String value, String type) {
		this.key = key;
		this.value = value;
		this.type = type;
	}

	/**
	 * @return the key
	 */
	@Override
	@XmlAttribute(name = "key")
	public String getKey() {
		return this.key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Override
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the type
	 */
	@Override
	@XmlAttribute(name = "type")
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	@Override
	@XmlValue
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s=%s", getKey(), getValue());
	}
}
