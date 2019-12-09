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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import org.fusesource.ide.foundation.core.util.Strings;


@XmlRootElement(name="header")
@XmlAccessorType(XmlAccessType.FIELD)
public class Header {
	@XmlAttribute(name = "key", required = true)
	private String name;
	@XmlAttribute(required = false)
	private String type;
    @XmlValue
	private String text;
    @XmlTransient
	private Object value;

	public Header() {
	}

	public Header(String name, Object value) {
		this.name = name;
		this.value = value;
		if (value != null) {
			this.type = value.getClass().getCanonicalName();
		}
		// force the text to be associated
		getText();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		if (text == null) {
			text = Strings.getOrElse(value, "");
		}
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.value = null;
	}

	public Object getValue() {
		if (value == null) {
			// lets convert the text into the correct type
			value = TypeConverters.stringToType(text, type);
		}
		return value;
	}

	public void setValue(Object value) {
		this.text = null;
		this.value = value;
	}
}
