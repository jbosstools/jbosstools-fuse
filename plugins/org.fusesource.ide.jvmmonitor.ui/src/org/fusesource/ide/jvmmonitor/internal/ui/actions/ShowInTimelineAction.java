/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.actions;

import java.util.ArrayList;
import java.util.List;

import javax.management.ObjectName;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup.AxisUnit;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline.ConfigureChartDialog;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline.MBeanAttribute;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The action to show in timeline tab.
 */
abstract public class ShowInTimelineAction extends Action implements
        ISelectionChangedListener {

    /** The selected attributes. */
    List<MBeanAttribute> selections;

    /** The property section. */
    private AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param section
     *            The property section
     */
    public ShowInTimelineAction(AbstractJvmPropertySection section) {
        setText(Messages.showInTimelineLabel);
        setId(getClass().getName());

        this.section = section;
        selections = new ArrayList<MBeanAttribute>();
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        IActiveJvm jvm = section.getJvm();
        if (!(event.getSelection() instanceof StructuredSelection)
                || jvm == null || !jvm.isConnected()) {
            setEnabled(false);
            return;
        }

        Object[] elements = ((StructuredSelection) event.getSelection())
                .toArray();
        if (elements.length == 0) {
            setEnabled(false);
            return;
        }

        boolean enabled = true;

        selections.clear();
        for (Object element : elements) {
            MBeanAttribute attribute = getMBeanAttribute(element);
            if (attribute != null) {
                selections.add(attribute);
            }
            if (!getEnabled(element)) {
                enabled = false;
            }
        }

        setEnabled(enabled);
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        IActiveJvm jvm = section.getJvm();
        if (jvm == null || selections.isEmpty()) {
            return;
        }
        ShowInTimelineDialog dialog = new ShowInTimelineDialog(Display
                .getDefault().getActiveShell(), selections.get(0)
                .getAttributeName(), jvm);

        if (dialog.open() == Window.OK) {
            performShowInTimeline(dialog.getChartTitle(), dialog.getAxisUnit(),
                    dialog.getAttributes());
        }
    }

    /**
     * Gets the state indicating if action can be enabled for the given element.
     * 
     * @param selectedElement
     *            The selected element
     * @return True if action can be enabled
     */
    abstract protected boolean getEnabled(Object selectedElement);

    /**
     * Gets the MBean attribute.
     * 
     * @param selectedElement
     *            The selected element
     * @return The MBean attribute
     */
    abstract protected MBeanAttribute getMBeanAttribute(Object selectedElement);

    /**
     * Performs showing a new chart with attributes in Timeline tab.
     * 
     * @param chartTitle
     *            The chart title
     * @param axisUnit
     *            The axis unit
     * @param attributes
     *            The attributes
     */
    private void performShowInTimeline(String chartTitle, AxisUnit axisUnit,
            List<MBeanAttribute> attributes) {
        IActiveJvm jvm = section.getJvm();
        if (jvm == null) {
            return;
        }

        IMonitoredMXBeanGroup group = jvm.getMBeanServer()
                .addMonitoredAttributeGroup(chartTitle, axisUnit);

        for (MBeanAttribute attribute : attributes) {
            ObjectName objectName = attribute.getObjectName();
            String attributeName = attribute.getAttributeName();
            RGB rgb = attribute.getRgb();
            try {
                group.addAttribute(objectName.getCanonicalName(),
                        attributeName,
                        new int[] { rgb.red, rgb.green, rgb.blue });
            } catch (JvmCoreException e) {
                Activator
                        .log(NLS.bind(Messages.addAttributeFailedMsg,
                                attributeName), e);
            }
        }
    }

    /**
     * The dialog to show a new chart in timeline tab.
     */
    private class ShowInTimelineDialog extends ConfigureChartDialog {

        /**
         * The constructor.
         * 
         * @param shell
         *            The parent shell
         * @param attributeName
         *            The attribute name
         * @param jvm
         *            The JVM
         */
        protected ShowInTimelineDialog(Shell shell, String attributeName,
                IActiveJvm jvm) {
            super(shell, attributeName, null, selections, jvm, false);
        }

        /*
         * @see Dialog#create()
         */
        @Override
        public void create() {
            super.create();
            getShell().setText(Messages.showInTimelineDialogTitle);
        }

        /*
         * @see Window#configureShell(Shell)
         */
        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            PlatformUI.getWorkbench().getHelpSystem()
                    .setHelp(newShell, IHelpContextIds.SHOW_IN_TIMELINE_DIALOG);
        }
    }
}
