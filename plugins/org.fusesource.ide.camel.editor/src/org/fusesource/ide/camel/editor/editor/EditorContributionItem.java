package org.fusesource.ide.camel.editor.editor;

import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.editor.provider.generated.AddNodeMenuFactory;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.commons.ui.Menus;
import org.fusesource.ide.commons.util.Strings;


public class EditorContributionItem extends ContributionItem {

	public EditorContributionItem() {
	}

	@Override
	public void fill(Menu menu, int index) {
		super.fill(menu, index);

		String label = EditorMessages.camelMenuLabel;
		MenuItem subMenuItem = Menus.getMenuItemByText(menu, label);
		if (subMenuItem == null) {
			subMenuItem = new MenuItem(menu, SWT.CASCADE);
			subMenuItem.setText(label);
			// subMenuItem.setImage(DeployPlugin.getDefault().getImage("deploy.png"));
		}
		Menu subMenu = subMenuItem.getMenu();
		if (subMenu == null) {
			subMenu = new Menu(menu);
			subMenuItem.setMenu(subMenu);
		}
		final Menu theMenu = subMenu;
		subMenu.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event) {
				recreateMenu(theMenu);
			}
		});

		/*
		recreateMenu();
		manager.fill(menu, index);
		 */
	}

	private void recreateMenu(Menu routeMenu) {
		Menus.disposeItems(routeMenu);

		final RiderDesignEditor editor = Activator.getDiagramEditor();

		MenuItem addRouteItem = new MenuItem(routeMenu, SWT.PUSH);
		addRouteItem.setText(EditorMessages.addRouteCommandLabel);
		addRouteItem.setImage(Activator.getDefault().getImage("editor/add.png"));
		addRouteItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editor.addNewRoute();
				editor.fireModelChanged();
				editor.update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		MenuItem deleteRouteItem = new MenuItem(routeMenu, SWT.PUSH);
		deleteRouteItem.setText(EditorMessages.deleteRouteCommandLabel);
		deleteRouteItem.setImage(Activator.getDefault().getImage("editor/delete.gif"));
		deleteRouteItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editor.deleteRoute();
				if (editor.getModel().getChildren().size()<1) {
					editor.addNewRoute();
					editor.getEditor().recreateDesignPage();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		new MenuItem(routeMenu, SWT.SEPARATOR);

		MenuItem addMenuItem = new MenuItem(routeMenu, SWT.CASCADE);
		addMenuItem.setText(EditorMessages.camelMenuAddLabel);
		Menu addMenu = new Menu(addMenuItem);
		addMenuItem.setMenu(addMenu);

		AddNodeMenuFactory factory = new AddNodeMenuFactory();
		factory.fillMenu(editor, addMenu);

		RouteContainer model = editor.getModel();
		if (model != null) {
			final RouteSupport selectedRoute = editor.getSelectedRoute();
			List<AbstractNode> children = model.getChildren();
			int counter = 0;
			if (!children.isEmpty()) {
				new MenuItem(routeMenu, SWT.SEPARATOR);
			}
			for (AbstractNode node : children) {
				if (node instanceof RouteSupport) {
					final RouteSupport route = (RouteSupport) node;

					MenuItem menuItem = new MenuItem(routeMenu, SWT.RADIO);
					String id = route.getId();
					if (Strings.isBlank(id)) {
						id = "" + (counter + 1);
					}
					menuItem.setText("Route: " + id);
					menuItem.setImage(Activator.getDefault().getImage("route16.png"));
					// menuItem.setAccelerator(action.getAccelerator());
					final int routeIndex = counter;
					menuItem.addSelectionListener(new SelectionListener() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							editor.setSelectedRouteIndex(routeIndex);
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});
					if (route == selectedRoute) {
						menuItem.setSelection(true);
					}
					counter++;
				}
			}
		}
	}
}
