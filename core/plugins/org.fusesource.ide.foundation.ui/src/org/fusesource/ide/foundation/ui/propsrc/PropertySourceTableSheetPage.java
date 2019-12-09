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

package org.fusesource.ide.foundation.ui.propsrc;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.foundation.ui.tree.HasOwner;
import org.fusesource.ide.foundation.ui.tree.HasRefreshableUI;
import org.fusesource.ide.foundation.ui.tree.Refreshable;
import org.fusesource.ide.foundation.ui.tree.Refreshables;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.foundation.ui.views.ViewPropertySheetPage;
import org.jboss.tools.jmx.core.tree.Node;



public class PropertySourceTableSheetPage extends ViewPropertySheetPage implements Refreshable {
	private PropertySourceTableView tableView;
	private final Node owner;

	public PropertySourceTableSheetPage(Node owner, String viewId) {
		this(owner, viewId, new PropertySourceTableView(viewId));
	}

	public PropertySourceTableSheetPage(Node ownerNode, String viewId, PropertySourceTableView tableView) {
		this.owner = ownerNode;
		this.tableView = tableView;
		setView(tableView);

		tableView.setDoubleClickAction(new Action() {

			@Override
			public void run() {
				Object first = Selections.getFirstSelection(getTableView().getViewer());
				if (first != null && owner instanceof HasRefreshableUI) {
					if (first instanceof HasOwner) {
						HasOwner ho = (HasOwner) first;
						first = ho.getOwner();
					}
					HasRefreshableUI hr = (HasRefreshableUI) owner;
					Selections.setSingleSelection(hr.getRefreshableUI(), first);
				}
			}
		});
	}

	public List<?> getPropertySources() {
		return tableView.getPropertySources();
	}

	public void setPropertySources(List<IPropertySource> propertySources) {
		tableView.setPropertySources(propertySources);
	}

	public PropertySourceTableView getTableView() {
		return tableView;
	}

	@Override
	public void refresh() {
		Refreshables.refresh(getTableView());
	}
}
