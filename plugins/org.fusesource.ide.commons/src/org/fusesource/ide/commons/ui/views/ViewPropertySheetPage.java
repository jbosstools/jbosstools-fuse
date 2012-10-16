package org.fusesource.ide.commons.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.fusesource.ide.commons.Activator;


public class ViewPropertySheetPage extends Page implements IPropertySheetPage {

	private IViewPage view;
	private IWorkbenchPart part;
	private Composite control;

	public ViewPropertySheetPage() {
	}

	public ViewPropertySheetPage(IViewPage view) {
		this.view = view;
	}

	@Override
	public void createControl(Composite parent) {
		// PageBook.PageBookLayout doesn't like being switched to something else
		// so lets use a separate composite
		// control = view.getViewer().getControl()
		// view.createPartControl(parent);
		/*
		view.createPartControl(parent);
		 */
		control = new Composite(parent, SWT.NONE);
		view.createPartControl(control);

		if (view instanceof ISection) {
			ISection section = (ISection) view;
			section.aboutToBeShown();
		}
	}

	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		view.init(pageSite);
	}

	public IViewPage getView() {
		return view;
	}

	public void setView(IViewPage view) {
		this.view = view;
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void dispose() {
		view.dispose();
		/*
		if (control != null) {
			control.dispose();
			control = null;
		}
		 */
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (this.part != part) {
			this.part = part;
			IWorkbenchPartSite site = part.getSite();
			if (site instanceof IViewSite) {
				try {
					view.init((IViewSite) site);
				} catch (PartInitException e) {
					Activator.getLogger().warning(e);
				}
			}
		}
	}

	@Override
	public void setActionBars(IActionBars actionBars) {

	}

	@Override
	public void setFocus() {
		view.setFocus();
	}

}