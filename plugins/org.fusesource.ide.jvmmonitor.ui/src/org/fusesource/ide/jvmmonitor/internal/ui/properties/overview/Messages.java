/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.overview;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.properties.overview.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // units

    /** */
    public static String daysLabel;

    /** */
    public static String hoursLabel;

    /** */
    public static String minLabel;

    /** */
    public static String secLabel;

    /** */
    public static String kBytesLabel;

    // categories

    /** */
    public static String runtimeCategoryLabel;

    /** */
    public static String memoryCategoryLabel;

    /** */
    public static String threadCategoryLabel;

    /** */
    public static String classLoadingCategoryLabel;

    /** */
    public static String compilationCategoryLabel;

    /** */
    public static String garbageCollectorCategoryLabel;

    /** */
    public static String operatingSystemCategoryLabel;

    // attributes

    /** */
    public static String runtimeNameLabel;

    /** */
    public static String inputArtumentsLabel;

    /** */
    public static String systemPropertiesLabel;

    /** */
    public static String uptimeLabel;

    /** */
    public static String classPathLabel;

    /** */
    public static String libraryPathLabel;

    /** */
    public static String bootClassPathLabel;

    /** */
    public static String vmVendorLabel;

    /** */
    public static String vmNameLabel;

    /** */
    public static String vmVersionLabel;

    /** */
    public static String heapMemoryUsageLabel;

    /** */
    public static String maxHeapHemoryLabel;

    /** */
    public static String committedHeapMemoryLabel;

    /** */
    public static String usedNonHeapMemoryLabel;

    /** */
    public static String maxNonHeapMemoryLabel;

    /** */
    public static String committedNonHeapMemoryLabel;

    /** */
    public static String objectPendingFinalizationCountLabel;

    /** */
    public static String totalStartedThreadCountLabel;

    /** */
    public static String threadCountLabel;

    /** */
    public static String peakThreadCountLabel;

    /** */
    public static String daemonThreadCountLabel;

    /** */
    public static String totalLoadedClassCountLabel;

    /** */
    public static String loadedClassCountLabel;

    /** */
    public static String unloadedClassCountLabel;

    /** */
    public static String totalCompilationTimeLabel;

    /** */
    public static String compilerNameLabel;

    /** */
    public static String collectionCountLabel;

    /** */
    public static String collectionTimeLabel;

    /** */
    public static String totalPhysicalMemorySizeLabel;

    /** */
    public static String freePhysicalMemorySizeLabel;

    /** */
    public static String totalSwapMemorySizeLabel;

    /** */
    public static String freeSwapMemorySizeLabel;

    /** */
    public static String committedVirtualMemorySizeLabel;

    /** */
    public static String processCpuTimeLabel;

    /** */
    public static String operationSystemNameLabel;

    /** */
    public static String operationSystemVersionLabel;

    /** */
    public static String operationSystemArchitectureLabel;

    /** */
    public static String avaliableProcessorsLabel;

    // job name

    /** */
    public static String refreshOverviewSectionJobLabel;

    // error log messages

    /** */
    public static String getObjectNameFailedMsg;

    /** */
    public static String getMBeanAttributeFailedMsg;
}
