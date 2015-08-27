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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
