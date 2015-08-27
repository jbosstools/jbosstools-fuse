package org.fusesource.ide.jmx.commons.messages;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.commons.util.TextFilter;
import org.fusesource.ide.commons.util.TextFilters;
import org.fusesource.ide.foundation.core.util.Objects;


@XmlRootElement(name = "exchange")
@XmlAccessorType(XmlAccessType.FIELD)
public class Exchange implements IExchange, TextFilter, PreMarshalHook, Comparable<Exchange> {
	@XmlAttribute(required = false)
	private String id;

	@XmlElement(name="message", required = false)
	private Message in;
	@XmlElement(name="outMessage", required = false)
	private Message out;

	// TODO implement JAXB persistence...
	@XmlTransient
	private Map<String, Object> properties = new HashMap<String, Object>();
	private Date timestamp;
	private Integer exchangeIndex;
	
	public Exchange() {
	}

	public Exchange(Message in) {
		this.in = in;
	}
	
	@Override
	public int compareTo(Exchange that) {
		if (this == that) {
			return 0;
		}
		int answer = Objects.compare(this.id, that.id);
		if (answer == 0) {
			// same exchange id, so sort by uuid if given
			Long uuid1 = in != null ? in.getUuid() : null;
			Long uuid2 = that.in != null ? that.in.getUuid() : null;
			if (uuid1 != null && uuid2 != null) {
				answer = uuid1.compareTo(uuid2);
			}
		}
		// sort by exchange index
		if (answer == 0) {
			answer = Objects.compare(this.in.getExchangeIndex(), that.in.getExchangeIndex());
		}
		// and then timestamp
		if (answer == 0) {
			answer = Objects.compare(this.in.getTimestamp(), that.in.getTimestamp());
		}
		return answer;
	}

	@Override
	public String toString() {
		return "Exchange[" + Strings.join(", ", id) + " : " + in + "]";
	}

	public Integer getExchangeIndex() {
		if (in != null) {
			return in.getExchangeIndex();
		}
		return null;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public IMessage getIn() {
		return in;
	}

	@Override
	public IMessage getOut() {
		return out;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIn(Message in) {
		this.in = in;
	}

	public void setOut(Message out) {
		this.out = out;
	}

	@Override
	public boolean matches(String searchText) {
		return TextFilters.matches(searchText, getId()) || 
		TextFilters.matches(searchText, getProperties()) || 
		TextFilters.matches(searchText, getIn()) || TextFilters.matches(searchText, getOut());
	}

	@Override
	public void preMarshal() {
		if (in != null) {
			in.preMarshal();
		}
		if (out != null) {
			out.preMarshal();
		}
	}

}
