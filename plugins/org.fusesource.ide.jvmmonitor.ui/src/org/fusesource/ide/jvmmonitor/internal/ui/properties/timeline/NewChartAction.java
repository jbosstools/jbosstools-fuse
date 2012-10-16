/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.util.ArrayList;
import java.util.List;

import javax.management.ObjectName;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
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
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The action to create a new chart.
 */
public class NewChartAction extends Action {

    /** The property section. */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param section
     *            The property section
     */
    public NewChartAction(AbstractJvmPropertySection section) {
        setText(Messages.newChartLabel);
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.NEW_CHART_IMG_PATH));
        setDisabledImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.DISABLED_NEW_CHART_IMG_PATH));
        setId(getClass().getName());

        this.section = section;
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        IActiveJvm jvm = section.getJvm();
        if (jvm == null) {
            return;
        }

        NewChartDialog dialog = new NewChartDialog(Display.getDefault()
                .getActiveShell(), jvm);

        if (dialog.open() == Window.OK) {
            performNew(dialog.getChartTitle(), dialog.getAxisUnit(),
                    dialog.getAttributes(), jvm);
        }
    }

    /**
     * Performs creating a new chart.
     * 
     * @param chartTitle
     *            The chart title
     * @param axisUnit
     *            The axis unit
     * @param attributes
     *            The attributes
     * @param jvm
     *            The jvm
     */
    private void performNew(String chartTitle, AxisUnit axisUnit,
            List<MBeanAttribute> attributes, IActiveJvm jvm) {
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
                Activator.log(Messages.addAttributeFailedMsg, e);
            }
        }
    }

    /**
     * The dialog to create a new chart.
     */
    private static class NewChartDialog extends ConfigureChartDialog {

        /**
         * The constructor.
         * 
         * @param shell
         *            The parent shell
         * @param jvm
         *            The JVM
         */
        protected NewChartDialog(Shell shell, IActiveJvm jvm) {
            super(shell, "", null, new ArrayList<MBeanAttribute>(), jvm, true); //$NON-NLS-1$
        }

        /*
         * @see Dialog#create()
         */
        @Override
        public void create() {
            super.create();
            getShell().setText(Messages.newChartDialogTitle);
        }

        /*
         * @see Window#configureShell(Shell)
         */
        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            PlatformUI.getWorkbench().getHelpSystem()
                    .setHelp(newShell, IHelpContextIds.NEW_CHART_DIALOG);
        }
    }
}