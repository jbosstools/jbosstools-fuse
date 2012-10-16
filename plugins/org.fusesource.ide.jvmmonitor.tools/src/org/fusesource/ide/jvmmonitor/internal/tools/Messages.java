/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.tools;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.tools.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // tools preference page

    /** */
    public static String toolsPreferencePageLabel;

    /** */
    public static String jdkRootDirectoryLabel;

    /** */
    public static String browseButton;

    /** */
    public static String autoDetectGroupLabel;

    /** */
    public static String updatePeriodLabel;

    /** */
    public static String memoryGroupLabel;

    /** */
    public static String enterMaxNumberOfClassesMsg;

    /** */
    public static String maxNumberOfClassesInvalidMsg;

    /** */
    public static String maxNumberOfClassesOutOfRangeMsg;

    /** */
    public static String maxNumberOfClassesLabel;

    // error log messages

    /** */
    public static String addingClassPathFailedMsg;

    /** */
    public static String addingLibraryPathFailedMsg;

    /** */
    public static String classPathAddedMsg;

    /** */
    public static String jdkRootDirectoryNotFoundMsg;

    /** */
    public static String jdkRootDirectoryFoundMsg;

    /** */
    public static String jdkRootDirectoryNotEnteredMsg;

    /** */
    public static String notSupportedMsg;

    /** */
    public static String selectJdkRootDirectoryMsg;

    /** */
    public static String updatePeriodNotEnteredMsg;

    /** */
    public static String illegalUpdatePeriodMsg;

    /** */
    public static String updatePeriodOutOfRangeMsg;

    /** */
    public static String directoryNotExistMsg;

    /** */
    public static String notJdkRootDirectoryMsg;

    /** */
    public static String loadAgentFailedMsg;

    /** */
    public static String agentJarNotFoundMsg;

    /** */
    public static String agentJarFoundMsg;

    /** */
    public static String corePluginNoFoundMsg;

    /** */
    public static String charsetNotSupportedMsg;

    /** */
    public static String readInputStreamFailedMsg;

    /** */
    public static String updateTimerCanceledMsg;

    /** */
    public static String getMonitoredJvmFailedMsg;

    /** */
    public static String getLocalConnectorAddressFailedMsg;

    /** */
    public static String connectTargetJvmFailedMsg;

    /** */
    public static String getMainClassNameFailed;

    /** */
    public static String fileNotFoundMsg;
}
