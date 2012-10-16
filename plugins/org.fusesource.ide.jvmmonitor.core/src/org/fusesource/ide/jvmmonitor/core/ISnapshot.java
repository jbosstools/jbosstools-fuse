/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

import org.eclipse.core.filesystem.IFileStore;

/**
 * The snapshot that can be one of types {@link SnapshotType}.
 */
public interface ISnapshot {

    /**
     * Gets the file store.
     * 
     * @return The file store
     */
    IFileStore getFileStore();

    /**
     * Gets the JVM.
     * 
     * @return The JVM
     */
    IJvm getJvm();

    /**
     * Gets the time stamp that is not the date when snapshot file was
     * created/modified but the date when snapshot was taken.
     * 
     * @return The time stamp
     */
    String getTimeStamp();

    /**
     * Gets the snapshot type.
     * 
     * @return The snapshot type
     */
    SnapshotType getType();

    /**
     * Renames the snapshot.
     * 
     * @param newName
     *            The new name
     * @throws JvmCoreException
     *             if renaming fails
     */
    void rename(String newName) throws JvmCoreException;

    /**
     * The snapshot type.
     */
    public enum SnapshotType {

        /** The thread snapshot. */
        Thread,

        /** The heap snapshot. */
        Heap,

        /** The CPU snapshot. */
        Cpu,

        /** The heap snapshot in hprof format. */
        Hprof;

        /**
         * Gets the extension.
         * 
         * @return The extension
         */
        public String getExtension() {
            return name().toLowerCase();
        }
    }
}
