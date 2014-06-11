/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.cpu;

/**
 * The thread node.
 */
public interface IThreadNode extends ITreeNode {

    /**
     * Gets the total invocation time in the thread.
     * 
     * @return The total invocation time in the thread
     */
    long getTotalTime();
}
