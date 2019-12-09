/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.StyleUtil;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class HighlightNodeCommand extends RecordingCommand {
	
	private final CamelDesignEditor designEditor;
	private AbstractCamelModelElement node;
	private boolean highlight;

	public HighlightNodeCommand(CamelDesignEditor designEditor, TransactionalEditingDomain editingDomain, AbstractCamelModelElement node, boolean highlight) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.node = node;
		this.highlight = highlight;
	}

	@Override
	protected void doExecute() {
		if (node == null) {
			// skip
			return;
		}

		// check if we are in the design view mode - if not, then switch to it
		if (this.designEditor.getParent().getActivePage() == CamelEditor.SOURCE_PAGE_INDEX) {
			this.designEditor.getParent().switchToDesignEditor();
		}
		
		// check if we need to switch to another route for highlighting
		if (highlight && isDifferentRouteCurrentlySelected()) {
			// switch the route
			this.designEditor.setSelectedContainer(node.getRoute());											
		}
		
		PictogramElement pe = designEditor.getFeatureProvider().getPictogramElementForBusinessObject(node);
		if (pe == null) {
			CamelEditorUIActivator.pluginLog().logWarning("Warning could not find PictogramElement for highlight node: " + node);
			return;
		}
		
		IGaService gaService = Graphiti.getGaService();
		
		try {
			if (pe instanceof ContainerShape) {
				updateShapeStyle(gaService, (ContainerShape) pe);
				updateTextStyle(pe, gaService);
			}				
		} catch (Exception e) {
			CamelEditorUIActivator.pluginLog().logError(e);
			// ignore
		}
	}

	protected void updateTextStyle(PictogramElement pe, IGaService gaService) {
		for (GraphicsAlgorithm ga : pe.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
			if (ga instanceof Text) {
				Text text = (Text) ga;
				
				// now update node highlight
				if (highlight) {
					// set highlight
					text.setForeground(gaService.manageColor(designEditor.getDiagramTypeProvider().getDiagram(), StyleUtil.HIGHLIGHT_COLOR));
				} else {
					// delete highlight
					text.setForeground(gaService.manageColor(designEditor.getDiagramTypeProvider().getDiagram(), StyleUtil.E_CLASS_TEXT_FOREGROUND));
				}
			}
		}
	}

	protected void updateShapeStyle(IGaService gaService, ContainerShape cs) {
		if (highlight) {
			if (this.node.getUnderlyingMetaModelObject().canHaveChildren()) {
				// container node - border already visible
				cs.getGraphicsAlgorithm().setLineWidth(3);
				cs.getGraphicsAlgorithm().setForeground(gaService.manageColor(designEditor.getDiagramTypeProvider().getDiagram(), StyleUtil.HIGHLIGHT_COLOR));
			} else {
				// child node - no border visible
				cs.getGraphicsAlgorithm().setForeground(gaService.manageColor(designEditor.getDiagramTypeProvider().getDiagram(), StyleUtil.HIGHLIGHT_COLOR));
				cs.getGraphicsAlgorithm().setLineWidth(3);
				cs.getGraphicsAlgorithm().setLineVisible(true);
			}
		} else {
			if (this.node.getUnderlyingMetaModelObject().canHaveChildren()) {
				// container node - border already visible
				cs.getGraphicsAlgorithm().setForeground(gaService.manageColor(designEditor.getDiagramTypeProvider().getDiagram(), StyleUtil.CONTAINER_FIGURE_BORDER_COLOR));
				cs.getGraphicsAlgorithm().setLineWidth(1);
			} else {
				// child node - no border visible
				cs.getGraphicsAlgorithm().setForeground(cs.getGraphicsAlgorithm().getBackground());
				cs.getGraphicsAlgorithm().setLineVisible(false);
				cs.getGraphicsAlgorithm().setLineWidth(1);
			}
		}
	}

	protected boolean isDifferentRouteCurrentlySelected() {
		return node.getRoute() != null && designEditor.getSelectedContainer() != node.getRouteContainer() && node.getRoute() != designEditor.getSelectedContainer();
	}
}
