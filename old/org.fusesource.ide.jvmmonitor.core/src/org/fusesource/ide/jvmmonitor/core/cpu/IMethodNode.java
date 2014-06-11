/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.cpu;

/**
 * The method node used in callers/callees and hot spots. As for call tree,
 * {@link ICallTreeNode} is used instead.
 */
public interface IMethodNode extends ITreeNode {

    /**
     * Gets the thread to which the node belongs.
     * 
     * @return The thread
     */
    String getThread();

    /**
     * Gets the self invocation time in percentage.
     * 
     * @return The self invocation time in percentage
     */
    double getSelfTimeInPercentage();

    /**
     * Gets the self invocation time in milliseconds.
     * 
     * @return The self invocation time in milliseconds
     */
    long getSelfTime();

    /**
     * Gets the invocation count.
     * 
     * @return The invocation count
     */
    int getInvocationCount();

    /**
     * Gets the non-qualified method name.
     * 
     * @return The non-qualified method name
     */
    String getNonqualifiedName();
}
