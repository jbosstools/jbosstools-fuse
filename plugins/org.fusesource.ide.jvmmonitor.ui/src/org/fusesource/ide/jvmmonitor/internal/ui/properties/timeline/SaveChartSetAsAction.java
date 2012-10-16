/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanAttribute;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The action to save chart set as given name.
 */
public class SaveChartSetAsAction extends AbstractChartSetAction {

    /**
     * The constructor.
     * 
     * @param section
     *            The property section
     */
    public SaveChartSetAsAction(AbstractJvmPropertySection section) {
        super(section);
        setText(Messages.saveChartSetAsLabel);
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        SaveChartSetAsDialog dialog;
        try {
            dialog = new SaveChartSetAsDialog(section.getPart().getSite()
                    .getShell(), getChartSets(), getPredefinedChartSets());
        } catch (WorkbenchException e) {
            Activator.log(IStatus.ERROR,
                    Messages.openSaveChartSetAsDialogFailedMsg, e);
            return;
        } catch (IOException e) {
            Activator.log(IStatus.ERROR,
                    Messages.openSaveChartSetAsDialogFailedMsg, e);
            return;
        }

        if (dialog.open() == Window.OK) {
            try {
                performSave(dialog.getChartSet(), dialog.getChartSets());
            } catch (WorkbenchException e) {
                Activator.log(IStatus.ERROR, Messages.saveChartSetFailedMsg, e);
            } catch (IOException e) {
                Activator.log(IStatus.ERROR, Messages.saveChartSetFailedMsg, e);
            }
        }
    }

    /**
     * Performs saving chart set.
     * 
     * @param newChartSet
     *            The specified new chart set
     * @param chartSets
     *            The changed chart sets
     * @throws WorkbenchException
     * @throws IOException
     */
    private void performSave(String newChartSet, List<String> chartSets)
            throws WorkbenchException, IOException {
        IMemento oldChartSetsMemento = getChartSetsMemento();
        IMemento[] oldMementos;
        if (oldChartSetsMemento == null) {
            oldMementos = new IMemento[0];
        } else {
            oldMementos = oldChartSetsMemento.getChildren(CHART_SET);
        }

        XMLMemento chartSetsMemento = XMLMemento.createWriteRoot(CHART_SETS);
        for (String chartSet : chartSets) {
            for (IMemento memento : oldMementos) {
                if (chartSet.equals(memento.getID())
                        && !chartSet.equals(newChartSet)) {
                    chartSetsMemento.createChild(CHART_SET).putMemento(memento);
                    break;
                }
            }
        }

        addNewChartSet(chartSetsMemento, newChartSet);

        StringWriter writer = new StringWriter();
        chartSetsMemento.save(writer);
        Activator.getDefault().getPreferenceStore()
                .setValue(CHART_SETS, writer.getBuffer().toString());
    }

    /**
     * Adds the new chart set to the given memento.
     * 
     * @param memento
     *            The memento
     * @param chartSet
     *            The new chart set
     */
    private void addNewChartSet(XMLMemento memento, String chartSet) {
        IMemento chartSetMemento = memento.createChild(CHART_SET, chartSet);
        for (IMonitoredMXBeanGroup group : section.getJvm().getMBeanServer()
                .getMonitoredAttributeGroups()) {
            IMemento groupMemento = chartSetMemento.createChild(GROUP,
                    group.getName());
            groupMemento.putString(UNIT, group.getAxisUnit().name());

            for (IMonitoredMXBeanAttribute attribute : group.getAttributes()) {
                IMemento attributeMemento = groupMemento.createChild(ATTRIBUTE,
                        attribute.getAttributeName());
                attributeMemento.putString(OBJECT_NAME, attribute
                        .getObjectName().getCanonicalName());
                attributeMemento.putString(COLOR,
                        getRGBString(attribute.getRGB()));
            }
        }
    }

    /**
     * Gets the RGB string corresponding to the given RGB integer array.
     * 
     * @param rgb
     *            The RGB integer array
     * @return The RGB string "r,g,b" (e.g. "225,225,0")
     */
    private String getRGBString(int[] rgb) {
        return new StringBuffer().append(rgb[0]).append(',').append(rgb[1])
                .append(',').append(rgb[2]).toString();
    }
}
