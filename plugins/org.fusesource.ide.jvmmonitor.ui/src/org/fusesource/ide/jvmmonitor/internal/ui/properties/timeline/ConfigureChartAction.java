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
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanAttribute;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup.AxisUnit;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The action to configure chart.
 */
public class ConfigureChartAction extends Action {

    /** The chart. */
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
    public ConfigureChartAction(TimelineChart chart,
            AbstractJvmPropertySection section) {
        this.chart = chart;
        this.section = section;

        setText(Messages.configureChartLabel);
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.CONFIGURE_IMG_PATH));
        setId(getClass().getName());
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

        String title = chart.getSection().getText();
        IMonitoredMXBeanGroup group = chart.getAttributeGroup();
        AxisUnit unit = group.getAxisUnit();
        List<MBeanAttribute> attributes = getAttributes();

        ConfigureChartDialog dialog = new ConfigureChartDialog(
                chart.getShell(), title, unit, attributes, jvm, true);

        if (dialog.open() == Window.OK) {
            performConfiguration(dialog.getChartTitle(), dialog.getAxisUnit(),
                    dialog.getAttributes(), dialog.getRemovedAttributes());
        }
    }

    /**
     * Performs the configuration.
     * 
     * @param chartTitle
     *            The chart title
     * @param axisUnit
     *            The axis unit
     * @param attributes
     *            The attributes
     * @param removedAttributes
     *            The removed attributes
     */
    private void performConfiguration(String chartTitle, AxisUnit axisUnit,
            List<MBeanAttribute> attributes,
            List<MBeanAttribute> removedAttributes) {
        IMonitoredMXBeanGroup group = chart.getAttributeGroup();

        group.setName(chartTitle);
        group.setAxisUnit(axisUnit);

        for (MBeanAttribute attribute : attributes) {
            ObjectName objectName = attribute.getObjectName();
            String attributeName = attribute.getAttributeName();
            IMonitoredMXBeanAttribute monitoredAttribute = group.getAttribute(
                    objectName, attributeName);
            RGB rgb = attribute.getRgb();
            if (monitoredAttribute == null) {
                try {
                    group.addAttribute(objectName.getCanonicalName(),
                            attributeName, new int[] { rgb.red, rgb.green,
                                    rgb.blue });
                } catch (JvmCoreException e) {
                    Activator.log(Messages.addAttributeFailedMsg, e);
                }
            } else {
                monitoredAttribute.setRGB(rgb.red, rgb.green, rgb.blue);
            }
        }

        for (MBeanAttribute removedAttribute : removedAttributes) {
            group.removeAttribute(removedAttribute.getObjectName()
                    .getCanonicalName(), removedAttribute.getAttributeName());
        }
        chart.refresh();
    }

    /**
     * Gets the attributes.
     * 
     * @return The attributes
     */
    private List<MBeanAttribute> getAttributes() {
        List<MBeanAttribute> attributes = new ArrayList<MBeanAttribute>();
        for (IMonitoredMXBeanAttribute attribute : chart.getAttributeGroup()
                .getAttributes()) {
            ObjectName objectName = attribute.getObjectName();
            String attributeName = attribute.getAttributeName();
            int[] rgb = attribute.getRGB();
            attributes.add(new MBeanAttribute(objectName, attributeName,
                    new RGB(rgb[0], rgb[1], rgb[2])));
        }
        return attributes;
    }
}