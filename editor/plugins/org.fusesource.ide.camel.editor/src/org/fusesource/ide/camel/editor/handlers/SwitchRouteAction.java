/**
 * 
 */
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

package org.fusesource.ide.camel.editor.handlers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author lhein
 *
 */
public class SwitchRouteAction extends Action {

	private RouteSupport route;
	private int counter;
	
	/**
	 * 
	 * @param route
	 */
	public SwitchRouteAction(RouteSupport route, int counter) {
		this.route = route;
		this.counter = counter;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		final RiderDesignEditor editor = Activator.getDiagramEditor();
		editor.setSelectedRouteIndex(counter);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		String id = route.getId();
		if (Strings.isBlank(id)) {
			id = "" + (counter + 1);
		}
		return "Route: " + id;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getDefault().getImageDescriptor("route16.png");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isChecked()
	 */
	@Override
	public boolean isChecked() {
		final RiderDesignEditor editor = Activator.getDiagramEditor();
		final RouteSupport selectedRoute = editor.getSelectedRoute();
		return route == selectedRoute;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getStyle()
	 */
	@Override
	public int getStyle() {
		return IAction.AS_CHECK_BOX;
	}
}
