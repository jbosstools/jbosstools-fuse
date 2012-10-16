/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.core.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // error log messages

    /** */
    public static String deleteFileFailedMsg;

    /** */
    public static String createDirectoryFailedMsg;

    /** */
    public static String openOutputStreamFailedMsg;

    /** */
    public static String writePropertiesFileFailedMsg;

    /** */
    public static String getJmxServiceUrlForPidFailedMsg;

    /** */
    public static String getJmxServiceUrlForPortFailedMsg;

    /** */
    public static String getJmxServiceUrlForUrlFailedMsg;

    /** */
    public static String attachNotSupportedMsg;

    /** */
    public static String getPidFailedMsg;

    /** */
    public static String getHostNameFailedMsg;

    /** */
    public static String savePropertiesFileFailedMsg;

    /** */
    public static String dumpCpuProfileDataFailedMsg;

    /** */
    public static String parseCpuDumpFailedMsg;

    /** */
    public static String agentNotLoadedMsg;

    /** */
    public static String removeJvmFailedMsg;

    /** */
    public static String queryObjectNameFailedMsg;

    /** */
    public static String getAttributeFailedMsg;

    /** */
    public static String setAttributeFailedMsg;

    /** */
    public static String getMBeanInfoFailedMsg;

    /** */
    public static String getMBeanFailedMsg;

    /** */
    public static String mBeanOperationFailedMsg;

    /** */
    public static String dumpFailedMsg;

    /** */
    public static String subscribeMBeanNotificationFailedMsg;

    /** */
    public static String unsubscribeMBeanNotificationFailedMsg;

    /** */
    public static String getObjectNameFailedMsg;

    /** */
    public static String connectToMBeanServerFailedMsg;

    /** */
    public static String readFileFailedMsg;

    /** */
    public static String renameFileFailedMsg;

    /** */
    public static String jvmNotReachableMsg;

    // job names
    
    /** */
    public static String transformClassesTask;
}
