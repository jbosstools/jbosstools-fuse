/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanAttribute;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup.AxisUnit;
import org.fusesource.ide.jvmmonitor.internal.ui.IConstants;
import org.fusesource.ide.jvmmonitor.internal.ui.RefreshJob;
import org.fusesource.ide.jvmmonitor.ui.Activator;

import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;
import org.swtchart.internal.PlotArea;

/**
 * The timeline chart.
 */
public class TimelineChart extends Chart implements IPropertyChangeListener {

    /** The job to refresh model and UI. */
    private RefreshJob refreshJob;

    /** The attribute group. */
    private IMonitoredMXBeanGroup attributeGroup;

    /** The section id. */
    private String sectionId;

    /** The section. */
    private ExpandableComposite section;

    /** The colors. */
    private List<Color> colors;

    /** The marker. */
    Marker marker;

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent composite
     * @param section
     *            The section
     * @param group
     *            The monitored attribute group
     * @param style
     *            The style
     * @param sectionId
     *            The section id
     */
    public TimelineChart(Composite parent, ExpandableComposite section,
            IMonitoredMXBeanGroup group, int style, String sectionId) {
        super(parent, style);
        this.section = section;
        this.attributeGroup = group;
        this.sectionId = sectionId;
        colors = new ArrayList<Color>();

        createChart(parent);
        Activator.getDefault().getPreferenceStore()
                .addPropertyChangeListener(this);

        marker = new Marker(this);
    }

    /*
     * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (IConstants.LEGEND_VISIBILITY.equals(event.getProperty())
                && !isDisposed()) {
            getLegend().setVisible((Boolean) (event.getNewValue()));
            redraw();
        }
    }

    /*
     * @see Chart#dispose()
     */
    @Override
    public void dispose() {
        if (!section.isDisposed()) {
            Composite sectionContainer = section.getParent();
            section.dispose();
            sectionContainer.layout();
        }
        super.dispose();

        for (Color color : colors) {
            color.dispose();
        }
    }

    /**
     * Gets the monitored attribute group.
     * 
     * @return The monitored attribute group
     */
    public IMonitoredMXBeanGroup getAttributeGroup() {
        return attributeGroup;
    }

    /**
     * Refreshes the chart.
     */
    public void refresh() {
        refreshJob = new RefreshJob(NLS.bind(Messages.refreshChartJobLabel,
                attributeGroup.getName()), sectionId + attributeGroup.getName()) {

            @Override
            protected void refreshModel(IProgressMonitor monitor) {
                // do nothing
            }

            @Override
            protected void refreshUI() {
                refreshChartWidget();
            }
        };

        refreshJob.schedule();
    }

    /**
     * Gets the section.
     * 
     * @return The section
     */
    public ExpandableComposite getSection() {
        return section;
    }

    /**
     * Adds the monitored series.
     * 
     * @param attribute
     *            The monitored attribute
     * @return The series
     */
    private ISeries addMonitoredSeries(final IMonitoredMXBeanAttribute attribute) {
        ILineSeries series = (ILineSeries) getSeriesSet().createSeries(
                SeriesType.LINE, getSeriesId(attribute));

        series.setXDateSeries(new Date[0]);
        series.setYSeries(new double[0]);
        series.setSymbolType(PlotSymbolType.NONE);
        setColor(series, attribute.getRGB());
        return series;
    }

    /**
     * Sets the color.
     * 
     * @param series
     *            The series
     * @param rgb
     *            The color
     */
    private void setColor(ILineSeries series, int[] rgb) {
        if (rgb != null && !hasColor(rgb)) {
            Color color = new Color(Display.getDefault(), rgb[0], rgb[1],
                    rgb[2]);
            series.setLineColor(color);
            colors.add(color);
        }
    }

    /**
     * Checks if the given color is already available in this chart.
     * 
     * @param rgb
     *            The RGB
     * @return <tt>true</tt> if the given color is already available in this
     *         chart
     */
    private boolean hasColor(int[] rgb) {
        for (Color color : colors) {
            if (color.getRGB().red == rgb[0] && color.getRGB().green == rgb[1]
                    && color.getRGB().blue == rgb[2]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates the chart.
     * 
     * @param parent
     *            The parent composite
     */
    private void createChart(Composite parent) {
        getTitle().setVisible(false);

        setBackground(parent.getBackground());
        Color black = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
        getAxisSet().getXAxis(0).getTick().setForeground(black);
        getAxisSet().getXAxis(0).getTitle().setVisible(false);
        getAxisSet().getYAxis(0).getTick().setForeground(black);
        getAxisSet().getYAxis(0).getTick().setTickMarkStepHint(30);
        getAxisSet().getYAxis(0).getTitle().setVisible(false);

        getLegend().setPosition(SWT.BOTTOM);
        getLegend().setVisible(
                Activator.getDefault().getPreferenceStore()
                        .getBoolean(IConstants.LEGEND_VISIBILITY));

        MyMouseListener plotAreaListener = new MyMouseListener(getPlotArea());
        getPlotArea().addListener(SWT.MouseMove, plotAreaListener);
        getPlotArea().addListener(SWT.MouseDown, plotAreaListener);
        getPlotArea().addListener(SWT.MouseUp, plotAreaListener);

        MyMouseListener chartListener = new MyMouseListener(this);
        addListener(SWT.MouseMove, chartListener);
        addListener(SWT.MouseDown, chartListener);
        addListener(SWT.MouseUp, chartListener);
    }

    /**
     * Refreshes the chart widget.
     */
    void refreshChartWidget() {
        if (isDisposed()) {
            return;
        }

        deleteMonitoredSeries();

        for (IMonitoredMXBeanAttribute attribute : attributeGroup
                .getAttributes()) {
            String seriesId = getSeriesId(attribute);
            ISeries series = getSeriesSet().getSeries(seriesId);
            if (series == null) {
                series = addMonitoredSeries(attribute);
                if (series == null) {
                    return;
                }
            }

            series.setXDateSeries(attribute.getDates().toArray(new Date[0]));
            series.setYSeries(getYSeries(attribute));
            setColor((ILineSeries) series, attribute.getRGB());
        }

        AxisUnit axisUnit = attributeGroup.getAxisUnit();
        if (axisUnit == AxisUnit.MBytes) {
            getAxisSet().getYAxis(0).getTick()
                    .setFormat(new DecimalFormat("#####.#M")); //$NON-NLS-1$
            getAxisSet().adjustRange();
        } else if (axisUnit == AxisUnit.Percent) {
            getAxisSet().getYAxis(0).getTick()
                    .setFormat(new DecimalFormat("###%")); //$NON-NLS-1$
            getAxisSet().getXAxis(0).adjustRange();
            getAxisSet().getYAxis(0).setRange(
                    new Range(0, (getSize().y + 10) / (double) getSize().y));
        } else if (axisUnit == AxisUnit.Count) {
            getAxisSet().getYAxis(0).getTick()
                    .setFormat(NumberFormat.getIntegerInstance());
            getAxisSet().adjustRange();
        } else {
            getAxisSet().adjustRange();
        }

        section.setText(attributeGroup.getName());
        section.layout();
        marker.redraw();
        redraw();
    }

    /**
     * Gets the Y series with given attribute.
     * 
     * @param attribute
     *            The attribute
     * @return The Y Series
     */
    private double[] getYSeries(IMonitoredMXBeanAttribute attribute) {
        List<Number> values = attribute.getValues();

        double[] ySeries = new double[values.size()];

        double ratio = 1d;
        if (attributeGroup.getAxisUnit() == AxisUnit.MBytes) {
            ratio = 0.000001;
        }

        for (int i = 0; i < ySeries.length; i++) {
            Number value = values.get(i);
            if (value instanceof Integer) {
                ySeries[i] = (Integer) value * ratio;
            } else if (value instanceof Long) {
                ySeries[i] = (Long) value * ratio;
            } else if (value instanceof Double) {
                ySeries[i] = (Double) value * ratio;
            }
        }
        return ySeries;
    }

    /**
     * Delete series if not monitored any longer.
     */
    private void deleteMonitoredSeries() {
        ISeriesSet seriesSet = getSeriesSet();
        List<IMonitoredMXBeanAttribute> attributes = attributeGroup
                .getAttributes();

        for (ISeries series : seriesSet.getSeries()) {
            String seriesId = series.getId();
            boolean found = false;
            for (IMonitoredMXBeanAttribute attribute : attributes) {
                if (series.getId().equals(getSeriesId(attribute))) {
                    found = true;
                }
            }
            if (!found) {
                seriesSet.deleteSeries(seriesId);
            }
        }
    }

    /**
     * Gets the series ID.
     * 
     * @param attribute
     *            The monitored attribute
     * @return The series ID
     */
    private String getSeriesId(IMonitoredMXBeanAttribute attribute) {
        final String NAME = "name="; //$NON-NLS-1$
        final String DELIMITER = ","; //$NON-NLS-1$

        String canoticalName = attribute.getObjectName().getCanonicalName();
        if (canoticalName.contains(NAME)) {
            String[] elements = canoticalName.split(NAME);
            String name = elements[1];
            if (name.contains(DELIMITER)) {
                name = name.split(DELIMITER)[0];
            }
            return name + " : " + attribute.getAttributeName(); //$NON-NLS-1$
        }
        return attribute.getAttributeName();
    }

    /**
     * The mouse listener to show marker on chart.
     */
    private class MyMouseListener implements Listener {

        /** The control to add listener. */
        private Control control;

        /**
         * The constructor.
         * 
         * @param control
         *            The control to add listener
         */
        public MyMouseListener(Control control) {
            this.control = control;
        }

        /*
         * @see Listener#handleEvent(Event)
         */
        @Override
        public void handleEvent(Event event) {
            int position;
            if (control instanceof Chart) {
                position = event.x - getPlotArea().getBounds().x;
            } else if (control instanceof PlotArea) {
                position = event.x;
            } else {
                throw new IllegalStateException("unknown object");//$NON-NLS-1$
            }

            switch (event.type) {
            case SWT.MouseMove:
                if (!marker.isDisposed()) {
                    marker.setPosition(position);
                }
                break;
            case SWT.MouseDown:
                if (event.button == 1) {
                    marker.setPosition(position);
                }
                break;
            case SWT.MouseUp:
                marker.dispose();
                break;
            default:
                break;
            }
        }
    }
}
