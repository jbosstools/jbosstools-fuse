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

package org.fusesource.ide.camel.editor.editor;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.features.create.CreateFlowFeature;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;


public class CamelModelLoader {
	private Diagram diagram;
	private IFeatureProvider featureProvider;

	public CamelModelLoader(Diagram diagram, IFeatureProvider featureProvider) {
		this.diagram = diagram;
		this.featureProvider = featureProvider;
	}

	public void loadModel(RouteSupport route) {
		if (route == null) {
			return;
		}
		List<AbstractNode> processedNodes = new ArrayList<AbstractNode>();
		List<AbstractNode> children = route.getRootNodes();
		int x = 40;
		int y = 40;
		for (AbstractNode node : children) {
			y = addProcessor(route, node, x, y, processedNodes);
		}
	}

	private int addProcessor(AbstractNode route, AbstractNode node, int x, int y, List<AbstractNode> processedNodes) {
		// Create the context information
		AddContext addContext = new AddContext();
		addContext.setNewObject(node);
		addContext.setTargetContainer(diagram);
		addContext.setX(x);
		addContext.setY(y);
		y += 100;

		IAddFeature addFeature = featureProvider.getAddFeature(addContext);
		if (addFeature.canAdd(addContext)) {
			PictogramElement destState = null;
			if (processedNodes.contains(node) == false) {
				destState = addFeature.add(addContext);
			} else {
				destState = featureProvider.getPictogramElementForBusinessObject(node);
			}
			// remember the node
			processedNodes.add(node);
			
			if (!(route instanceof RouteSupport)) {
				// lets remove the current connection between the parent and node so that
				// createFeature.canCreate() returns true
				route.removeConnection(node);

				CreateFlowFeature createFeature = new CreateFlowFeature(featureProvider);
				CreateConnectionContext connectContext = new CreateConnectionContext();
				PictogramElement srcState = featureProvider.getPictogramElementForBusinessObject(route);
				//PictogramElement destState = featureProvider.getPictogramElementForBusinessObject(node);
				if (srcState == destState) {
					System.out.println("Should not be the same element for different nodes: " + route + " and " + node);
				}
				connectContext.setSourcePictogramElement(srcState);
				connectContext.setTargetPictogramElement(destState);
				Anchor srcAnchor = DiagramUtils.getAnchor(srcState);
				Anchor destAnchor = DiagramUtils.getAnchor(destState);
				if (srcAnchor != null && destAnchor != null) {
					connectContext.setSourceAnchor(srcAnchor);
					connectContext.setTargetAnchor(destAnchor);
					if (createFeature.canCreate(connectContext)) {
						createFeature.execute(connectContext);
					}
				}
			}

			List<AbstractNode> children = node.getOutputs();
			for (AbstractNode child : children) {
				y = addProcessor(node, child, x, y, processedNodes);
			}
		} else {
			System.out.println("Cannot add node: " + node);
		}

		return y;
	}


}
