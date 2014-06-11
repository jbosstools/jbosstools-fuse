/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // find dialog

    /** */
    public static String findTitle;

    /** */
    public static String findTextLabel;

    /** */
    public static String directionLabel;

    /** */
    public static String forwardLabel;

    /** */
    public static String backwardLabel;

    /** */
    public static String findButtonLabel;

    // configuration dialog

    /** */
    public static String configureCpuProfilerTitle;

    /** */
    public static String addLabel;

    /** */
    public static String removeLabel;

    /** */
    public static String profiledPackagesLabel;

    /** */
    public static String profilerTypeGroupLabel;

    /** */
    public static String samplingButtonLabel;

    /** */
    public static String bciButtonLabel;

    /** */
    public static String samplingPeriodLabel;

    /** */
    public static String invalidVersionMsg;

    /** */
    public static String agentNotLoadedMsg;

    // add package dialog

    /** */
    public static String addPackageDialogTitle;

    /** */
    public static String selectPackagesMessage;

    /** */
    public static String enterPackageName;

    /** */
    public static String packageNameLabel;

    // actions

    /** */
    public static String findLabel;

    /** */
    public static String showCallersCalleesLabel;

    /** */
    public static String focusOnLabel;

    /** */
    public static String clearCpuProfilingDataLabel;

    /** */
    public static String configureCpuProfilerLabel;

    /** */
    public static String dumpCpuLabel;

    /** */
    public static String resumeCpuProfilingLabel;

    /** */
    public static String suspendCpuProfilingLabel;

    // job names

    /** */
    public static String getProfiledPackagesJobLabel;

    /** */
    public static String openDialogJobLabel;

    /** */
    public static String configureProfilerJobLabel;

    /** */
    public static String dumpCpuProfileDataJobLabel;

    /** */
    public static String resumeCpuProfilingJob;

    /** */
    public static String suspendCpuProfilingJobLabel;

    // error log messages

    /** */
    public static String getProfiledPackagesFailedMsg;

    /** */
    public static String dumpCpuProfileDataFailedMsg;

    /** */
    public static String getJavaModelFailedMsg;

    /** */
    public static String resumeCpuProfilingFailedMsg;

    /** */
    public static String suspendingCpuProfilingFailedMsg;

    /** */
    public static String setProfiledPackagesFailedMsg;
}