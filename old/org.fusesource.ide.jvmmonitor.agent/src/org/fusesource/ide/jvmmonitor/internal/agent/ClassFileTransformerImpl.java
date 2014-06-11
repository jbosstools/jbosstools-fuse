/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;

import org.fusesource.ide.jvmmonitor.internal.agent.asm.ClassAdapter;
import org.fusesource.ide.jvmmonitor.internal.agent.asm.ClassReader;
import org.fusesource.ide.jvmmonitor.internal.agent.asm.ClassWriter;


/**
 * The class file transformer to instrument byte-codes.
 */
@SuppressWarnings("nls")
public class ClassFileTransformerImpl implements ClassFileTransformer {

    /** The target classes to transform. */
    private Set<Class<?>> targetClasses;

    /** The transformed classes. */
    private Set<Class<?>> transformedClasses;

    /**
     * The constructor.
     * 
     * @param targetClasses
     *            The target classes to transform
     * @param transformedClasses
     *            The transformed classes
     */
    public ClassFileTransformerImpl(Set<Class<?>> targetClasses,
            Set<Class<?>> transformedClasses) {
        this.targetClasses = targetClasses;
        this.transformedClasses = transformedClasses;
    }

    /*
     * @see ClassFileTransformer#transform(ClassLoader, String, Class,
     * ProtectionDomain, byte[])
     */
    @Override
    public byte[] transform(ClassLoader loader, String className,
            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!isProfiledClassLoader(loader)
                || !matches(className, Config.getInstance().profiledPackages)
                || matches(className, Config.getInstance().ignoredPackages)) {
            return classfileBuffer;
        }

        /*
         * Classes could be loaded after collecting targetClasses in
         * CpuBciProfilerMXBeanImpl.setFilter(String, String)
         */
        targetClasses.add(classBeingRedefined);
        transformedClasses.add(classBeingRedefined);
        Agent.logInfo(Messages.INSTRUMENTED_CLASS, className);

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassAdapter visitor = new ClassVisitorImpl(writer, className);
        reader.accept(visitor, ClassReader.SKIP_DEBUG);

        return writer.toByteArray();
    }

    /**
     * Checks if the given class name matches with one of the packages list.
     * 
     * @param className
     *            The class name (e.g. java/lang/String)
     * @param packages
     *            The packages
     * @return <tt>true</tt> if the given class belongs to one of the packages
     */
    private boolean matches(String className, Set<String> packages) {
        if (packages.isEmpty()) {
            return false;
        }

        String packageName;
        if (className.contains("/")) {
            packageName = className.substring(0, className.lastIndexOf('/'))
                    .replace('/', '.');
        } else if (className.startsWith("$")) {
            return false; // e.g. $Proxy0
        } else {
            packageName = Constants.DEFAULT_PACKAGE;
        }

        for (String pkg : packages) {
            if (pkg.endsWith("*")) {
                if (packageName.concat(".").startsWith(
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
     * Gets the state indicating if the classes loaded by given class loader are
     * profiled.
     * 
     * @param loader
     *            The class loader
     * @return <tt>true</tt> if the classes loaded by given class loader are
     *         profiled
     */
    private boolean isProfiledClassLoader(ClassLoader loader) {
        if (Config.getInstance().profiledClassLoaders.isEmpty()) {
            return true;
        }

        for (String classLoader : Config.getInstance().profiledClassLoaders) {
            if (loader != null
                    && loader.getClass().getName().equals(classLoader)) {
                return true;
            }
        }
        return false;
    }
}
