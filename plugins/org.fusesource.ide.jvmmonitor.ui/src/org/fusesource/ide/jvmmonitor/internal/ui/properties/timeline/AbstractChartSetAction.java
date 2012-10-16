/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.fusesource.ide.jvmmonitor.internal.ui.IConstants;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.ui.Activator;

/**
 * The abstract class for action related to chart set management.
 */
abstract public class AbstractChartSetAction extends Action implements
        IConstants {

    /** The predefined overview chart set. */
    static final String OVERVIEW_CHART_SET = "Overview"; //$NON-NLS-1$

    /** The predefined memory chart set. */
    static final String MEMORY_CHART_SET = "Memory"; //$NON-NLS-1$

    /** The property section. */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param section
     *            The property section
     */
    public AbstractChartSetAction(AbstractJvmPropertySection section) {
        setId(getClass().getName());
        this.section = section;
    }

    /**
     * Gets the chart sets memento.
     * 
     * @return The chart sets memento, or <tt>null</tt> if no chart sets are
     *         saved yet
     * @throws WorkbenchException
     * @throws IOException
     */
    IMemento getChartSetsMemento() throws WorkbenchException, IOException {
        String chartSetsString = Activator.getDefault().getPreferenceStore()
                .getString(CHART_SETS);
        if (chartSetsString.isEmpty()) {
            return null;
        }
        return XMLMemento.createReadRoot(new StringReader(chartSetsString));
    }

    /**
     * Gets the chart sets stored as chart sets memento.
     * 
     * @return The chart sets
     * @throws WorkbenchException
     * @throws IOException
     */
    List<String> getChartSets() throws WorkbenchException, IOException {
        List<String> elements = new ArrayList<String>();

        IMemento chartSetsMemento = getChartSetsMemento();
        if (chartSetsMemento == null) {
            return elements;
        }

        for (IMemento element : chartSetsMemento.getChildren(CHART_SET)) {
            elements.add(element.getID());
        }

        Collections.sort(elements);

        return elements;
    }

    /**
     * Gets the predefined chart sets.
     * 
     * @return The predefined chart sets
     */
    List<String> getPredefinedChartSets() {
        return Arrays.asList(new String[] { OVERVIEW_CHART_SET,
                MEMORY_CHART_SET });
    }
}
