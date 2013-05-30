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

package org.fusesource.ide.camel.editor.outline.tree;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.widgets.Widget;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.outline.RiderOutlinePage;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;


/**
 * @author lhein
 */
public class RouteTreeEditPartFactory implements EditPartFactory {
	private final RiderOutlinePage owner;

	public RouteTreeEditPartFactory(RiderOutlinePage owner) {
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		AbstractTreeEditPart part = null;

		if (model instanceof RouteSupport) {
			part = new RouteTreeEditPart();
		} else if (model instanceof RouteContainer) {
			part = new ContainerTreeEditPart();
		} else {
			part = new GenericTreeEditPart();
		}
		if (part != null) {
			part.setModel(model);
			Widget widget = null;
			if (context != null) {
				EditPartViewer viewer = context.getViewer();
				if (viewer == null) {
					RootEditPart root = context.getRoot();
					if (root != null) {
						viewer = root.getViewer();
					}
				}
				if (viewer != null) {
					widget = viewer.getControl();
				}
			}
			if (widget == null) {
				widget = owner.getTree();
			}
			if (widget != null) {
				part.setWidget(widget);
			} else {
				Activator.getLogger().debug("Could not find widget yet for part " + part);
			}
		}
		
//		System.err.println("Created EditPart for Object: " + model + "[" + model.hashCode() + "] with context: " + (context != null ? context.getModel() : context) + "[" + (context != null && context.getModel() != null ? context.getModel().hashCode() : "null") + "]");
		
		return part;
	}
}