/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.util.HashSet;
import java.util.Set;

import javax.management.ObjectName;

import org.fusesource.ide.jvmmonitor.core.IActiveJvm;


/**
 * The MBean type.
 */
public class MBeanType {

    /** The MBean type. */
    private String mBeanType;

    /** The MBean names. */
    private Set<MBeanName> mBeanNames;

    /** The MBean domain. */
    private MBeanDomain mBeanDomain;

    /** The JVM. */
    private IActiveJvm jvm;

    /**
     * The constructor.
     * 
     * @param jvm
     *            The active JVM
     * @param type
     *            The MBean type
     * @param mBeanDomain
     *            The MBean domain
     */
    public MBeanType(IActiveJvm jvm, String type, MBeanDomain mBeanDomain) {
        this.jvm = jvm;
        this.mBeanType = type;
        this.mBeanDomain = mBeanDomain;
        mBeanNames = new HashSet<MBeanName>();
    }

    /**
     * Gets the MBean domain.
     * 
     * @return The MBean domain
     */
    public MBeanDomain getMBeanDomain() {
        return mBeanDomain;
    }

    /**
     * Gets the MBean names.
     * 
     * @return The MBean names
     */
    public MBeanName[] getMBeanNames() {
        return mBeanNames.toArray(new MBeanName[0]);
    }

    /**
     * Adds the MBean name.
     * 
     * @param mBeanName
     *            The MBean name
     */
    public void addMBeanName(MBeanName mBeanName) {
        mBeanNames.add(mBeanName);
    }

    /**
     * Gets the corresponding MBean name to the given object name.
     * 
     * @param objectName
     *            The object name
     * @return The MBean name
     */
    public MBeanName getMBeanName(ObjectName objectName) {
        for (MBeanName mBean : mBeanNames) {
            if (mBean.getObjectName().getCanonicalName()
                    .equals(objectName.getCanonicalName())) {
                return mBean;
            }
        }
        return null;
    }

    /**
     * Gets the type name.
     * 
     * @return The type name
     */
    protected String getName() {
        return mBeanType;
    }

    /**
     * Gets the JVM.
     * 
     * @return the JVM
     */
    protected IActiveJvm getJvm() {
        return jvm;
    }

    /**
     * Sets the JVM.
     * 
     * @param jvm
     *            The JVM
     */
    protected void setJvm(IActiveJvm jvm) {
        this.jvm = jvm;
    }
}