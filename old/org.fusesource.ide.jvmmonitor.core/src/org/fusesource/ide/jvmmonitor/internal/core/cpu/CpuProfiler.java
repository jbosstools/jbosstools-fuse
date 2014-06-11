/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core.cpu;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.management.Attribute;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent;
import org.fusesource.ide.jvmmonitor.core.ISnapshot.SnapshotType;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent.State;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent.CpuModelState;
import org.fusesource.ide.jvmmonitor.core.dump.CpuDumpParser;
import org.fusesource.ide.jvmmonitor.internal.core.AbstractJvm;
import org.fusesource.ide.jvmmonitor.internal.core.ActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.core.Host;
import org.fusesource.ide.jvmmonitor.internal.core.MBeanServer;
import org.fusesource.ide.jvmmonitor.internal.core.Messages;
import org.fusesource.ide.jvmmonitor.internal.core.Snapshot;
import org.fusesource.ide.jvmmonitor.internal.core.Util;
import org.xml.sax.SAXException;

/**
 * The CPU profiler.
 */
public class CpuProfiler implements ICpuProfiler {

    /** The profiler MXBean name. */
    private static final String PROFILER_MXBEAN_NAME = "org.fusesource.ide.jvmmonitor:type=CPU BCI Profiler"; //$NON-NLS-1$

    /** The transformClasses method in CpuProfilerMXBean. */
    private static final String TRANSFORM_CLASSES = "transformClasses"; //$NON-NLS-1$

    /** The interruptTransform method in CpuProfilerMXBean. */
    private static final String INTERRUPT_TRANSFORM = "interruptTransform"; //$NON-NLS-1$

    /** The clear method in CpuProfilerMXBean. */
    private static final String CLEAR = "clear"; //$NON-NLS-1$

    /** The dump method in CpuProfilerMXBean. */
    private static final String DUMP = "dump"; //$NON-NLS-1$

    /** The setFilter method in CpuProfilerMXBean. */
    private static final String SET_FILTER = "setFilter"; //$NON-NLS-1$

    /** The TransformStatus attribute in CpuProfilerMXBean. */
    private static final String TRANSFORM_STATUS = "TransformStatus"; //$NON-NLS-1$

    /** The composite key targetClassesCount in TransformStatus. */
    private static final String TARGET_CLASSES_COUNT = "targetClassesCount"; //$NON-NLS-1$

    /** The composite key transformedClassesCount in TransformStatus. */
    private static final String TRANSFORMED_CLASSES_COUNT = "transformedClassesCount"; //$NON-NLS-1$

    /** The Version attribute in CpuProfilerMXBean. */
    private static final String VERSION = "Version"; //$NON-NLS-1$

    /** The Running attribute in CpuProfilerMXBean. */
    private static final String RUNNING = "Running"; //$NON-NLS-1$

    /** the ProfiledPackages attribute in CpuProfilerMXBean. */
    private static final String PROFILED_PACKAGES = "ProfiledPackages"; //$NON-NLS-1$

    /** the profiled java packages property key. */
    private static final String PROFILED_PACKAGES_PROP_KEY = "jvmmonitor.profiled.packages"; //$NON-NLS-1$

    /** The CPU model */
    private CpuModel cpuModel;

    /** The active JVM. */
    private ActiveJvm jvm;

    /** The agent jar version. */
    private String agentJarVersion;

    /** The profiler type. */
    private ProfilerType type;

    /** The profiled packages. */
    private Set<String> profiledPackages;

    /**
     * The constructor.
     * 
     * @param jvm
     *            The active JVM
     */
    public CpuProfiler(ActiveJvm jvm) {
        cpuModel = new CpuModel();
        this.jvm = jvm;
        type = ProfilerType.SAMPLING;

        profiledPackages = new HashSet<String>();
    }

    /*
     * @see ICpuProfiler#setProfilerType(ICpuProfiler.ProfilerType)
     */
    @Override
    public void setProfilerType(ProfilerType type) {
        this.type = type;
    }

    /*
     * @see ICpuProfiler#getProfilerType()
     */
    @Override
    public ProfilerType getProfilerType() {
        return type;
    }

    /*
     * @see ICpuProfiler#transformClasses()
     */
    @Override
    public void transformClasses(IProgressMonitor monitor)
            throws JvmCoreException, InterruptedException {
        if (type == ProfilerType.BCI) {
            validateAgent();

            int target = getTransformStatusCompositeValue(TARGET_CLASSES_COUNT);
            int previousTransformed = getTransformStatusCompositeValue(TRANSFORMED_CLASSES_COUNT);

            monitor.beginTask(Messages.transformClassesTask, target);
            monitor.worked(previousTransformed);

            // trigger to transform classes in target JVM
            invokeCpuProfilerMXBeanMethod(TRANSFORM_CLASSES, null, null);

            // monitor the progress
            while (true) {
                if (monitor.isCanceled()) {
                    invokeCpuProfilerMXBeanMethod(INTERRUPT_TRANSFORM, null,
                            null);
                    throw new InterruptedException();
                }

                target = getTransformStatusCompositeValue(TARGET_CLASSES_COUNT);
                int transformed = getTransformStatusCompositeValue(TRANSFORMED_CLASSES_COUNT);
                if (target == transformed) {
                    break;
                }

                monitor.worked(transformed - previousTransformed);
                previousTransformed = transformed;
            }
        }
    }

    /*
     * @see ICpuProfiler#resume()
     */
    @Override
    public void resume() throws JvmCoreException {
        if (type == ProfilerType.BCI) {
            validateAgent();
            ObjectName objectName = jvm.getMBeanServer().getObjectName(
                    PROFILER_MXBEAN_NAME);
            if (objectName != null) {
                jvm.getMBeanServer().setAttribute(objectName,
                        new Attribute(RUNNING, true));
            }
        } else {
            jvm.getMBeanServer().resumeSampling();
        }
    }

    /*
     * @see ICpuProfiler#suspend()
     */
    @Override
    public void suspend() throws JvmCoreException {
        if (type == ProfilerType.BCI) {
            validateAgent();
            ObjectName objectName = jvm.getMBeanServer().getObjectName(
                    PROFILER_MXBEAN_NAME);
            if (objectName != null) {
                jvm.getMBeanServer().setAttribute(objectName,
                        new Attribute(RUNNING, false));
            }
        } else {
            jvm.getMBeanServer().suspendSampling();
        }
    }

    /*
     * @see ICpuProfiler#clear()
     */
    @Override
    public void clear() throws JvmCoreException {
        if (type == ProfilerType.BCI) {
            validateAgent();
            invokeCpuProfilerMXBeanMethod(CLEAR, null, null);
        }
        cpuModel.removeAll();

        cpuModel.notifyModelChanged(new CpuModelEvent(
                CpuModelState.CpuModelChanged));
    }

    /*
     * @see ICpuProfiler#dump()
     */
    @Override
    public IFileStore dump() throws JvmCoreException {
        String dump = cpuModel.getCpuDumpString(jvm.getPid() + "@" //$NON-NLS-1$
                + jvm.getHost().getName(), jvm.getMainClass(), jvm
                .getMBeanServer().getJvmArguments());

        StringBuffer fileName = new StringBuffer();
        fileName.append(new Date().getTime()).append('.')
                .append(SnapshotType.Cpu.getExtension());
        IFileStore fileStore = Util.getFileStore(fileName.toString(),
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
            os = fileStore.openOutputStream(EFS.NONE, null);
            os.write(dump.getBytes());

            Snapshot snapshot = new Snapshot(fileStore, abstractJvm);
            abstractJvm.addSnapshot(snapshot);

            JvmModel.getInstance().fireJvmModelChangeEvent(
                    new JvmModelEvent(State.ShapshotTaken, jvm, snapshot));
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
                    Messages.dumpCpuProfileDataFailedMsg, fileStore.toURI()
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
        return fileStore;
    }

    /*
     * @see ICpuProfiler#getCpuModel()
     */
    @Override
    public CpuModel getCpuModel() {
        return cpuModel;
    }

    /*
     * @see ICpuProfiler#refreshBciProfileCache(IProgressMonitor)
     */
    @Override
    public void refreshBciProfileCache(IProgressMonitor monitor)
            throws JvmCoreException {
        if (type != ProfilerType.BCI) {
            return;
        }

        validateAgent();

        if (!isBciProfilerRunning()) {
            return;
        }

        String dumpString = (String) invokeCpuProfilerMXBeanMethod(DUMP, null,
                null);
        if (dumpString == null) {
            return;
        }

        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(dumpString.getBytes());
            CpuDumpParser parser = new CpuDumpParser(input, cpuModel, monitor);
            parser.parse();
        } catch (ParserConfigurationException e) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.parseCpuDumpFailedMsg, e);
        } catch (SAXException e) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.parseCpuDumpFailedMsg, e);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.parseCpuDumpFailedMsg, e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    /*
     * @see ICpuProfiler#setProfiledPackages(Set<String>)
     */
    @Override
    public void setProfiledPackages(Set<String> packages)
            throws JvmCoreException {

        if (type == ProfilerType.BCI) {
            validateAgent();

            ProfilerState state = getState();
            if (state != ProfilerState.READY && state != ProfilerState.RUNNING) {
                return;
            }

            StringBuffer buffer = new StringBuffer();
            for (String item : packages) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                buffer.append(item);
            }

            invokeCpuProfilerMXBeanMethod(
                    SET_FILTER,
                    new String[] { PROFILED_PACKAGES_PROP_KEY,
                            buffer.toString() },
                    new String[] { String.class.getName(),
                            String.class.getName() });
        } else {
            this.profiledPackages = packages;
        }

        JvmModel.getInstance().fireJvmModelChangeEvent(
                new JvmModelEvent(State.CpuProfilerConfigChanged, jvm));
    }

    /*
     * @see ICpuProfiler#getProfiledPackages()
     */
    @Override
    public Set<String> getProfiledPackages() throws JvmCoreException {
        if (type == ProfilerType.BCI) {
            validateAgent();

            Set<String> packages = new LinkedHashSet<String>();
            ProfilerState state = getState();
            if (state != ProfilerState.READY && state != ProfilerState.RUNNING) {
                return packages;
            }

            ObjectName objectName = jvm.getMBeanServer().getObjectName(
                    PROFILER_MXBEAN_NAME);
            if (jvm.isConnected()) {
                for (String item : (String[]) jvm.getMBeanServer()
                        .getAttribute(objectName, PROFILED_PACKAGES)) {
                    if (!item.isEmpty()) {
                        packages.add(item);
                    }
                }
            }
            return packages;
        }
        return profiledPackages;
    }

    /*
     * @see ICpuProfiler#getState()
     */
    @Override
    public ProfilerState getState() {
        return getState(type);
    }

    /*
     * @see ICpuProfiler#getState(ProfilerType)
     */
    @Override
    public ProfilerState getState(ProfilerType typeToQuery) {
        if (typeToQuery == ProfilerType.SAMPLING) {
            MBeanServer server = jvm.getMBeanServer();
            if (server == null) {
                return ProfilerState.UNKNOWN;
            }
            return server.getProfilerState();
        }

        if (!jvm.isRemote()
                && !JvmModel.getInstance().getAgentLoadHandler()
                        .isAgentLoaded()) {
            return ProfilerState.AGENT_NOT_LOADED;
        }

        try {
            if (!isValidAgentVersion()) {
                return ProfilerState.INVALID_VERSION;
            }

            if (!isBciProfilerRunning()) {
                return ProfilerState.READY;
            }
        } catch (JvmCoreException e) {
            return ProfilerState.UNKNOWN;
        }

        return ProfilerState.RUNNING;
    }

    /*
     * @see ICpuProfiler#getSamplingPeriod()
     */
    @Override
    public Integer getSamplingPeriod() {
        return jvm.getMBeanServer().getSamplingPeriod();
    }

    /*
     * @see ICpuProfiler#setSamplingPeriod(int)
     */
    @Override
    public void setSamplingPeriod(Integer samplingPeriod) {
        jvm.getMBeanServer().setSamplingPeriod(samplingPeriod);
    }

    /**
     * Gets the state indicating if the version of loaded agent is valid.
     * 
     * @return True if the version of loaded agent is valid
     * @throws JvmCoreException
     */
    private boolean isValidAgentVersion() throws JvmCoreException {
        if (agentJarVersion == null) {
            ObjectName objectName = jvm.getMBeanServer().getObjectName(
                    PROFILER_MXBEAN_NAME);
            if (objectName != null) {
                Object value = jvm.getMBeanServer().getAttribute(objectName,
                        VERSION);
                if (value == null) {
                    return false;
                }
                agentJarVersion = String.valueOf(value);
            }
        }
        String bundleVersion = Activator.getDefault().getBundle().getVersion()
                .toString();
        return bundleVersion.startsWith(agentJarVersion);
    }

    /**
     * Gets the composite value of transform status corresponding to the given
     * composite key.
     * 
     * @param compositeKey
     *            The composite key
     * @return The composite value
     * @throws JvmCoreException
     */
    private int getTransformStatusCompositeValue(String compositeKey)
            throws JvmCoreException {
        ObjectName objectName = jvm.getMBeanServer().getObjectName(
                PROFILER_MXBEAN_NAME);
        Object attribute = jvm.getMBeanServer().getAttribute(objectName,
                TRANSFORM_STATUS);

        if (attribute instanceof CompositeData) {
            Object element = ((CompositeData) attribute).get(compositeKey);
            if (element instanceof Integer) {
                return (Integer) element;
            }
        }
        throw new IllegalStateException();
    }

    /**
     * Gets the state indicating if the BCI profiler is running.
     * 
     * @return True if the profiler is running
     * @throws JvmCoreException
     */
    private boolean isBciProfilerRunning() throws JvmCoreException {
        ObjectName objectName = jvm.getMBeanServer().getObjectName(
                PROFILER_MXBEAN_NAME);
        if (objectName != null) {
            Object attribute = jvm.getMBeanServer().getAttribute(objectName,
                    RUNNING);
            return attribute == null ? false : (Boolean) attribute;
        }
        return false;
    }

    /**
     * Validates the agent.
     * 
     * @throws JvmCoreException
     */
    private void validateAgent() throws JvmCoreException {
        if (!jvm.isRemote()
                && !JvmModel.getInstance().getAgentLoadHandler()
                        .isAgentLoaded()) {
            type = ProfilerType.SAMPLING;
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.agentNotLoadedMsg, new Exception());
        }
    }

    /**
     * Invokes the method of ProfilerMXBean.
     * 
     * @param method
     *            The method name
     * @param params
     *            The parameters of method
     * @param signatures
     *            The signatures of method
     * @return The return value of method, or <tt>null</tt> if not connected
     * @throws JvmCoreException
     */
    private Object invokeCpuProfilerMXBeanMethod(String method,
            String[] params, String[] signatures) throws JvmCoreException {
        ObjectName objectName = jvm.getMBeanServer().getObjectName(
                PROFILER_MXBEAN_NAME);
        return jvm.getMBeanServer().invoke(objectName, method, params,
                signatures);
    }
}
