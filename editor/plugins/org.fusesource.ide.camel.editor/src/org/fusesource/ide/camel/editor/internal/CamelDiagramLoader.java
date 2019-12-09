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
package org.fusesource.ide.camel.editor.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.features.add.AddFlowFeature;
import org.fusesource.ide.camel.editor.features.add.AddNodeFeature;
import org.fusesource.ide.camel.editor.features.create.CreateFlowFeature;
import org.fusesource.ide.camel.editor.utils.FigureUIFactory;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;

/**
 * this class loads a camel route element into the diagram 
 * 
 * @author lhein
 */
public class CamelDiagramLoader {
	
	private Diagram diagram;
	private IFeatureProvider featureProvider;
	private int orientation = PositionConstants.EAST;

	/**
	 * 
	 * @param diagram
	 * @param featureProvider
	 */
	public CamelDiagramLoader(Diagram diagram, IFeatureProvider featureProvider) {
		this.diagram = diagram;
		this.featureProvider = featureProvider;
		if (PreferenceManager.getInstance().containsPreference(PreferencesConstants.EDITOR_LAYOUT_ORIENTATION)) {
			this.orientation = PreferenceManager.getInstance().loadPreferenceAsInt(PreferencesConstants.EDITOR_LAYOUT_ORIENTATION);
		}
	}

	/**
	 * 
	 * @param container
	 */
	public void loadModel(TransactionalEditingDomain editingDomain, AbstractCamelModelElement container) {
		if (container == null) {
			return;
		}
		List<AbstractCamelModelElement> processedNodes = new ArrayList<>();
		List<AbstractCamelModelElement> children = container instanceof CamelRouteElement ? Arrays.asList(container) : container.getChildElements();
		int x = 40;
		int y = 40;
		AbstractCamelModelElement lastElem = container;
		for (AbstractCamelModelElement node : children) {
			int res = addProcessor(lastElem, node, x, y, processedNodes, diagram);
			if (this.orientation == PositionConstants.EAST) {
				x = res;	
			} else {
				y = res;
			}
			lastElem = node;
		}
		DiagramOperations.layoutDiagram(editingDomain, featureProvider, diagram, container);
	}

	private int addProcessor(AbstractCamelModelElement lastElement, AbstractCamelModelElement node, int x, int y, List<AbstractCamelModelElement> processedNodes, ContainerShape container) {
		// Create the context information
		AddContext addContext = new AddContext();
		addContext.setNewObject(node);
		addContext.setTargetContainer(container);
		addContext.setX(x);
		addContext.setY(y);
		addContext.putProperty(AddNodeFeature.DEACTIVATE_LAYOUT, true);

		int retVal = this.orientation == PositionConstants.EAST ? x : y;
		
		IAddFeature addFeature = featureProvider.getAddFeature(addContext);
		if (addFeature.canAdd(addContext)) {
			PictogramElement destState;
			if (!processedNodes.contains(node)) {
				destState = addFeature.add(addContext);
				// remember the node
				processedNodes.add(node);
			} else {
				destState = featureProvider.getPictogramElementForBusinessObject(node);
			}
			
			// we don't want to connect children of a container to the container
			boolean createConnection = lastElement != null && lastElement.equals(node.getInputElement()); 
			if (createConnection ) {
				// reset outputs and inputs
				lastElement.setOutputElement(null);
				node.setInputElement(null);

				// create the connection
				CreateFlowFeature createFeature = new CreateFlowFeature(featureProvider);
				CreateConnectionContext connectContext = new CreateConnectionContext();
				PictogramElement srcState = featureProvider.getPictogramElementForBusinessObject(lastElement);
				if (srcState == destState) {
					CamelEditorUIActivator.pluginLog().logWarning("Should not be the same element for different nodes: " + lastElement + " and " + node);
				}
				connectContext.setSourcePictogramElement(srcState);
				connectContext.setTargetPictogramElement(destState);
				connectContext.putProperty(AddFlowFeature.DEACTIVATE_LAYOUT, true);
				Anchor srcAnchor = getAnchor(srcState);
				Anchor destAnchor = getAnchor(destState);
				if (srcAnchor != null && destAnchor != null) {
					connectContext.setSourceAnchor(srcAnchor);
					connectContext.setTargetAnchor(destAnchor);
					if (createFeature.canCreate(connectContext)) {
						createFeature.execute(connectContext);
					}
				}
			}

			if (!node.getChildElements().isEmpty()) {
				int subX = 40, subY = 40;
				AbstractCamelModelElement lastSub = null;
				for (AbstractCamelModelElement subNode : node.getChildElements()) {
					int res = addProcessor(lastSub, subNode, subX, subY, processedNodes, getContainerShape(destState));
					if (this.orientation == PositionConstants.EAST) {
						subX = res;	
					} else {
						subY = res;
					}
					lastSub = subNode;
				}
			}
			if (this.orientation == PositionConstants.EAST) {
				retVal += FigureUIFactory.FIGURE_MAX_WIDTH + 20;
			} else {
				retVal += FigureUIFactory.IMAGE_DEFAULT_HEIGHT + FigureUIFactory.IMAGE_DEFAULT_HEIGHT + 30;
			}
		} else {
			CamelEditorUIActivator.pluginLog().logWarning("Cannot add node: " + node);
		}
		
		return retVal;
	}

	private ContainerShape getContainerShape(PictogramElement pe) {
		return (ContainerShape)pe;
	}
	
	/**
	 * retrieves the anchor for a given pictogram element
	 * 
	 * @param element	the pictogram
	 * @return	the anchor or null
	 */
	private Anchor getAnchor(PictogramElement element) {
		if (element instanceof AnchorContainer) {
			AnchorContainer container = (AnchorContainer) element;
			EList<Anchor> anchors = container.getAnchors();
			if (anchors != null && !anchors.isEmpty()) {
				return anchors.get(0);
			}
		}
		return null;
	}

}
