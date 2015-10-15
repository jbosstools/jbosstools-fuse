/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package old.org.fusesource.ide.camel.editor.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.handlers.SwitchRouteAction;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;

/**
 * @author lhein
 */
public class RoutesMenuContributionItem extends CompoundContributionItem {

	/**
	 * 
	 */
	public RoutesMenuContributionItem() {
		super();
	}

	/**
	 * @param id
	 */
	public RoutesMenuContributionItem(String id) {
		super(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> items = new ArrayList<IContributionItem>();
	    
    	final RiderDesignEditor editor = Activator.getDiagramEditor();

		RouteContainer model = editor.getModel();
		if (model != null) {
			List<AbstractNode> children = model.getChildren();
			int counter = 0;
			for (AbstractNode node : children) {
				if (node instanceof RouteSupport) {
					final RouteSupport route = (RouteSupport) node;
					ActionContributionItem item = new ActionContributionItem(new SwitchRouteAction(route, counter));
					items.add(item);
					counter++;
				}
			}
		}
	 
	    return items.toArray(new IContributionItem[items.size()]);
	}
}
