/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.IJvm;
import org.fusesource.ide.jvmmonitor.core.ITerminatedJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent.State;

/**
 * The host.
 */
public class Host implements IHost {

    /** The directory suffix. */
    public static final String DIR_SUFFIX = ".host"; //$NON-NLS-1$

    /** The properties file. */
    public static final String PROPERTIES_FILE = "properties.xml"; //$NON-NLS-1$

    /** The property key for host. */
    public static final String HOST_PROP_KEY = "Host"; //$NON-NLS-1$

    /** The host name. */
    private String hostName;

    /** The terminated JVMs. */
    private List<ITerminatedJvm> terminatedJvms;

    /** The active JVMs. */
    private List<IActiveJvm> activeJvms;

    /**
     * The constructor.
     * 
     * @param hostname
     *            The host name
     */
    public Host(String hostname) {
        this.hostName = hostname;
        terminatedJvms = new CopyOnWriteArrayList<ITerminatedJvm>();
        activeJvms = new CopyOnWriteArrayList<IActiveJvm>();

        saveHostProperties();
        refreshSnapshots();
    }

    /*
     * @see IHost#getName()
     */
    @Override
    public String getName() {
        return hostName;
    }

    /*
     * @see IHost#getJvms()
     */
    @Override
    public List<IJvm> getJvms() {
        List<IJvm> jvms = new CopyOnWriteArrayList<IJvm>();
        jvms.addAll(activeJvms);
        jvms.addAll(terminatedJvms);
        return jvms;
    }

    /*
     * @see IHost#getTerminatedJvms()
     */
    @Override
    public List<ITerminatedJvm> getTerminatedJvms() {
        return terminatedJvms;
    }

    /*
     * @see IHost#getActiveJvms()
     */
    @Override
    public List<IActiveJvm> getActiveJvms() {
        return activeJvms;
    }

    /*
     * @see IHost#addRemoteActiveJvm(int, String, String, int)
     */
    @Override
    public IActiveJvm addRemoteActiveJvm(int port, String userName,
            String password, int updatePeriod) throws JvmCoreException {
        for (IActiveJvm jvm : getActiveJvms()) {
            if (jvm.getPort() == port) {
                return jvm;
            }
        }

        IActiveJvm jvm = new ActiveJvm(port, userName, password, this,
                updatePeriod);
        return addActiveJvm(jvm);
    }

    /*
     * @see IHost#addLocalActiveJvm(int, String, String, String)
     */
    @Override
    public IActiveJvm addLocalActiveJvm(int pid, String mainClass, String url,
            String errorStateMessage) throws JvmCoreException {
        for (IActiveJvm jvm : getActiveJvms()) {
            if (jvm.getPid() == pid) {
                return jvm;
            }
        }

        ActiveJvm jvm = new ActiveJvm(pid, url, this);
        jvm.setMainClass(mainClass);
        jvm.setErrorStateMessage(errorStateMessage);
        return addActiveJvm(jvm);
    }

    /*
     * @see IHost#removeJvm(int)
     */
    @Override
    public void removeJvm(int pid) {
        IPath hostDir = null;
        try {
            hostDir = getHostDir();
        } catch (JvmCoreException e) {
            Activator.log(IStatus.ERROR,
                    NLS.bind(Messages.removeJvmFailedMsg, +pid), e);
            return;
        }

        for (ITerminatedJvm jvm : terminatedJvms) {
            if (jvm.getPid() == pid) {
                terminatedJvms.remove(jvm);
                int id = (jvm.getPid() != -1) ? jvm.getPid() : jvm.getPort();
                IPath dirPath = hostDir.append(File.separator + id
                        + IJvm.DIR_SUFFIX);
                Util.deleteDir(dirPath.toFile());
                JvmModel.getInstance().fireJvmModelChangeEvent(
                        new JvmModelEvent(State.JvmRemoved, null));
                break;
            }
        }
        for (IActiveJvm jvm : activeJvms) {
            if (jvm.getPid() == pid) {
                if (jvm.isConnected()) {
                    jvm.disconnect();
                }
                if (jvm.getShapshots().size() > 0) {
                    addTerminatedJvm(jvm.getPid(), -1, jvm.getMainClass());
                } else {
                    int id = (jvm.getPid() != -1) ? jvm.getPid() : jvm
                            .getPort();
                    IPath dirPath = hostDir.append(File.separator + id
                            + IJvm.DIR_SUFFIX);
                    Util.deleteDir(dirPath.toFile());
                }
                activeJvms.remove(jvm);
                JvmModel.getInstance().fireJvmModelChangeEvent(
                        new JvmModelEvent(State.JvmRemoved, null));
                break;
            }
        }
    }

    /*
     * @see IHost#isLocalHost()
     */
    @Override
    public boolean isLocalHost() {
        return LOCALHOST.equals(hostName);
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return hostName;
    }

    /**
     * Gets the host directory.
     * 
     * @return The host directory
     * @throws JvmCoreException
     */
    public IPath getHostDir() throws JvmCoreException {
        IPath hostDir = Activator.getDefault().getStateLocation()
                .append(File.separator + hostName + Host.DIR_SUFFIX);
        if (!hostDir.toFile().exists() && !hostDir.toFile().mkdir()) {
            throw new JvmCoreException(IStatus.ERROR, NLS.bind(
                    Messages.createDirectoryFailedMsg, hostDir.toFile()
                            .getName()), null);
        }
        return hostDir;
    }

    /**
     * Adds the terminated JVM.
     * 
     * @param pid
     *            The process ID.
     * @param port
     *            The port
     * @param mainClass
     *            The main class
     * @return The terminated JVM
     */
    public ITerminatedJvm addTerminatedJvm(int pid, int port, String mainClass) {
        for (ITerminatedJvm jvm : terminatedJvms) {
            if (jvm.getPid() == pid) {
                return jvm;
            }
        }

        TerminatedJvm terminatedJvm = new TerminatedJvm(pid, port, mainClass,
                this);
        terminatedJvms.add(terminatedJvm);
        return terminatedJvm;
    }

    /**
     * Adds the active JVM.
     * 
     * @param jvm
     *            The active JVM.
     * @return The active JVM
     */
    protected IActiveJvm addActiveJvm(IActiveJvm jvm) {
        for (IActiveJvm activeJvm : activeJvms) {
            if (activeJvm.getPid() == jvm.getPid()) {
                return jvm;
            }
        }

        activeJvms.add(jvm);
        JvmModel.getInstance().fireJvmModelChangeEvent(
                new JvmModelEvent(State.JvmAdded, jvm));
        return jvm;
    }

    /**
     * Refreshes the snapshots.
     */
    private void refreshSnapshots() {
        IPath hostDir;
        try {
            hostDir = getHostDir();
        } catch (JvmCoreException e) {
            // do nothing
            return;
        }

        File[] files = hostDir.toFile().listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }

            String jvmDir = file.getName();
            if (!jvmDir.endsWith(IJvm.DIR_SUFFIX)) {
                continue;
            }

            IPath filePath = hostDir.append(File.separator + jvmDir
                    + File.separator + IJvm.PROPERTIES_FILE);
            if (!filePath.toFile().exists() || file.list().length == 1) {
                Util.deleteDir(file);
                continue;
            }

            Properties props = Util.loadProperties(filePath);
            if (props == null) {
                Util.deleteDir(file);
                continue;
            }

            String host = props.getProperty(IJvm.HOST_PROP_KEY);
            if (!hostName.equals(host)) {
                Util.deleteDir(file);
                continue;
            }

            String pidString = props.getProperty(IJvm.PID_PROP_KEY);
            int pid = (pidString == null) ? -1 : Integer.valueOf(pidString);

            String portString = props.getProperty(IJvm.PORT_PROP_KEY);
            int port = (portString == null) ? -1 : Integer.valueOf(portString);

            String mainClass = props.getProperty(IJvm.MAIN_CLASS_PROP_KEY);

            addTerminatedJvm(pid, port, mainClass);
        }
    }

    /**
     * Saves the properties.
     */
    private void saveHostProperties() {
        Properties props = new Properties();
        IPath hostDir;
        try {
            hostDir = getHostDir();
        } catch (JvmCoreException e) {
            Activator.log(IStatus.ERROR, Messages.savePropertiesFileFailedMsg,
                    e);
            return;
        }

        IFileStore fileStore = Util.getFileStore(Host.PROPERTIES_FILE, hostDir);
        OutputStream os = null;
        try {
            os = fileStore.openOutputStream(EFS.NONE, null);

            props.setProperty(Host.HOST_PROP_KEY, hostName);

            props.storeToXML(os, "Host Properties"); //$NON-NLS-1$
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
}
