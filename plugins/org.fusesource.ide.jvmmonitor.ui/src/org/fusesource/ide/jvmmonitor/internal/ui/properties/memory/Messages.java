/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.properties.memory.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // heap column

    /** */
    public static String classColumnLabel;

    /** */
    public static String classColumnToolTip;

    /** */
    public static String sizeColumnLabel;

    /** */
    public static String sizeColumnToolTip;

    /** */
    public static String countColumnLabel;

    /** */
    public static String countColumnToolTip;

    /** */
    public static String deltaColumnLabel;

    /** */
    public static String deltaColumnToolTip;

    /** */
    public static String heapHistogramLabel;

    // SWT resources

    /** */
    public static String swtResourcesLabel;

    /** */
    public static String nameColumnLabel;

    /** */
    public static String nameColumnToolTip;

    /** */
    public static String clearResourcesLabel;

    /** */
    public static String clearResourcesJobLabel;

    /** */
    public static String layoutLabel;

    // dump hprof dialog

    /** */
    public static String dumpHprofTitle;

    /** */
    public static String hprofFileLabel;

    /** */
    public static String pathContainsInvalidCharactersMsg;

    /** */
    public static String transferHprofFileLabel;

    /** */
    public static String transferingHprofFileNotSupportedMsg;

    /** */
    public static String fileNameEmptyMsg;

    // instruction message

    /** */
    public static String notSupportedOnRemoteHostMsg;

    /** */
    public static String notSupportedForEclipseItselfOn64bitOS;

    // actions

    /** */
    public static String dumpHprofLabel;

    /** */
    public static String dumpHeapLabel;

    /** */
    public static String garbageCollectorLabel;

    /** */
    public static String clearDeltaLabel;

    // job names

    /** */
    public static String clearHeapDeltaJobLabel;

    /** */
    public static String dumpHprofDataJobLabel;

    /** */
    public static String dumpHeapDataJobLabel;

    /** */
    public static String runGarbageCollectorJobLabel;

    /** */
    public static String refreshMemorySectionJobLabel;

    // error log messages

    /** */
    public static String dumpHeapDataFailedMsg;

    /** */
    public static String runGarbageCollectorFailedMsg;

    /** */
    public static String refreshHeapDataFailedMsg;

    /** */
    public static String clearSWTResoucesFailedMsg;
}
