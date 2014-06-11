/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashSet;
import java.util.Set;

/**
 * The profiler controller.
 */
@SuppressWarnings("nls")
public class CpuBciProfilerMXBeanImpl implements CpuBciProfilerMXBean {

    /** The instrumentation. */
    Instrumentation inst;

    /** The class file transformer. */
    ClassFileTransformerImpl classFileTransformer;

    /** The target classes to transform. */
    private Set<Class<?>> targetClasses;

    /** The transformed classes. */
    private Set<Class<?>> transformedClasses;

    /** The state indicating if transformation has to be interrupted. */
    private boolean interrupted;

    /**
     * The constructor.
     * 
     * @param inst
     *            The instrumentation
     * @throws IOException
     */
    public CpuBciProfilerMXBeanImpl(Instrumentation inst) throws IOException {
        this.inst = inst;
        transformedClasses = new HashSet<Class<?>>();
        targetClasses = new HashSet<Class<?>>();
        classFileTransformer = new ClassFileTransformerImpl(transformedClasses,
                targetClasses);
        interrupted = false;

        CpuBciProfiler.initialize();
        if (Config.getInstance().isProfilerEnabled()) {
            setRunning(true);
        }
    }

    /*
     * @see CpuBciProfilerMXBean#transformClasses()
     */
    @Override
    public void transformClasses() {
        new Thread() {
            public void run() {
                try {
                    inst.addTransformer(classFileTransformer, true);
                    retransformClasses();
                    inst.removeTransformer(classFileTransformer);
                } catch (Throwable t) {
                    Agent.logError(t, Messages.CANNOT_TRANSFORM_CLASSES);
                }
            }
        }.start();
    }

    /*
     * @see CpuBciProfilerMXBean#getTransformStatus()
     */
    @Override
    public TransformStatusCompositeData getTransformStatus() {
        return new TransformStatusCompositeData(targetClasses.size(),
                transformedClasses.size());
    }

    /*
     * @see CpuBciProfilerMXBean#interruptTransform()
     */
    @Override
    public void interruptTransform() {
        interrupted = true;
    }

    /*
     * @see CpuBciProfilerMXBean#setRunning(boolean)
     */
    @Override
    public void setRunning(boolean run) {
        if (run) {
            inst.addTransformer(classFileTransformer, true);
            Config.getInstance().setProfilerEnabled(true);
        } else {
            try {
                inst.removeTransformer(classFileTransformer);
                Config.getInstance().setProfilerEnabled(false);
            } catch (Throwable t) {
                Agent.logError(t, Messages.CANNOT_SUSPEND);
            }
        }
    }

    /*
     * @see ProfilerMXBean#isRunning()
     */
    @Override
    public boolean isRunning() {
        try {
            return Config.getInstance().isProfilerEnabled();
        } catch (Throwable t) {
            Agent.logError(t, Messages.CANNOT_GET_RUNNING_STATE);
            return false;
        }
    }

    /*
     * @see ProfilerMXBean#clear()
     */
    @Override
    public void clear() {
        try {
            CpuBciProfiler.getModel().clear();
        } catch (Throwable t) {
            Agent.logError(t, Messages.CANNOT_CLEAR);
        }
    }

    /*
     * @see ProfilerMXBean#dump()
     */
    @Override
    public String dump() {
        try {
            return CpuBciProfiler.getModel().dump();
        } catch (Throwable t) {
            Agent.logError(t, Messages.CANNOT_GET_DUMP);
            return "";
        }
    }

    /*
     * @see ProfilerMXBean#dumpToFile()
     */
    @Override
    public void dumpToFile() {
        try {
            CpuBciProfiler.getModel().dumpToFile();
        } catch (Throwable t) {
            Agent.logError(t, Messages.CANNOT_DUMP_TO_FILE);
        }
    }

    /*
     * @see CpuBciProfilerMXBean#getDumpDir()
     */
    @Override
    public String getDumpDir() {
        return Config.getInstance().getDumpDir();
    }

    /*
     * @see CpuBciProfilerMXBean#setDumpDir(String)
     */
    @Override
    public void setDumpDir(String dir) {
        Config.getInstance().setDumpDir(dir);
    }

    /*
     * @see ProfilerMXBean#getVersion()
     */
    @Override
    public String getVersion() {
        return Constants.VERSION;
    }

    /*
     * @see CpuBciProfilerMXBean#setFilter(String, String)
     */
    @Override
    public void setFilter(String key, String value) {
        if (Constants.PROFILED_PACKAGES_PROP_KEY.equals(key)) {
            Config.getInstance().profiledPackages.clear();
            Config.getInstance().addElements(
                    Config.getInstance().profiledPackages, value);

            // store target classes
            targetClasses.clear();
            for (Class<?> clazz : inst.getAllLoadedClasses()) {
                String className = clazz.getName();
                if (!className.startsWith("[")
                        && matchs(className,
                                Config.getInstance().profiledPackages)) {
                    targetClasses.add(clazz);
                }
            }
        }
    }

    /*
     * @see CpuBciProfilerMXBean#getProfiledClassloaders()
     */
    @Override
    public String[] getProfiledClassloaders() {
        Set<String> list = Config.getInstance().profiledClassLoaders;
        return list.toArray(new String[list.size()]);
    }

    /*
     * @see CpuBciProfilerMXBean#getProfiledPackages()
     */
    @Override
    public String[] getProfiledPackages() {
        Set<String> list = Config.getInstance().profiledPackages;
        return list.toArray(new String[list.size()]);
    }

    /*
     * @see CpuBciProfilerMXBean#getIgnoredPackages()
     */
    @Override
    public String[] getIgnoredPackages() {
        Set<String> list = Config.getInstance().ignoredPackages;
        return list.toArray(new String[list.size()]);
    }

    /**
     * Re-transforms the loaded classes.
     */
    void retransformClasses() {
        interrupted = false;

        Set<Class<?>> transformedTargetClasses = new HashSet<Class<?>>(
                transformedClasses);
        transformedTargetClasses.retainAll(targetClasses);
        Set<Class<?>> classesToTransform = getClassesToTransform(transformedTargetClasses);
        transformedClasses.clear();
        transformedClasses.addAll(transformedTargetClasses);

        // transform classes
        for (Class<?> clazz : classesToTransform) {
            if (interrupted) {
                transformedClasses.retainAll(targetClasses);
                return;
            }

            try {
                inst.retransformClasses(clazz);
                Agent.logInfo(Messages.RETRANSFORMED_CLASS, clazz);
            } catch (UnmodifiableClassException e) {
                Agent.logError(e, Messages.CANNOT_RETRANSFORM_CLASS);
            } catch (InternalError e) {
                // do nothing
            }
        }

        transformedClasses.clear();
        transformedClasses.addAll(targetClasses);
    }

    /**
     * Gets the classes to transform.
     * 
     * @param transformedTargetClasses
     *            The transformed target classes
     * @return The classes to transform
     */
    private Set<Class<?>> getClassesToTransform(
            Set<Class<?>> transformedTargetClasses) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.addAll(targetClasses);
        classes.addAll(transformedClasses);
        classes.removeAll(transformedTargetClasses);
        return classes;
    }

    /**
     * Checks if the given class belongs to one of the packages list.
     * 
     * @param className
     *            the class name (e.g. java.lang.String)
     * @param packages
     *            the list of packages
     * @return true if the given class belongs to one of the packages list
     */
    private boolean matchs(String className, Set<String> packages) {
        if (packages.isEmpty()) {
            return false;
        }

        String packageName;
        if (className.contains(".")) {
            packageName = className.substring(0, className.lastIndexOf('.'));
        } else {
            packageName = Constants.DEFAULT_PACKAGE;
        }
        for (String pkg : packages) {
            if (pkg.endsWith("*")) {
                if (packageName.startsWith(pkg.substring(0, pkg.length() - 2))) {
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
}
