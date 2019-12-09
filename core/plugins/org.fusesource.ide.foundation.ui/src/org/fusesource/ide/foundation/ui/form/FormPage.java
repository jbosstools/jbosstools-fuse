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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;



public class FormPage extends Page {
	private FormSupport form;
	private Composite control;
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            Object source = event.getSource();
            IWorkbenchPart part = null;
            if (source instanceof IWorkbenchPart) {
                part = (IWorkbenchPart) source;
            } else {
                part = getSite().getPage().getActivePart();
            }
            ((ISelectionListener)form).selectionChanged(part, event.getSelection());
        }
    };

	public FormPage(FormSupport form) {
		this.form = form;
	}


	public FormSupport getForm() {
		return form;
	}


	@Override
	public void init(final IPageSite pageSite) {
		super.init(pageSite);
		ISelectionProvider selectionProvider = pageSite.getSelectionProvider();
		if (selectionProvider != null && form instanceof ISelectionListener) {
			selectionProvider.addSelectionChangedListener(selectionListener);
		}
	}


	@Override
	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());
		//control.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.createDialogArea(control);
	}


	@Override
	public void dispose() {
	    final IPageSite pageSite = getSite();
	    if (pageSite != null) {
            ISelectionProvider selectionProvider = pageSite.getSelectionProvider();
            if (selectionProvider != null && form instanceof ISelectionListener) {
                selectionProvider.removeSelectionChangedListener(selectionListener);
            }
	    }
		form.dispose();
		super.dispose();
	}

	@Override
	public Control getControl() {
		return control;
		//return form.getControl();
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}
}
