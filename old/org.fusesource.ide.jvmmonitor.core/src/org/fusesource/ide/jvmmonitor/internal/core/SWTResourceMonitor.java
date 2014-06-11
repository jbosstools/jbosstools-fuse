/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.jvmmonitor.core.ISWTResourceElement;
import org.fusesource.ide.jvmmonitor.core.ISWTResourceMonitor;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.JvmModel;

/**
 * The SWT resource monitor.
 */
public class SWTResourceMonitor implements ISWTResourceMonitor {

    /** The SWT resource monitor MXBean name. */
    final static String SWT_RESOURCE_MONITOR_MXBEAN_NAME = "org.fusesource.ide.jvmmonitor:type=SWT Resource Monitor"; //$NON-NLS-1$

    /** The Tracking attribute in SWTResourceMonitorMXBean. */
    private static final String TRACKING = "Tracking"; //$NON-NLS-1$

    /** The Resources attribute in SWTResourceMonitorMXBean. */
    private static final String RESOURCES = "Resources"; //$NON-NLS-1$

    /** The Clear operation in SWTResourceMonitorMXBean. */
    private static final String CLEAR = "clear"; //$NON-NLS-1$

    /** The name attribute in resource composite data. */
    private static final String NAME = "name"; //$NON-NLS-1$

    /** The stack trace attribute in resource composite data. */
    private static final String STACK_TRACE = "stackTrace"; //$NON-NLS-1$

    /** The class name attribute in resource composite data. */
    private static final String CLASS_NAME = "className"; //$NON-NLS-1$

    /** The file name attribute in resource composite data. */
    private static final String FILE_NAME = "fileName"; //$NON-NLS-1$

    /** The line number attribute in resource composite data. */
    private static final String LINE_NUMBER = "lineNumber"; //$NON-NLS-1$

    /** The method name attribute in resource composite data. */
    private static final String METHOD_NAME = "methodName"; //$NON-NLS-1$

    /** The native method attribute in resource composite data. */
    private static final String NATIVE_METHOD = "nativeMethod"; //$NON-NLS-1$

    /** The JVM. */
    private ActiveJvm jvm;

    /** The SWT resource elements. */
    private Map<String, ISWTResourceElement> resourceElements;

    /** The SWT resources cache. */
    private List<ISWTResourceElement> resources;

    /**
     * The constructor.
     * 
     * @param jvm
     *            The JVM
     */
    public SWTResourceMonitor(ActiveJvm jvm) {
        this.jvm = jvm;
        resourceElements = new HashMap<String, ISWTResourceElement>();
        resources = new ArrayList<ISWTResourceElement>();
    }

    /*
     * @see ISWTResourceMonitor#setTracking(boolean)
     */
    @Override
    public void setTracking(boolean tracking) throws JvmCoreException {
        ObjectName objectName = validateAgent();
        if (objectName != null) {
            jvm.getMBeanServer().setAttribute(objectName,
                    new Attribute(TRACKING, tracking));
        }
    }

    /*
     * @see ISWTResourceMonitor#isTracking()
     */
    @Override
    public boolean isTracking() throws JvmCoreException {
        ObjectName objectName = validateAgent();
        if (objectName != null) {
            Object attribute = jvm.getMBeanServer().getAttribute(objectName,
                    TRACKING);
            if (attribute instanceof Boolean) {
                return ((Boolean) attribute).booleanValue();
            }
        }
        return false;
    }

    /*
     * @see ISWTResourceMonitor#refreshResourcesCache()
     */
    @Override
    public void refreshResourcesCache() throws JvmCoreException {
        resources.clear();
        ObjectName objectName = validateAgent();
        if (objectName != null) {
            Object attribute = jvm.getMBeanServer().getAttribute(objectName,
                    RESOURCES);
            if (attribute instanceof CompositeData[]) {
                resources = new ArrayList<ISWTResourceElement>(
                        getSWTResourceElements((CompositeData[]) attribute));
            }
        }
    }

    /*
     * @see ISWTResourceMonitor#getResources()
     */
    @Override
    public ISWTResourceElement[] getResources() {
        return resources.toArray(new ISWTResourceElement[resources.size()]);
    }

    /*
     * @see ISWTResourceMonitor#clear()
     */
    @Override
    public void clear() throws JvmCoreException {
        ObjectName objectName = validateAgent();
        if (objectName != null) {
            jvm.getMBeanServer().invoke(objectName, CLEAR, new Object[0],
                    new String[0]);
        }
    }

    /*
     * @see ISWTResourceMonitor#isSupported()
     */
    @Override
    public boolean isSupported() {
        try {
            ObjectName objectName = validateAgent();
            if (objectName == null) {
                return false;
            }
            Object attribute = jvm.getMBeanServer().getAttribute(objectName,
                    TRACKING);
            return attribute != null;
        } catch (JvmCoreException e) {
            return false;
        }
    }

    /**
     * Validates the agent.
     * 
     * @return The object name for SWT resource monitor MXBean
     * @throws JvmCoreException
     */
    private ObjectName validateAgent() throws JvmCoreException {
        if (!jvm.isRemote()
                && !JvmModel.getInstance().getAgentLoadHandler()
                        .isAgentLoaded()) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.agentNotLoadedMsg, new Exception());
        }

        return jvm.getMBeanServer().getObjectName(
                SWT_RESOURCE_MONITOR_MXBEAN_NAME);
    }

    /**
     * Gets the SWT resource elements.
     * 
     * @param resourceComposites
     *            The resources in composite data array
     * @return The SWT resource elements
     */
    private Collection<ISWTResourceElement> getSWTResourceElements(
            CompositeData[] resourceComposites) {
        Map<String, ISWTResourceElement> newResourceElements = new HashMap<String, ISWTResourceElement>();
        for (CompositeData compositeData : resourceComposites) {
            Object name = compositeData.get(NAME);
            if (!(name instanceof String)) {
                continue;
            }

            ISWTResourceElement element = resourceElements.get(name);
            if (element == null) {
                Object stackTraceElements = compositeData.get(STACK_TRACE);
                if (!(stackTraceElements instanceof CompositeData[])) {
                    continue;
                }
                element = new SWTResourceElement((String) name,
                        getStackTrace((CompositeData[]) stackTraceElements));
            }

            newResourceElements.put((String) name, element);
        }
        resourceElements = newResourceElements;
        return resourceElements.values();
    }

    /**
     * Gets the stack trace elements.
     * 
     * @param stackTrace
     *            The stack trace in composite data array
     * @return The stack trace elements
     */
    private StackTraceElement[] getStackTrace(CompositeData[] stackTrace) {
        List<StackTraceElement> list = new ArrayList<StackTraceElement>();
        for (CompositeData compositeData : stackTrace) {
            Object className = compositeData.get(CLASS_NAME);
            Object fileName = compositeData.get(FILE_NAME);
            Object lineNumber = compositeData.get(LINE_NUMBER);
            Object metohdName = compositeData.get(METHOD_NAME);
            Object nativeMethod = compositeData.get(NATIVE_METHOD);

            if ((className instanceof String) && (className instanceof String)
                    && (fileName instanceof String)
                    && (lineNumber instanceof Integer)
                    && (metohdName instanceof String)
                    && (nativeMethod instanceof Boolean)) {
                list.add(new StackTraceElement((String) className,
                        (String) metohdName, (String) fileName,
                        (Boolean) nativeMethod ? -2 : (Integer) lineNumber));
            }
        }
        return list.toArray(new StackTraceElement[list.size()]);
    }
}
