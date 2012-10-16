/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.thread;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.properties.thread.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // thread column

    /** */
    public static String threadColumnLabel;

    /** */
    public static String stateColumnLabel;

    /** */
    public static String cpuColumnLabel;

    /** */
    public static String blockedTimeColumnLabel;

    /** */
    public static String blockedCountColumnLabel;

    /** */
    public static String waitedTimeColumnLabel;

    /** */
    public static String waitedCountColumnLabel;

    /** */
    public static String lockColumnLabel;

    /** */
    public static String lockOwnerColumnLabel;

    /** */
    public static String threadColumnToolTip;

    /** */
    public static String stateColumnToolTip;

    /** */
    public static String cpuColumnToolTip;

    /** */
    public static String blockedCountColumnToolTip;

    /** */
    public static String blockedTimeColumnToolTip;

    /** */
    public static String waitedCountColumnToolTip;

    /** */
    public static String waitedTimeColumnToolTip;

    /** */
    public static String lockColumnToolTip;

    /** */
    public static String lockOwnerColumnToolTip;

    // actions

    /** */
    public static String dumpThreadsLabel;

    /** */
    public static String layoutLabel;

    // job names

    /** */
    public static String dumpThreadsJobLabel;

    /** */
    public static String refreshThreadsSectionJobLabel;

    // error log message

    /** */
    public static String dumpThreadsFailedMsg;
}