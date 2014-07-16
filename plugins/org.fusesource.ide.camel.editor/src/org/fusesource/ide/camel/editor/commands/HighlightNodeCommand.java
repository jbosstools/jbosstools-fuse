/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.editor.RiderEditor;
import org.fusesource.ide.camel.editor.utils.StyleUtil;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;

/**
 * @author lhein
 */
public class HighlightNodeCommand extends RecordingCommand {
	
	private final RiderDesignEditor designEditor;
	private AbstractNode node;
	private boolean highlight;

	public HighlightNodeCommand(RiderDesignEditor designEditor, TransactionalEditingDomain editingDomain, AbstractNode node, boolean highlight) {
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
		if (this.designEditor.getMultiPageEditor().getActivePage() == RiderEditor.SOURCE_PAGE_INDEX) {
			this.designEditor.getMultiPageEditor().switchToDesignEditor();
		}
		
		// check if we need to switch to another route for highlighting
		if (this.node != null && 
			highlight) {
			if (node.getParent() != null) {
				// seems the next breakpoint is in a different route and we need to switch to that route now
				if (node.getParent() instanceof RouteSupport) {
					// switch the route
					this.designEditor.setSelectedRoute((RouteSupport)node.getParent());											
				}
			}
		}
		
		PictogramElement pe = designEditor.getFeatureProvider().getPictogramElementForBusinessObject(node);
		if (pe == null) {
			Activator.getLogger().debug("Warning could not find PictogramElement for highlight node: " + node);
			return;
		}
		
		IGaService gaService = Graphiti.getGaService();
		
		try {
			if (pe instanceof ContainerShape) {
				ContainerShape cs = (ContainerShape) pe;
				if (highlight) {
					cs.getGraphicsAlgorithm().setLineVisible(true);
					cs.getGraphicsAlgorithm().setLineStyle(LineStyle.DASH);
					cs.getGraphicsAlgorithm().setLineWidth(cs.getGraphicsAlgorithm().getLineWidth() + 3);
					cs.getGraphicsAlgorithm().setForeground(gaService.manageColor(designEditor.getDiagram(), StyleUtil.getColorConstant("255,0,0")));
				} else {
					cs.getGraphicsAlgorithm().setLineVisible(false);
					cs.getGraphicsAlgorithm().setLineStyle(LineStyle.SOLID);
					cs.getGraphicsAlgorithm().setLineWidth(cs.getGraphicsAlgorithm().getLineWidth() - 3);
					cs.getGraphicsAlgorithm().setForeground(gaService.manageColor(designEditor.getDiagram(), StyleUtil.E_CLASS_FOREGROUND));
				}
				
				for (Shape shape : cs.getChildren()) {
					if (shape.getGraphicsAlgorithm() instanceof Text) {
						Text text = (Text) shape.getGraphicsAlgorithm();

						// now update node highlight
						if (highlight) {
							// set highlight
							text.setForeground(gaService.manageColor(designEditor.getDiagram(), StyleUtil.getColorConstant("255,0,0")));
						} else {
							// delete highlight
							text.setForeground(gaService.manageColor(designEditor.getDiagram(), StyleUtil.E_CLASS_TEXT_FOREGROUND));
						}
					}
				}
			}				
		} catch (Exception e) {
			// ignore
		}
	}
}
