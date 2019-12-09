/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.ui.IEditorPart;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.features.misc.MoveNodeFeature;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.test.util.editor.AbstractCamelEditorIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CamelEditorMoveIT extends AbstractCamelEditorIT {
	
	@Parameters(name = "Route container of type: {0}")
	public static String[] data() {
	    return new String[] { "camelContext", "routes", "routeContext" };
	}
	
	private CamelDesignEditor editor;
	
	public CamelEditorMoveIT(String routeContainerType) {
		this.routeContainerType = routeContainerType;
	}

	@Test
	public void testMoveInsideSameContainerNotAllowed() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/moveInRoute");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement beanFromRoute1 = model.findNode("_bean_inRoute1");
		Shape beanFromRoute1PE = (Shape) fp.getPictogramElementForBusinessObject(beanFromRoute1);
		
		MoveShapeContext moveShapeContext = new MoveShapeContext(beanFromRoute1PE);
		moveShapeContext.setSourceContainer(beanFromRoute1PE.getContainer());
		moveShapeContext.setTargetContainer(beanFromRoute1PE.getContainer());
		
		assertThat(new MoveNodeFeature(fp).canMoveShape(moveShapeContext)).isFalse();
	}
	
	@Test
	public void testMoveToCanvasNotAllowed() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/moveInRoute");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement beanFromRoute1 = model.findNode("_bean_inRoute1");
		Shape beanFromRoute1PE = (Shape) fp.getPictogramElementForBusinessObject(beanFromRoute1);
		
		MoveShapeContext moveShapeContext = new MoveShapeContext(beanFromRoute1PE);
		moveShapeContext.setSourceContainer(beanFromRoute1PE.getContainer());
		moveShapeContext.setTargetContainer(beanFromRoute1PE.getContainer().getContainer());
		
		assertThat(new MoveNodeFeature(fp).canMoveShape(moveShapeContext)).isFalse();
	}
	
	@Test
	public void testMoveFromOneRouteToAnother() throws Exception {
		testMove("_bean_inRoute1", "_route2");
	}

	@Test
	public void testMoveFromWhenToAnotherWhenInSameRoute() throws Exception {
		testMove("_bean_inWhen1_inRoute1", "_when2_inRoute1");
	}
	
	@Test
	public void test2ConsecutivesMove() throws Exception {
		testMove("_bean_inWhen1_inRoute1", "_when2_inRoute1");
		
		assertThat(editor.getModel().findAllNodesWithId("_bean_inWhen1_inRoute1")).hasSize(1);
		move("_bean_inWhen1_inRoute1", "_when1_inRoute1", editor.getFeatureProvider(), editor.getModel());		
		assertThat(editor.getModel().findAllNodesWithId("_bean_inWhen1_inRoute1")).hasSize(1);
	}

	@Test
	public void testMoveFromWhenToAnotherWhenInAnotherRoute() throws Exception {
		testMove("_bean_inWhen1_inRoute1", "_when1_inRoute2");
	}
	
	@Test
	public void testMoveAcontainerToAnotherContainer() throws Exception {
		testMove("_when1_inRoute1", "_choice_inRoute2");
		
		CamelFile model = editor.getModel();		
		AbstractCamelModelElement movedElement = model.findNode("_when1_inRoute1");
		assertThat(movedElement.getChildElements()).hasSize(2);
		
		//ensure children of the moved When are visible
		IFeatureProvider fp = editor.getFeatureProvider();
		assertThat(fp.hasPictogramElementForBusinessObject(model.findNode("_bean_inWhen1_inRoute1"))).isTrue();
		assertThat(fp.hasPictogramElementForBusinessObject(model.findNode("_bean2_inWhen1_inRoute1"))).isTrue();
		
		//ensure Connection are available
		AnchorContainer firstBeanMovedGraphicalRepresentation = (AnchorContainer) fp.getPictogramElementForBusinessObject(model.findNode("_bean_inWhen1_inRoute1"));
		Anchor anchor = firstBeanMovedGraphicalRepresentation.getAnchors().get(0);
		Connection connection = anchor.getOutgoingConnections().get(0);
		assertThat(connection.getEnd().getParent()).isEqualTo(fp.getPictogramElementForBusinessObject(model.findNode("_bean2_inWhen1_inRoute1")));
	}
	
	@Test
	public void testMoveAcontainerWithSeveralContainerToAnotherContainer() throws Exception {
		testMove("_choice_inRoute1", "_route2");
		
		CamelFile model = editor.getModel();		
		AbstractCamelModelElement movedElement = model.findNode("_route2");
		assertThat(movedElement.getChildElements()).hasSize(3);
		
		//ensure children of the moved When are visible
		IFeatureProvider fp = editor.getFeatureProvider();
		assertThat(fp.hasPictogramElementForBusinessObject(model.findNode("_choice_inRoute1"))).isTrue();
		assertThat(fp.hasPictogramElementForBusinessObject(model.findNode("_bean_inWhen1_inRoute1"))).isTrue();
		assertThat(fp.hasPictogramElementForBusinessObject(model.findNode("_bean2_inWhen1_inRoute1"))).isTrue();
		
		//ensure Connection not created inside the choice
		AnchorContainer firstBeanMovedGraphicalRepresentation = (AnchorContainer) fp.getPictogramElementForBusinessObject(model.findNode("_when1_inRoute1"));
		Anchor anchor = firstBeanMovedGraphicalRepresentation.getAnchors().get(0);
		assertThat(anchor.getOutgoingConnections()).isEmpty();
	}
	
	@Test
	public void testMoveAndInsertDropOnConnection() throws Exception {
		testMoveAndInsert("_bean_inWhen1_inRoute1", "_bean_inRoute2", "_choice_inRoute2");
	}
	
	@Test
	public void testMoveAndInsertDropOnConnectionNotAllowedOnConnectedConnection() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/moveInRoute");
		
		readAndDispatch(20);
		
		editor = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = editor.getFeatureProvider();
		CamelFile model = editor.getModel();
		
		AbstractCamelModelElement nodeToMove = model.findNode("_bean_inWhen1_inRoute1");
		Shape nodeToMoveGraphical = (Shape) fp.getPictogramElementForBusinessObject(nodeToMove);
		
		AbstractCamelModelElement sourceOfConnection = model.findNode("_bean_inWhen1_inRoute1");
		ContainerShape sourceOfConnectionGraphical = (ContainerShape) fp.getPictogramElementForBusinessObject(sourceOfConnection);
		
		Connection targetConnection = sourceOfConnectionGraphical.getAnchors().get(0).getOutgoingConnections().get(0);
		
		MoveShapeContext moveShapeContext = new MoveShapeContext(nodeToMoveGraphical);
		moveShapeContext.setSourceContainer(nodeToMoveGraphical.getContainer());
		moveShapeContext.setTargetContainer(sourceOfConnectionGraphical.getContainer());
		moveShapeContext.setTargetConnection(targetConnection);
		
		MoveNodeFeature moveNodeFeature = new MoveNodeFeature(fp);
		assertThat(moveNodeFeature.canMoveShape(moveShapeContext)).isFalse();
	}
	
	@Test
	public void testMoveAndInsertDropOnConnectionInSameContainer() throws Exception {
		testMoveAndInsert("_bean2_inRoute1", "_bean_inRoute1", "_choice_inRoute1");
	}
	
	@Test
	public void testMoveAndPrependDropInSameRoute() throws Exception {
		testMove("_bean_inRoute1", "_bean2_inRoute1");
	}
	
	@Test
	public void testMoveAndPrependDropInAnotherRoute() throws Exception {
		testMove("_bean_inRoute2", "_bean_inRoute1");
	}
	
	@Test
	public void testMoveAndAppendDrop() throws Exception {
		testMove("_bean_inRoute2", "_bean2_inRoute1");
	}
	
	private void testMoveAndInsert(String nodeIdToMove, String nodeIdSourceOFConnection, String nodeIdTargetOFconnection) throws Exception{
		IEditorPart openEditorOnFileStore = openFileInEditor("/moveInRoute");
		
		readAndDispatch(20);
		
		editor = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = editor.getFeatureProvider();
		CamelFile model = editor.getModel();
		AbstractCamelModelElement targetContainer = moveOnConnection(nodeIdToMove, nodeIdSourceOFConnection, nodeIdTargetOFconnection, fp, model);
		
		AbstractCamelModelElement movedElement = model.findNode(nodeIdToMove);
		assertThat(movedElement.getParent()).isEqualTo(targetContainer);
		if("bean".equals(movedElement.getNodeTypeId())){
			assertThat(movedElement.getParameter("beanType")).isEqualTo("MyBeanType");
		}
		
	}

	private AbstractCamelModelElement moveOnConnection(String nodeIdToMove, String nodeIdSourceOFConnection, String nodeIdTargetOFconnection, IFeatureProvider fp, CamelFile model) {
		AbstractCamelModelElement nodeToMove = model.findNode(nodeIdToMove);
		Shape nodeToMoveGraphical = (Shape) fp.getPictogramElementForBusinessObject(nodeToMove);
		
		AbstractCamelModelElement sourceOfConnection = model.findNode(nodeIdSourceOFConnection);
		ContainerShape sourceOfConnectionGraphical = (ContainerShape) fp.getPictogramElementForBusinessObject(sourceOfConnection);
		
		Connection targetConnection = sourceOfConnectionGraphical.getAnchors().get(0).getOutgoingConnections().get(0);
		
		MoveShapeContext moveShapeContext = new MoveShapeContext(nodeToMoveGraphical);
		moveShapeContext.setSourceContainer(nodeToMoveGraphical.getContainer());
		moveShapeContext.setTargetContainer(sourceOfConnectionGraphical.getContainer());
		moveShapeContext.setTargetConnection(targetConnection);
		
		MoveNodeFeature moveNodeFeature = new MoveNodeFeature(fp);
		assertThat(moveNodeFeature.canMoveShape(moveShapeContext)).isTrue();
		
		executeCommandInTransactionDomain(moveShapeContext, moveNodeFeature);
		return sourceOfConnection.getParent();
	}

	private void testMove(String nodeIdToMove, String nodeIdTargetForMoveIn) throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/moveInRoute");
		
		readAndDispatch(20);
		
		editor = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = editor.getFeatureProvider();
		CamelFile model = editor.getModel();
		AbstractCamelModelElement targetContainer = move(nodeIdToMove, nodeIdTargetForMoveIn, fp, model);
		
		AbstractCamelModelElement movedElement = model.findNode(nodeIdToMove);
		if("bean".equalsIgnoreCase(targetContainer.getNodeTypeId())){
			assertThat(movedElement.getParent()).isEqualTo(targetContainer.getParent());
		} else {
			assertThat(movedElement.getParent()).isEqualTo(targetContainer);
		}
		if("bean".equals(movedElement.getNodeTypeId())){
			assertThat(movedElement.getParameter("beanType")).isEqualTo("MyBeanType");
		}
	}

	private AbstractCamelModelElement move(String nodeIdToMove, String nodeIdTargetForMove, IFeatureProvider fp, CamelFile model) {
		AbstractCamelModelElement nodeToMove = model.findNode(nodeIdToMove);
		Shape nodeToMoveGraphical = (Shape) fp.getPictogramElementForBusinessObject(nodeToMove);
		
		AbstractCamelModelElement targetContainer = model.findNode(nodeIdTargetForMove);
		ContainerShape targetContainerGraphical = (ContainerShape) fp.getPictogramElementForBusinessObject(targetContainer);
		
		MoveShapeContext moveShapeContext = new MoveShapeContext(nodeToMoveGraphical);
		moveShapeContext.setSourceContainer(nodeToMoveGraphical.getContainer());
		moveShapeContext.setTargetContainer(targetContainerGraphical);
		
		MoveNodeFeature moveNodeFeature = new MoveNodeFeature(fp);
		assertThat(moveNodeFeature.canMoveShape(moveShapeContext)).isTrue();
		
		executeCommandInTransactionDomain(moveShapeContext, moveNodeFeature);
		return targetContainer;
	}
	
}
