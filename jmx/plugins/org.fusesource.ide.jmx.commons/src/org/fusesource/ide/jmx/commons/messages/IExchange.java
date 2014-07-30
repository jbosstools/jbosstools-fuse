package org.fusesource.ide.jmx.commons.messages;

import java.util.Map;

/**
 * Represents a message exchange in ActiveMQ, Camel, CXF or ServiceMix
 *
 */
public interface IExchange {
	public String getId();
	
	public Map<String,Object> getProperties();

	public IMessage getIn();
	public IMessage getOut();

}
