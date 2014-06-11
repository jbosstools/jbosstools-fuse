/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.views.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // new JVM connection wizard

    /** */
    public static String newJvmConnectionPageTitle;

    /** */
    public static String newJvmConnectionTitle;

    /** */
    public static String createNewJvmConnectionMsg;

    /** */
    public static String connectionGroupLabel;

    /** */
    public static String remoteHostTextLabel;

    /** */
    public static String portTextLabel;

    /** */
    public static String jmxUrlTextLabel;

    /** */
    public static String authenticateGroupLabel;

    /** */
    public static String userNameTextLabel;

    /** */
    public static String passwordTextLabel;

    /** */
    public static String emptyRemoteHostNameMsg;

    /** */
    public static String emptyPortMsg;

    /** */
    public static String invalidPortMsg;

    /** */
    public static String emptyJmxUrlMsg;

    /** */
    public static String invalidJmxUrlHeaderMsg;

    /** */
    public static String invalidJmxUrlMsg;

    /** */
    public static String connectWithHostAndPort;

    /** */
    public static String connectWithJmxUrl;

    /** */
    public static String determineIpAddressFailedMsg;

    /** */
    public static String connectionTimedOutMsg;

    /** */
    public static String connectionFailedMsg;

    // rename dialog

    /** */
    public static String renameTitle;

    /** */
    public static String newNameLabel;

    /** */
    public static String fileAlreadyExistsMsg;

    /** */
    public static String fileContainsInvalidCharactersMsg;

    /** */
    public static String errorDialogTitle;

    /** */
    public static String renameFailedMsg;

    // confirm delete dialog

    /** */
    public static String confirmDeleteTitle;

    /** */
    public static String confirmDeleteElementsMsg;

    /** */
    public static String confirmDeleteElementMsg;

    /** */
    public static String confirmDeleteSelectedElementMsg;

    // actions

    /** */
    public static String newJvmConnectionLabel;

    /** */
    public static String startMonitoringLabel;

    /** */
    public static String stopMonitoringLabel;

    /** */
    public static String renameLabel;

    /** */
    public static String deleteLabel;

    /** */
    public static String openSnapshotLabel;

    // status line message

    /** */
    public static String invalidJdkLocationMsg;

    /** */
    public static String connectedMsg;

    /** */
    public static String disconnectedMsg;

    /** */
    public static String cpuProfilerRunningMsg;

    // job names

    /** */
    public static String initializeJvmExplorer;

    /** */
    public static String refreshStatusLineJobLabel;

    /** */
    public static String startMonitoringJobLabel;

    /** */
    public static String stopMonitoringJobLabel;

    // error log messages

    /** */
    public static String accessFileFailedMsg;

    /** */
    public static String connectJvmFailedMsg;

    /** */
    public static String bringPropertiesViewToFrontFailedMsg;
}
