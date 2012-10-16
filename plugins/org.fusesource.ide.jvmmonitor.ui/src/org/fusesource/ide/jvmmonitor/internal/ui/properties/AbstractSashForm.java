/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.ToggleOrientationAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.ToggleOrientationAction.ISashForm;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.ToggleOrientationAction.Orientation;

/**
 * The abstract sash form with orientation actions.
 */
public abstract class AbstractSashForm extends SashForm implements ISashForm {

    /** The orientation. */
    Orientation currentOrientation;

    /** The actions to toggle orientation. */
    private List<ToggleOrientationAction> orientationActions;

    /** The initial sash weight. */
    protected int[] initialSashWeights;

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent composite
     * @param actionBars
     *            The action bars
     * @param sashWeights
     *            The sash weight
     */
    public AbstractSashForm(Composite parent, IActionBars actionBars,
            int[] sashWeights) {
        super(parent, SWT.HORIZONTAL);
        this.initialSashWeights = sashWeights;

        addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                if (currentOrientation == Orientation.AUTOMATIC) {
                    updateOrientation(currentOrientation);
                }
            }
        });
    }

    /**
     * Creates the sash form controls.
     * 
     * @param sashForm
     *            The sash form
     * @param actionBars
     *            The action bars
     */
    abstract protected void createSashFormControls(SashForm sashForm,
            IActionBars actionBars);

    /*
     * @see ToggleOrientationAction.ISashForm#setOrientation(Orientation)
     */
    @Override
    public void setOrientation(Orientation orientation) {
        if (currentOrientation != orientation) {
            updateOrientation(orientation);
            currentOrientation = orientation;
        }
    }

    /**
     * Gets the orientation actions.
     * 
     * @return The orientation actions
     */
    public List<ToggleOrientationAction> getOrientationActions() {
        if (orientationActions == null) {
            orientationActions = new ArrayList<ToggleOrientationAction>();
            for (Orientation orientation : Orientation.values()) {
                ToggleOrientationAction action = new ToggleOrientationAction(
                        this, orientation);
                orientationActions.add(action);
                if (action.isChecked()) {
                    updateOrientation(orientation);
                    currentOrientation = orientation;
                }
            }
        }
        return orientationActions;
    }

    /**
     * Updates the orientation.
     * 
     * @param orientation
     *            The orientation
     */
    void updateOrientation(Orientation orientation) {
        if (orientation == Orientation.AUTOMATIC) {
            Rectangle r = getBounds();
            if (r.width < r.height) {
                setOrientation(SWT.VERTICAL);
            } else {
                setOrientation(SWT.HORIZONTAL);
            }
        }

        if (orientation == currentOrientation) {
            return;
        }

        int[] sashWeights = initialSashWeights;

        if (orientation == Orientation.HORIZONTAL) {
            setOrientation(SWT.HORIZONTAL);
        } else if (orientation == Orientation.VERTICAL) {
            setOrientation(SWT.VERTICAL);
        } else if (orientation == Orientation.SINGLE) {
            sashWeights = new int[] { 100, 0 };
        }

        setWeights(sashWeights);
    }
}
