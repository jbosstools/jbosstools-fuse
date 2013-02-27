/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The tree label provider.
 */
abstract public class AbstractLabelProvider implements ITableLabelProvider,
        ILabelProvider, ITableFontProvider, ISharedImages {

    /** The monospaced font names. */
    private static final String[] monospacedFonts = { "Courier New", //$NON-NLS-1$
            "Monospace" }; //$NON-NLS-1$

    /** The value of hundred. */
    protected static final double HUNDRED = 100.0;

    /** The paths for percentage image. */
    private static final String[] PERCENTAGE_IMG_PATHS = { PERCENT_0_IMG_PATH,
            PERCENT_2_IMG_PATH, PERCENT_4_IMG_PATH, PERCENT_7_IMG_PATH,
            PERCENT_10_IMG_PATH, PERCENT_15_IMG_PATH, PERCENT_20_IMG_PATH,
            PERCENT_25_IMG_PATH, PERCENT_30_IMG_PATH, PERCENT_35_IMG_PATH,
            PERCENT_40_IMG_PATH, PERCENT_50_IMG_PATH, PERCENT_60_IMG_PATH,
            PERCENT_70_IMG_PATH, PERCENT_80_IMG_PATH, PERCENT_90_IMG_PATH,
            PERCENT_100_IMG_PATH };

    /** The percentage categories. */
    private static final double[] percentageCategories = { 1, 3, 5.5, 8.5,
            12.5, 17.5, 22.5, 27.5, 32.5, 37.5, 45, 55, 65, 75, 85, 95 };

    /** The thread image. */
    private Image threadImage;

    /** The method image. */
    private Image methodImage;

    /** The percentage image */
    private Image[] perrcentageImages;

    /** The monospaced font. */
    private Font monospacedFont;

    /**
     * The constructor.
     */
    public AbstractLabelProvider() {
        perrcentageImages = new Image[PERCENTAGE_IMG_PATHS.length];
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        if (threadImage != null) {
            threadImage.dispose();
        }

        if (methodImage != null) {
            methodImage.dispose();
        }

        for (Image percentageImage : perrcentageImages) {
            if (percentageImage != null) {
                percentageImage.dispose();
            }
        }

        if (monospacedFont != null) {
            monospacedFont.dispose();
            monospacedFont = null;
        }
    }

    /*
     * @see IBaseLabelProvider#addListener(ILabelProviderListener)
     */
    @Override
    public void addListener(ILabelProviderListener listener) {
        // do nothing
    }

    /*
     * @see IBaseLabelProvider#isLabelProperty(Object, String)
     */
    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /*
     * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
     */
    @Override
    public void removeListener(ILabelProviderListener listener) {
        // do nothing
    }

    /**
     * Gets the thread image.
     * 
     * @return The image
     */
    protected Image getThreadImage() {
        if (threadImage == null || threadImage.isDisposed()) {
            threadImage = Activator.getImageDescriptor(THREAD_IMG_PATH)
                    .createImage();
        }
        return threadImage;
    }

    /**
     * Gets the method image.
     * 
     * @return The image
     */
    protected Image getMethodImage() {
        if (methodImage == null || methodImage.isDisposed()) {
            methodImage = Activator.getImageDescriptor(METHOD_IMG_PATH)
                    .createImage();
        }
        return methodImage;
    }

    /**
     * Gets the percentage image.
     * 
     * @param percentage
     *            The percentage
     * @return The image
     */
    protected Image getPercentageImage(double percentage) {
        int index = 0;
        while (index < percentageCategories.length) {
            if (percentage <= percentageCategories[index]) {
                break;
            }
            index++;
        }
        if (perrcentageImages[index] == null
                || perrcentageImages[index].isDisposed()) {
            perrcentageImages[index] = Activator.getImageDescriptor(
                    PERCENTAGE_IMG_PATHS[index]).createImage();
        }
        return perrcentageImages[index];
    }

    /**
     * Gets the milliseconds text with the given milliseconds.
     * 
     * @param milliseconds
     *            The milliseconds
     * @param length
     *            The string length
     * @return the milliseconds text
     */
    protected String getMillisecondsText(long milliseconds, int length) {
        return String.format(
                Messages.percentageLabel + length
                        + "d" + Messages.millisecondsLabel, //$NON-NLS-1$
                milliseconds).toString();
    }

    /**
     * Gets the monospaced font.
     * 
     * @param baseFont
     *            The base font
     * @return The monospaced font
     */
    protected Font getmonospacedFont(Font baseFont) {
        if (monospacedFont == null) {
            FontData[] fontData = baseFont.getFontData();

            for (String name : monospacedFonts) {
                if (!fontData[0].getName().equals(name)) {
                    fontData[0].setName(name);
                    break;
                }
            }
            monospacedFont = new Font(Display.getDefault(), fontData);
            return monospacedFont;
        }
        return monospacedFont;
    }
}
