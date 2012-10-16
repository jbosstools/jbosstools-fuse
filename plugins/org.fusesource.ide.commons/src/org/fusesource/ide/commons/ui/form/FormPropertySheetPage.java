package org.fusesource.ide.commons.ui.form;

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
