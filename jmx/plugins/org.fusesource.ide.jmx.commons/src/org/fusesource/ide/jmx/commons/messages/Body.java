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
import javax.xml.bind.annotation.XmlValue;

import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.xml.XmlEscapeUtility;



@XmlRootElement(name="body")
@XmlAccessorType(XmlAccessType.FIELD)
public class Body implements BodyType {
	@XmlAttribute(required = false)
	private String type;
    @XmlValue
    private String value;

    
	public Body() {
	}

	public Body(Object value) {
		String text = Strings.getOrElse(value, "");
		// lets encode the text
		text = XmlEscapeUtility.escape(text);
		this.value = text;
		if (value != null) {
			this.type = value.getClass().getCanonicalName();
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
