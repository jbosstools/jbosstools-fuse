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

package org.fusesource.ide.camel.editor.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.fusesource.ide.camel.editor.utils.StyleUtil;
import org.fusesource.ide.camel.model.Flow;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 *
 */
public class AddFlowFeature extends AbstractAddFeature {

	public AddFlowFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public boolean canAdd(IAddContext context) {
		// return true if given business object is an EReference
		// note, that the context must be an instance of IAddConnectionContext
		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof Flow) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public PictogramElement add(IAddContext context) {
		IAddConnectionContext addConContext = (IAddConnectionContext) context;
		Flow addedEReference = (Flow) context.getNewObject();

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		// CONNECTION WITH POLYLINE
		Connection connection = peCreateService.createFreeFormConnection(getDiagram());
		connection.setStart(addConContext.getSourceAnchor());
		connection.setEnd(addConContext.getTargetAnchor());
		
		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(connection);
		polyline.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
		polyline.setForeground(manageColor(StyleUtil.getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_CONNECTION_COLOR))));

		// create link and wire it
		link(connection, addedEReference);

		// add dynamic text decorator for the reference name
		ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
		Text text = gaService.createDefaultText(getDiagram(), getDiagram().getGraphicsAlgorithm());
		textDecorator.setConnection(connection);
		text.setStyle(StyleUtil.getStyleForCamelText((getDiagram())));
		gaService.setLocation(text, 10, 0);
		// set reference name in the text decorator
		Flow flow = (Flow) context.getNewObject();
		text.setValue(flow.getName());
		
		// add static graphical decorators (composition and navigable)
		ConnectionDecorator cd;
		cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		createArrow(cd);
		
		return connection;
	}
	
	private Polyline createArrow(GraphicsAlgorithmContainer gaContainer) {
		int xy[] = new int[] { -10, -5, 0, 0, -10, 5, -8, 0 };
		int beforeAfter[] = new int[] { 3, 3, 0, 0, 3, 3, 3, 3 };
		Polygon polyline = Graphiti.getGaCreateService().createPolygon(gaContainer, xy, beforeAfter);
		polyline.setStyle(StyleUtil.getStyleForPolygon(getDiagram()));
		return polyline;
	}
}
