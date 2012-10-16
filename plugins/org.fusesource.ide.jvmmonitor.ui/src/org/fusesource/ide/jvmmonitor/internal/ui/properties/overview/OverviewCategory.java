/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.overview;

/**
 * The overview category.
 */
public enum OverviewCategory {

    /** The runtime. */
    Runtime(Messages.runtimeCategoryLabel),

    /** The memory. */
    Memory(Messages.memoryCategoryLabel),

    /** The thread. */
    Threading(Messages.threadCategoryLabel),

    /** The class loading. */
    ClassLoading(Messages.classLoadingCategoryLabel),

    /** The compilation. */
    Compilation(Messages.compilationCategoryLabel),

    /** The garbage collector. */
    GarbageCollector(Messages.garbageCollectorCategoryLabel),

    /** The operating system. */
    OperatingSystem(Messages.operatingSystemCategoryLabel);

    /** The display name. */
    public final String displayName;

    /**
     * The constructor.
     * 
     * @param displayName
     *            The display name
     */
    private OverviewCategory(String displayName) {
        this.displayName = displayName;
    }
}
