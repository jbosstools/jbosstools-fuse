/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.overview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * The overview content provider.
 */
public class OverviewContentProvider implements ITreeContentProvider {

    /** The overview properties. */
    private OverviewProperties overviewProperties;

    /** The system properties. */
    private List<OverviewProperty> systemProperties;

    /**
     * The constructor.
     * 
     * @param overviewProperties
     *            The overview properties
     */
    public OverviewContentProvider(OverviewProperties overviewProperties) {
        this.overviewProperties = overviewProperties;
        systemProperties = new ArrayList<OverviewProperty>();
    }

    /*
     * @see IStructuredContentProvider#getElements(Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        return OverviewCategory.values();
    }

    /*
     * @see ITreeContentProvider#getChildren(Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof OverviewCategory) {
            return overviewProperties
                    .getOverviewProperties((OverviewCategory) parentElement);
        }

        if (parentElement instanceof OverviewProperty) {
            Object value = ((OverviewProperty) parentElement).getValue();

            // system properties
            if (value instanceof TabularData) {
                return toArray((TabularData) value);
            }
        }

        return null;
    }

    /*
     * @see ITreeContentProvider#getParent(Object)
     */
    @Override
    public Object getParent(Object element) {
        return null;
    }

    /*
     * @see ITreeContentProvider#hasChildren(Object)
     */
    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof OverviewCategory) {
            return true;
        }

        if (element instanceof OverviewProperty) {
            // system properties
            OverviewProperty property = (OverviewProperty) element;
            if (property.getValue() instanceof TabularData) {
                return true;
            }
        }
        return false;
    }

    /*
     * @see IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        // do nothing
    }

    /*
     * @see IContentProvider#inputChanged(Viewer, Object, Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing
    }

    /**
     * Gets the array corresponding to the given tabular data.
     * 
     * @param tabularData
     *            The tabular data
     * @return The array
     */
    private Object[] toArray(TabularData tabularData) {
        List<OverviewProperty> list = new ArrayList<OverviewProperty>();
        for (Object object : tabularData.values()) {
            if (object instanceof CompositeData) {
                CompositeData compositeData = (CompositeData) object;
                String[] elements = compositeData.values().toArray(
                        new String[0]);
                OverviewProperty property = getProperty(elements[0]);
                if (property == null) {
                    property = new OverviewProperty(OverviewCategory.Runtime,
                            elements[0], elements[0], false);
                }
                property.setValue(elements[1]);
                list.add(property);
            }
        }

        Collections.sort(list, new Comparator<OverviewProperty>() {
            @Override
            public int compare(OverviewProperty o1, OverviewProperty o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        systemProperties = list;
        return systemProperties.toArray(new OverviewProperty[systemProperties
                .size()]);
    }

    /**
     * Gets the overview property corresponding to the given attribute name.
     * 
     * @param attribute
     *            The attribute name
     * @return The overview property
     */
    private OverviewProperty getProperty(String attribute) {
        for (OverviewProperty property : systemProperties) {
            if (property.getAttributeName().equals(attribute)) {
                return property;
            }
        }
        return null;
    }
}
