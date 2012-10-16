package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.util.ArrayList;
import java.util.List;

import javax.management.ObjectName;

import org.eclipse.swt.graphics.RGB;

/**
 * The attribute node.
 */
public class AttributeNode {

    /** The attribute name. */
    private String name;

    /** The child nodes. */
    private List<AttributeNode> children;

    /** The parent node. */
    private AttributeNode parent;

    /** The state indicating if this node is valid leaf. */
    private boolean validLeaf;

    /** The object name. */
    private ObjectName objectName;

    /** The RGB. */
    private RGB rgb;

    /**
     * The constructor.
     * 
     * @param name
     *            The attribute name
     * @param parent
     *            The parent node
     */
    public AttributeNode(String name, AttributeNode parent) {
        this(name, parent, parent.getObjectName());
    }

    /**
     * The constructor.
     * 
     * @param name
     *            The attribute name
     * @param parent
     *            The parent node, or <tt>null</tt> in case of root node
     * @param objectName
     *            The object name
     */
    public AttributeNode(String name, AttributeNode parent,
            ObjectName objectName) {
        this.name = name;
        this.parent = parent;
        this.objectName = objectName;
        validLeaf = false;
        children = new ArrayList<AttributeNode>();
        rgb = new RGB(0, 0, 255);
    }

    /**
     * Gets the attribute node name.
     * 
     * @return The attribute node name
     */
    protected String getName() {
        return name;
    }

    /**
     * Gets the object name.
     * 
     * @return The object name
     */
    protected ObjectName getObjectName() {
        return objectName;
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
     * @return True if the node has children
     */
    protected boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * Removes this node from attribute tree.
     */
    protected void remove() {
        if (parent == null) {
            return;
        }
        parent.getChildren().remove(this);
        if (parent.getChildren().size() == 0) {
            parent.remove();
        }
    }

    /**
     * Sets the state indicating if this node is valid leaf.
     * 
     * @param validLeaf
     *            The state indicating if this node is valid leaf
     */
    protected void setValidLeaf(boolean validLeaf) {
        this.validLeaf = validLeaf;
    }

    /**
     * Gets the state indicating if this node is valid leaf.
     * 
     * @return True if this node is valid leaf
     */
    protected boolean isValidLeaf() {
        return validLeaf;
    }

    /**
     * Sets the RGB.
     * 
     * @param rgb
     *            The RGB
     */
    protected void setRgb(RGB rgb) {
        this.rgb = rgb;
    }

    /**
     * Gets the RGB.
     * 
     * @return The RGB
     */
    protected RGB getRgb() {
        return rgb;
    }

    /**
     * Gets the qualified name.
     * 
     * @return The qualified name
     */
    protected String getQualifiedName() {
        String qualifiedName = name;
        AttributeNode node = this;
        while (node.parent != null) {
            node = node.parent;
            qualifiedName = node.getName() + "." + qualifiedName; //$NON-NLS-1$
        }
        return qualifiedName;
    }
}