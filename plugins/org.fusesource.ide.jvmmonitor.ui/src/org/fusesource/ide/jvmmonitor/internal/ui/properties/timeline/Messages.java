/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline.messages";//$NON-NLS-1$

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
    public static String objectNameColumnLabel;

    /** */
    public static String attributeColumnLabel;

    /** */
    public static String objectNameColumnToolTip;

    /** */
    public static String attributeColumnToolTip;

    // confirm delete dialog

    /** */
    public static String confirmDeleteChartTitle;

    /** */
    public static String confirmDeleteChartMsg;

    // new chart dialog

    /** */
    public static String newChartDialogTitle;

    /** */
    public static String chartTitleEmptyMsg;

    // configure chart dialog

    /** */
    public static String configureChartDialogTitle;

    /** */
    public static String attributeSelectionDialogTitle;

    /** */
    public static String attributesToAddOnChartLabel;

    /** */
    public static String colorLabel;

    /** */
    public static String chartTitleDuplicatedMsg;

    /** */
    public static String chartTitleLabel;

    /** */
    public static String yAxisUnitLabel;

    /** */
    public static String monitoredAttributesLabel;

    /** */
    public static String addButtonLabel;

    /** */
    public static String removeButtonLabel;

    // save chart set as dialog

    /** */
    public static String saveChartSetAsDialogTitle;

    /** */
    public static String enterOrSelectChartSetLabel;

    /** */
    public static String chartSetLabel;

    /** */
    public static String existingChartSetsLabel;

    /** */
    public static String deleteButtonLabel;

    /** */
    public static String illiegalChartSetMsg;

    // load chart set dialog

    /** */
    public static String loadChartSetDialogTitle;

    /** */
    public static String selectChartSetLabel;

    /** */
    public static String makeDefaultButtonLabel;

    /** */
    public static String defaultChartSet;

    /** */
    public static String errorDialogTitle;

    /** */
    public static String attributeNotSupportedMsg;

    // actions

    /** */
    public static String clearTimelineDataLabel;

    /** */
    public static String configureChartLabel;

    /** */
    public static String newChartLabel;

    /** */
    public static String deleteChartLabel;

    /** */
    public static String saveChartSetAsLabel;

    /** */
    public static String loadChartSetLabel;

    /** */
    public static String loadDefaultChartSetLabel;

    // tooltip

    /** */
    public static String timeLabel;

    // job name

    /** */
    public static String refreshChartJobLabel;

    /** */
    public static String reconstructChartJobLabel;

    // error log messages

    /** */
    public static String configureMonitoredAttributesFailedMsg;

    /** */
    public static String getMBeanNamesFailedMsg;

    /** */
    public static String getMBeanInfoFailedMsg;

    /** */
    public static String getMBeanAttributeFailedMsg;

    /** */
    public static String addAttributeFailedMsg;

    /** */
    public static String loadChartSetFailedMsg;

    /** */
    public static String saveChartSetFailedMsg;

    /** */
    public static String openSaveChartSetAsDialogFailedMsg;

    /** */
    public static String getMemoryPoolAttributeFailedMsg;
}