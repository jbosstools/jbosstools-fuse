/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.overview;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

/**
 * The overview property.
 */
public class OverviewProperty {

    /** The display name. */
    private String displayName;

    /** The attribute name. */
    private String attributeName;

    /** The format. */
    private IFormat format;

    /** The object name. */
    private String objectName;

    /** The property value. */
    private Object value;

    /** The state indicating if timeline is supported. */
    private boolean isTimelineSupported;

    /**
     * The constructor that is used to distinguish the duplicated MBean
     * attribute names.
     * 
     * @param category
     *            The category
     * @param displayName
     *            The display name
     * @param attributeName
     *            The qualified attribute name
     * @param isTimelineSupported
     *            The state indicating if timeline is supported
     */
    public OverviewProperty(OverviewCategory category, String displayName,
            String attributeName, boolean isTimelineSupported) {
        this(category, displayName, attributeName, null, null,
                isTimelineSupported);
    }

    /**
     * The constructor that is used to distinguish the duplicated MBean
     * attribute names.
     * 
     * @param category
     *            The category
     * @param displayName
     *            The display name
     * @param attributeName
     *            The qualified attribute name
     * @param format
     *            The format
     * @param isTimelineSupported
     *            The state indicating if timeline is supported
     */
    public OverviewProperty(OverviewCategory category, String displayName,
            String attributeName, IFormat format, boolean isTimelineSupported) {
        this(category, displayName, attributeName, null, format,
                isTimelineSupported);
    }

    /**
     * The constructor that is used to distinguish the duplicated MBean
     * attribute names.
     * 
     * @param category
     *            The category
     * @param displayName
     *            The display name
     * @param attributeName
     *            The qualified attribute name
     * @param name
     *            The name
     * @param format
     *            The format
     * @param isTimelineSupported
     *            The state indicating if timeline is supported
     */
    public OverviewProperty(OverviewCategory category, String displayName,
            String attributeName, String name, IFormat format,
            boolean isTimelineSupported) {
        this.displayName = displayName;
        this.attributeName = attributeName;
        this.format = format;
        this.isTimelineSupported = isTimelineSupported;

        if (name == null) {
            objectName = String.format("java.lang:type=%s", category.name()); //$NON-NLS-1$
        } else {
            objectName = String.format("java.lang:type=%s,name=%s", //$NON-NLS-1$
                    category.name(), name);
        }
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(displayName);
        buffer.append('\t').append(getValueString());
        return buffer.toString();
    }

    /**
     * Gets the display name.
     * 
     * @return The display name
     */
    protected String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the attribute name.
     * 
     * @return The attribute name
     */
    protected String getAttributeName() {
        return attributeName;
    }

    /**
     * Gets the object name.
     * 
     * @return The object name
     */
    protected String getObjectName() {
        return objectName;
    }

    /**
     * Gets the property value.
     * 
     * @return The property value
     */
    protected Object getValue() {
        return value;
    }

    /**
     * Sets the property value.
     * 
     * @param value
     *            The property value
     */
    protected void setValue(Object value) {
        this.value = value;
    }

    /**
     * Gets the state indicating if timeline is supported.
     * 
     * @return The state indicating if timeline is supported
     */
    protected boolean isTimelineSupported() {
        return isTimelineSupported;
    }

    /**
     * Gets the string representation of value.
     * 
     * @return The string representation of value
     */
    protected String getValueString() {
        if (value == null) {
            return ""; //$NON-NLS-1$
        }

        if (value instanceof String[]) {
            StringBuffer buffer = new StringBuffer();
            for (String element : (String[]) value) {
                buffer.append(element).append(' ');
            }
            return buffer.toString();
        }

        if (value instanceof CompositeData) {
            if (!attributeName.contains(".")) { //$NON-NLS-1$
                return ""; //$NON-NLS-1$
            }
            CompositeData compositeData = (CompositeData) value;
            String[] elements = attributeName.split("\\."); //$NON-NLS-1$
            String compositeDataKey = elements[elements.length - 1];
            value = compositeData.get(compositeDataKey);
        }

        if (value instanceof String) {
            return ((String) value).replace("\n", "\\n"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (value instanceof Long) {
            if (format != null) {
                return format.format(value);
            }
        }

        if (value instanceof TabularData) {
            return ""; //$NON-NLS-1$
        }

        return value.toString();
    }
}
