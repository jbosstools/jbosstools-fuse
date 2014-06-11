/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.util.ArrayList;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;

/**
 * The attribute parser.
 */
public class AttributeParser {

    /**
     * Parse the given object and store into the given string buffer.
     * 
     * @param buffer
     *            The string buffer
     * @param object
     *            The object
     * @param indentation
     *            The indentation
     */
    protected void parseObject(StringBuffer buffer, Object object,
            int indentation) {
        int indent = indentation;
        if (object instanceof CompositeData) {
            CompositeData compositeData = (CompositeData) object;
            for (String key : compositeData.getCompositeType().keySet()) {
                Object value = compositeData.get(key);
                if (value instanceof CompositeData) {
                    buffer.append(key).append(":\n"); //$NON-NLS-1$
                    parseObject(buffer, value, ++indent);
                } else {
                    for (int i = 0; i < indent; i++) {
                        buffer.append('\t');
                    }
                    buffer.append(key).append(": ").append(value).append('\n'); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Refreshes the child nodes of given attribute recursively.
     * 
     * @param node
     *            The attribute node
     */
    protected void refreshAttribute(AttributeNode node) {
        Object value = node.getValue();
        if (value instanceof CompositeData) {
            refreshCompositeData(node);
        } else if (value instanceof TabularData) {
            refreshTabularData(node);
        } else if (value instanceof CompositeData[]) {
            refreshCompositeDataArray(node);
        } else if (value instanceof Object[]) {
            refreshObjectArray(node);
        } else if (value instanceof byte[] || value instanceof short[]
                || value instanceof int[] || value instanceof long[]
                || value instanceof float[] || value instanceof double[]) {
            refreshPrimitiveArray(node);
        }
    }

    /**
     * Refreshes the composite data.
     * 
     * @param node
     *            The attribute node
     */
    private void refreshCompositeData(AttributeNode node) {
        List<AttributeNode> children = new ArrayList<AttributeNode>();

        CompositeData compositeData = (CompositeData) node.getValue();
        CompositeType type = compositeData.getCompositeType();
        for (String key : type.keySet()) {
            AttributeNode attribute = null;
            for (AttributeNode child : node.getChildren()) {
                if (child.getName().equals(key)) {
                    attribute = child;
                    attribute.setValue(compositeData.get(key));
                    break;
                }
            }
            if (attribute == null) {
                attribute = new AttributeNode(key, node, compositeData.get(key));
            }
            children.add(attribute);
        }

        node.removeChildren();
        for (AttributeNode child : children) {
            node.addChild(child);
            refreshAttribute(child);
        }
    }

    /**
     * Refreshes the composite data array.
     * 
     * @param node
     *            The attribute node
     */
    private void refreshCompositeDataArray(AttributeNode node) {
        List<AttributeNode> children = new ArrayList<AttributeNode>();

        CompositeData[] values = (CompositeData[]) node.getValue();
        for (int i = 0; i < values.length; i++) {
            String name = CompositeData.class.getSimpleName() + "[" + i + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            AttributeNode attribute = null;
            for (AttributeNode child : node.getChildren()) {
                if (child.getName().equals(name)) {
                    attribute = child;
                    attribute.setValue(values[i]);
                    break;
                }
            }
            if (attribute == null) {
                attribute = new AttributeNode(name, node, values[i]);
            }
            children.add(attribute);
        }

        node.removeChildren();
        for (AttributeNode child : children) {
            node.addChild(child);
            refreshAttribute(child);
        }
    }

    /**
     * Refreshes the tabular data.
     * 
     * @param node
     *            The attribute node
     */
    private void refreshTabularData(AttributeNode node) {
        List<AttributeNode> children = new ArrayList<AttributeNode>();

        TabularData tabularData = (TabularData) node.getValue();
        for (Object keyList : tabularData.keySet()) {
            @SuppressWarnings("unchecked")
            Object[] keys = ((List<Object>) keyList).toArray(new Object[0]);

            AttributeNode attribute = null;
            for (AttributeNode child : node.getChildren()) {
                if (child.getName().equals(String.valueOf(keys[0]))) {
                    attribute = child;
                    attribute.setValue(tabularData.get(keys));
                    break;
                }
            }
            if (attribute == null) {
                attribute = new AttributeNode(String.valueOf(keys[0]), node,
                        tabularData.get(keys));
            }
            children.add(attribute);
        }

        node.removeChildren();
        for (AttributeNode child : children) {
            node.addChild(child);
            refreshAttribute(child);
        }
    }

    /**
     * Refreshes the primitive array.
     * 
     * @param node
     *            The parent node
     */
    private void refreshPrimitiveArray(AttributeNode node) {
        List<AttributeNode> children = new ArrayList<AttributeNode>();

        Object value = node.getValue();
        Number[] numbers = getNumbers(value);
        for (int i = 0; i < numbers.length; i++) {
            AttributeNode attribute = null;
            String name = value.getClass().getComponentType().getSimpleName()
                    + "[" + i + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            for (AttributeNode child : node.getChildren()) {
                if (child.getName().equals(name)) {
                    attribute = child;
                    attribute.setValue(numbers[i]);
                    break;
                }
            }
            if (attribute == null) {
                attribute = new AttributeNode(name, node, numbers[i]);
            }
            children.add(attribute);
        }

        node.removeChildren();
        for (AttributeNode child : children) {
            node.addChild(child);
            refreshAttribute(child);
        }
    }

    /**
     * Refreshes the object array.
     * 
     * @param node
     *            The attribute node
     */
    private void refreshObjectArray(AttributeNode node) {
        List<AttributeNode> children = new ArrayList<AttributeNode>();

        Object[] objects = (Object[]) node.getValue();

        for (int i = 0; i < objects.length; i++) {
            AttributeNode attribute = null;
            String name = objects.getClass().getComponentType().getSimpleName()
                    + "[" + i + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            for (AttributeNode child : node.getChildren()) {
                if (child.getName().equals(name)) {
                    attribute = child;
                    attribute.setValue(objects[i]);
                    break;
                }
            }
            if (attribute == null) {
                attribute = new AttributeNode(name, node, objects[i]);
            }
            children.add(attribute);
        }

        node.removeChildren();
        for (AttributeNode child : children) {
            node.addChild(child);
            refreshAttribute(child);
        }
    }

    /**
     * Gets the array of <tt>Number</tt>.
     * 
     * @param value
     *            The array of primitive type
     * @return The array of <tt>Number</tt>
     */
    private Number[] getNumbers(Object value) {
        Number[] numbers = null;
        if (value instanceof byte[]) {
            byte[] array = (byte[]) value;
            numbers = new Number[array.length];
            for (int i = 0; i < array.length; i++) {
                numbers[i] = array[i];
            }
        } else if (value instanceof short[]) {
            short[] array = (short[]) value;
            numbers = new Number[array.length];
            for (int i = 0; i < array.length; i++) {
                numbers[i] = array[i];
            }
        } else if (value instanceof int[]) {
            int[] array = (int[]) value;
            numbers = new Number[array.length];
            for (int i = 0; i < array.length; i++) {
                numbers[i] = array[i];
            }
        } else if (value instanceof long[]) {
            long[] array = (long[]) value;
            numbers = new Number[array.length];
            for (int i = 0; i < array.length; i++) {
                numbers[i] = array[i];
            }
        } else if (value instanceof float[]) {
            float[] array = (float[]) value;
            numbers = new Number[array.length];
            for (int i = 0; i < array.length; i++) {
                numbers[i] = array[i];
            }
        } else if (value instanceof double[]) {
            double[] array = (double[]) value;
            numbers = new Number[array.length];
            for (int i = 0; i < array.length; i++) {
                numbers[i] = array[i];
            }
        }
        return numbers;
    }
}
