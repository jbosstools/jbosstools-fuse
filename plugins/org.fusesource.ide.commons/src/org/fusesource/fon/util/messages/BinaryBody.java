package org.fusesource.fon.util.messages;

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

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
}
