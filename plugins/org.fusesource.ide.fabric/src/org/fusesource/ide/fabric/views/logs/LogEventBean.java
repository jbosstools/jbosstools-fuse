package org.fusesource.ide.fabric.views.logs;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.util.BeanSupport;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.insight.log.LogEvent;

import com.google.common.base.Joiner;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LogEventBean extends BeanSupport implements Comparable<LogEventBean> {
	protected static final transient Joiner newlineJoiner = Joiner.on("/n");

	private final LogEvent event;

	public static LogEventBean toLogEventBean(Object element) {
		if (element instanceof LogEventBean) {
			return (LogEventBean) element;
		}
		if (element instanceof LogEvent) {
			LogEvent event = (LogEvent) element;
			return new LogEventBean(event);
		}
		return null;
	}

	public LogEventBean(LogEvent event) {
		this.event = event;
		// TODO Auto-generated constructor stub
	}

	public LogEventBean() {
		this(new LogEvent());
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogEventBean other = (LogEventBean) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		return true;
	}

	@Override
	public int compareTo(LogEventBean that) {
		return this.event.compareTo(that.event);
	}

	public String getExceptionText() {
		String[] exception = getException();
		if (exception == null) {
			return "";
		}
		return newlineJoiner.join(exception);
	}

	public Image getLevelImage() {
		String l = getLevel();
		if (l != null) {
			if ("INFO".equalsIgnoreCase(l)) {
				return FabricPlugin.getDefault().getImage("information.gif");
			} else if ("ERROR".equalsIgnoreCase(l)) {
				return FabricPlugin.getDefault().getImage("error.gif");
			} else if ("WARN".equalsIgnoreCase(l)) {
				return FabricPlugin.getDefault().getImage("warning.gif");
			}
		}
		return null;
	}

	public String getClassName() {
		return event.getClassName();
	}

	public String[] getException() {
		return event.getException();
	}

	public String getFileName() {
		return event.getFileName();
	}

	public String getHost() {
		return event.getHost();
	}

	public String getContainer() {
		return event.getContainerName();
	}

	public String getLevel() {
		return event.getLevel();
	}

	public String getLineNumber() {
		return event.getLineNumber();
	}

	public String getLogger() {
		return event.getLogger();
	}

	public String getMessage() {
		return event.getMessage();
	}

	public String getMethodName() {
		return event.getMethodName();
	}

	public Map<String, String> getProperties() {
		return event.getProperties();
	}

	public Long getSeq() {
		return event.getSeq();
	}

	public String getThread() {
		return event.getThread();
	}

	public Date getTimestamp() {
		return event.getTimestamp();
	}

	public void setClassName(String className) {
		event.setClassName(className);
	}

	public void setException(String[] exception) {
		event.setException(exception);
	}

	public void setFileName(String fileName) {
		event.setFileName(fileName);
	}

	public void setHost(String host) {
		event.setHost(host);
	}

	public void setContainer(String containerName) {
		event.setContainerName(containerName);
	}

	public void setLevel(String level) {
		event.setLevel(level);
	}

	public void setLineNumber(String lineNumber) {
		event.setLineNumber(lineNumber);
	}

	public void setLogger(String logger) {
		event.setLogger(logger);
	}

	public void setMessage(String message) {
		event.setMessage(message);
	}

	public void setMethodName(String methodName) {
		event.setMethodName(methodName);
	}

	public void setProperties(Map<String, String> properties) {
		event.setProperties(properties);
	}

	public void setSeq(Long seq) {
		event.setSeq(seq);
	}

	public void setThread(String thread) {
		event.setThread(thread);
	}

	public void setTimestamp(Date timestamp) {
		event.setTimestamp(timestamp);
	}

	@Override
	public String toString() {
		return event.toString();
	}

	// Delegation methods

}
