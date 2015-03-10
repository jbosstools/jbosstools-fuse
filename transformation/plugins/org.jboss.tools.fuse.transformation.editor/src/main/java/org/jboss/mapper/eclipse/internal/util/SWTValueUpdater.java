/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.util;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.DisposeEvent;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.databinding.viewers.IViewerObservable;
import org.eclipse.jface.internal.databinding.swt.WidgetListenerUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * SWTValueUpdater
 * <p/>
 * Handles manual updating of a binding (i.e. Binding.updateTargetToModel())
 * when the UpdateValueStrategy is configured for manual updates
 * (POLICY_ON_REQUEST or POLICY_CONVERT) and the target observable is an SWT
 * control. Updates are triggered on SWT.FocusOut and SWT.DefaultSelection. In
 * addition, the updater will reset the target value when the user presses ESC
 * (i.e. Binding.updateModelToTarget()).
 */
@SuppressWarnings("restriction")
public final class SWTValueUpdater implements Listener, IDisposeListener, IValueChangeListener {

    /**
     * Attach a value updater to the binding.
     *
     * @param binding the binding to attach to.
     * @return the binding, useful for chaining.
     */
    @SuppressWarnings("unused")
    public static Binding attach(final Binding binding) {
        new SWTValueUpdater(binding);
        return binding;
    }

    private Control control;
    private final org.eclipse.core.databinding.Binding binding;
    private final IObservable target;
    private boolean dirty;
    private boolean updating;

    private SWTValueUpdater(final org.eclipse.core.databinding.Binding binding) {
        this.binding = binding;
        target = binding.getTarget();
        if (target instanceof ISWTObservable
                && ((ISWTObservable) binding.getTarget()).getWidget() instanceof Control) {
            control = (Control) ((ISWTObservable) binding.getTarget()).getWidget();
        } else if (target instanceof IViewerObservable) {
            control = ((IViewerObservable) target).getViewer().getControl();
        } else {
            throw new IllegalArgumentException(
                    "target of binding must be an ISWTObservable whose widget is a Control.");
        }
        addListeners();
    }

    private void addListeners() {
        WidgetListenerUtil.asyncAddListener(control, SWT.KeyUp, this);
        WidgetListenerUtil.asyncAddListener(control, SWT.FocusOut, this);
        WidgetListenerUtil.asyncAddListener(control, SWT.FocusIn, this);
        WidgetListenerUtil.asyncAddListener(control, SWT.DefaultSelection, this);
        WidgetListenerUtil.asyncAddListener(control, SWT.Selection, this);
        WidgetListenerUtil.asyncAddListener(control, SWT.Dispose, this);
        target.addDisposeListener(this);
        ((IObservableValue) target).addValueChangeListener(this);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent(Event event) {
        if (updating) {
            return;
        }
        if (event.type == SWT.KeyUp) {
            if (event.keyCode == SWT.ESC) {
                if (binding.isDisposed()) {
                    dispose();
                    return;
                }
                updating = true;
                try {
                    binding.updateModelToTarget();
                } finally {
                    updating = false;
                }
                if (control instanceof Text) {
                    ((Text) control).setSelection(0, ((Text) control).getCharCount());
                }
                dirty = false;
            }
        } else if (event.type == SWT.FocusOut || event.type == SWT.DefaultSelection
                || event.type == SWT.Selection) {
            if (binding.isDisposed()) {
                dispose();
                return;
            }
            if (((IStatus) binding.getValidationStatus().getValue()).getSeverity() == IStatus.ERROR) {
                control.setFocus();
                return;
            }
            if (dirty) {
                updating = true;
                try {
                    binding.updateTargetToModel();
                } finally {
                    updating = false;
                }
                dirty = false;
            }
        } else if (event.type == SWT.FocusIn) {
            dirty = false;
        } else if (event.type == SWT.Dispose) {
            dispose();
        }

    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void handleValueChange(ValueChangeEvent event) {
        if (updating) {
            return;
        }
        dirty = true;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void handleDispose(DisposeEvent event) {
        dispose();
    }

    private synchronized void dispose() {
        if (control != null) {
            WidgetListenerUtil.asyncRemoveListener(control, SWT.KeyUp, this);
            WidgetListenerUtil.asyncRemoveListener(control, SWT.FocusOut, this);
            WidgetListenerUtil.asyncRemoveListener(control, SWT.FocusIn, this);
            WidgetListenerUtil.asyncRemoveListener(control, SWT.DefaultSelection, this);
            WidgetListenerUtil.asyncRemoveListener(control, SWT.Selection, this);
            WidgetListenerUtil.asyncRemoveListener(control, SWT.Dispose, this);
            control = null;
            target.removeDisposeListener(this);
            ((IObservableValue) target).removeValueChangeListener(this);
        }
    }
}
