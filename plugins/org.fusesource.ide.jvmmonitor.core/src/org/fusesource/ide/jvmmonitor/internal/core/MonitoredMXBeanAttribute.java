/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.ObjectName;

import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanAttribute;


/**
 * The monitored MXBean attributes.
 */
public class MonitoredMXBeanAttribute implements IMonitoredMXBeanAttribute {

    /** The number of durations. */
    private static final long NUM_OF_DURATIONS = 500;

    /** The object name. */
    private ObjectName objectName;

    /** The attribute name. */
    private String atributeName;

    /** The attribute values. */
    private List<Number> values;

    /** The time values. */
    private List<Date> dates;

    /** The previous duration index. */
    private int previousDurationIndex;

    /** The RGB. */
    private int[] rgb;

    /**
     * The constructor.
     * 
     * @param objectName
     *            The object name
     * @param attributeName
     *            The qualified attribute name
     * @param rgb
     *            The RGB
     */
    public MonitoredMXBeanAttribute(ObjectName objectName,
            String attributeName, int[] rgb) {
        this.objectName = objectName;
        this.atributeName = attributeName;
        previousDurationIndex = 0;
        this.rgb = rgb;

        values = new CopyOnWriteArrayList<Number>();
        dates = new CopyOnWriteArrayList<Date>();
    }

    /*
     * @see IMonitoredMXBeanAttribute#getObjectName()
     */
    @Override
    public ObjectName getObjectName() {
        return objectName;
    }

    /*
     * @see IMonitoredMXBeanAttribute#getAttributeName()
     */
    @Override
    public String getAttributeName() {
        return atributeName;
    }

    /*
     * @see IMonitoredMXBeanAttribute#getRGB()
     */
    @Override
    public int[] getRGB() {
        return rgb;
    }

    /*
     * @see IMonitoredMXBeanAttribute#getDates()
     */
    @Override
    public List<Date> getDates() {
        return dates;
    }

    /*
     * @see IMonitoredMXBeanAttribute#getValues()
     */
    @Override
    public List<Number> getValues() {
        return values;
    }

    /*
     * @see IMonitoredMXBeanAttribute#setRGB(int, int, int)
     */
    @Override
    public void setRGB(int r, int g, int b) {
        rgb = new int[] { r, g, b };
    }

    /*
     * @see IMonitoredMXBeanAttribute#clear()
     */
    @Override
    public void clear() {
        dates.clear();
        values.clear();
        previousDurationIndex = 0;
    }

    /**
     * Adds the attribute value.
     * 
     * @param value
     *            The attribute value
     * @param date
     *            The date
     */
    public void add(Number value, Date date) {
        values.add(value);
        dates.add(date);

        // compress data if exceeding the certain size
        if (dates.size() > NUM_OF_DURATIONS * 2) {
            compress();
        }
    }

    /**
     * Gets the data size.
     * 
     * @return The data size.
     */
    public int getSize() {
        return dates.size();
    }

    /**
     * Compresses the data diving into several durations and keeping only the
     * min / max value in each duration.
     */
    private void compress() {
        ArrayList<Date> compressedDates = new ArrayList<Date>();
        ArrayList<Number> compressedValues = new ArrayList<Number>();

        int minIndex = 0;
        int maxIndex = 0;
        previousDurationIndex = 0;
        for (int i = 0; i < dates.size(); i++) {
            if (isInSameDurationAsPrevious(dates.get(i).getTime())) {

                // get the min / max values in the same duration
                double value = values.get(i).doubleValue();
                if (value > values.get(maxIndex).doubleValue()) {
                    maxIndex = i;
                }
                if (value < values.get(minIndex).doubleValue()) {
                    minIndex = i;
                }
            } else {
                addValues(compressedDates, compressedValues, minIndex, maxIndex);
                maxIndex = i;
                minIndex = i;
            }
        }

        addValues(compressedDates, compressedValues, minIndex, maxIndex);

        dates = compressedDates;
        values = compressedValues;
    }

    /**
     * Adds the values to the given array.
     * 
     * @param datesArray
     *            The array of dates
     * @param valuesArray
     *            The array of values
     * @param indexA
     *            The index of {@link #dates} and {@link #values} to add
     * @param indexB
     *            The index of {@link #values} and {@link #values} to add
     */
    private void addValues(ArrayList<Date> datesArray,
            ArrayList<Number> valuesArray, int indexA, int indexB) {
        if (indexA < indexB) {
            datesArray.add(dates.get(indexA));
            valuesArray.add(values.get(indexA));
            datesArray.add(dates.get(indexB));
            valuesArray.add(values.get(indexB));
        } else if (indexA > indexB) {
            datesArray.add(dates.get(indexB));
            valuesArray.add(values.get(indexB));
            datesArray.add(dates.get(indexA));
            valuesArray.add(values.get(indexA));
        } else {
            datesArray.add(dates.get(indexB));
            valuesArray.add(values.get(indexB));
        }
    }

    /**
     * Checks if the given date is in the same duration as previous.
     * 
     * @param date
     *            the date
     * @return true if the given date is in the same duration as previous
     */
    private boolean isInSameDurationAsPrevious(long date) {

        double minDate = dates.get(0).getTime();
        double maxDate = dates.get(dates.size() - 1).getTime();

        // calculate the duration index
        int durationIndex = (int) ((date - minDate) / (maxDate - minDate) * NUM_OF_DURATIONS);

        // check if the duration index is the same as previous
        boolean isInSameGridAsPrevious = (durationIndex == previousDurationIndex);

        // store the previous duration index
        previousDurationIndex = durationIndex;

        return isInSameGridAsPrevious;
    }
}
