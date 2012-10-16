/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabContents;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * The part listener.
 */
public class PartListener implements IPartListener2 {

    /** The tabbed property sheet page. */
    private TabbedPropertySheetPage tabbedPropertySheetPage;

    /** The property section. */
    private AbstractJvmPropertySection propertySection;

    /**
     * The constructor.
     * 
     * @param propertySection
     *            The property section
     * @param tabbedPropertySheetPage
     *            The tabbed property sheet page
     */
    public PartListener(AbstractJvmPropertySection propertySection,
            TabbedPropertySheetPage tabbedPropertySheetPage) {
        this.tabbedPropertySheetPage = tabbedPropertySheetPage;
        this.propertySection = propertySection;
        tabbedPropertySheetPage.getSite().getPage().addPartListener(this);
    }

    /*
     * @see IPartListener2#partActivated(IWorkbenchPartReference)
     */
    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        // do nothing
    }

    /*
     * @see IPartListener2#partBroughtToTop(IWorkbenchPartReference)
     */
    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        // do nothing
    }

    /*
     * @see IPartListener2#partClosed(IWorkbenchPartReference)
     */
    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        propertySection.partClosed();
    }

    /*
     * @see IPartListener2#partDeactivated(IWorkbenchPartReference)
     */
    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        // do nothing
    }

    /*
     * @see IPartListener2#partHidden(IWorkbenchPartReference)
     */
    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        if (validatePart(partRef)) {
            propertySection.deactivateSection();
        }
    }

    /*
     * @see IPartListener2#partInputChanged(IWorkbenchPartReference)
     */
    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
        // do nothing
    }

    /*
     * @see IPartListener2#partOpened(IWorkbenchPartReference)
     */
    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        // do nothing
    }

    /*
     * @see IPartListener2#partVisible(IWorkbenchPartReference)
     */
    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        if (validatePart(partRef)) {
            propertySection.activateSection();
        }
    }

    /**
     * disposes the resources.
     */
    protected void dispose() {
        tabbedPropertySheetPage.getSite().getPage().removePartListener(this);
    }

    /**
     * Validates the given part.
     * 
     * @param partRef
     *            The part reference
     * @return True if the given part is valid for further operation
     */
    private boolean validatePart(IWorkbenchPartReference partRef) {

        // check if the part is a property sheet
        if (!partRef.getId().equals(IPageLayout.ID_PROP_SHEET)) {
            return false;
        }

        // check if the property sheet is the corresponding one
        if (!propertySection.getPropertySheetId().equals(partRef.getPart(false).toString())) {
            return false;
        }

        // checks if the section is the corresponding one
        TabContents currentTab = tabbedPropertySheetPage.getCurrentTab();
        if (currentTab == null) {
            return false;
        }
        ISection[] sections = currentTab.getSections();
        if (sections == null || sections.length == 0
                || !propertySection.equals(sections[0])) {
            return false;
        }

        return true;
    }
}
