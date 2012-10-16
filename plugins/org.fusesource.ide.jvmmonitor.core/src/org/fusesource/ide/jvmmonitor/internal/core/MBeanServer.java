/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Signature;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.IHeapDumpHandler;
import org.fusesource.ide.jvmmonitor.core.IHeapElement;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.IThreadElement;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent;
import org.fusesource.ide.jvmmonitor.core.ISnapshot.SnapshotType;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent.State;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerState;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerType;
import org.fusesource.ide.jvmmonitor.core.mbean.IMBeanNotification;
import org.fusesource.ide.jvmmonitor.core.mbean.IMBeanServer;
import org.fusesource.ide.jvmmonitor.core.mbean.IMBeanServerChangeListener;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanAttribute;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup;
import org.fusesource.ide.jvmmonitor.core.mbean.MBeanServerEvent;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup.AxisUnit;
import org.fusesource.ide.jvmmonitor.core.mbean.MBeanServerEvent.MBeanServerState;
import org.fusesource.ide.jvmmonitor.internal.core.cpu.CallTreeNode;
import org.fusesource.ide.jvmmonitor.internal.core.cpu.CpuModel;
import org.fusesource.ide.jvmmonitor.internal.core.cpu.MethodNode;
import org.fusesource.ide.jvmmonitor.internal.core.cpu.ThreadNode;

/**
 * The MBean server. MBeanServerConnection is hidden for clients, since its
 * method invocation doesn't return until timeout occurs when target JVM is
 * disconnected.
 */
public class MBeanServer implements IMBeanServer {

    /** The data transfer MXBean name. */
    private final static String DATA_TRANSFER_MXBEAN_NAME = "org.fusesource.ide.jvmmonitor:type=Data Transfer"; //$NON-NLS-1$

    /** The MBean server connection. */
    private MBeanServerConnection connection;

    /** The JMX connector */
    private JMXConnector connector;

    /** The JVM. */
    private ActiveJvm jvm;

    /** The MBean notification. */
    private IMBeanNotification mBeanNotification;

    /** The monitored MXBean attribute groups. */
    private List<IMonitoredMXBeanGroup> monitoredAttributeGroups;

    /** The previous process CPU time. */
    private long previousProcessCpuTime;

    /** The MXBeans. */
    @SuppressWarnings("rawtypes")
    private Map<Class, Object> mxBeans;

    /** The heap list elements. */
    private Map<String, HeapElement> heapListElements;

    /** The thread list elements. */
    private Map<String, ThreadElement> threadListElements;

    /** The timer to update. */
    Timer timer;

    /** The timer to sample profile data. */
    Timer samplingTimer;

    /** The sampling period. */
    private Integer samplingPeriod;

    /** The previous sampling time. */
    private long previousSamplingTime;

    /** The previous thread process CPU time. */
    private Map<Long, Long> previousThreadProcessCpuTime;

    /** The state indicating if handling only live objects. */
    private boolean isLive;

    /** The state indicating if the jvm is reachable. */
    private boolean isJvmReachable;

    /** The MBean server change listeners. */
    private List<IMBeanServerChangeListener> listeners;

    /** The previous stack trace. */
    private Map<String, StackTraceElement[]> previousStackTraces;

    /** The JMX server URL. */
    private JMXServiceURL jmxUrl;

    /**
     * The constructor.
     * 
     * @param jmxUrl
     *            The JVM URL
     * @param jvm
     *            The JVM
     */
    @SuppressWarnings("rawtypes")
    protected MBeanServer(JMXServiceURL jmxUrl, ActiveJvm jvm) {
        this.jmxUrl = jmxUrl;
        this.jvm = jvm;
        mxBeans = new HashMap<Class, Object>();
        mBeanNotification = new MBeanNotification(jvm);
        previousThreadProcessCpuTime = new HashMap<Long, Long>();
        heapListElements = new LinkedHashMap<String, HeapElement>();
        threadListElements = new LinkedHashMap<String, ThreadElement>();
        isLive = true;
        isJvmReachable = false;
        listeners = new CopyOnWriteArrayList<IMBeanServerChangeListener>();
        previousSamplingTime = 0;
        samplingPeriod = 50;
        previousStackTraces = new HashMap<String, StackTraceElement[]>();
        monitoredAttributeGroups = new CopyOnWriteArrayList<IMonitoredMXBeanGroup>();
    }

    /*
     * @see IMBeanServer#queryNames(ObjectName)
     */
    @Override
    public Set<ObjectName> queryNames(ObjectName objectName)
            throws JvmCoreException {
        if (!checkReachability()) {
            return new HashSet<ObjectName>();
        }

        try {
            return connection.queryNames(objectName, null);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.queryObjectNameFailedMsg, e);
        }
    }

    /*
     * @see IMBeanServer#getAttribute(ObjectName, String)
     */
    @Override
    public Object getAttribute(ObjectName objectName,
            String qualifiedAttributeName) throws JvmCoreException {
        Assert.isNotNull(objectName);
        Assert.isNotNull(qualifiedAttributeName);

        if (!checkReachability()) {
            return null;
        }

        String attributeName = qualifiedAttributeName;
        if (attributeName.contains(".")) { //$NON-NLS-1$
            attributeName = attributeName.split("\\.")[0]; //$NON-NLS-1$
        }

        try {
            return connection.getAttribute(objectName, attributeName);
        } catch (JMException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getAttributeFailedMsg, attributeName), e);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getAttributeFailedMsg, attributeName), e);
        }
    }

    /*
     * @see IMBeanServer#setAttribute(ObjectName, Attribute)
     */
    @Override
    public void setAttribute(ObjectName objectName, Attribute attribute)
            throws JvmCoreException {
        Assert.isNotNull(objectName);
        Assert.isNotNull(attribute);

        if (!checkReachability()) {
            return;
        }

        try {
            connection.setAttribute(objectName, attribute);
        } catch (JMException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.setAttributeFailedMsg, attribute.getName()), e);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.setAttributeFailedMsg, attribute.getName()), e);
        }
    }

    /*
     * @see IMBeanServer#getMonitoredAttributeGroups()
     */
    @Override
    public List<IMonitoredMXBeanGroup> getMonitoredAttributeGroups() {
        return monitoredAttributeGroups;
    }

    /*
     * @see IMBeanServer#addMonitoredAttributeGroup(String,
     * IMonitoredMXBeanGroup.AxisUnit)
     */
    @Override
    public IMonitoredMXBeanGroup addMonitoredAttributeGroup(String name,
            AxisUnit axisUnit) {
        Assert.isNotNull(name);
        Assert.isNotNull(axisUnit);

        for (IMonitoredMXBeanGroup group : monitoredAttributeGroups) {
            if (group.getName().equals(name)) {
                group.setAxisUnit(axisUnit);
                group.clearAttributes();
                return group;
            }
        }

        IMonitoredMXBeanGroup group = new MonitoredMXBeanGroup(this, name,
                axisUnit);
        monitoredAttributeGroups.add(group);
        fireMBeanServerChangeEvent(new MBeanServerEvent(
                MBeanServerState.MonitoredAttributeGroupAdded, group));
        return group;
    }

    /*
     * @see IMBeanServer#removeMonitoredAttributeGroup(String)
     */
    @Override
    public void removeMonitoredAttributeGroup(String name) {
        Assert.isNotNull(name);

        IMonitoredMXBeanGroup targetGroup = null;
        for (IMonitoredMXBeanGroup group : monitoredAttributeGroups) {
            if (group.getName().equals(name)) {
                targetGroup = group;
                break;
            }
        }

        if (targetGroup != null) {
            monitoredAttributeGroups.remove(targetGroup);
            fireMBeanServerChangeEvent(new MBeanServerEvent(
                    MBeanServerState.MonitoredAttributeGroupRemoved,
                    targetGroup));
        }
    }

    /*
     * @see IMBeanServer#getMBeanInfo(ObjectName)
     */
    @Override
    public MBeanInfo getMBeanInfo(ObjectName objectName)
            throws JvmCoreException {
        Assert.isNotNull(objectName);

        if (!checkReachability()) {
            return null;
        }

        try {
            return connection.getMBeanInfo(objectName);
        } catch (JMException e) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.getMBeanInfoFailedMsg, e);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.getMBeanInfoFailedMsg, e);
        }
    }

    /*
     * @see IMBeanServer#getMBeanNotification()
     */
    @Override
    public IMBeanNotification getMBeanNotification() {
        return mBeanNotification;
    }

    /*
     * @see IMBeanServer#runGarbageCollector()
     */
    @Override
    public void runGarbageCollector() throws JvmCoreException {
        if (!checkReachability()) {
            return;
        }

        MemoryMXBean memoryMXBean;
        try {
            memoryMXBean = (MemoryMXBean) getMXBean(MemoryMXBean.class,
                    ManagementFactory.MEMORY_MXBEAN_NAME);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getMBeanFailedMsg,
                    ManagementFactory.MEMORY_MXBEAN_NAME), e);
        }
        if (memoryMXBean != null) {
            memoryMXBean.gc();
        }
    }

    /*
     * @see IMBeanServer#invoke(ObjectName, String, String[], String[])
     */
    @Override
    public Object invoke(ObjectName objectName, String method, Object[] params,
            String[] signatures) throws JvmCoreException {
        Assert.isNotNull(objectName);
        Assert.isNotNull(method);

        if (!checkReachability()) {
            return null;
        }

        try {
            return connection.invoke(objectName, method, params, signatures);
        } catch (JMException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.mBeanOperationFailedMsg, method), e);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.mBeanOperationFailedMsg, method), e);
        }
    }

    /*
     * @see IMBeanServer#getThreadNames()
     */
    @Override
    public String[] getThreadNames() throws JvmCoreException {
        IThreadElement[] threads = getThreadCache();

        List<String> threadNames = new ArrayList<String>();
        for (IThreadElement thread : threads) {
            threadNames.add(thread.getThreadName());
        }
        return threadNames.toArray(new String[0]);
    }

    @Override
	public JMXServiceURL getJmxUrl() {
		return jmxUrl;
	}

	@Override
	public MBeanServerConnection getConnection() {
		return connection;
	}

	@Override
	public JMXConnector getConnector() {
		return connector;
	}

	/*
     * @see IMBeanServer#getThreadCache()
     */
    @Override
    public IThreadElement[] getThreadCache() {
        Collection<ThreadElement> values = threadListElements.values();
        return values.toArray(new IThreadElement[values.size()]);
    }

    /*
     * @see IMBeanServer#refreshThreadCache()
     */
    @Override
    public void refreshThreadCache() throws JvmCoreException {
        if (!checkReachability()) {
            return;
        }

        ThreadMXBean threadMXBean;
        try {
            threadMXBean = (ThreadMXBean) getMXBean(ThreadMXBean.class,
                    ManagementFactory.THREAD_MXBEAN_NAME);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getMBeanFailedMsg,
                    ManagementFactory.THREAD_MXBEAN_NAME), e);
        }

        if (threadMXBean == null) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getMBeanFailedMsg,
                    ManagementFactory.THREAD_MXBEAN_NAME), null);
        }

        long[] ids = threadMXBean.findDeadlockedThreads();
        LinkedHashMap<String, ThreadElement> newThreadListElements = new LinkedHashMap<String, ThreadElement>();
        List<ThreadInfo> allThreads = Arrays.asList(threadMXBean
                .dumpAllThreads(true, false));
        Collections.reverse(allThreads);

        for (ThreadInfo threadInfo : allThreads) {
            String threadName = threadInfo.getThreadName();
            long threadId = threadInfo.getThreadId();
            if (threadInfo.getStackTrace().length == 0
                    || threadName.startsWith("RMI ") //$NON-NLS-1$
                    || threadName.startsWith("JMX ")) { //$NON-NLS-1$
                continue;
            }
            boolean isDeadlocked = false;
            if (ids != null) {
                Arrays.sort(ids);
                if (Arrays.binarySearch(ids, threadId) >= 0) {
                    isDeadlocked = true;
                }
            }

            ThreadElement oldElement = threadListElements.get(threadName);
            long processCpuTime = threadMXBean.getThreadCpuTime(threadId);
            Long previousCpuTime = previousThreadProcessCpuTime.get(threadId);
            double cpuUsage = 0;
            previousThreadProcessCpuTime.put(threadId, processCpuTime);
            if (previousCpuTime != null) {
                cpuUsage = Math.min(
                        (processCpuTime - previousCpuTime) / 10000000d, 100);
            }
            previousThreadProcessCpuTime.put(threadId, processCpuTime);
            if (oldElement == null) {
                newThreadListElements.put(threadName, new ThreadElement(
                        threadInfo, isDeadlocked, cpuUsage));
            } else {
                oldElement.setThreadInfo(threadInfo);
                oldElement.setDeadlocked(isDeadlocked);
                oldElement.setCpuUsage(cpuUsage);
                newThreadListElements.put(threadName, oldElement);
            }
        }
        threadListElements = newThreadListElements;
    }

    /*
     * @see IMBeanServer#getHeapCache()
     */
    @Override
    public IHeapElement[] getHeapCache() {
        IHeapElement[] result = new IHeapElement[heapListElements.size()];
        int i = 0;
        for (Iterator<HeapElement> iterator = heapListElements.values()
                .iterator(); iterator.hasNext();) {
            result[i++] = iterator.next();
        }
        return result;
    }

    /*
     * @see IMBeanServer#refreshHeapCache()
     */
    @Override
    public void refreshHeapCache() throws JvmCoreException {
        if (!checkReachability()) {
            return;
        }

        IHeapDumpHandler heapDumpHandler = JvmModel.getInstance()
                .getHeapDumpHandler();
        if (heapDumpHandler != null) {
            String heap = heapDumpHandler.dumpHeap(jvm.getPid(), isLive);
            int maxNumberOfClasses = JvmModel.getInstance()
                    .getHeapDumpHandler().getMaxClassesNumber();
            parseHeap(heap, maxNumberOfClasses);
        }
    }

    /*
     * @see IMBeanServer#clearHeapDelta()
     */
    @Override
    public void clearHeapDelta() {
        for (Iterator<HeapElement> iterator = heapListElements.values()
                .iterator(); iterator.hasNext();) {
            HeapElement element = iterator.next();
            element.resetBaseSize();
        }
    }

    /*
     * @see IMBeanServer#dumpHprof(String, boolean, IProgressMonitor)
     */
    @Override
    public IFileStore dumpHprof(String hprofFileName, boolean transfer,
            IProgressMonitor monitor) throws JvmCoreException {
        if (!checkReachability()) {
            throw new JvmCoreException(IStatus.WARNING,
                    Messages.jvmNotReachableMsg, null);
        }

        IFileStore fileStore = null;
        String fileName;
        if (jvm.isRemote()) {
            fileName = hprofFileName;
        } else {
            fileStore = dump(SnapshotType.Hprof, null, null);
            fileName = fileStore.toString();
        }
        if (monitor.isCanceled()) {
            return null;
        }

        ObjectName objectName = jvm.getMBeanServer().getObjectName(
                "com.sun.management:type=HotSpotDiagnostic"); //$NON-NLS-1$
        invoke(objectName, "dumpHeap", new Object[] { fileName, Boolean.TRUE }, //$NON-NLS-1$
                new String[] { String.class.getCanonicalName(), "boolean" }); //$NON-NLS-1$

        if (jvm.isRemote() && transfer) {
            fileStore = dump(SnapshotType.Hprof, hprofFileName, monitor);
        }

        return fileStore;
    }

    /*
     * @see IMBeanServer#dumpHeap()
     */
    @Override
    public IFileStore dumpHeap() throws JvmCoreException {
        return dump(SnapshotType.Heap, null, null);
    }

    /*
     * @see IMBeanServer#dumpThreads()
     */
    @Override
    public IFileStore dumpThreads() throws JvmCoreException {
        return dump(SnapshotType.Thread, null, null);
    }

    /*
     * @see IMBeanServer#setUpdatePeriod(Integer)
     */
    @Override
    public void setUpdatePeriod(Integer updatePeriod) {
        startUpdateTimer(updatePeriod);
    }

    /*
     * @see IMBeanServer#addServerChangeListener(IMBeanServerChangeListener)
     */
    @Override
    public void addServerChangeListener(IMBeanServerChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /*
     * @see IMBeanServer#removeServerChangeListener(IMBeanServerChangeListener)
     */
    @Override
    public void removeServerChangeListener(IMBeanServerChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the object name instance without communicating with target JVM.
     * 
     * @param name
     *            The object name
     * @return The object name instance
     * @throws JvmCoreException
     */
    public ObjectName getObjectName(String name) throws JvmCoreException {
        try {
            return ObjectName.getInstance(name);
        } catch (MalformedObjectNameException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getObjectNameFailedMsg, name), e);
        } catch (NullPointerException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getObjectNameFailedMsg, name), e);
        }
    }

    /**
     * Unregisters the MBean.
     * 
     * @param objectName
     *            The object name
     */
    public void unregisterMBean(ObjectName objectName) {
        if (isJvmReachable) {
            try {
                connection.unregisterMBean(objectName);
            } catch (JMException e) {
                // do nothing
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * Resumes the sampling.
     */
    public void resumeSampling() {
        if (samplingTimer != null) {
            samplingTimer.cancel();
        }
        samplingTimer = new Timer(true);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    sampleProfilingData();
                } catch (JvmCoreException e) {
                    Activator.log(IStatus.ERROR, e.getMessage(), e);
                    suspendSampling();
                } catch (Throwable t) {
                    suspendSampling();
                }
            }
        };
        samplingTimer.schedule(timerTask, 0, samplingPeriod);
    }

    /**
     * Suspends the sampling.
     */
    public void suspendSampling() {
        if (samplingTimer != null) {
            samplingTimer.cancel();
            samplingTimer = null;
        }
    }

    /**
     * Gets the state.
     * 
     * @return The profiler state
     */
    public ProfilerState getProfilerState() {
        return samplingTimer == null ? ProfilerState.READY
                : ProfilerState.RUNNING;
    }

    /**
     * Gets the sampling period.
     * 
     * @return The sampling period
     */
    public Integer getSamplingPeriod() {
        return samplingPeriod;
    }

    /**
     * Sets the sampling period.
     * 
     * @param samplingPeriod
     *            The sampling period
     */
    public void setSamplingPeriod(Integer samplingPeriod) {
        this.samplingPeriod = samplingPeriod;
        if (jvm.getCpuProfiler().getState() == ProfilerState.RUNNING
                && jvm.getCpuProfiler().getProfilerType() == ProfilerType.SAMPLING) {
            resumeSampling();
        }
    }

    /**
     * Gets the JVM arguments.
     * 
     * @return The JVM arguments
     */
    public String getJvmArguments() {
        if (!checkReachability()) {
            return ""; //$NON-NLS-1$
        }

        List<String> arguments;
        try {
            RuntimeMXBean runtimeMXBean = (RuntimeMXBean) getMXBean(
                    RuntimeMXBean.class, ManagementFactory.RUNTIME_MXBEAN_NAME);
            arguments = runtimeMXBean.getInputArguments();
        } catch (IOException e) {
            Activator
                    .log(IStatus.ERROR,
                            NLS.bind(Messages.getAttributeFailedMsg,
                                    "InputArguments"), e); //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuffer buffer = new StringBuffer();
        for (String argument : arguments) {
            if (buffer.length() > 0) {
                buffer.append(" "); //$NON-NLS-1$
            }
            buffer.append(argument);
        }
        return buffer.toString();
    }

    /**
     * Adds the notification listener.
     * 
     * @param objectName
     *            The object name
     * @param listener
     *            The notification listener
     * @throws JvmCoreException
     */
    protected void addNotificationListener(ObjectName objectName,
            NotificationListener listener) throws JvmCoreException {
        if (isJvmReachable) {
            try {
                connection.addNotificationListener(objectName, listener, null,
                        null);
            } catch (InstanceNotFoundException e) {
                throw new JvmCoreException(IStatus.ERROR,
                        Messages.subscribeMBeanNotificationFailedMsg, e);
            } catch (IOException e) {
                throw new JvmCoreException(IStatus.ERROR,
                        Messages.subscribeMBeanNotificationFailedMsg, e);
            }
        }
    }

    /**
     * Removes the notification listener.
     * 
     * @param objectName
     *            The object name
     * @param listener
     *            The notification listener
     * @throws JvmCoreException
     */
    protected void removeNotificationListener(ObjectName objectName,
            NotificationListener listener) throws JvmCoreException {
        if (isJvmReachable) {
            try {
                connection.removeNotificationListener(objectName, listener);
            } catch (JMException e) {
                throw new JvmCoreException(IStatus.ERROR,
                        Messages.unsubscribeMBeanNotificationFailedMsg, e);
            } catch (IOException e) {
                throw new JvmCoreException(IStatus.ERROR,
                        Messages.unsubscribeMBeanNotificationFailedMsg, e);
            }
        }
    }

    /**
     * Fires the JVM model change event.
     * 
     * @param e
     *            The JVM model changed event
     */
    protected void fireMBeanServerChangeEvent(MBeanServerEvent e) {
        for (IMBeanServerChangeListener listener : listeners) {
            listener.serverChanged(e);
        }
    }

    /**
     * Gets the runtime name with PID@HOSTNAME.
     * 
     * @return The runtime name
     * @throws JvmCoreException
     */
    protected String getRuntimeName() throws JvmCoreException {
        if (!checkReachability()) {
            return null;
        }

        try {
            RuntimeMXBean runtimeMXBean = (RuntimeMXBean) getMXBean(
                    RuntimeMXBean.class, ManagementFactory.RUNTIME_MXBEAN_NAME);
            if (runtimeMXBean == null) {
                throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                        Messages.getMBeanFailedMsg,
                        ManagementFactory.RUNTIME_MXBEAN_NAME), new Exception());
            }

            return runtimeMXBean.getName();
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getMBeanFailedMsg,
                    ManagementFactory.RUNTIME_MXBEAN_NAME), e);
        }
    }

    /**
     * Connects to MBean server.
     * 
     * @param updatePeriod
     * 
     * @throws JvmCoreException
     */
    protected void connect(int updatePeriod) throws JvmCoreException {
        connection = connectToMBeanServer(jmxUrl);
        enableThreadContentionMonitoring();

        mxBeans.clear();
        previousThreadProcessCpuTime.clear();
        heapListElements.clear();
        threadListElements.clear();
        isJvmReachable = true;
        listeners.clear();
        previousSamplingTime = 0;
        previousStackTraces.clear();
        monitoredAttributeGroups.clear();

        startUpdateTimer(updatePeriod);
    }

    /**
     * Refreshes the MBean model.
     * 
     * @throws JvmCoreException
     */
    protected void refresh() throws JvmCoreException {
        if (!checkReachability()) {
            return;
        }
        for (IMonitoredMXBeanGroup group : monitoredAttributeGroups) {
            for (IMonitoredMXBeanAttribute attribute : group.getAttributes()) {
                String attributeName = attribute.getAttributeName();
                Object attributeObject = getAttribute(
                        attribute.getObjectName(), attributeName);

                Number value = getAttributeValue(attributeObject, attributeName);
                if (value == null) {
                    continue;
                }

                // exceptional handling for process CPU time
                if ("ProcessCpuTime".equals(attribute.getAttributeName())) { //$NON-NLS-1$
                    if (previousProcessCpuTime == 0) {
                        previousProcessCpuTime = (Long) value;
                        continue;
                    }

                    Double percent = ((Long) value - previousProcessCpuTime) / 1000000000d;
                    previousProcessCpuTime = (Long) value;
                    value = percent > 1 ? 1 : percent;
                }
                ((MonitoredMXBeanAttribute) attribute).add(value, new Date());
            }
        }

        JvmModel.getInstance().fireJvmModelChangeEvent(
                new JvmModelEvent(State.JvmModified, jvm));
    }

    /**
     * Disposes the resources.
     */
    protected void dispose() {
        if (timer != null) {
            timer.cancel();
        }
        if (samplingTimer != null) {
            samplingTimer.cancel();
        }
        ((MBeanNotification) mBeanNotification).dispose();
    }

    /**
     * Gets the attribute numerical value.
     * 
     * @param attributeObject
     *            The attribute object
     * @param attributeName
     *            The qualified attribute name (e.g. HeapMemoryUsage.used)
     * @return The attribute numerical value
     */
    private Number getAttributeValue(Object attributeObject,
            String attributeName) {
        if (attributeObject instanceof Number) {
            return (Number) attributeObject;
        }

        if (attributeObject instanceof CompositeData) {
            CompositeData compositeData = (CompositeData) attributeObject;
            if (attributeName.contains(".")) { //$NON-NLS-1$
                Object value = compositeData.get(attributeName.split("\\.")[1]); //$NON-NLS-1$
                return getAttributeValue(value,
                        attributeName.substring(attributeName.indexOf(".") + 1)); //$NON-NLS-1$
            }
        } else if (attributeObject instanceof TabularData) {
            TabularData tabularData = (TabularData) attributeObject;
            String key = attributeName.split("\\.")[1]; //$NON-NLS-1$
            for (Object keyList : tabularData.keySet()) {
                @SuppressWarnings("unchecked")
                Object[] keys = ((List<Object>) keyList).toArray(new Object[0]);
                if (String.valueOf(keys[0]).equals(key)) {
                    return getAttributeValue(
                            tabularData.get(keys),
                            attributeName.substring(attributeName.indexOf(".") + 1)); //$NON-NLS-1$
                }
            }
        }
        return null;
    }

    /**
     * Connects to the MBean server in the given VM.
     * 
     * @param url
     *            The JMX service URL
     * @return The MBean server connection
     * @throws JvmCoreException
     */
    private MBeanServerConnection connectToMBeanServer(JMXServiceURL url)
            throws JvmCoreException {
        try {
            if (jvm.getUserName() != null && jvm.getPassword() != null) {
                Map<String, String[]> env = new HashMap<String, String[]>();
                env.put(JMXConnector.CREDENTIALS,
                        new String[] { jvm.getUserName(), jvm.getPassword() });
                connector = JMXConnectorFactory.connect(url, env);
            } else {
                connector = JMXConnectorFactory.connect(url);
            }
            return connector.getMBeanServerConnection();
        } catch (IOException e) {
            IHost host = jvm.getHost();
            if (host != null && host.getActiveJvms().contains(jvm)) {
                host.removeJvm(jvm.getPid());
            }
            throw new JvmCoreException(IStatus.INFO,
                    Messages.connectToMBeanServerFailedMsg, e);
        }
    }

    /**
     * Enables the thread contention monitoring.
     * 
     * @throws JvmCoreException
     */
    private void enableThreadContentionMonitoring() throws JvmCoreException {
        try {
            ThreadMXBean threadMXBean = (ThreadMXBean) getMXBean(
                    ThreadMXBean.class, ManagementFactory.THREAD_MXBEAN_NAME);
            if (threadMXBean != null) {
                threadMXBean.setThreadContentionMonitoringEnabled(true);
            }
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getMBeanFailedMsg,
                    ManagementFactory.THREAD_MXBEAN_NAME), e);
        }
    }

    /**
     * Gets the MXBean.
     * 
     * @param mxBeanClass
     *            The MXBean class
     * @param mxBeanName
     *            The MXBean name
     * @return The MXBean
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private Object getMXBean(@SuppressWarnings("rawtypes") Class mxBeanClass,
            String mxBeanName) throws IOException {
        Object mxBean = mxBeans.get(mxBeanClass);
        if (mxBean == null && connection != null) {
            mxBean = newPlatformMXBeanProxy(connection, mxBeanName, mxBeanClass);
            mxBeans.put(mxBeanClass, mxBean);
        }
        return mxBean;
    }

    /**
     * Parses the given heap.
     * 
     * @param heap
     *            The heap
     * @param maxNumberOfClasses
     *            The max number of classes
     */
    private void parseHeap(String heap, int maxNumberOfClasses) {
        Map<String, HeapElement> newHeapElements = new LinkedHashMap<String, HeapElement>();

        String[] lines = heap.split("\n"); //$NON-NLS-1$
        for (String line : lines) {
            Scanner scanner = new Scanner(line);
            if (!scanner.hasNext()) {
                continue;
            }
            scanner.next();
            if (!scanner.hasNextLong()) {
                continue;
            }
            long count = scanner.nextLong();
            if (!scanner.hasNextLong()) {
                continue;
            }
            long size = scanner.nextLong();
            if (scanner.hasNext()) {
                String className = scanner.next();
                if (className.startsWith("<")) { //$NON-NLS-1$
                    continue;
                }
                className = convertClassName(className);

                HeapElement oldElement = heapListElements.get(className);
                if (oldElement == null) {
                    newHeapElements.put(className, new HeapElement(className,
                            size, count));
                } else {
                    // WORKAROUND heap from target JVM has a
                    // duplicated entry...
                    if (!newHeapElements.containsKey(className)) {
                        oldElement.setSizeAndCount(size, count);
                        newHeapElements.put(className, oldElement);
                    }
                }
                if (newHeapElements.size() >= maxNumberOfClasses) {
                    break;
                }
            }
        }
        heapListElements = newHeapElements;
    }

    /**
     * Converts the class name. e.g "[I" to "int[]"
     * 
     * @param className
     *            The class name
     * @return The class name
     */
    private String convertClassName(String className) {
        if (className.startsWith("[")) { //$NON-NLS-1$
            return Signature.toString(className);
        }
        return className;
    }

    /**
     * Dumps the profile data into file.
     * 
     * @param type
     *            The snapshot type
     * @param dumpFileName
     *            The dump file name
     * @param monitor
     *            The progress monitor
     * @return The file store
     * @throws JvmCoreException
     */
    private IFileStore dump(SnapshotType type, String dumpFileName,
            IProgressMonitor monitor) throws JvmCoreException {

        String simpleFileName;
        if (dumpFileName == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(new Date().getTime()).append('.')
                    .append(type.getExtension());
            simpleFileName = stringBuffer.toString();
        } else {
            simpleFileName = new File(dumpFileName).getName();
        }

        IFileStore fileStore = Util.getFileStore(simpleFileName,
                jvm.getBaseDirectory());

        // restore the terminated JVM if already removed
        AbstractJvm abstractJvm = jvm;
        if (!((Host) jvm.getHost()).getJvms().contains(jvm)) {
            jvm.saveJvmProperties();
            abstractJvm = (AbstractJvm) ((Host) jvm.getHost())
                    .addTerminatedJvm(jvm.getPid(), jvm.getPort(),
                            jvm.getMainClass());
        }

        OutputStream os = null;
        try {
            if (type == SnapshotType.Heap || type == SnapshotType.Thread) {
                String dump = getDumpString(type);
                os = fileStore.openOutputStream(EFS.NONE, null);
                os.write(dump.getBytes());
            } else if (type == SnapshotType.Hprof && jvm.isRemote()) {
                ObjectName objectName = getObjectName(DATA_TRANSFER_MXBEAN_NAME);
                os = fileStore.openOutputStream(EFS.NONE, null);
                byte[] bytes = new byte[0];
                int offset = 0;
                final int SIZE = 4096;
                final String[] SIGNATURES = new String[] {
                        String.class.getCanonicalName(), "int", "int" };//$NON-NLS-1$ //$NON-NLS-2$
                do {
                    bytes = (byte[]) invoke(objectName, "read", new Object[] { //$NON-NLS-1$
                            dumpFileName, offset, SIZE }, SIGNATURES);
                    os.write(bytes);
                    offset += SIZE;
                    if (monitor != null && monitor.isCanceled()) {
                        return null;
                    }
                } while (bytes.length > 0);
            }

            Snapshot snapshot = new Snapshot(fileStore, abstractJvm);
            abstractJvm.addSnapshot(snapshot);

            JvmModel.getInstance().fireJvmModelChangeEvent(
                    new JvmModelEvent(State.ShapshotTaken, abstractJvm,
                            snapshot));
        } catch (CoreException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.openOutputStreamFailedMsg, fileStore.toURI()
                            .getPath()), e);
        } catch (IOException e) {
            try {
                fileStore.delete(EFS.NONE, null);
            } catch (CoreException e1) {
                // do nothing
            }

            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.dumpFailedMsg, fileStore.toURI().getPath()), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
        return fileStore;
    }

    /**
     * Gets the dump string.
     * 
     * @param type
     *            The snapshot type
     * @return The dump string
     */
    private String getDumpString(SnapshotType type) {
        Date currentDate = new Date();
        String date = new SimpleDateFormat("yyyy/MM/dd").format(currentDate); //$NON-NLS-1$
        String time = new SimpleDateFormat("HH:mm:ss").format(currentDate); //$NON-NLS-1$

        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //$NON-NLS-1$
        buffer.append("<?JvmMonitor version=\""); //$NON-NLS-1$
        buffer.append(Activator.getDefault().getBundle().getVersion()
                .toString());
        buffer.append("\"?>\n"); //$NON-NLS-1$

        if (type == SnapshotType.Heap) {
            buffer.append("<heap-profile date=\""); //$NON-NLS-1$
        } else if (type == SnapshotType.Thread) {
            buffer.append("<thread-profile date=\""); //$NON-NLS-1$
        }
        buffer.append(date).append(' ').append(time).append("\" "); //$NON-NLS-1$
        buffer.append("runtime=\"").append(jvm.getPid()).append("@") //$NON-NLS-1$ //$NON-NLS-2$
                .append(jvm.getHost().getName()).append("\" "); //$NON-NLS-1$
        buffer.append("mainClass=\"").append(jvm.getMainClass()).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("arguments=\"").append(getJvmArguments()).append("\">\n"); //$NON-NLS-1$ //$NON-NLS-2$

        if (type == SnapshotType.Heap) {
            for (HeapElement element : heapListElements.values()) {
                element.dump(buffer);
            }
            buffer.append("</heap-profile>"); //$NON-NLS-1$
        } else if (type == SnapshotType.Thread) {
            for (ThreadElement element : threadListElements.values()) {
                element.dump(buffer);
            }
            buffer.append("</thread-profile>"); //$NON-NLS-1$
        }
        return buffer.toString();
    }

    /**
     * Checks if the jvm is still reachable.
     * 
     * @return True if the JVM is still reachable
     */
    synchronized private boolean checkReachability() {
        if (!isJvmReachable) {
            return false;
        }

        try {
            connection.getDefaultDomain();
        } catch (IOException ex) {
            isJvmReachable = false;

            timer.cancel();
            if (samplingTimer != null) {
                samplingTimer.cancel();
                samplingTimer = null;
            }
            if (jvm.getHost().getActiveJvms().contains(jvm)) {
                jvm.getHost().removeJvm(jvm.getPid());
            }

            return false;
        }
        return true;
    }

    /**
     * Samples the profiling data.
     * 
     * @throws JvmCoreException
     */
    void sampleProfilingData() throws JvmCoreException {
        if (!checkReachability()) {
            return;
        }

        ThreadMXBean threadMXBean;
        try {
            threadMXBean = (ThreadMXBean) getMXBean(ThreadMXBean.class,
                    ManagementFactory.THREAD_MXBEAN_NAME);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getMBeanFailedMsg,
                    ManagementFactory.THREAD_MXBEAN_NAME), e);
        }

        if (threadMXBean == null) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getMBeanFailedMsg,
                    ManagementFactory.THREAD_MXBEAN_NAME), null);
        }

        CpuModel cpuModel = (CpuModel) jvm.getCpuProfiler().getCpuModel();
        long samplingTime = System.currentTimeMillis();
        long actualSamplingPeriodInMilliSeconds;
        if (previousSamplingTime == 0) {
            actualSamplingPeriodInMilliSeconds = samplingPeriod;
        } else {
            actualSamplingPeriodInMilliSeconds = samplingTime
                    - previousSamplingTime;
        }

        Set<String> profiledPackages = jvm.getCpuProfiler()
                .getProfiledPackages();
        for (ThreadInfo threadInfo : threadMXBean.dumpAllThreads(true, false)) {
            StackTraceElement[] stackTrace = threadInfo.getStackTrace();
            String threadName = threadInfo.getThreadName();
            if (stackTrace.length > 0 && !threadName.startsWith("JMX ") //$NON-NLS-1$
                    && !threadName.startsWith("RMI ")) { //$NON-NLS-1$
                ThreadNode<CallTreeNode> callTreeThreadNode = cpuModel
                        .getCallTreeThread(threadName);
                ThreadNode<MethodNode> hotSpotThreadNode = cpuModel
                        .getHotSpotThread(threadName);
                if (callTreeThreadNode == null) {
                    callTreeThreadNode = new ThreadNode<CallTreeNode>(
                            threadName);
                }
                if (hotSpotThreadNode == null) {
                    hotSpotThreadNode = new ThreadNode<MethodNode>(threadName);
                }

                updateCpuModel(callTreeThreadNode, hotSpotThreadNode,
                        profiledPackages, invertStackTrace(stackTrace),
                        actualSamplingPeriodInMilliSeconds);

                if (callTreeThreadNode.hasChildren()) {
                    cpuModel.addCallTreeThread(callTreeThreadNode);
                }
                if (hotSpotThreadNode.hasChildren()) {
                    cpuModel.addHotSpotThread(hotSpotThreadNode);
                }
            }
        }
        previousSamplingTime = samplingTime;
    }

    /**
     * Gets the inverted stack trace.
     * 
     * @param stackTrace
     *            The stack trace
     * @return The inverted stack trace
     */
    private StackTraceElement[] invertStackTrace(StackTraceElement[] stackTrace) {
        StackTraceElement[] invertedStackTrace = new StackTraceElement[stackTrace.length];
        for (int i = 0; i < stackTrace.length; i++) {
            invertedStackTrace[i] = stackTrace[stackTrace.length - 1 - i];
        }
        return invertedStackTrace;
    }

    /**
     * Updates the CPU model.
     * 
     * @param stackTrace
     *            The stack trace
     * @param callTreeThreadNode
     *            The call tree thread node
     * @param hotSpotThreadNode
     *            The hot spot thread node
     * @param profiledPackages
     *            The profiled packages
     * @param period
     *            The actual sampling period
     */
    private void updateCpuModel(ThreadNode<CallTreeNode> callTreeThreadNode,
            ThreadNode<MethodNode> hotSpotThreadNode,
            Set<String> profiledPackages, StackTraceElement[] stackTrace,
            long period) {

        String threadName = callTreeThreadNode.getName();

        StackTraceElement[] previousStackTrace = previousStackTraces
                .get(threadName);
        boolean isNewStack = false;

        CallTreeNode currentFrameNode = null;
        boolean isRootStack = true;
        for (int i = 0; i < stackTrace.length; i++) {
            if (!isProfiledPackage(stackTrace[i].getClassName(),
                    profiledPackages)) {
                continue;
            }

            String methodName = stackTrace[i].getClassName() + "." //$NON-NLS-1$
                    + stackTrace[i].getMethodName() + "()"; //$NON-NLS-1$

            if (previousStackTrace == null || i >= previousStackTrace.length
                    || !stackTrace[i].equals(previousStackTrace[i])) {
                isNewStack = true;
            }

            updateMethodNode(hotSpotThreadNode, methodName, isNewStack, period);

            currentFrameNode = updateFrameNode(callTreeThreadNode,
                    currentFrameNode, methodName, isNewStack, period,
                    i == stackTrace.length - 1);

            hotSpotThreadNode.setTotalTime(hotSpotThreadNode.getTotalTime()
                    + period);
            if (isRootStack) {
                callTreeThreadNode.setTotalTime(callTreeThreadNode
                        .getTotalTime() + period);
            }

            isRootStack = false;
        }

        previousStackTraces.put(threadName, stackTrace);
    }

    /**
     * Updates the frame node.
     * 
     * @param callTreeThreadNode
     *            The call tree thread node
     * @param currentFrameNode
     *            The current frame node
     * @param methodName
     *            The method name
     * @param isNewStack
     *            True if the given method is new stack
     * @param period
     *            The sampling period
     * @param isLeaf
     *            True if the given method name is leaf
     * @return The frame node
     */
    private CallTreeNode updateFrameNode(
            ThreadNode<CallTreeNode> callTreeThreadNode,
            CallTreeNode currentFrameNode, String methodName,
            boolean isNewStack, long period, boolean isLeaf) {
        CallTreeNode frameNode;
        if (currentFrameNode == null) {
            frameNode = (CallTreeNode) callTreeThreadNode.getChild(methodName);
        } else {
            frameNode = currentFrameNode.getChild(methodName);
        }

        if (frameNode == null) {
            if (currentFrameNode == null) {
                frameNode = new CallTreeNode(
                        jvm.getCpuProfiler().getCpuModel(), methodName, period,
                        1, callTreeThreadNode);
                callTreeThreadNode.addChild(frameNode);
            } else {
                frameNode = new CallTreeNode(
                        jvm.getCpuProfiler().getCpuModel(), methodName, period,
                        1, currentFrameNode, callTreeThreadNode);
                currentFrameNode.addChild(frameNode);
            }
        } else {
            if (isNewStack) {
                frameNode
                        .setInvocationCount(frameNode.getInvocationCount() + 1);
            }
            frameNode.setTotalTime(frameNode.getTotalTime() + period);
        }

        if (isLeaf) {
            frameNode.setSelfTime(frameNode.getSelfTime() + period);
        }

        return frameNode;
    }

    /**
     * Updates the method node.
     * 
     * @param hotSpotThreadNode
     *            The hot spot thread node
     * @param methodName
     *            The method name
     * @param isNewStack
     *            True if the given method is a new stack
     * @param period
     *            The sampling period
     */
    private void updateMethodNode(ThreadNode<MethodNode> hotSpotThreadNode,
            String methodName, boolean isNewStack, long period) {
        MethodNode methodNode = (MethodNode) hotSpotThreadNode
                .getChild(methodName);
        if (methodNode == null) {
            methodNode = new MethodNode(jvm.getCpuProfiler().getCpuModel(),
                    methodName, hotSpotThreadNode);
            hotSpotThreadNode.addChild(methodNode);
        }

        if (isNewStack) {
            methodNode.incrementCount(1);
        }

        methodNode.incrementTime(period);
    }

    /**
     * Checks if the given class belongs to one of the packages list.
     * 
     * @param className
     *            the class name (e.g. java.lang.String)
     * @param packages
     *            The profiled packages
     * @return true if the given class belongs to one of the packages list
     */
    private boolean isProfiledPackage(String className, Set<String> packages) {
        if (packages.isEmpty()) {
            return false;
        }

        String packageName;
        if (className.contains(".")) { //$NON-NLS-1$
            packageName = className.substring(0, className.lastIndexOf('.'));
        } else if (className.startsWith("$")) { //$NON-NLS-1$
            return false; // e.g. $Proxy0
        } else {
            packageName = "<default>"; //$NON-NLS-1$
        }

        for (String pkg : packages) {
            if (pkg.endsWith("*")) { //$NON-NLS-1$
                if (packageName.concat(".").startsWith( //$NON-NLS-1$
                        pkg.substring(0, pkg.length() - 1))) {
                    return true;
                }
            } else {
                if (packageName.equals(pkg)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Starts the update timer.
     * 
     * @param updatePeriod
     *            The update period
     */
    private void startUpdateTimer(int updatePeriod) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    refresh();
                } catch (JvmCoreException e) {
                    Activator.log(IStatus.ERROR, e.getMessage(), e);
                    timer.cancel();
                } catch (Throwable t) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(timerTask, 0, updatePeriod);
    }
}
