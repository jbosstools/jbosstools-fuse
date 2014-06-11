/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.overview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.JMException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The overview properties.
 */
public class OverviewProperties {

    /** The overview properties. */
    private Map<OverviewCategory, List<OverviewProperty>> overviewProperties;

    /** The active JVM. */
    private IActiveJvm activeJvm;

    /**
     * The constructor.
     */
    public OverviewProperties() {
        overviewProperties = new HashMap<OverviewCategory, List<OverviewProperty>>();
    }

    /**
     * Gets the overview properties.
     * 
     * @param category
     *            The overview category
     * @return The overview properties
     */
    public OverviewProperty[] getOverviewProperties(OverviewCategory category) {
        List<OverviewProperty> list = overviewProperties.get(category);
        if (list != null) {
            return list.toArray(new OverviewProperty[0]);
        }
        return new OverviewProperty[0];
    }

    /**
     * Refreshes the overview properties.
     * 
     * @param jvm
     *            The JVM
     */
    public void refresh(IActiveJvm jvm) {
        if (!jvm.equals(activeJvm)) {
            this.activeJvm = jvm;
            addPropertyItems();
        }

        refreshPropertyValues();
    }

    /**
     * Refreshes the property values.
     */
    private void refreshPropertyValues() {
        for (List<OverviewProperty> properties : overviewProperties.values()) {
            for (OverviewProperty property : properties) {
                try {
                    ObjectName objectName = ObjectName.getInstance(property
                            .getObjectName());
                    String attributeName = property.getAttributeName();

                    Object attribute = activeJvm.getMBeanServer().getAttribute(
                            objectName, attributeName);

                    property.setValue(attribute);
                } catch (JvmCoreException e) {
                    Activator.log(Messages.getMBeanAttributeFailedMsg, e);
                } catch (JMException e) {
                    Activator.log(IStatus.ERROR,
                            Messages.getMBeanAttributeFailedMsg, e);
                }
            }
        }
    }

    /**
     * Adds the property items.
     */
    private void addPropertyItems() {
        addRuntimeProperties();
        addMemoryProperties();
        addThreadProperties();
        addClassLoadingProperties();
        addCompilationProperties();
        addGarbageCollectionProperties();
        addOperatingSystemProperties();
    }

    /**
     * Adds the runtime properties.
     */
    private void addRuntimeProperties() {
        List<OverviewProperty> list = new ArrayList<OverviewProperty>();
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.runtimeNameLabel, "Name", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.inputArtumentsLabel, "InputArguments", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.systemPropertiesLabel, "SystemProperties", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.uptimeLabel, "Uptime", IFormat.MILLISEC_FORMAT, false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.classPathLabel, "ClassPath", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.libraryPathLabel, "LibraryPath", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.bootClassPathLabel, "BootClassPath", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.vmVendorLabel, "VmVendor", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.vmNameLabel, "VmName", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Runtime,
                Messages.vmVersionLabel, "VmVersion", false)); //$NON-NLS-1$
        overviewProperties.put(OverviewCategory.Runtime, list);
    }

    /**
     * Adds the memory properties.
     */
    private void addMemoryProperties() {
        List<OverviewProperty> list = new ArrayList<OverviewProperty>();
        list.add(new OverviewProperty(OverviewCategory.Memory,
                Messages.heapMemoryUsageLabel, "HeapMemoryUsage.used", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.Memory,
                Messages.maxHeapHemoryLabel,
                "HeapMemoryUsage.max", IFormat.BYTES_FORMAT, //$NON-NLS-1$
                true));
        list.add(new OverviewProperty(OverviewCategory.Memory,
                Messages.committedHeapMemoryLabel, "HeapMemoryUsage.committed", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.Memory,
                Messages.usedNonHeapMemoryLabel, "NonHeapMemoryUsage.used", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.Memory,
                Messages.maxNonHeapMemoryLabel, "NonHeapMemoryUsage.max", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.Memory,
                Messages.committedNonHeapMemoryLabel,
                "NonHeapMemoryUsage.committed", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.Memory,
                Messages.objectPendingFinalizationCountLabel,
                "ObjectPendingFinalizationCount", true)); //$NON-NLS-1$
        overviewProperties.put(OverviewCategory.Memory, list);
    }

    /**
     * Adds the thread properties.
     */
    private void addThreadProperties() {
        List<OverviewProperty> list = new ArrayList<OverviewProperty>();
        list.add(new OverviewProperty(OverviewCategory.Threading,
                Messages.totalStartedThreadCountLabel,
                "TotalStartedThreadCount", true)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Threading,
                Messages.threadCountLabel, "ThreadCount", true)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Threading,
                Messages.peakThreadCountLabel, "PeakThreadCount", true)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Threading,
                Messages.daemonThreadCountLabel, "DaemonThreadCount", true)); //$NON-NLS-1$
        overviewProperties.put(OverviewCategory.Threading, list);
    }

    /**
     * Adds the class loading properties.
     */
    private void addClassLoadingProperties() {
        List<OverviewProperty> list = new ArrayList<OverviewProperty>();
        list.add(new OverviewProperty(OverviewCategory.ClassLoading,
                Messages.totalLoadedClassCountLabel,
                "TotalLoadedClassCount", true)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.ClassLoading,
                Messages.loadedClassCountLabel, "LoadedClassCount", true)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.ClassLoading,
                Messages.unloadedClassCountLabel, "UnloadedClassCount", true)); //$NON-NLS-1$
        overviewProperties.put(OverviewCategory.ClassLoading, list);
    }

    /**
     * Adds the compilation properties.
     */
    private void addCompilationProperties() {
        List<OverviewProperty> list = new ArrayList<OverviewProperty>();
        list.add(new OverviewProperty(OverviewCategory.Compilation,
                Messages.totalCompilationTimeLabel,
                "TotalCompilationTime", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.Compilation,
                Messages.compilerNameLabel, "Name", false)); //$NON-NLS-1$
        overviewProperties.put(OverviewCategory.Compilation, list);
    }

    /**
     * Adds the garbage collection properties.
     */
    private void addGarbageCollectionProperties() {
        List<OverviewProperty> list = new ArrayList<OverviewProperty>();
        try {
            ObjectName queryObjectName = ObjectName
                    .getInstance("java.lang:type=GarbageCollector,name=*"); //$NON-NLS-1$
            Set<ObjectName> objectNames = activeJvm.getMBeanServer()
                    .queryNames(queryObjectName);

            for (ObjectName objectName : objectNames) {
                String name = objectName.getKeyProperty("name"); //$NON-NLS-1$
                list.add(new OverviewProperty(
                        OverviewCategory.GarbageCollector, NLS.bind(
                                Messages.collectionCountLabel, name),
                        "CollectionCount", name, null, true)); //$NON-NLS-1$
                list.add(new OverviewProperty(
                        OverviewCategory.GarbageCollector, NLS.bind(
                                Messages.collectionTimeLabel, name),
                        "CollectionTime", name, IFormat.MILLISEC_FORMAT, true)); //$NON-NLS-1$
            }
        } catch (MalformedObjectNameException e) {
            Activator.log(IStatus.ERROR, Messages.getObjectNameFailedMsg, e);
        } catch (JvmCoreException e) {
            Activator.log(Messages.getObjectNameFailedMsg, e);
        }
        overviewProperties.put(OverviewCategory.GarbageCollector, list);
    }

    /**
     * Adds the operating system properties.
     */
    private void addOperatingSystemProperties() {
        List<OverviewProperty> list = new ArrayList<OverviewProperty>();
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.totalPhysicalMemorySizeLabel,
                "TotalPhysicalMemorySize", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.freePhysicalMemorySizeLabel, "FreePhysicalMemorySize", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.totalSwapMemorySizeLabel, "TotalSwapSpaceSize", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.freeSwapMemorySizeLabel, "FreeSwapSpaceSize", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.committedVirtualMemorySizeLabel,
                "CommittedVirtualMemorySize", //$NON-NLS-1$
                IFormat.BYTES_FORMAT, true));
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.processCpuTimeLabel,
                "ProcessCpuTime", IFormat.NANOSEC_FORMAT, //$NON-NLS-1$
                true));
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.operationSystemNameLabel, "Name", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.operationSystemVersionLabel, "Version", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.operationSystemArchitectureLabel, "Arch", false)); //$NON-NLS-1$
        list.add(new OverviewProperty(OverviewCategory.OperatingSystem,
                Messages.avaliableProcessorsLabel, "AvailableProcessors", false)); //$NON-NLS-1$
        overviewProperties.put(OverviewCategory.OperatingSystem, list);
    }
}
