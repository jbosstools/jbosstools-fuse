/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;


/**
 * The action to delete chart.
 */
public class DeleteChartAction extends Action {

    /** The timeline chart. */
    private TimelineChart chart;

    /** The property section. */
    private AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param chart
     *            The chart
     * @param section
     *            The property section
     */
    public DeleteChartAction(TimelineChart chart,
            AbstractJvmPropertySection section) {
        this.chart = chart;
        this.section = section;
        setText(Messages.deleteChartLabel);
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
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

        if (MessageDialog.openConfirm(chart.getShell(),
                Messages.confirmDeleteChartTitle, NLS.bind(
                        Messages.confirmDeleteChartMsg, chart.getSection()
                                .getText()))) {
            jvm.getMBeanServer().removeMonitoredAttributeGroup(
                    chart.getAttributeGroup().getName());
        }
    }
}
