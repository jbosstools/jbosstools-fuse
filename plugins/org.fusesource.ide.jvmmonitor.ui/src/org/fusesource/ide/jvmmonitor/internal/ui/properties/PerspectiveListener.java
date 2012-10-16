/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import org.eclipse.ui.IPageService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.properties.PropertySheet;

/**
 * The perspective listener.
 */
public class PerspectiveListener implements IPerspectiveListener {

    /** The property section. */
    private AbstractJvmPropertySection propertySection;

    /** The page service. */
    private IPageService service;

    /** The property sheet. */
    private PropertySheet propertySheet;

    /**
     * The constructor.
     * 
     * @param propertySection
     *            The property section
     * @param pageSite
     *            The workbench page
     * @param propertySheet
     *            The property sheet
     */
    public PerspectiveListener(AbstractJvmPropertySection propertySection,
            IPageSite pageSite, PropertySheet propertySheet) {
        this.propertySection = propertySection;
        this.propertySheet = propertySheet;

        service = (IPageService) pageSite.getService(IPageService.class);
        if (service == null) {
            throw new IllegalStateException("page service not found"); //$NON-NLS-1$
        }
        service.addPerspectiveListener(this);
    }

    /*
     * @see IPerspectiveListener#perspectiveActivated(IWorkbenchPage,
     * IPerspectiveDescriptor)
     */
    @Override
    public void perspectiveActivated(IWorkbenchPage page,
            IPerspectiveDescriptor perspective) {
        if (!propertySheet.isPinned()) {
            propertySection.deactivateSection();
        }
    }

    /*
     * @see IPerspectiveListener#perspectiveChanged(IWorkbenchPage,
     * IPerspectiveDescriptor, String)
     */
    @Override
    public void perspectiveChanged(IWorkbenchPage page,
            IPerspectiveDescriptor perspective, String changeId) {
        // do nothing
    }

    /**
     * Disposes the resources.
     */
    protected void dispose() {
        if (service != null) {
            service.removePerspectiveListener(this);
        }
    }
}
