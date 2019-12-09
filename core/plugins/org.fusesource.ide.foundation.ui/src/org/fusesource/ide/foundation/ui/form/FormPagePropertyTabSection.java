/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.ui.form;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.foundation.ui.util.Selections;

public abstract class FormPagePropertyTabSection extends AbstractPropertySection {

    private Composite container;
    private ControlListener controlListener;
    private FormPage page;
    private TabbedPropertySheetPage parentPage;

    @Override
    public void createControls(Composite parent, final TabbedPropertySheetPage atabbedPropertySheetPage) {
        super.createControls(parent, atabbedPropertySheetPage);
        parentPage = atabbedPropertySheetPage;
        container = getWidgetFactory().createFlatFormComposite(parent);
        controlListener = new ControlAdapter() {
            @Override
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
        if (first == null) {
            return;
        }
        page = createPage(first);
        if (page != null) {
        	page.init(parentPage.getSite());
            page.createControl(container);
            FormData data = new FormData();
            data.left = new FormAttachment(0, 0);
            data.right = new FormAttachment(100, 0);
            data.top = new FormAttachment(0, 0);
            data.bottom = new FormAttachment(100, 0);
            page.getControl().setLayoutData(data);

            page.getControl().addControlListener(controlListener);
        }

        container.layout(true, true);
        parentPage.resizeScrolledComposite();
    }

    @Override
    public void dispose() {
        if (page != null) {
            aboutToBeHidden();
            page.dispose();
            page = null;
        }
        super.dispose();
    }

    @Override
    public boolean shouldUseExtraSpace() {
        return true;
    }

    protected abstract FormPage createPage(Object selection);
}
