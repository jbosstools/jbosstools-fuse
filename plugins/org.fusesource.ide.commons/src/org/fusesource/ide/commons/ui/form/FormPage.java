package org.fusesource.ide.commons.ui.form;

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
			final ISelectionListener listener = (ISelectionListener) form;
			selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					Object source = event.getSource();
					IWorkbenchPart part = null;
					if (source instanceof IWorkbenchPart) {
						part = (IWorkbenchPart) source;
					} else {
						part = pageSite.getPage().getActivePart();
					}
					listener.selectionChanged(part, event.getSelection());
				}
			});
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
