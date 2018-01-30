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
package org.fusesource.ide.launcher.debug.model;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.fusesource.ide.camel.model.service.core.jmx.camel.ICamelDebuggerMBeanFacade;
import org.fusesource.ide.launcher.Activator;

/**
 * this is the mapping between eclipse debugger and the camel debugger mbean
 * 
 * @author lhein
 */
public class CamelDebugFacade implements ICamelDebuggerMBeanFacade {

	private static final boolean DEVELOPER_MODE = false;
	
	private static final String CAMEL_PROCESSOR_MBEAN = "org.apache.camel:type=processors,name=\"%s\",*";
	public static final String CAMEL_DEBUGGER_MBEAN_DEFAULT = "org.apache.camel:type=tracer,name=BacklogDebugger,*";
	private static final String CAMEL_CONTEXT_MBEAN = "org.apache.camel:type=context,name=\"%s\",*";
	
	
	private static final long TIMEOUT_MBEAN_REGISTRATION = 30 * 1000L; // 30 secs
	
	private ObjectName objectNameDebugger = null;
	private ObjectName objectNameContext = null;
	
	private CamelDebugTarget debugTarget;
	private String contextId;
	
	/**
	 * our jmx connection
	 */
	private MBeanServerConnection mbsc;
	
	/**
	 * creates a debugger facade via JMX
	 * 
	 * @param debugTarget
	 * @param mbsc
	 * @param contextId
	 * @param contextType
	 * @throws IOException 
	 * @throws MalformedObjectNameException 
	 * @throws Exception
	 */
	public CamelDebugFacade(CamelDebugTarget debugTarget, MBeanServerConnection mbsc, String contextId) throws MalformedObjectNameException, IOException {
		this.mbsc = mbsc;
		this.debugTarget = debugTarget;
		this.contextId = contextId;
		long startTime = System.currentTimeMillis();
		while (this.objectNameDebugger == null && System.currentTimeMillis() - startTime <= TIMEOUT_MBEAN_REGISTRATION) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Activator.getLogger().error(e);
				Thread.currentThread().interrupt();
			}
			initializeDebuggerMBean();
		}
	}
	
	/**
	 * initialize the mbean
	 * @throws IOException 
	 * @throws MalformedObjectNameException 
	 * 
	 * @throws Exception
	 */
	private void initializeDebuggerMBean() throws MalformedObjectNameException, IOException {
		Set<ObjectInstance> mbeans = mbsc.queryMBeans(new ObjectName(CAMEL_DEBUGGER_MBEAN_DEFAULT), null);
    	if (mbeans.size() == 1) {
	    	// remember the mbean
	    	Object oMbean = mbeans.iterator().next();
	    	if (oMbean instanceof ObjectInstance) {
	    		ObjectInstance oi = (ObjectInstance)oMbean;
	    		this.objectNameDebugger = oi.getObjectName();
	    	}
	    }
	}
	
	/**
	 * initialize the mbean
	 * 
	 * @throws Exception
	 */
	private void initializeContextMBean() throws MalformedObjectNameException, IOException {
    	Set<ObjectInstance> mbeans = mbsc.queryMBeans(new ObjectName(String.format(CAMEL_CONTEXT_MBEAN, this.contextId)), null);
    	if (mbeans.size() == 1) {
	    	// remember the mbean
	    	Object oMbean = mbeans.iterator().next();
	    	if (oMbean instanceof ObjectInstance) {
	    		ObjectInstance oi = (ObjectInstance)oMbean;
	    		this.objectNameContext = oi.getObjectName();
	    	}
	    }
	}
	
	/**
	 * initialize the mbean
	 * 
	 * @throws Exception
	 */
	private ObjectName initializeProcessorMBean(String processorId) throws MalformedObjectNameException, IOException {
    	Set<ObjectInstance> mbeans = mbsc.queryMBeans(new ObjectName(String.format(CAMEL_PROCESSOR_MBEAN, processorId)), null);
    	if (mbeans.size() == 1) {
	    	// remember the mbean
	    	Object oMbean = mbeans.iterator().next();
	    	if (oMbean instanceof ObjectInstance) {
	    		ObjectInstance oi = (ObjectInstance)oMbean;
	    		return oi.getObjectName();
	    	}
	    }
    	return null;
	}
	
	@Override
	public String getContextId() {
		return this.contextId;
	}
	
	@Override
	public void updateContext(String xmlDump) {
		log("updateContext(" + xmlDump + ")");
		
		try {
			// first we need to leave the debug mode otherwise the update will block forever
			// disable the debugger
			disableDebugger();
			// resume all breakpoints
			resumeAll();
			// resume all threads
			this.debugTarget.resumeAllThreads();
			// then invoke the update
			mbsc.invoke(this.objectNameContext, "addOrUpdateRoutesFromXml", new Object[] { xmlDump } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		} finally {
//			// update the editor input
//			this.debugTarget.updateEditorInput();
			// finally enable the debugger again
			enableDebugger();
			// and install the breakpoints again
			this.debugTarget.started(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getLoggingLevel()
	 */
	@Override
	public String getLoggingLevel() {
		log("getLoggingLevel()");
		try {
			String logLevel = (String) mbsc.invoke(this.objectNameDebugger, "getLoggingLevel", new Object[] { } , new String[] { }); 
			return logLevel;
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#setLoggingLevel(java.lang.String)
	 */
	@Override
	public void setLoggingLevel(String level) {
		log("setLoggingLevel(" + level + ")");	
		try {
			mbsc.invoke(this.objectNameDebugger, "setLoggingLevel", new Object[] { level } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		log("isEnabled()");
		try {
			boolean b = (boolean) mbsc.invoke(this.objectNameDebugger, "isEnabled", new Object[] { } , new String[] { }); 
			return b;
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#enableDebugger()
	 */
	@Override
	public void enableDebugger() {
		log("enableDebugger()");
		try {
			mbsc.invoke(this.objectNameDebugger, "enableDebugger", new Object[] { } , new String[] { }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#disableDebugger()
	 */
	@Override
	public void disableDebugger() {
		log("disableDebugger()");
		try {
			mbsc.invoke(this.objectNameDebugger, "disableDebugger", new Object[] { } , new String[] { }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#addBreakpoint(java.lang.String)
	 */
	@Override
	public void addBreakpoint(String nodeId) {
		log("addBreakpoint(" + nodeId + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "addBreakpoint", new Object[] { nodeId } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#addConditionalBreakpoint(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addConditionalBreakpoint(String nodeId, String language,
			String predicate) {
		log("addConditionalBreakpoint(" + nodeId + ", " + language + ", " + predicate + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "addConditionalBreakpoint", new Object[] { nodeId, language, predicate } , new String[] { String.class.getName(), String.class.getName(), String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#removeBreakpoint(java.lang.String)
	 */
	@Override
	public void removeBreakpoint(String nodeId) {
		log("removeBreakpoint(" + nodeId + ")");		
		try {
			mbsc.invoke(this.objectNameDebugger, "removeBreakpoint", new Object[] { nodeId } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#removeAllBreakpoints()
	 */
	@Override
	public void removeAllBreakpoints() {
		log("removeAllBreakpoints()");
		try {
			mbsc.invoke(this.objectNameDebugger, "removeAllBreakpoints", new Object[] { } , new String[] { }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#resumeBreakpoint(java.lang.String)
	 */
	@Override
	public void resumeBreakpoint(String nodeId) {
		log("resumeBreakpoint(" + nodeId + ")");	
		try {
			mbsc.invoke(this.objectNameDebugger, "resumeBreakpoint", new Object[] { nodeId } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#setMessageBodyOnBreakpoint(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setMessageBodyOnBreakpoint(String nodeId, Object body) {
		log("setMessageBodyOnBreakpoint(" + nodeId + ", " + body + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "setMessageBodyOnBreakpoint", new Object[] { nodeId, body } , new String[] { String.class.getName(), Object.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#setMessageBodyOnBreakpoint(java.lang.String, java.lang.Object, java.lang.String)
	 */
	@Override
	public void setMessageBodyOnBreakpoint(String nodeId, Object body,
			String type) {
		log("setMessageBodyOnBreakpoint(" + nodeId + ", " + body + ", " + type + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "setMessageBodyOnBreakpoint", new Object[] { nodeId, body, type } , new String[] { String.class.getName(), Object.class.getName(), String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#removeMessageBodyOnBreakpoint(java.lang.String)
	 */
	@Override
	public void removeMessageBodyOnBreakpoint(String nodeId) {
		log("removeMessageBodyOnBreakpoint(" + nodeId + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "removeMessageBodyOnBreakpoint", new Object[] { nodeId } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#setMessageHeaderOnBreakpoint(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setMessageHeaderOnBreakpoint(String nodeId, String headerName,
			Object value) {
		log("setMessageHeaderOnBreakpoint(" + nodeId + ", " + headerName + ", " + value + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "setMessageHeaderOnBreakpoint", new Object[] { nodeId, headerName, value } , new String[] { String.class.getName(), String.class.getName(), Object.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#removeMessageHeaderOnBreakpoint(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeMessageHeaderOnBreakpoint(String nodeId, String headerName) {
		log("removeMessageHeaderOnBreakpoint(" + nodeId + ", " + headerName + ")");	
		try {
			mbsc.invoke(this.objectNameDebugger, "removeMessageHeaderOnBreakpoint", new Object[] { nodeId, headerName } , new String[] { String.class.getName(), String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#setMessageHeaderOnBreakpoint(java.lang.String, java.lang.String, java.lang.Object, java.lang.String)
	 */
	@Override
	public void setMessageHeaderOnBreakpoint(String nodeId, String headerName,
			Object value, String type) {
		log("setMessageHeaderOnBreakpoint(" + nodeId +", " + headerName + ", " + value + ", " + type + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "setMessageHeaderOnBreakpoint", new Object[] { nodeId, headerName, value, type } , new String[] { String.class.getName(), String.class.getName(), Object.class.getName(), String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#resumeAll()
	 */
	@Override
	public void resumeAll() {
		log("resumeAll()");
		try {
			mbsc.invoke(this.objectNameDebugger, "resumeAll", new Object[] { } , new String[] { }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#stepBreakpoint(java.lang.String)
	 */
	@Override
	public void stepBreakpoint(String nodeId) {
		log("stepBreakpoint(" + nodeId + ")");	
		try {
			mbsc.invoke(this.objectNameDebugger, "stepBreakpoint", new Object[] { nodeId } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#isSingleStepMode()
	 */
	@Override
	public boolean isSingleStepMode() {
		log("isSingleStepMode()");
		try {
			return (boolean) mbsc.invoke(this.objectNameDebugger, "isSingleStepMode", new Object[] { } , new String[] { });
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#step()
	 */
	@Override
	public void step() {
		log("step()");
		try {
			mbsc.invoke(this.objectNameDebugger, "step", new Object[] { } , new String[] { }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getBreakpoints()
	 */
	@Override
	public Set<String> getBreakpoints() {
		log("getBreakpoints()");
		try {
			Collection<String> breakpoints = (Collection<String>) mbsc.invoke(this.objectNameDebugger, "getBreakpoints", new Object[] { } , new String[] { });
			return new HashSet<>(breakpoints); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return new HashSet<>();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getSuspendedBreakpointNodeIds()
	 */
	@Override
	public Set<String> getSuspendedBreakpointNodeIds() throws Exception {
		log("getSuspendedBreakpointsNodeIds()");
		Collection<String> suspendedBreakpointNodeIds = (Collection<String>) mbsc.invoke(this.objectNameDebugger, "getSuspendedBreakpointNodeIds", new Object[] { } , new String[] { });
		return new HashSet<>(suspendedBreakpointNodeIds); 
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#disableBreakpoint(java.lang.String)
	 */
	@Override
	public void disableBreakpoint(String nodeId) {
		log("disableBreakpoint(" + nodeId + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "disableBreakpoint", new Object[] { nodeId } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#enableBreakpoint(java.lang.String)
	 */
	@Override
	public void enableBreakpoint(String nodeId) {
		log("enableBreakpoint(" + nodeId + ")");		
		try {
			mbsc.invoke(this.objectNameDebugger, "enableBreakpoint", new Object[] { nodeId } , new String[] { String.class.getName() }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getBodyMaxChars()
	 */
	@Override
	public int getBodyMaxChars() {
		log("getBodyMaxChars()");
		try {
			return (int) mbsc.invoke(this.objectNameDebugger, "getBodyMaxChars", new Object[] { } , new String[] { });
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#setBodyMaxChars(int)
	 */
	@Override
	public void setBodyMaxChars(int bodyMaxChars) {
		log("setBodyMaxChars(" + bodyMaxChars + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "setBodyMaxChars", new Object[] { bodyMaxChars } , new String[] { "int" }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#isBodyIncludeStreams()
	 */
	@Override
	public boolean isBodyIncludeStreams() {
		log("isBodyIncludeStreams()");
		try {
			return (boolean) mbsc.invoke(this.objectNameDebugger, "isBodyIncludeStreams", new Object[] { } , new String[] { });
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#setBodyIncludeStreams(boolean)
	 */
	@Override
	public void setBodyIncludeStreams(boolean bodyIncludeStreams) {
		log("setBodyIncludeStreams(" + bodyIncludeStreams + ")");	
		try {
			mbsc.invoke(this.objectNameDebugger, "setBodyIncludeStreams", new Object[] { bodyIncludeStreams } , new String[] { "boolean" }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#isBodyIncludeFiles()
	 */
	@Override
	public boolean isBodyIncludeFiles() {
		log("isBodyIncludeFiles()");
		try {
			return (boolean) mbsc.invoke(this.objectNameDebugger, "isBodyIncludeFiles", new Object[] { } , new String[] { });
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#setBodyIncludeFiles(boolean)
	 */
	@Override
	public void setBodyIncludeFiles(boolean bodyIncludeFiles) {
		log("setBodyIncludeFiles(" + bodyIncludeFiles + ")");
		try {
			mbsc.invoke(this.objectNameDebugger, "setBodyIncludeFiles", new Object[] { bodyIncludeFiles } , new String[] { "boolean" }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#dumpTracedMessagesAsXml(java.lang.String)
	 */
	@Override
	public String dumpTracedMessagesAsXml(String nodeId) {
		log("dumpTracedMessagesAsXml()");
		try {
			String dump = (String) mbsc.invoke(this.objectNameDebugger, "dumpTracedMessagesAsXml", new Object[] { nodeId } , new String[] { String.class.getName() });
			log(">>>>>>>>>>>>> DUMP:\n" + dump);
			return dump;
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getDebugCounter()
	 */
	@Override
	public long getDebugCounter() {
		log("getDebugCounter()");
		try {
			return (long) mbsc.invoke(this.objectNameDebugger, "getDebugCounter", new Object[] { } , new String[] { });
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#resetDebugCounter()
	 */
	@Override
	public void resetDebugCounter() {
		log("resetDebugCounter()");		
		try {
			mbsc.invoke(this.objectNameDebugger, "resetDebugCounter", new Object[] { } , new String[] { }); 
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getContextXmlDump()
	 */
	@Override
	public String getContextXmlDump() {
		log("getContextXmlDump(" + this.contextId + ")");
		if (this.objectNameContext == null) {
			try {
				initializeContextMBean();
			} catch (Exception ex) {
				Activator.getLogger().error(ex);
				return null;
			}
		}
		try {
			String xmlDump = (String) mbsc.invoke(this.objectNameContext, "dumpRoutesAsXml", new Object[] { } , new String[] { });
			return xmlDump.replaceFirst("<routes ", "<routes id=\"" + contextId + "\" ");
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getRouteId(java.lang.String)
	 */
	@Override
	public String getRouteId(String processorId) {
		log("getRouteId(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (String) mbsc.getAttribute(objName, "RouteId");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getCamelId(java.lang.String)
	 */
	@Override
	public String getCamelId(String processorId) {
		log("getCamelId(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (String) mbsc.getAttribute(objName, "CamelId");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getCompletedExchanges(java.lang.String)
	 */
	@Override
	public long getCompletedExchanges(String processorId) {
		log("getCompletedExchanges(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "ExchangesCompleted");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getFailedExchanges(java.lang.String)
	 */
	@Override
	public long getFailedExchanges(String processorId) {
		log("getFailedExchanges(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "ExchangesFailed");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getTotalExchanges(java.lang.String)
	 */
	@Override
	public long getTotalExchanges(String processorId) {
		log("getTotalExchanges(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "ExchangesTotal");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getRedeliveries(java.lang.String)
	 */
	@Override
	public long getRedeliveries(String processorId) {
		log("getRedeliveries(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "Redeliveries");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getExternalRedeliveries(java.lang.String)
	 */
	@Override
	public long getExternalRedeliveries(String processorId) {
		log("getExternalRedeliveries(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "ExternalRedeliveries");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getHandledFailures(java.lang.String)
	 */
	@Override
	public long getHandledFailures(String processorId) {
		log("getHandledFailures(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "FailuresHandled");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getLastProcessingTime(java.lang.String)
	 */
	@Override
	public long getLastProcessingTime(String processorId) {
		log("getLastProcessingTime(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "LastProcessingTime");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getMinProcessingTime(java.lang.String)
	 */
	@Override
	public long getMinProcessingTime(String processorId) {
		log("getMinProcessingTime(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "MinProcessingTime");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getMaxProcessingTime(java.lang.String)
	 */
	@Override
	public long getMaxProcessingTime(String processorId) {
		log("getMaxProcessingTime(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "MaxProcessingTime");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getAverageProcessingTime(java.lang.String)
	 */
	@Override
	public long getAverageProcessingTime(String processorId) {
		log("getAverageProcessingTime(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "MeanProcessingTime");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.ICamelDebuggerMBeanFacade#getTotalProcessingTime(java.lang.String)
	 */
	@Override
	public long getTotalProcessingTime(String processorId) {
		log("getTotalProcessingTime(" + processorId + ")");
		try {
			ObjectName objName = initializeProcessorMBean(processorId);
			if (objName != null){
				return (long) mbsc.getAttribute(objName, "TotalProcessingTime");
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}
	
	private static void log(String logString) {
		if (DEVELOPER_MODE){
			System.err.println(logString);
		}
	}
}
