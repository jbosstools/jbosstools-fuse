package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup.AxisUnit;
import org.swtchart.ISeries;

/**
 * The marker showing the rectangle symbols and tooltip on chart.
 */
public class Marker {

    /** The margin for text on hover. */
    private static final int MARGIN = 3;

    /** The offset of hover that is the size of hover arrow. */
    private static final int OFFSET = 25;

    /** The key for hover to show time. */
    private static final String TIME_KEY = "time"; //$NON-NLS-1$

    /** The chart. */
    private TimelineChart chart;

    /** The hovers. */
    private Map<String, Shell> hovers;

    /** The hover texts. */
    Map<String, String> texts;

    /** The mouse position. */
    private int mouseXPosition;

    /**
     * The constructor.
     * 
     * @param chart
     *            The chart
     */
    public Marker(TimelineChart chart) {
        this.chart = chart;
        hovers = new HashMap<String, Shell>();
        texts = new HashMap<String, String>();
    }

    /**
     * Check if marker is disposed.
     * 
     * @return <tt>true</tt> if marker is disposed.
     */
    protected boolean isDisposed() {
        return hovers.isEmpty();
    }

    /**
     * Disposes the resource.
     */
    protected void dispose() {
        disposeHovers();
    }

    /**
     * Redraws the marker.
     */
    protected void redraw() {
        if (!isDisposed()) {
            setPosition(mouseXPosition);
        }
    }

    /**
     * Sets the position.
     * 
     * @param x
     *            The x pixel coordinate
     */
    protected void setPosition(int x) {
        mouseXPosition = x;
        Integer invertedSeriesIndex = getInvertedSeriesIndex(x);

        createHovers();
        if (invertedSeriesIndex != null) {
            configureHovers(invertedSeriesIndex);
        }
    }

    /**
     * Creates the hovers.
     */
    private void createHovers() {

        // create hover keys
        List<String> keys = new ArrayList<String>();
        keys.add(TIME_KEY);
        for (ISeries series : chart.getSeriesSet().getSeries()) {
            keys.add(series.getId());
        }

        // create or reuse hovers
        Map<String, Shell> newHovers = new HashMap<String, Shell>();
        for (final String key : keys) {
            Shell hover = hovers.get(key);
            if (hover == null) {
                hover = createHover(key);
            }
            newHovers.put(key, hover);
        }

        // dispose unused hovers
        for (Entry<String, Shell> entrySet : hovers.entrySet()) {
            if (!keys.contains(entrySet.getKey())) {
                Shell hover = entrySet.getValue();
                Region region = hover.getRegion();
                if (region != null) {
                    region.dispose();
                }
                hover.dispose();
            }
        }

        hovers = newHovers;
    }

    /**
     * Creates the hover.
     * 
     * @param key
     *            The hover key
     * @return The hover
     */
    private Shell createHover(final String key) {
        Shell hover = new Shell(Display.getDefault().getActiveShell(),
                SWT.NO_TRIM | SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);

        hover.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                String text = texts.get(key);
                if (text != null) {
                    e.gc.drawText(texts.get(key), OFFSET + MARGIN, OFFSET);
                }
            }
        });
        hover.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_CYAN));
        hover.setAlpha(220);

        return hover;
    }

    /**
     * Creates the hovers assuming that the time is almost the same among series
     * 
     * @param invertedSeriesIndex
     *            The inverted series index
     */
    private void configureHovers(Integer invertedSeriesIndex) {
        ISeries[] seriesArray = chart.getSeriesSet().getSeries();

        // create hover for time
        Date[] dates = seriesArray[0].getXDateSeries();
        long time = dates[dates.length - invertedSeriesIndex].getTime();

        StringBuffer buffer = new StringBuffer();
        buffer.append(Messages.timeLabel).append(' ')
                .append(new SimpleDateFormat("HH:mm:ss") //$NON-NLS-1$
                        .format(time));
        texts.put(TIME_KEY, buffer.toString());

        int timeInPixel = chart.getAxisSet().getXAxes()[0]
                .getPixelCoordinate(time);
        configureHover(hovers.get(TIME_KEY), buffer.toString(), timeInPixel,
                chart.getPlotArea().getSize().y, true);

        // create hover for values
        for (ISeries series : chart.getSeriesSet().getSeries()) {
            double[] ySeries = series.getYSeries();
            int seriesIndex = ySeries.length - invertedSeriesIndex;
            if (seriesIndex < 0) {
                continue;
            }

            buffer = new StringBuffer();
            buffer.append(series.getId()).append(": ") //$NON-NLS-1$
                    .append(getFormattedValue(ySeries[seriesIndex]));
            texts.put(series.getId(), buffer.toString());

            int valueInPixel = chart.getAxisSet().getYAxes()[0]
                    .getPixelCoordinate(ySeries[seriesIndex]);
            configureHover(hovers.get(series.getId()), buffer.toString(),
                    timeInPixel, valueInPixel, false);
        }
    }

    /**
     * Configures the hover.
     * 
     * @param hover
     *            The hover
     * @param text
     *            The hover text
     * @param x
     *            The x coordinate in pixels
     * @param y
     *            The y coordinate in pixels
     * @param showBelow
     *            <tt>true</tt> to show hover below data point
     */
    private void configureHover(Shell hover, String text, int x, int y,
            boolean showBelow) {

        // set size
        Point textExtent = getExtent(hover, text);
        Point hoverSize = new Point(textExtent.x + MARGIN * 2 + OFFSET * 2,
                textExtent.y + OFFSET * 2);
        hover.setSize(hoverSize);

        // set location
        boolean showOnRight = Display.getDefault().map(chart.getPlotArea(),
                null, x + hoverSize.x, y).x < Display.getDefault().getBounds().width;
        int hoverX = showOnRight ? x : x - hoverSize.x;
        int hoverY = showBelow ? y : y - hoverSize.y;
        hover.setLocation(chart.getPlotArea().toDisplay(hoverX, hoverY));

        // set region
        Region region = hover.getRegion();
        if (region != null) {
            region.dispose();
        }
        region = getHoverRegion(textExtent, showOnRight, showBelow);
        hover.setRegion(region);

        hover.redraw();
        hover.setVisible(true);
    }

    /**
     * Gets the hover region. The direction of hover arrow can be changed so
     * that hover is completely shown within screen.
     * 
     * @param textExtent
     *            The text extent
     * @param showOnRight
     *            <tt>true</tt> to show hover on the right hand side of data
     *            point
     * @param showBelow
     *            <tt>true</tt> to show hover below data point
     * @return The hover region
     */
    private Region getHoverRegion(Point textExtent, boolean showOnRight,
            boolean showBelow) {
        Region region = new Region();
        int[] pointArray;
        if (showOnRight && showBelow) {
            pointArray = new int[] { 0, 0, OFFSET + 5, OFFSET,
                    textExtent.x + MARGIN * 2 + OFFSET, OFFSET,
                    textExtent.x + MARGIN * 2 + OFFSET, textExtent.y + OFFSET,
                    OFFSET, textExtent.y + OFFSET, OFFSET, OFFSET + 5, 0, 0 };
        } else if (!showOnRight && showBelow) {
            pointArray = new int[] { OFFSET, OFFSET,
                    textExtent.x + MARGIN * 2 + OFFSET - 5, OFFSET,
                    textExtent.x + MARGIN * 2 + OFFSET * 2, 0,
                    textExtent.x + MARGIN * 2 + OFFSET, OFFSET + 5,
                    textExtent.x + MARGIN * 2 + OFFSET, textExtent.y + OFFSET,
                    OFFSET, textExtent.y + OFFSET, OFFSET, OFFSET };
        } else if (showOnRight && !showBelow) {
            pointArray = new int[] { OFFSET, OFFSET,
                    textExtent.x + MARGIN * 2 + OFFSET, OFFSET,
                    textExtent.x + MARGIN * 2 + OFFSET, textExtent.y + OFFSET,
                    OFFSET + 5, textExtent.y + OFFSET, 0,
                    textExtent.y + OFFSET * 2, OFFSET,
                    textExtent.y + OFFSET - 5, OFFSET, OFFSET };
        } else /* if (!showOnRight && !showBelow) */{
            pointArray = new int[] { OFFSET, OFFSET,
                    textExtent.x + MARGIN * 2 + OFFSET, OFFSET,
                    textExtent.x + MARGIN * 2 + OFFSET,
                    textExtent.y + OFFSET - 5,
                    textExtent.x + MARGIN * 2 + OFFSET * 2,
                    textExtent.y + OFFSET * 2,
                    textExtent.x + MARGIN * 2 + OFFSET - 5,
                    textExtent.y + OFFSET, OFFSET, textExtent.y + OFFSET,
                    OFFSET, OFFSET };
        }
        region.add(pointArray);
        return region;
    }

    /**
     * Gets the text extent.
     * 
     * @param hover
     *            The hover
     * @param text
     *            The text
     * @return The text extent
     */
    private Point getExtent(Shell hover, String text) {
        GC gc = new GC(hover);
        Point textExtent = gc.textExtent(text);
        gc.dispose();
        return textExtent;
    }

    /**
     * Gets the inverted series index that is the nearest to mouse position.
     * 
     * @param desiredX
     *            The desired x pixel coordinate
     * @return The inverted series index, or <tt>null</tt> if not found
     */
    private Integer getInvertedSeriesIndex(int desiredX) {
        long desiredTime = (long) chart.getAxisSet().getAxes()[0]
                .getDataCoordinate(desiredX);

        // find largest size of series
        ISeries largestSeries = null;
        for (ISeries series : chart.getSeriesSet().getSeries()) {
            int length = series.getXSeries().length;
            if (largestSeries == null
                    || largestSeries.getXSeries().length < length) {
                largestSeries = series;
            }
        }
        if (largestSeries == null) {
            return null;
        }

        // find the time series index
        Date[] dates = largestSeries.getXDateSeries();
        for (int i = 0; i < dates.length; i++) {
            if (dates[i].getTime() < desiredTime && i != dates.length - 1) {
                continue;
            }
            int nearestIndex;
            if (i > 0
                    && dates[i].getTime() - desiredTime > desiredTime
                            - dates[i - 1].getTime()) {
                nearestIndex = i - 1;
            } else {
                nearestIndex = i;
            }
            return dates.length - nearestIndex;
        }
        return null;
    }

    /**
     * Shows the tool tip.
     * 
     * @param value
     *            The value
     * @return The formatted value
     */
    private String getFormattedValue(Object value) {
        AxisUnit axisUnit = chart.getAttributeGroup().getAxisUnit();
        if (axisUnit == AxisUnit.Percent) {
            return new DecimalFormat("###%").format(value); //$NON-NLS-1$
        } else if (axisUnit == AxisUnit.MBytes) {
            return new DecimalFormat("#####.#M").format(value); //$NON-NLS-1$
        } else if (axisUnit == AxisUnit.Count) {
            return NumberFormat.getIntegerInstance().format(value);
        }
        return value.toString();
    }

    /**
     * Disposes the hovers.
     */
    private void disposeHovers() {
        for (Shell hover : hovers.values()) {
            Region region = hover.getRegion();
            if (region != null) {
                region.dispose();
            }
            hover.dispose();
        }
        hovers.clear();
        texts.clear();
    }
}
