/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.behaviours;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.internal.command.AddModelObjectCommand;
import org.eclipse.graphiti.ui.internal.command.CreateModelObjectCommand;
import org.eclipse.graphiti.ui.internal.parts.ConnectionEditPart;
import org.eclipse.graphiti.ui.internal.policy.DefaultConnectionEditPolicy;
import org.eclipse.graphiti.ui.internal.policy.ShapeXYLayoutEditPolicy;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.ui.platform.IConfigurationProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;

//TODO: remove after Graphiti issue fixed https://bugs.eclipse.org/bugs/show_bug.cgi?id=499720
final class CamelDefaultConnectionEditPolicy extends DefaultConnectionEditPolicy {
	CamelDefaultConnectionEditPolicy(IConfigurationProvider configurationProvider) {
		super(configurationProvider);
	}

	/** 
	 * /!\ Copy-pasted from parent
	 * See comments where it is overriden
	 * */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Command cmd = UnexecutableCommand.INSTANCE;
		ContainerShape targetContainerShape;

		GraphicalViewer graphicalViewer = getConfigurationProvider().getDiagramContainer().getGraphicalViewer();
		
		//START Override - The goal is to be aware of editor scroll
		Control control = graphicalViewer.getControl();
		Point searchLocation = request.getLocation().getCopy();
		if(control instanceof FigureCanvas){
			FreeformViewport viewport = (FreeformViewport)((FigureCanvas) control).getViewport();
			Point viewPortLocation = viewport.getViewLocation();
			searchLocation.translate(viewPortLocation);
		}
		EditPart findEditPartAt = GraphitiUiInternal.getGefService().findEditPartAt(graphicalViewer, searchLocation, false);
		//END Override
		if (findEditPartAt != null && findEditPartAt.getModel() instanceof ContainerShape) {
			targetContainerShape = (ContainerShape) findEditPartAt.getModel();
		} else {
			targetContainerShape = getCommonContainerShape();
		}

		Object createdObject = request.getNewObject();

		// determine constraint

		Rectangle rectangle = new Rectangle();

		if (findEditPartAt != null) {
			Point where = createRealLocation(request.getLocation(), findEditPartAt);
			rectangle.setLocation(where);
		} else {
			rectangle.setLocation(request.getLocation());
		}

		if (request.getSize() != null) {
			rectangle.setSize(request.getSize());
		}

		Connection connection = (Connection) getHost().getModel();

		if (request.getNewObjectType() == ICreateFeature.class) {
			ICreateContext context = ShapeXYLayoutEditPolicy.createCreateContext(targetContainerShape, rectangle);
			((CreateContext) context).setTargetConnection(connection);
			ICreateFeature createFeature = (ICreateFeature) createdObject;
			cmd = new CreateModelObjectCommand(getConfigurationProvider(), createFeature, context);
			cmd.setLabel(createFeature.getDescription());
		} else if (request.getNewObjectType() == ISelection.class) {
			cmd = new AddModelObjectCommand(getConfigurationProvider(), targetContainerShape, (ISelection) createdObject, rectangle,
					connection);

		}

		return cmd;
	}

	/**
	 * 
	 * /!\ Copy-pasted from parent
	 * @param location
	 * @param findEditPartAt
	 * @return
	 */
	private Point createRealLocation(Point location, EditPart findEditPartAt) {
		IFigure layoutContainer = ((GraphicalEditPart) findEditPartAt).getContentPane();
		Point where = location.getCopy();
		layoutContainer.translateToRelative(where);
		layoutContainer.translateFromParent(where);
		Point layoutOrigin = layoutContainer.getClientArea().getLocation();
		where.translate(layoutOrigin.getNegated());
		return where;
	}

	/**
	 * /!\ Copy-pasted from parent
	 * @return
	 */
	private ContainerShape getCommonContainerShape() {

		ConnectionEditPart connectionEditPart = (ConnectionEditPart) getHost();

		EditPart sourceEditPart = connectionEditPart.getSource();
		EditPart targetEditPart = connectionEditPart.getTarget();
		EditPart parent = getCommonEditPart(sourceEditPart, targetEditPart);

		return (ContainerShape) parent.getModel();
	}

	/**
	 * /!\ Copy-pasted from parent
	 * investigates the common parent of both editparts. the least common parent
	 * is the diagram editpart
	 */
	private EditPart getCommonEditPart(EditPart source, EditPart target) {

		// create two lists with editparts to the root

		List<EditPart> list1 = new ArrayList<EditPart>();
		List<EditPart> list2 = new ArrayList<EditPart>();

		EditPart editPart = source;

		while (!(editPart instanceof RootEditPart)) {
			list1.add(0, editPart);
			editPart = editPart.getParent();
		}

		editPart = target;

		while (!(editPart instanceof RootEditPart)) {
			list2.add(0, editPart);
			editPart = editPart.getParent();
		}

		// compare the lists until that position where editparts differ
		int index = 0;
		while (true) {
			if (index == list1.size())
				return list1.get(index - 1);

			if (index == list2.size())
				return list1.get(index - 1);

			if (list1.get(index) != list2.get(index))
				return list1.get(index - 1);

			index++;
		}
	}
}