/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.util.ArrayList;
import java.util.List;

/**
 * The attribute node.
 */
public class AttributeNode {

    /** The attribute name. */
    private String name;

    /** The attribute value. */
    private Object value;

    /** The child nodes. */
    private List<AttributeNode> children;

    /** The parent node. */
    private AttributeNode parent;

    /** The state indicating if the attribute is writable. */
    private boolean writable;

    /**
     * The constructor.
     * 
     * @param name
     *            The attribute node name
     * @param parent
     *            The parent node
     * @param value
     *            The attribute value
     */
    public AttributeNode(String name, AttributeNode parent, Object value) {
        this.name = name;
        this.parent = parent;
        this.value = value;
        writable = false;
        children = new ArrayList<AttributeNode>();
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(name);
        if (isValidLeaf()) {
            buffer.append('\t').append(value);
        }
        return buffer.toString();
    }

    /**
     * Gets the attribute name.
     * 
     * @return The attribute name
     */
    protected String getName() {
        return name;
    }

    /**
     * Gets the attribute value;
     * 
     * @return The attribute value
     */
    protected Object getValue() {
        return value;
    }

    /**
     * Sets the attribute value.
     * 
     * @param value
     *            The attribute value
     */
    protected void setValue(Object value) {
        this.value = value;
    }

    /**
     * Adds the child node.
     * 
     * @param node
     *            The attribute node
     */
    protected void addChild(AttributeNode node) {
        children.add(node);
    }

    /**
     * Gets the children.
     * 
     * @return The children
     */
    protected List<AttributeNode> getChildren() {
        return children;
    }

    /**
     * Gets the state indicating if the node has children.
     * 
     * @return <tt>true</tt> if the node has children
     */
    protected boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * Gets the parent node.
     * 
     * @return The parent node, or <tt>null</tt> if it doesn't exist
     */
    protected AttributeNode getParent() {
        return parent;
    }

    /**
     * Removes the child nodes.
     */
    protected void removeChildren() {
        children.clear();
    }

    /**
     * Gets the state indicating if the attribute is writable.
     * 
     * @return <tt>true</tt> if the attribute is writable
     */
    protected boolean isWritable() {
        return writable && value != null;
    }

    /**
     * Sets the state indicating if the attribute is writable.
     * 
     * @param writable
     *            <tt>true</tt> if the attribute is writable
     */
    protected void setWritable(boolean writable) {
        this.writable = writable;
    }

    /**
     * Gets the state indicating if this node is valid leaf, that is the node
     * value is an expected instance of <tt>Number</tt>, <tt>String</tt> or
     * <tt>Boolean</tt>.
     * 
     * @return <tt>true</tt> if this node is valid leaf
     */
    protected boolean isValidLeaf() {
        if (value instanceof Number || value instanceof String
                || value instanceof Boolean) {
            return true;
        }
        return false;
    }
}