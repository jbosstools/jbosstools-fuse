package org.fusesource.ide.jmx.commons.messages;

import java.util.Date;
import java.util.Map;

public interface IMessage {
	public Map<String,Object> getHeaders();
	public Object getBody();
	public String getId();
	public Long getUuid();

	// optional tracing information
	public Date getTimestamp();
	public String getToNode();
	public String getEndpointUri();
	public Long getRelativeTime();
	public Integer getExchangeIndex();
	public void setTimestamp(Date timestamp);
	public void setToNode(String toNode);
	public void setEndpointUri(String endpointUri);
	public void setRelativeTime(Long elapsed);
	public abstract void setElapsedTime(Long elapsedTime);
	public abstract Long getElapsedTime();
	public void setExchangeIndex(Integer size);
	public void setUuid(Long uuid);

}
