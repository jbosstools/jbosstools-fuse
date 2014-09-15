/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.core.dto;

import io.fabric8.insight.log.support.Objects;

import java.util.Date;
import java.util.Map;

import org.fusesource.ide.commons.util.BeanSupport;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.fabric8.core.utils.JsonHelper;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 */
public class LogEventDTO extends BeanSupport implements Comparable<LogEventDTO> {
	// the default value for undefined property values in json
	private static final String PROPERTY_VALUE_UNDEFINED	= "?";
	private static final String NEW_LINE = System.getProperty("line.separator");  //$NON-NLS-1$

	// LogEvent JSON
	private static final String PROPERTY_LOGEVENT_HOST 		= "host";
	private static final String PROPERTY_LOGEVENT_SEQ		= "seq";
	private static final String PROPERTY_LOGEVENT_TIMESTAMP = "timestamp";
	private static final String PROPERTY_LOGEVENT_LOGLEVEL	= "level";
	private static final String PROPERTY_LOGEVENT_LOGGER	= "logger";
	private static final String PROPERTY_LOGEVENT_THREAD	= "thread";
	private static final String PROPERTY_LOGEVENT_MESSAGE	= "message";
	private static final String PROPERTY_LOGEVENT_EXCEPTION = "exception";
	private static final String PROPERTY_LOGEVENT_PROPERTIES= "properties";
	private static final String PROPERTY_LOGEVENT_CLASS		= "className";
	private static final String PROPERTY_LOGEVENT_FILE		= "fileName";
	private static final String PROPERTY_LOGEVENT_METHOD	= "methodName";
	private static final String PROPERTY_LOGEVENT_CONTAINER = "containerName";
	private static final String PROPERTY_LOGEVENT_LINE		= "lineNumber";
	
	// LogEvent.Properties JSON
	public static final String PROPERTY_LOGEVENT_PROPERTIES_BUNDLEID 		= "bundle.id";
	public static final String PROPERTY_LOGEVENT_PROPERTIES_BUNDLEVERSION 	= "bundle.version";
	public static final String PROPERTY_LOGEVENT_PROPERTIES_MAVEN_COORDS	= "maven.coordinates";
	public static final String PROPERTY_LOGEVENT_PROPERTIES_BUNDLENAME		= "bundle.name";

	private String className;
	private String container;
	private String[] exception;
	private String fileName;
	private String eventhost;
	private String logLevel;
	private String lineNo;
	private String logger;
	private String logMessage;
	private String methodName;
	private Long seq;
	private String threadName;
	private Date eventTimestamp;
	private Map<String, String> propertiesMap;
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the container
	 */
	public String getContainer() {
		return this.container;
	}

	/**
	 * @param container the container to set
	 */
	public void setContainer(String container) {
		this.container = container;
	}

	/**
	 * @return the exception
	 */
	public String[] getException() {
		return this.exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(String[] exception) {
		this.exception = exception;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the eventhost
	 */
	public String getEventhost() {
		return this.eventhost;
	}

	/**
	 * @param eventhost the eventhost to set
	 */
	public void setEventhost(String eventhost) {
		this.eventhost = eventhost;
	}

	/**
	 * @return the logLevel
	 */
	public String getLogLevel() {
		return this.logLevel;
	}

	/**
	 * @param logLevel the logLevel to set
	 */
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * @return the lineNo
	 */
	public String getLineNo() {
		return this.lineNo;
	}

	/**
	 * @param lineNo the lineNo to set
	 */
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * @return the logger
	 */
	public String getLogger() {
		return this.logger;
	}

	/**
	 * @param logger the logger to set
	 */
	public void setLogger(String logger) {
		this.logger = logger;
	}

	/**
	 * @return the logMessage
	 */
	public String getLogMessage() {
		return this.logMessage;
	}

	/**
	 * @param logMessage the logMessage to set
	 */
	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage.replaceAll("tat", "	at");
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return this.methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the seq
	 */
	public Long getSeq() {
		return this.seq;
	}

	/**
	 * @param seq the seq to set
	 */
	public void setSeq(Long seq) {
		this.seq = seq;
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return this.threadName;
	}

	/**
	 * @param threadName the threadName to set
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * @return the eventTimestamp
	 */
	public Date getEventTimestamp() {
		return this.eventTimestamp;
	}

	/**
	 * @param eventTimestamp the eventTimestamp to set
	 */
	public void setEventTimestamp(Date eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	/**
	 * @return the propertiesMap
	 */
	public Map<String, String> getPropertiesMap() {
		return this.propertiesMap;
	}

	/**
	 * @param propertiesMap the propertiesMap to set
	 */
	public void setPropertiesMap(Map<String, String> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}

	/**
	 * generates a log event dto from a json node
	 * 
	 * @param parent	the log results dto
	 * @param ev		the model node for the log event
	 * @return			the log event dto
	 */
	public static LogEventDTO fromJson(LogResultsDTO parent, ModelNode ev) throws Exception {
		String className = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_CLASS);
		String container = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_CONTAINER);
		String[] exception = JsonHelper.getAsStringArray(ev, PROPERTY_LOGEVENT_EXCEPTION);
		String fileName = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_FILE);
		String eventhost = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_HOST);
		String logLevel = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_LOGLEVEL);
		String lineNo = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_LINE);
		String logger = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_LOGGER);
		String logMessage = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_MESSAGE);
		String methodName = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_METHOD);
		Long seq = JsonHelper.getAsLong(ev, PROPERTY_LOGEVENT_SEQ);
		String threadName = JsonHelper.getAsString(ev, PROPERTY_LOGEVENT_THREAD);
		Date eventTimestamp = new Date(JsonHelper.getAsLong(ev, PROPERTY_LOGEVENT_TIMESTAMP));
		Map<String, String> propertiesMap = JsonHelper.getAsPropertiesMap(ev, PROPERTY_LOGEVENT_PROPERTIES);
		
		LogEventDTO event = new LogEventDTO();
		event.setClassName(className);
		event.setContainer(container);
		event.setException(exception);
		event.setFileName(fileName);
		event.setEventhost(Strings.isBlank(eventhost) ? parent.getHost() : eventhost);
		event.setLogLevel(logLevel);
		event.setLineNo(lineNo);
		event.setLogger(logger);
		event.setLogMessage(logMessage);
		event.setMethodName(methodName);
		event.setSeq(seq);
		event.setThreadName(threadName);
		event.setEventTimestamp(eventTimestamp);
		event.setPropertiesMap(propertiesMap);

		return event;
	}

	@Override
	public int hashCode() {
		int result = eventhost != null ? eventhost.hashCode() : 0;
		result = 31 * result + (container != null ? container.hashCode() : 0);
		result = 31 * result + (seq != null ? seq.hashCode() : 0);
		result = 31 * result + (eventTimestamp != null ? eventTimestamp.hashCode() : 0);
		result = 31 * result + (logger != null ? logger.hashCode() : 0);
		result = 31 * result + (threadName != null ? threadName.hashCode() : 0);
		result = 31 * result + (logMessage != null ? logMessage.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LogEventDTO logEvent = (LogEventDTO) o;
		if (eventhost != null ? !eventhost.equals(logEvent.eventhost) : logEvent.eventhost != null) return false;
		if (container != null ? !container.equals(logEvent.container) : logEvent.container != null) return false;
		if (logger != null ? !logger.equals(logEvent.logger) : logEvent.logger != null) return false;
		if (logMessage != null ? !logMessage.equals(logEvent.logMessage) : logEvent.logMessage != null) return false;
		if (seq != null ? !seq.equals(logEvent.seq) : logEvent.seq != null) return false;
		if (threadName != null ? !threadName.equals(logEvent.threadName) : logEvent.threadName != null) return false;
		if (eventTimestamp != null ? !eventTimestamp.equals(logEvent.eventTimestamp) : logEvent.eventTimestamp != null) return false;
		return true;
	}

	@Override
	public int compareTo(LogEventDTO that) {
		// use reverse order for timestamp and seq
		int answer = Objects.compare(this.eventTimestamp, that.eventTimestamp);
		if (answer == 0) {
			answer = Objects.compare(this.seq, that.seq);
			if (answer == 0) {
				answer = Objects.compare(this.eventhost, that.eventhost);
				if (answer == 0) {
					answer = Objects.compare(this.container, that.container);
					if (answer == 0) {
						answer = Objects.compare(this.threadName, that.threadName);
						if (answer == 0) {
							answer = Objects.compare(this.logger, that.logger);
							if (answer == 0) {
								answer = Objects.compare(this.logMessage, that.logMessage);
							}
						}
					}
				}
			}
		}
		return answer;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (getLogLevel().equalsIgnoreCase("error")) {
			// output as exception stack trace
			String[] lines = getException();
			if (lines != null && lines.length > 0) {
				buf.append(getLogMessage()).append(NEW_LINE);
				for (int i = 0; i < lines.length; i++) {
					// workaround as the exception output from fabric8 seems to be borked
					String line = lines[i];
					if (line.startsWith("t... ")) {
						buf.append(line.replaceFirst("t... ", "... ")).append(NEW_LINE);
					} else if (line.startsWith("tat ")) {
						buf.append(line.replaceFirst("tat ", "   at ")).append(NEW_LINE);	
					} else {
						buf.append(line).append(NEW_LINE);
					}				
				}
			} else {
				// lets use the location of the
				String className = getClassName();
				String methodName = getMethodName();
				String fileName = getFileName();
				String lineNumber = getLineNo();
				buf.append(getLogMessage()).append(NEW_LINE);
				buf.append('\t');
				buf.append("at " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")");
				buf.append(NEW_LINE);;
			}			
		} else {
			// output as information
			buf.append(String.format("Timestamp: %s\nContainer:%s\n\n[%s] %s   -   %s\n", getEventTimestamp(), getContainer(), getLogLevel(), getLogger(), getLogMessage()));
		}
		return buf.toString();
	}
}
