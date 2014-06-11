/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
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
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The action to clear SWT resources.
 */
public class ClearSWTResourceAction extends Action {

    /** The SWT resource page. */
    SWTResourcesPage resourcePage;

    /** The property section. */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param resourcePage
     *            The SWT resource page
     * @param section
     *            The property section
     */
    public ClearSWTResourceAction(SWTResourcesPage resourcePage,
            AbstractJvmPropertySection section) {
        setText(Messages.clearResourcesLabel);
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.CLEAR_IMG_PATH));
        setDisabledImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.DISABLED_CLEAR_IMG_PATH));
        setId(getClass().getName());

        this.resourcePage = resourcePage;
        this.section = section;
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        new Job(Messages.clearResourcesJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IActiveJvm jvm = section.getJvm();
                if (jvm == null) {
                    return Status.CANCEL_STATUS;
                }

                try {
                    jvm.getSWTResourceMonitor().clear();
                } catch (JvmCoreException e) {
                    Activator.log(Messages.clearSWTResoucesFailedMsg, e);
                    return Status.CANCEL_STATUS;
                }
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        resourcePage.refresh(true);
                    }
                });
                return Status.OK_STATUS;
            }
        }.schedule();
    }
}
