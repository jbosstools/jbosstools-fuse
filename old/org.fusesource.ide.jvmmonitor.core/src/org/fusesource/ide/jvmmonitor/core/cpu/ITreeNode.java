/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.cpu;

import java.util.List;

/**
 * The tree node.
 */
public interface ITreeNode {

    /**
     * Gets the child nodes.
     * 
     * @return The child nodes
     */
    List<? extends ITreeNode> getChildren();

    /**
     * Gets the child node corresponding to the given name.
     * 
     * @param name
     *            The node name
     * @return The child node, or <tt>null</tt> if not found
     */
    ITreeNode getChild(String name);

    /**
     * Gets the state indicating if this node has child nodes.
     * 
     * @return <tt>true</tt> if this node has child nodes
     */
    boolean hasChildren();

    /**
     * Gets the parent node.
     * 
     * @return The parent node, or <tt>null</tt> if not found
     */
    ITreeNode getParent();

    /**
     * Gets the node name.
     * 
     * @return The node name
     */
    String getName();
}
