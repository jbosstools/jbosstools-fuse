/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The action to toggle orientation.
 */
public class ToggleOrientationAction extends Action {

    /** The dialog settings section name. */
    private static final String DIALOG_SETTINGS_NAME = "ToggleOrientationAction"; //$NON-NLS-1$

    /** The sash form. */
    private ISashForm sashForm;

    /** The orientation. */
    private Orientation orientation;

    /**
     * The constructor.
     * 
     * @param orientation
     *            The orientation
     */
    public ToggleOrientationAction(Orientation orientation) {
        super(orientation.label, AS_RADIO_BUTTON);
        setImageDescriptor(Activator.getImageDescriptor(orientation.imagePath));
        setId(getClass().getName() + orientation.toString());

        this.orientation = orientation;
    }

    /**
     * The constructor.
     * 
     * @param sashForm
     *            The sash form
     * @param orientation
     *            The orientation
     */
    public ToggleOrientationAction(ISashForm sashForm, Orientation orientation) {
        this(orientation);
        this.sashForm = sashForm;

        refreshCheckState();
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        if (sashForm != null) {
            sashForm.setOrientation(orientation);
            storeCheckState();
        }
    }

    /**
     * Sets the sash form.
     * 
     * @param sashForm
     *            The sash form
     */
    public void setSashForm(ISashForm sashForm) {
        this.sashForm = sashForm;
        refreshCheckState();
    }

    /**
     * Refreshes the check state.
     */
    private void refreshCheckState() {
        IDialogSettings settings = Activator.getDefault().getDialogSettings(
                DIALOG_SETTINGS_NAME);
        String key = sashForm.getClass().getCanonicalName();
        String orientationString = settings.get(key);
        if ((orientationString != null && Orientation
                .valueOf(orientationString) == orientation)
                || (orientationString == null && orientation == Orientation.AUTOMATIC)) {
            setChecked(true);
        }
    }

    /**
     * Stores the check state.
     */
    private void storeCheckState() {
        IDialogSettings settings = Activator.getDefault().getDialogSettings(
                DIALOG_SETTINGS_NAME);
        String key = sashForm.getClass().getCanonicalName();
        settings.put(key, orientation.name());
    }

    /**
     * The sash form.
     */
    public interface ISashForm {

        /**
         * Sets the orientation.
         * 
         * @param orientation
         *            The orientation
         */
        void setOrientation(Orientation orientation);
    }

    /**
     * The orientation.
     */
    public enum Orientation {

        /** The vertical orientation. */
        VERTICAL(Messages.verticalOrientationLabel,
                ISharedImages.VERTICAL_LAYOUT_IMG_PATH),

        /** The horizontal orientation. */
        HORIZONTAL(Messages.horizontalOrientationLabel,
                ISharedImages.HORIZONTAL_LAYOUT_IMG_PATH),

        /** The automatic orientation. */
        AUTOMATIC(Messages.automaticOrientationLabel,
                ISharedImages.AUTOMATIC_LAYOUT_IMG_PATH),

        /** The single orientation. */
        SINGLE(Messages.singleOrientationLabel,
                ISharedImages.SINGLE_LAYOUT_IMG_PATH);

        /** The displayed label. */
        public final String label;

        /** The image path. */
        public final String imagePath;

        /**
         * The constructor.
         * 
         * @param label
         *            The displayed label
         * @param imagePath
         *            The image path
         */
        private Orientation(String label, String imagePath) {
            this.label = label;
            this.imagePath = imagePath;
        }
    }
}
