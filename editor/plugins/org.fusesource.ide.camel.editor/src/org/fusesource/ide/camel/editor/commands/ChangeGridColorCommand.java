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

package org.fusesource.ide.camel.editor.commands;

import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.IColorConstant;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.utils.StyleUtil;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class ChangeGridColorCommand extends RecordingCommand {

	private final RiderDesignEditor designEditor;

	public ChangeGridColorCommand(RiderDesignEditor designEditor, TransactionalEditingDomain editingDomain) {
		super(editingDomain);
		this.designEditor = designEditor;
	}

	@Override
	protected void doExecute() {
		if (designEditor != null) {
			GraphicalViewer graphicalViewer = designEditor.getGraphicalViewer();
			if (graphicalViewer != null
					&& graphicalViewer.getEditPartRegistry() != null) {
				ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
				IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
				IColorConstant cc = StyleUtil.getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_GRID_COLOR));
				Diagram diagram = designEditor.getDiagram();
				if (cc != null && diagram != null) {
					GraphicsAlgorithm ga = diagram.getGraphicsAlgorithm();
					if (ga != null){
						ga.setForeground(GraphitiUi.getGaService().manageColor(diagram, cc));
						gridFigure.repaint();
						gridFigure.revalidate();
						designEditor.getDiagramBehavior().refreshContent();
					}
				}
			}
		}
	}
}
