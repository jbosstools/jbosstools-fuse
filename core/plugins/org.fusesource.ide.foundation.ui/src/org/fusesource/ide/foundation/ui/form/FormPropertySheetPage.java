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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

public class FormPropertySheetPage extends FormPage implements IPropertySheetPage {

	public FormPropertySheetPage(FormSupport form) {
		super(form);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		FormSupport form = getForm();
		if (form instanceof ISelectionListener) {
			ISelectionListener listener = (ISelectionListener) form;
			listener.selectionChanged(part, selection);
		}
	}



}
