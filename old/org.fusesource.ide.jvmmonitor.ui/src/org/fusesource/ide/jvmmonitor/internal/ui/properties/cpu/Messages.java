/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // columns

    /** */
    public static String callTreeColumnLabel;

    /** */
    public static String hotSpotColumnLabel;

    /** */
    public static String callerColumnLabel;

    /** */
    public static String calleeColumnLabel;

    /** */
    public static String timeInMsLabel;

    /** */
    public static String timeInPercentageLabel;

    /** */
    public static String selfTimeInMsLabel;

    /** */
    public static String selfTimeInPercentageLabel;

    /** */
    public static String countLabel;

    /** */
    public static String callTreeColumnToolTip;

    /** */
    public static String hotSpotColumnToolTip;

    /** */
    public static String callerColumnToolTip;

    /** */
    public static String calleeColumnToolTip;

    /** */
    public static String timeInMsToolTip;

    /** */
    public static String timeInPercentageToolTip;

    /** */
    public static String selfTimeInMsToolTip;

    /** */
    public static String selfTimeInPercentageToolTip;

    /** */
    public static String countToolTip;

    /** */
    public static String percentageLabel;

    /** */
    public static String millisecondsLabel;

    /** */
    public static String threadLabel;

    // instruction messages

    /** */
    public static String noCallersCalleesMessage;

    /** */
    public static String patckagesNotSpecifiedMsg;

    /** */
    public static String selectPackagesMsg;

    // content description

    /** */
    public static String callersCalleesTargetIndicator;

    /** */
    public static String focusTargetIndicator;

    /** */
    public static String threadIndicator;

    // tab names

    /** */
    public static String callersCalleesTabLabel;

    /** */
    public static String callTreeTabLabel;

    /** */
    public static String hotSpotsTabLabel;

    // job names

    /** */
    public static String refeshCpuSectionJobLabel;

    // error log messages

    /** */
    public static String refreshCpuProfileDataFailedMsg;

    /** */
    public static String setProfiledPackagesFailedMsg;

    /** */
    public static String getProfiledPackagesFailedMsg;

    /** */
    public static String clearCpuProfileDataFailedMsg;
}
