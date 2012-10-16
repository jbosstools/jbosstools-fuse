/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.management.remote.JMXServiceURL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.IJvm;
import org.fusesource.ide.jvmmonitor.core.ISWTResourceMonitor;
import org.fusesource.ide.jvmmonitor.core.IThreadElement;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent.State;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler;
import org.fusesource.ide.jvmmonitor.internal.core.cpu.CpuProfiler;

/**
 * The active JVM.
 */
public class ActiveJvm extends AbstractJvm implements IActiveJvm {

    /** The URL path. */
    private static final String URL_PATH = "/jndi/rmi://%s:%d/jmxrmi"; //$NON-NLS-1$

    /** The RMI protocol. */
    private static final String RMI_PROTOCOL = "rmi"; //$NON-NLS-1$

    /** The main thread. */
    private static final String MAIN_THREAD = "main"; //$NON-NLS-1$

    /** The state indicating if attach mechanism is supported. */
    private boolean isAttachSupported;

    /** The error state message. */
    private String errorStateMessage;

    /** The state indicating if JVM is running on remote host. */
    private boolean isRemote;

    /** The state indicating if JVM is connected with JMX. */
    private boolean isConnected;

    /** The MXBean server. */
    private MBeanServer mBeanServer;

    /** The CPU profiler. */
    private ICpuProfiler cpuProfiler;

    /** The SWT resource monitor. */
    private ISWTResourceMonitor swtResourceMonitor;

    /**
     * The constructor for local JVM.
     * 
     * @param pid
     *            The process ID
     * @param url
     *            The JMX service URL
     * @param host
     *            The host
     * @throws JvmCoreException
     */
    public ActiveJvm(int pid, String url, IHost host) throws JvmCoreException {
        super(pid, host);

        isRemote = false;

        JMXServiceURL jmxUrl = null;
        try {
            if (url != null) {
                jmxUrl = new JMXServiceURL(url);
                isAttachSupported = true;
            }
        } catch (MalformedURLException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getJmxServiceUrlForPidFailedMsg, pid), e);
        }

        initialize(jmxUrl);
        saveJvmProperties();
    }

    /**
     * The constructor for JVM communicating with RMI protocol.
     * 
     * @param port
     *            The port
     * @param userName
     *            The user name
     * @param password
     *            The password
     * @param host
     *            The host
     * @param updatePeriod
     *            The update period
     * @throws JvmCoreException
     */
    public ActiveJvm(int port, String userName, String password, IHost host,
            int updatePeriod) throws JvmCoreException {
        super(port, userName, password, host);

        isRemote = true;

        String urlPath = String.format(URL_PATH, host.getName(), port);
        JMXServiceURL url = null;
        try {
            url = new JMXServiceURL(RMI_PROTOCOL, "", 0, urlPath); //$NON-NLS-1$
            isAttachSupported = true;
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getJmxServiceUrlForPortFailedMsg, port), e);
        }
        initialize(url);

        // refresh
        connect(updatePeriod);
        refreshPid();
        refreshMainClass();
        refreshSnapshots();
        disconnect();

        saveJvmProperties();
    }

    /**
     * The constructor for JVM communicating with RMI protocol.
     * 
     * @param url
     *            The JMX URL
     * @param userName
     *            The user name
     * @param password
     *            The password
     * @param updatePeriod
     *            The update period
     * @throws JvmCoreException
     */
    public ActiveJvm(String url, String userName, String password,
            int updatePeriod) throws JvmCoreException {
        super(userName, password);

        isRemote = true;

        JMXServiceURL jmxUrl = null;
        try {
            jmxUrl = new JMXServiceURL(url);
            isAttachSupported = true;
        } catch (MalformedURLException e) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.getJmxServiceUrlForUrlFailedMsg, url), e);
        }

        initialize(jmxUrl);

        // refresh
        connect(updatePeriod);
        refreshPid();
        refreshMainClass();
        boolean jvmAddedToHost = refreshHost();
        disconnect();

        if (jvmAddedToHost) {
            refreshSnapshots();
        }
    }

    /*
     * @see IActiveJvm#connect(int)
     */
    @Override
    public void connect(int updatePeriod) throws JvmCoreException {
        if (!isAttachSupported) {
            throw new IllegalStateException(Messages.attachNotSupportedMsg);
        }

        mBeanServer.connect(updatePeriod);
        isConnected = true;

        if (!isRemote) {
            JvmModel.getInstance().getAgentLoadHandler().loadAgent(this);
        }

        if (swtResourceMonitor.isSupported()) {
            swtResourceMonitor.setTracking(true);
        }

        JvmModel.getInstance().fireJvmModelChangeEvent(
                new JvmModelEvent(State.JvmConnected, this));
    }

    /*
     * @see IActiveJvm#disconnect()
     */
    @Override
    public void disconnect() {
        isConnected = false;

        mBeanServer.dispose();
        try {
            if (swtResourceMonitor.isSupported()) {
                swtResourceMonitor.setTracking(false);
            }
        } catch (JvmCoreException e) {
            // do nothing
        }

        JvmModel.getInstance().fireJvmModelChangeEvent(
                new JvmModelEvent(State.JvmDisconnected, this));
    }

    /*
     * @see IActiveJvm#isConnected()
     */
    @Override
    public boolean isConnected() {
        return isConnected;
    }

    /*
     * @see IActiveJvm#isAttachSupported()
     */
    @Override
    public boolean isConnectionSupported() {
        return isAttachSupported;
    }

    /*
     * @see IActiveJvm#getErrorStateMessage()
     */
    @Override
    public String getErrorStateMessage() {
        return errorStateMessage;
    }

    /*
     * @see IActiveJvm#isRemote()
     */
    @Override
    public boolean isRemote() {
        return isRemote;
    }

    /*
     * @see IActiveJvm#getMBeanServer()
     */
    @Override
    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    /*
     * @see IActiveJvm#getCpuProfiler()
     */
    @Override
    public ICpuProfiler getCpuProfiler() {
        return cpuProfiler;
    }

    /*
     * @see IActiveJvm#getSWTResourceMonitor()
     */
    @Override
    public ISWTResourceMonitor getSWTResourceMonitor() {
        return swtResourceMonitor;
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return getMainClass();
    }

    /**
     * Saves the JVM properties.
     */
    public void saveJvmProperties() {
        IFileStore fileStore;
        try {
            fileStore = Util.getFileStore(IJvm.PROPERTIES_FILE,
                    getBaseDirectory());
            if (fileStore.fetchInfo().exists()) {
                return;
            }
        } catch (JvmCoreException e) {
            Activator.log(IStatus.ERROR, Messages.savePropertiesFileFailedMsg,
                    e);
            return;
        }

        Properties props = new Properties();
        OutputStream os = null;
        try {
            os = fileStore.openOutputStream(EFS.NONE, null);

            int pid = getPid();
            int port = getPort();
            String mainClass = getMainClass();

            props.setProperty(IJvm.PID_PROP_KEY, String.valueOf(pid));
            props.setProperty(IJvm.PORT_PROP_KEY, String.valueOf(port));
            if (mainClass != null) {
                props.setProperty(IJvm.MAIN_CLASS_PROP_KEY, mainClass);
            }
            props.setProperty(IJvm.HOST_PROP_KEY, getHost().getName());

            props.storeToXML(os, "JVM Properties"); //$NON-NLS-1$
        } catch (CoreException e) {
            Activator.log(IStatus.ERROR, NLS.bind(
                    Messages.openOutputStreamFailedMsg, fileStore.toURI()
                            .getPath()), e);
        } catch (IOException e) {
            try {
                fileStore.delete(EFS.NONE, null);
            } catch (CoreException e1) {
                // do nothing
            }
            Activator.log(IStatus.ERROR, NLS.bind(
                    Messages.writePropertiesFileFailedMsg, fileStore.toURI()
                            .getPath()), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Sets the error state message.
     * 
     * @param errorStateMessage
     *            The error state message
     */
    protected void setErrorStateMessage(String errorStateMessage) {
        this.errorStateMessage = errorStateMessage;
    }

    /**
     * Initializes the active JVM.
     * 
     * @param url
     *            The JMX service URL
     */
    private void initialize(JMXServiceURL url) {
        isConnected = false;
        cpuProfiler = new CpuProfiler(this);
        mBeanServer = new MBeanServer(url, this);
        swtResourceMonitor = new SWTResourceMonitor(this);
    }

    /**
     * Refreshes the PID.
     * 
     * @throws JvmCoreException
     */
    private void refreshPid() throws JvmCoreException {
        String[] elements = mBeanServer.getRuntimeName().split("@"); //$NON-NLS-1$
        if (elements == null || elements.length != 2) {
            throw new JvmCoreException(IStatus.ERROR, Messages.getPidFailedMsg,
                    new Exception());
        }

        setPid(Integer.valueOf(elements[0]));
    }

    /**
     * Refreshes the host.
     * 
     * @return True if JVM has been added to host
     * @throws JvmCoreException
     */
    private boolean refreshHost() throws JvmCoreException {
        String[] elements = mBeanServer.getRuntimeName().split("@"); //$NON-NLS-1$
        if (elements == null || elements.length != 2) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.getHostNameFailedMsg, new Exception());
        }

        String hostName = elements[1];
        Host host = (Host) JvmModel.getInstance().getHost(hostName);
        if (host == null) {
            host = new Host(hostName);
        } else {
            for (IJvm jvm : host.getActiveJvms()) {
                if (jvm.getPid() == getPid()) {
                    return false;
                }
            }
        }
        host.addActiveJvm(this);
        setHost(host);
        return true;
    }

    /**
     * Refreshes the main class.
     * 
     * @throws JvmCoreException
     */
    private void refreshMainClass() throws JvmCoreException {
        mBeanServer.refreshThreadCache();

        for (IThreadElement element : mBeanServer.getThreadCache()) {
            if (element.getThreadName().equals(MAIN_THREAD)) {
                StackTraceElement[] elements = element.getStackTraceElements();
                if (elements == null || elements.length == 0) {
                    return;
                }

                StackTraceElement lastElement = elements[elements.length - 1];
                setMainClass(lastElement.getClassName());
                break;
            }
        }
    }
}
