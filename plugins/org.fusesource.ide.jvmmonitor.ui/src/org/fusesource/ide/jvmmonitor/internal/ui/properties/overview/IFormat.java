/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.overview;

import java.text.NumberFormat;

/**
 * The format.
 */
public interface IFormat {

    /** The milliseconds format. */
    public static final IFormat MILLISEC_FORMAT = new IFormat() {
        final static long SEC = 1000;
        final static long MIN = 60 * SEC;
        final static long HRS = 60 * MIN;
        final static long DAY = 24 * HRS;

        @Override
        public String format(Object object) {
            if (!(object instanceof Long)) {
                throw new IllegalArgumentException("unexpected type");//$NON-NLS-1$
            }
            long millisec = (Long) object;

            long days = millisec / DAY;
            millisec %= DAY;
            long hrs = millisec / HRS;
            millisec %= HRS;
            long min = millisec / MIN;
            millisec %= MIN;
            double sec = millisec / (double) SEC;

            if (days > 0) {
                return String.format("%d " + Messages.daysLabel + " %d " //$NON-NLS-1$ //$NON-NLS-2$
                        + Messages.hoursLabel + " %d " + Messages.minLabel //$NON-NLS-1$
                        + " %.0f " + Messages.secLabel, days, hrs, min, sec); //$NON-NLS-1$
            } else if (hrs > 0) {
                return String.format("%d " + Messages.hoursLabel + " %d " //$NON-NLS-1$ //$NON-NLS-2$
                        + Messages.minLabel + " %.0f " + Messages.secLabel, //$NON-NLS-1$
                        hrs, min, sec);
            } else if (min > 0) {
                return String.format("%d " + Messages.minLabel + " %.0f " //$NON-NLS-1$ //$NON-NLS-2$
                        + Messages.secLabel, min, sec);
            } else {
                return String.format("%.3f " + Messages.secLabel, sec); //$NON-NLS-1$
            }
        }
    };

    /** The nanoseconds format. */
    public static final IFormat NANOSEC_FORMAT = new IFormat() {
        final static long SEC = 1000000000;

        @Override
        public String format(Object object) {
            if (!(object instanceof Long)) {
                throw new IllegalArgumentException("unexpected type");//$NON-NLS-1$
            }
            long nanosec = (Long) object;

            double sec = nanosec / (double) SEC;

            return String.format("%.3f " + Messages.secLabel, sec); //$NON-NLS-1$
        }
    };

    /** The bytes format. */
    public static final IFormat BYTES_FORMAT = new IFormat() {

        @Override
        public String format(Object object) {
            if (!(object instanceof Long)) {
                throw new IllegalArgumentException("unexpected type"); //$NON-NLS-1$
            }
            long bytes = (Long) object;

            return NumberFormat.getInstance().format(bytes / 1024) + " " //$NON-NLS-1$
                    + Messages.kBytesLabel;
        }
    };

    /**
     * Formats the given object.
     * 
     * @param object
     *            The object
     * @return the formatted string
     */
    public String format(Object object);
}
