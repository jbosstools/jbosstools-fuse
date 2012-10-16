package org.fusesource.ide.commons.ui.views;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.IPageSite;

public interface IViewPage extends IViewPart {

	public void init(IPageSite pageSite);
}