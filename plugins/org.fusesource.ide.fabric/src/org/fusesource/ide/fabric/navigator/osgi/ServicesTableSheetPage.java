package org.fusesource.ide.fabric.navigator.osgi;


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.commons.ui.views.IViewPage;


public class ServicesTableSheetPage extends PropertySourceTableSheetPage {
	private final ServicesNode osgiNode;

	public ServicesTableSheetPage(ServicesNode osgiNode) {
		super(osgiNode, ServicesTableSheetPage.class.getName());
		this.osgiNode = osgiNode;
	}


	public ServicesNode getNode() {
		return osgiNode;
	}



	@Override
	public void setView(IViewPage view) {
		super.setView(view);

		/*
		Action setVersionAction = new ActionSupport("Set Version") {};

		getTableView().addLocalMenuActions(setVersionAction);
		 */
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		IMenuManager menu = actionBars.getMenuManager();

		/** TODO add start/stop stuff */

	}

}