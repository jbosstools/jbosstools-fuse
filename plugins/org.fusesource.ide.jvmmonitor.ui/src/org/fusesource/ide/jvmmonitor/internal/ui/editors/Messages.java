/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.editors;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.editors.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // info page on editor

    /** */
    public static String runtimeSectionLabel;

    /** */
    public static String hostnameLabel;

    /** */
    public static String pidLabel;

    /** */
    public static String mainClassLabel;

    /** */
    public static String argumentsLabel;

    /** */
    public static String snapshotSectionLabel;

    /** */
    public static String dateLabel;

    /** */
    public static String commentsLabel;

    // dump editor

    /** */
    public static String infoTabLabel;

    // heap dump editor

    /** */
    public static String memoryTabLabel;

    /** */
    public static String parseHeapDumpFileJobLabel;

    // thread dump editor

    /** */
    public static String threadsTabLabel;

    // CPU dump editor

    /** */
    public static String callTreePageLabel;

    /** */
    public static String hotSpotsPageLabel;

    /** */
    public static String callerCalleePageLabel;

    /** */
    public static String parseCpuDumpFileJobLabel;

    /** */
    public static String noCallersCalleesMessage;

    /** */
    public static String callersCalleesTargetIndicator;

    /** */
    public static String focusTargetIndicator;

    /** */
    public static String threadIndicator;

    // error log messages

    /** */
    public static String saveFileFailedMsg;

    /** */
    public static String parseThreadDumpFileJobLabel;
}
