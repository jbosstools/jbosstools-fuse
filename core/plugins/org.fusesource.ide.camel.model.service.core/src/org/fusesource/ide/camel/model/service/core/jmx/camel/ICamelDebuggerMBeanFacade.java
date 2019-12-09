/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.jmx.camel;

import java.util.Set;

/**
 * this interface defines the methods available from the Camel Debugger MBean
 * and is almost 1:1 copy of methods from <code>org.apache.camel.api.management.mbean.ManagedBacklogDebuggerMBean</code>
 *  
 * @author lhein
 */
public interface ICamelDebuggerMBeanFacade {
	
	/**
	 * returns the camel context id
	 * 
	 * @return
	 */
	String getContextId();
	
	/**
	 * updates the runtime with the new context
	 * 
	 * @param xmlDump
	 */
	void updateContext(String xmlDump);
	
	/**
	 * Logging Level
	 * 
	 * @return
	 */
    String getLoggingLevel();

    /**
     * Logging Level
     * 
     * @param level
     */
    void setLoggingLevel(String level);

    /**
     * Is debugger enabled
     * 
     * @return
     */
    boolean isEnabled();

    /**
     * Enable the debugger
     */
    void enableDebugger();

    /**
     * Disable the debugger
     */
    void disableDebugger();

    /**
     * Add a breakpoint at the given node id
     * 
     * @param nodeId
     */
    void addBreakpoint(String nodeId);

    /**
     * Add a conditional breakpoint at the given node id
     * 
     * @param nodeId
     * @param language
     * @param predicate
     */
    void addConditionalBreakpoint(String nodeId, String language, String predicate);

    /**
     * Remote the breakpoint from the given node id (will resume suspend breakpoint first)
     * 
     * @param nodeId
     */
    void removeBreakpoint(String nodeId);

    /**
     * Remote all breakpoints (will resume all suspend breakpoints first and exists single step mode)
     */
    void removeAllBreakpoints();

    /**
     * Resume running from the suspended breakpoint at the given node id
     * 
     * @param nodeId
     */
    void resumeBreakpoint(String nodeId);

    /**
     * Updates the message body (uses same type as old body) on the suspended breakpoint at the given node id
     * 
     * @param nodeId
     * @param body
     */
    void setMessageBodyOnBreakpoint(String nodeId, Object body);

    /**
     * Updates the message body (with a new type) on the suspended breakpoint at the given node id
     * 
     * @param nodeId
     * @param body
     * @param type
     */
    void setMessageBodyOnBreakpoint(String nodeId, Object body, String type);

    /**
     * Removes the message body on the suspended breakpoint at the given node id
     * 
     * @param nodeId
     */
    void removeMessageBodyOnBreakpoint(String nodeId);

    /**
     * Updates/adds the message header (uses same type as old header value) on the suspended breakpoint at the given node id
     * 
     * @param nodeId
     * @param headerName
     * @param value
     */
    void setMessageHeaderOnBreakpoint(String nodeId, String headerName, Object value);

    /**
     * Removes the message header on the suspended breakpoint at the given node id
     * 
     * @param nodeId
     * @param headerName
     */
    void removeMessageHeaderOnBreakpoint(String nodeId, String headerName);

    /**
     * Updates/adds the message header (with a new type) on the suspended breakpoint at the given node id
     * 
     * @param nodeId
     * @param headerName
     * @param value
     * @param type
     */
    void setMessageHeaderOnBreakpoint(String nodeId, String headerName, Object value, String type);

    /**
     * Resume running any suspended breakpoints, and exits step mode
     */
    void resumeAll();

    /**
     * Starts single step debugging from the suspended breakpoint at the given node id
     * 
     * @param nodeId
     */
    void stepBreakpoint(String nodeId);

    /**
     * Whether currently in step mode
     * 
     * @return
     */
    boolean isSingleStepMode();

    /**
     * Steps to next node in step mode
     */
    void step();

    /**
     * Return the node ids which has breakpoints
     * 
     * @return
     */
    Set<String> getBreakpoints();

    /**
     * Return the node ids which is currently suspended
     * 
     * @return
     * @throws Exception	on connection errors
     */
    Set<String> getSuspendedBreakpointNodeIds() throws Exception;

    /**
     * Disables a breakpoint
     * 
     * @param nodeId
     */
    void disableBreakpoint(String nodeId);

    /**
     * Enables a breakpoint which has been disabled
     * 
     * @param nodeId
     */
    void enableBreakpoint(String nodeId);

    /**
     * Number of maximum chars in the message body in the trace message. Use zero or negative value to have unlimited size
     * 
     * @return
     */
    int getBodyMaxChars();

    /**
     * Number of maximum chars in the message body in the trace message. Use zero or negative value to have unlimited size
     * 
     * @param bodyMaxChars
     */
    void setBodyMaxChars(int bodyMaxChars);

    /**
     * Whether to include stream based message body in the trace message
     * 
     * @return
     */
    boolean isBodyIncludeStreams();

    /**
     * Whether to include stream based message body in the trace message
     * 
     * @param bodyIncludeStreams
     */
    void setBodyIncludeStreams(boolean bodyIncludeStreams);

    /**
     * Whether to include file based message body in the trace message
     * 
     * @return
     */
    boolean isBodyIncludeFiles();

    /**
     * Whether to include file based message body in the trace message
     * 
     * @param bodyIncludeFiles
     */
    void setBodyIncludeFiles(boolean bodyIncludeFiles);

    /**
     * Dumps the messages in xml format from the suspended breakpoint at the given node
     * 
     * @param nodeId	the id of the breakpoint node
     * @return
     */
    String dumpTracedMessagesAsXml(String nodeId);

    /**
     * Number of total debugged messages
     * 
     * @return	the total number of debugged messages
     */
    long getDebugCounter();

    /**
     * Resets the debug counter
     */
    void resetDebugCounter();
    
    /**
     * returns the xml dump of the given camel context
     * 
     * @return	the xml dump of the contained routes
     */
    String getContextXmlDump();

    /**
     * returns the route id for the given processor
     * 
     * @param processorId	the processor id
     * @return	the route id
     */
	String getRouteId(String processorId);
	
	/**
     * returns the camel id for the given processor
     * 
     * @param processorId	the processor id
     * @return	the camel id
     */
	String getCamelId(String processorId);
	
	/**
     * returns the amount of completed exchanges for the given processor
     * 
     * @param processorId	the processor id
     * @return	the amount of completed exchanges
     */
	long getCompletedExchanges(String processorId);
	
	/**
     * returns the amount of failed exchanges for the given processor
     * 
     * @param processorId	the processor id
     * @return	the amount of failed exchanges
     */
	long getFailedExchanges(String processorId);
	
	/**
     * returns the amount of exchanges for the given processor
     * 
     * @param processorId	the processor id
     * @return	the amount of exchanges
     */
	long getTotalExchanges(String processorId);
	
	/**
     * returns the amount of redelivered exchanges for the given processor
     * 
     * @param processorId	the processor id
     * @return	the amount of redelivered exchanges
     */
	long getRedeliveries(String processorId);
	
	/**
     * returns the amount of external redeliveries for the given processor
     * 
     * @param processorId	the processor id
     * @return	the amount of external redelivered exchanges
     */
	long getExternalRedeliveries(String processorId);

	/**
     * returns the amount of handled failures for the given processor
     * 
     * @param processorId	the processor id
     * @return	the amount of handled failures
     */
	long getHandledFailures(String processorId);
	
	/**
     * returns the last processing time for the given processor
     * 
     * @param processorId	the processor id
     * @return	the last processing time
     */
	long getLastProcessingTime(String processorId);
	
	/**
     * returns the min processing time for the given processor
     * 
     * @param processorId	the processor id
     * @return	the min processing time
     */
	long getMinProcessingTime(String processorId);
	
	/**
     * returns the max processing time for the given processor
     * 
     * @param processorId	the processor id
     * @return	the max processing time
     */
	long getMaxProcessingTime(String processorId);
	
	/**
     * returns the average processing time for the given processor
     * 
     * @param processorId	the processor id
     * @return	the average processing time
     */
	long getAverageProcessingTime(String processorId);
	
	/**
     * returns the total processing time for the given processor
     * 
     * @param processorId	the processor id
     * @return	the total processing time
     */
	long getTotalProcessingTime(String processorId);
}
