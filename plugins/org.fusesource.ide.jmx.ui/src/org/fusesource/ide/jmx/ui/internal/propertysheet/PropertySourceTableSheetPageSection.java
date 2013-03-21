/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.ui.internal.propertysheet;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.jmx.ui.JMXUIActivator;

/**
 * Wraps a PropertySourceTableSheetPage as a tabbed property section.
 */
public class PropertySourceTableSheetPageSection extends AbstractPropertySection {

    private Composite container;
    private ControlListener controlListener;
    private IPropertySheetPage page;
    private TabbedPropertySheetPage parentPage;

    @Override
    public void createControls(Composite parent, final TabbedPropertySheetPage atabbedPropertySheetPage) {
        super.createControls(parent, atabbedPropertySheetPage);
        parentPage = atabbedPropertySheetPage;
        container = getWidgetFactory().createFlatFormComposite(parent);
        controlListener = new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                atabbedPropertySheetPage.resizeScrolledComposite();
            }
        };
    }

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        if (part == getPart() && selection == getSelection()) {
            return;
        }
        super.setInput(part, selection);
        if (page != null) {
            page.getControl().removeControlListener(controlListener);
            aboutToBeHidden();
            page.dispose();
            page.getControl().dispose();
            page = null;
        }
        Object first = Selections.getFirstSelection(selection);
        if (first instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) first;
            page = (IPropertySheetPage) adaptable.getAdapter(IPropertySheetPage.class);
        }
        if (page != null) {
            if (page instanceof IPageBookViewPage) {
                try {
                    ((IPageBookViewPage) page).init(parentPage.getSite());
                } catch (PartInitException e) {
                    JMXUIActivator.log(e.getStatus());
                }
            }
            page.createControl(container);
            FormData data = new FormData();
            data.left = new FormAttachment(0, 0);
            data.right = new FormAttachment(100, 0);
            data.top = new FormAttachment(0, 0);
            data.bottom = new FormAttachment(100, 0);
            page.getControl().setLayoutData(data);

            page.selectionChanged(part, selection);
            page.getControl().addControlListener(controlListener);
        }

        container.layout(true, true);
        parentPage.resizeScrolledComposite();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (page != null) {
            page.dispose();
            page = null;
        }
    }

    @Override
    public void refresh() {
        if (page instanceof Refreshable) {
            ((Refreshable) page).refresh();
        }
    }

    @Override
    public boolean shouldUseExtraSpace() {
        return true;
    }

    @Override
    public void aboutToBeShown() {
        if (page instanceof PropertySourceTableSheetPage) {
            ((PropertySourceTableSheetPage) page).getTableView().aboutToBeShown();
        }
    }

    @Override
    public void aboutToBeHidden() {
        if (page instanceof PropertySourceTableSheetPage) {
            ((PropertySourceTableSheetPage) page).getTableView().aboutToBeHidden();
        }
    }

}
