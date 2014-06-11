/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean.messages";//$NON-NLS-1$

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
    public static String notificationTypeColumnLabel;

    /** */
    public static String notificationTypeColumnToolTip;

    /** */
    public static String dateColumnLabel;

    /** */
    public static String dateColumnToolTip;

    /** */
    public static String sequenceNumberColumnLabel;

    /** */
    public static String sequenceNumberColumnToolTip;

    /** */
    public static String messageColumnLabel;

    /** */
    public static String messageColumnToolTip;

    /** */
    public static String operationColumnLabel;

    /** */
    public static String operationColumnToolTip;

    // invoke dialog

    /** */
    public static String invokeButtonLabel;

    /** */
    public static String invokeDialogTitle;

    /** */
    public static String enterCommaSeparatedValuesToolTip;

    /** */
    public static String operationGroupLabel;

    /** */
    public static String parametersGroupLabel;

    /** */
    public static String returnValueLabel;

    /** */
    public static String mBeanOperationFailedLabel;

    /** */
    public static String mBeanOperationSucceededLabel;

    /** */
    public static String mBeanOperationFailedMsg;

    // notification details dialog

    /** */
    public static String dateLabel;

    /** */
    public static String sequenceNumberLabel;

    /** */
    public static String sourceLabel;

    /** */
    public static String typeLabel;

    /** */
    public static String messageLabel;

    /** */
    public static String prevButtonToolTip;

    /** */
    public static String nextButtonToolTip;

    // actions

    /** */
    public static String invokeLabel;

    /** */
    public static String subscribeLabel;

    /** */
    public static String clearLabel;

    /** */
    public static String notificationDetailsLabel;

    /** */
    public static String layoutLabel;

    // tab names

    /** */
    public static String attributesTabLabel;

    /** */
    public static String notificationsTabLabel;

    /** */
    public static String operationsTabLabel;

    // job names

    /** */
    public static String refreshAttributeTabJobLabel;

    /** */
    public static String setPropertyValueJobLabel;

    /** */
    public static String invokeMBeanOperationJobLabel;

    /** */
    public static String refreshMBeanSectionJobLabel;

    /** */
    public static String refreshNotificationTabJobLabel;

    /** */
    public static String refreshOperationsTabJobLabel;

    /** */
    public static String refreshSubscribeActionStateJobLabel;

    /** */
    public static String subscribeNotificationJobLabel;

    // instruction message

    /** */
    public static String mBeanNotSelectedMessage;

    /** */
    public static String notificationsNotSubscribedMsg;

    /** */
    public static String subscribeLinkLabel;

    /** */
    public static String notificationsLabel;

    // error log messages

    /** */
    public static String getMBeanInfoFailedMsg;

    /** */
    public static String getMBeanAttributeFailedMsg;

    /** */
    public static String setMBeanAttributeFailedMsg;

    /** */
    public static String getMBeanObjectNamesFailedMsg;

    /** */
    public static String subscribeNotificationFailedMsg;
}
