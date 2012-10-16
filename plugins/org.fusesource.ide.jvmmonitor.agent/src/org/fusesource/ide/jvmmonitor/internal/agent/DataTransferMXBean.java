package org.fusesource.ide.jvmmonitor.internal.agent;

import javax.management.MXBean;

/**
 * The MXBean to transfer data.
 */
@SuppressWarnings("nls")
@MXBean
public interface DataTransferMXBean {

    /** The data transfer MXBean name. */
    final static String DATA_TRANSFER_MXBEAN_NAME = "org.fusesource.ide.jvmmonitor:type=Data Transfer";

    /**
     * Reads the data from file on host where target JVM is running.
     * 
     * @param fileName
     *            The file name
     * @param pos
     *            The offset position of data in bytes to start reading data
     * @param maxSize
     *            The max size in bytes to read data
     * @return The file data
     */
    byte[] read(String fileName, int pos, int maxSize);

    /**
     * Gets the version.
     * 
     * @return The version
     */
    String getVersion();
}
