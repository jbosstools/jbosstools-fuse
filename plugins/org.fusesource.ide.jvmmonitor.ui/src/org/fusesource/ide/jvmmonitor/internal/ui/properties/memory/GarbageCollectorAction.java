/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The action to run garbage collector.
 */
public class GarbageCollectorAction extends Action {

    /** The property section. */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param section
     *            The property section
     */
    public GarbageCollectorAction(AbstractJvmPropertySection section) {
        setText(Messages.garbageCollectorLabel);
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.TRASH_IMG_PATH));
        setDisabledImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.DISABLED_TRASH_IMG_PATH));
        setId(getClass().getName());
        
        this.section = section;
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        new Job(Messages.runGarbageCollectorJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IActiveJvm jvm = section.getJvm();
                if (jvm == null) {
                    return Status.CANCEL_STATUS;
                }
                
                try {
                    jvm.getMBeanServer().runGarbageCollector();
                } catch (JvmCoreException e) {
                    Activator.log(Messages.runGarbageCollectorFailedMsg, e);
                    return Status.CANCEL_STATUS;
                }

                return Status.OK_STATUS;
            }
        }.schedule();
    }
}
