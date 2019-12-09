package org.fusesource.ide.jmx.commons.messages;

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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="binaryBody")
@XmlAccessorType(XmlAccessType.FIELD)
public class BinaryBody implements BodyType {
	@XmlInlineBinaryData
    @XmlValue
    private byte[] value;

    
	public BinaryBody() {
	}

	public BinaryBody(byte[] value) {
		this.value = value;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
}
