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

package org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.impl.DiagramImpl;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.features.create.CreateFlowFeature;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;


/**
 * @author lhein
 */
public class CreateNodeConnectionFeature extends AbstractCustomFeature {

	private Class<? extends AbstractNode> clazz;

	public CreateNodeConnectionFeature(IFeatureProvider fp, Class<? extends AbstractNode> clazz) {
		super(fp);
		this.clazz = clazz;
	}

	@Override
	public String getDescription() {
		return "Adds and connects a node to the current node"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return "&Add Node"; //$NON-NLS-1$
	}

	@Override
	public boolean isAvailable(IContext context) {
		return true;
		// commented out because this logic prevents the add menu to be displayed on null selection or route selection
		/*
		ICustomContext cc = (ICustomContext)context;
		PictogramLink link = cc.getPictogramElements()[0].getLink();
		if (link != null) {
			EList<EObject> businessObjects = link.getBusinessObjects();
			if (businessObjects != null &&businessObjects.size() > 0) {
				EObject node = businessObjects.get(0);
				return  node instanceof AbstractNode;
			}
		}
		return false;
		 */
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	@Override
	public void execute(final ICustomContext context) {
		// create the object to add
		AbstractNode node = null;
		try {
			node = createNode();
		} catch (Exception ex) {
			return;
		}
		if (node instanceof RouteSupport) {
			DiagramOperations.addNewRoute(Activator.getDiagramEditor());
			return;
		}

		boolean firstNode = context.getPictogramElements().length == 1 && context.getPictogramElements()[0] instanceof DiagramImpl;
	
		// TODO use the x / y from context if there's no node selected!

		// calculate the location where to add the new element to the diagram
		int x = context.getPictogramElements().length > 0 ? context.getPictogramElements()[0].getGraphicsAlgorithm().getX() + 150 : 150;
		int y = context.getPictogramElements().length > 0 ? context.getPictogramElements()[0].getGraphicsAlgorithm().getY() : 150;

		// create the add context
		AddContext addContext = new AddContext();
		addContext.setNewObject(node);
		addContext.setLocation(x, y);
		addContext.setTargetContainer(getDiagram());

		// execute the add to diagram action
		IAddFeature addFeature = getFeatureProvider().getAddFeature(addContext);
		PictogramElement newNode = null;
		if (addFeature.canAdd(addContext)) {
			newNode = addFeature.add(addContext);
			if (newNode != null) {
				RouteSupport selectedRoute = Activator.getDiagramEditor().getSelectedRoute();
				if (selectedRoute != null) {
					selectedRoute.addChild(node);
				}
			}
		}

		PictogramElement srcState  = null;
		PictogramElement destState = null;
		if (!firstNode) {
			CreateFlowFeature createFeature = new CreateFlowFeature(getFeatureProvider());
			CreateConnectionContext connectContext = new CreateConnectionContext();
			srcState = context.getPictogramElements()[0];
			destState = getFeatureProvider().getPictogramElementForBusinessObject(node);
			if (srcState == destState) {
				System.out.println("Should not be the same element for different nodes: " + context.getPictogramElements()[0].getLink().getBusinessObjects().get(0) + " and " + node);
			}
			connectContext.setSourcePictogramElement(srcState);
			connectContext.setTargetPictogramElement(destState);
			Anchor srcAnchor = DiagramUtils.getAnchor(srcState);
			Anchor destAnchor = DiagramUtils.getAnchor(destState);
			if (destAnchor != null) {
				connectContext.setSourceAnchor(srcAnchor);
				connectContext.setTargetAnchor(destAnchor);
				if (createFeature.canCreate(connectContext)) {
					createFeature.execute(connectContext);
				}
			}
		}
		
		final PictogramElement dState = destState;
		final PictogramElement nNode = newNode;
			
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				RiderDesignEditor ed = Activator.getDiagramEditor();
				
				// do autolayout
				DiagramOperations.layoutDiagram(ed);
				// then mark new node for selection
				if (dState != null) {
					ed.setPictogramElementForSelection(dState);
				} else {
					if (nNode != null) {
						ed.setPictogramElementForSelection(nNode);
					} else {
						ed.setPictogramElementForSelection(context.getPictogramElements()[0]);
					}
				}
				// and refresh the editor to do the selection
				ed.getDiagramBehavior().refresh();
			}
		});
	}

	protected AbstractNode createNode() throws Exception {
		return this.clazz.newInstance();
	}
}
