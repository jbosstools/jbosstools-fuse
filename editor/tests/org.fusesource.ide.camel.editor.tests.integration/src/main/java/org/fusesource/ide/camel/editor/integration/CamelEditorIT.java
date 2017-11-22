/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.ui.IEditorPart;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateConnectorFigureFeature;
import org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.editor.features.delete.DeleteFigureFeature;
import org.fusesource.ide.camel.editor.features.misc.ReconnectNodesFeature;
import org.fusesource.ide.camel.editor.utils.FigureUIFactory;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.test.util.editor.AbstractCamelEditorIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CamelEditorIT extends AbstractCamelEditorIT {
	
	@Parameters(name = "Route container of type: {0}")
	public static String[] data() {
	    return new String[] { "camelContext", "routes", "routeContext" };
	}
	
	public CamelEditorIT(String routeContainerType) {
		this.routeContainerType = routeContainerType;
	}
	
	@Test
	public void addWiredElementsAfterThenBeforeLog() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/basic");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelModel metaModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(ed.getWorkspaceProject(), new NullProgressMonitor());
		CamelFile model = ed.getModel();
		AbstractCamelModelElement logEP = model.findNode("log1");
		PictogramElement logPE = fp.getPictogramElementForBusinessObject(logEP);
		
		// first we try to append a file endpoint to a log endpoint by droping
		// it on the log endpoint instead of the diagram
		createConnector(fp, (ContainerShape)logPE, null, metaModel.getComponentForScheme("file"));
		AbstractCamelModelElement fileEP = model.findNode("_to1");
		assertThat(fileEP).isNotNull();
		assertThat(fileEP.isToEndpoint()).isTrue();
		assertThat(logEP.getOutputElement()).isEqualTo(fileEP);
		assertThat(fileEP.getInputElement()).isEqualTo(logEP);

		// then we try to prepend a file endpoint to a log endpoint by droping
		// it on the log endpoint instead of the diagram (as the output is already
		// occupied by the earlier file endpoint we now prepend it as input)
		createConnector(fp, (ContainerShape)logPE, null, metaModel.getComponentForScheme("file"));
		AbstractCamelModelElement fromFileEP = model.findNode("_to2");
		assertThat(fromFileEP).isNotNull();
		assertThat(fromFileEP.isFromEndpoint()).isTrue();
		assertThat(logEP.getInputElement()).isEqualTo(fromFileEP);
		assertThat(fromFileEP.getOutputElement()).isEqualTo(logEP);
	}
	
	@Test
	public void insertElementIntoFlow() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/insert");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelModel metaModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(ed.getWorkspaceProject(), new NullProgressMonitor());
		CamelFile model = ed.getModel();
		AbstractCamelModelElement inbox = model.findNode("inbox");
		AbstractCamelModelElement outbox = model.findNode("outbox");
		
		// make sure we have the connection
		assertThat(fp.getDiagramTypeProvider().getDiagram().getConnections().size()).isEqualTo(1);

		Connection con = fp.getDiagramTypeProvider().getDiagram().getConnections().get(0);

		// now drop another file endpoint onto the connection
		createConnector(fp, 
						(ContainerShape)fp.getPictogramElementForBusinessObject(inbox.getParent()), 
						con,
						metaModel.getComponentForScheme("file"));
		
		readAndDispatch(20);
		
		AbstractCamelModelElement insertedEP = model.findNode("_to1");
		assertThat(insertedEP).isNotNull();
		assertThat(insertedEP.isToEndpoint()).isTrue();
		assertThat(insertedEP.getInputElement()).isEqualTo(inbox);
		assertThat(insertedEP.getOutputElement()).isEqualTo(outbox);
	}
	
	@Test
	public void deleteElementFromFlow() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/delete");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement inbox = model.findNode("inbox");
		AbstractCamelModelElement outbox = model.findNode("outbox");
		AbstractCamelModelElement deleteNode = model.findNode("deleteMe");
		
		// now delete the node
		deleteNode(fp, deleteNode);
		
		readAndDispatch(20);
		
		assertThat(deleteNode).isNotNull();
		assertThat(deleteNode.getInputElement()).isNull();
		assertThat(deleteNode.getOutputElement()).isNull();
		assertThat(inbox.getOutputElement()).isEqualTo(outbox);
		assertThat(outbox.getInputElement()).isEqualTo(inbox);
	}
	
	@Test
	public void deleteElementThenInsert2Elements() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/delete");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement inbox = model.findNode("inbox");
		AbstractCamelModelElement outbox = model.findNode("outbox");
		AbstractCamelModelElement deleteNode = model.findNode("deleteMe");
		
		// now delete the node
		deleteNode(fp, deleteNode);
		
		readAndDispatch(20);
		
		assertThat(deleteNode.getInputElement()).isNull();
		assertThat(deleteNode.getOutputElement()).isNull();
		assertThat(inbox.getOutputElement()).isEqualTo(outbox);
		assertThat(outbox.getInputElement()).isEqualTo(inbox);
		
		EList<Connection> connections = fp.getDiagramTypeProvider().getDiagram().getConnections();
		assertThat(connections.size()).isEqualTo(1);

		Connection con = connections.get(0);
		CamelModel metaModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(ed.getWorkspaceProject(), new NullProgressMonitor());
		// now drop another file endpoint onto the connection
		createConnector(fp, 
						(ContainerShape)fp.getPictogramElementForBusinessObject(inbox.getParent()), 
						con,
						metaModel.getComponentForScheme("file"));
		
		readAndDispatch(20);
		
		AbstractCamelModelElement insertedEP = model.findNode("_to1");
		assertThat(insertedEP.isToEndpoint()).isTrue();
		assertThat(insertedEP.getInputElement()).isEqualTo(inbox);
		assertThat(insertedEP.getOutputElement()).isEqualTo(outbox);
		assertThat(connections.size()).isEqualTo(2);
		
		
		// now drop another second file endpoint onto the connection
		createConnector(fp, 
				(ContainerShape)fp.getPictogramElementForBusinessObject(inbox.getParent()), 
				connections.get(1),
				metaModel.getComponentForScheme("file"));

		readAndDispatch(20);

		AbstractCamelModelElement insertedEP2 = model.findNode("_to2");
		assertThat(insertedEP2.isToEndpoint()).isTrue();
		assertThat(insertedEP2.getInputElement()).isEqualTo(model.findNode("_to1"));
		assertThat(insertedEP2.getOutputElement()).isEqualTo(outbox);
		assertThat(connections.size()).isEqualTo(3);

	}
	
	@Test
	public void deleteElement_respectComponentHeightWhenThereAreHighHeightChoiceContainer() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/deleteKeepHeight");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement inbox = model.findNode("inbox");
		AbstractCamelModelElement outbox = model.findNode("outbox");
		AbstractCamelModelElement deleteNode = model.findNode("deleteMe");
		
		// now delete the node
		deleteNode(fp, deleteNode);
		
		readAndDispatch(20);
		
		assertThat(fp.getPictogramElementForBusinessObject(inbox).getGraphicsAlgorithm().getHeight()).isEqualTo(FigureUIFactory.IMAGE_DEFAULT_HEIGHT);
		assertThat(fp.getPictogramElementForBusinessObject(outbox).getGraphicsAlgorithm().getHeight()).isEqualTo(FigureUIFactory.IMAGE_DEFAULT_HEIGHT);
	}
	
	@Test
	public void createASecondRoute() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement route = model.findNode("_route5");
		CreateContext createCtx = new CreateContext();
		GraphicsAlgorithm exisitngRoutegraphic = fp.getPictogramElementForBusinessObject(route).getGraphicsAlgorithm();
		createCtx.setX(exisitngRoutegraphic.getX());
		createCtx.setY(exisitngRoutegraphic.getY() + exisitngRoutegraphic.getWidth() + 5);
		createCtx.setTargetContainer(fp.getDiagramTypeProvider().getDiagram());
		CreateFigureFeature createRouteFigureFeature = new CreateFigureFeature(fp, "Route", "", getEIP(ed, "route"));
		executeCommandInTransactionDomain(createCtx, createRouteFigureFeature);
		
		AbstractCamelModelElement createdRoute = model.findNode("_route1");
		assertThat(createdRoute).isNotNull();
	}

	protected Eip getEIP(CamelDesignEditor ed, String name) {
		return CamelCatalogCacheManager.getInstance().getCamelModelForProject(ed.getWorkspaceProject(), new NullProgressMonitor()).getEip(name);
	}

	@Test
	public void dropRouteOnRoute() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement route = model.findNode("_route5");
		CreateContext createCtx = new CreateContext();
		GraphicsAlgorithm exisitngRoutegraphic = fp.getPictogramElementForBusinessObject(route).getGraphicsAlgorithm();
		createCtx.setX(exisitngRoutegraphic.getX());
		createCtx.setY(exisitngRoutegraphic.getY() + exisitngRoutegraphic.getWidth() + 5);
		createCtx.setTargetContainer((ContainerShape)fp.getPictogramElementForBusinessObject(route));
		CreateFigureFeature createRouteFigureFeature = new CreateFigureFeature(fp, "Route", "", getEIP(ed, "route"));
		assertThat(createRouteFigureFeature.canExecute(createCtx)).isFalse();
	}
	
	@Test
	public void dropWhenOnRoute() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement route = model.findNode("_route5");
		
		CreateContext createCtx = new CreateContext();
		GraphicsAlgorithm exisitngRoutegraphic = fp.getPictogramElementForBusinessObject(route).getGraphicsAlgorithm();
		createCtx.setX(exisitngRoutegraphic.getX());
		createCtx.setY(exisitngRoutegraphic.getY() + exisitngRoutegraphic.getWidth() + 5);
		createCtx.setTargetContainer((ContainerShape)fp.getPictogramElementForBusinessObject(route));
		CreateFigureFeature createWhenFigureFeature = new CreateFigureFeature(fp, "When", "", getEIP(ed, "when"));
		assertThat(createWhenFigureFeature.canExecute(createCtx)).isTrue();
		executeCommandInTransactionDomain(createCtx, createWhenFigureFeature);
		AbstractCamelModelElement when = model.findNode("_when1");
		assertThat(when).isNotNull();
		PictogramElement pe = fp.getPictogramElementForBusinessObject(when);
		assertThat(pe).isNotNull();
	}
	
	@Test
	public void dropRouteOnLog() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/basic");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		AbstractCamelModelElement log = model.findNode("log1");
		CreateContext createCtx = new CreateContext();
		GraphicsAlgorithm exisitngLoggraphic = fp.getPictogramElementForBusinessObject(log).getGraphicsAlgorithm();
		createCtx.setX(exisitngLoggraphic.getX());
		createCtx.setY(exisitngLoggraphic.getY() + exisitngLoggraphic.getWidth() + 5);
		createCtx.setTargetContainer((ContainerShape)fp.getPictogramElementForBusinessObject(log));
		CreateFigureFeature createRouteFigureFeature = new CreateFigureFeature(fp, "Route", "", getEIP(ed, "route"));
		assertThat(createRouteFigureFeature.canExecute(createCtx)).isFalse();
	}
	
	@Test
	public void dropRouteOnCamelContext() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		Diagram dia = fp.getDiagramTypeProvider().getDiagram();

		CreateContext createCtx = new CreateContext();
		createCtx.setX(1);
		createCtx.setY(1);
		createCtx.setTargetContainer(dia);
		CreateFigureFeature createRouteFigureFeature = new CreateFigureFeature(fp, "Route", "", getEIP(ed, "route"));
		assertThat(createRouteFigureFeature.canExecute(createCtx)).isTrue();
	}
	
	@Test
	public void reconnectToUnwiredNodeShouldBePossible() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		
		AbstractCamelModelElement route = model.findNode("route1");
		AbstractCamelModelElement inbox = model.findNode("inbox");

		CreateContext createCtx = new CreateContext();
		GraphicsAlgorithm exisitngRoutegraphic = fp.getPictogramElementForBusinessObject(route).getGraphicsAlgorithm();
		createCtx.setX(exisitngRoutegraphic.getX());
		createCtx.setY(exisitngRoutegraphic.getY() + exisitngRoutegraphic.getWidth() + 5);
		createCtx.setTargetContainer((ContainerShape)fp.getPictogramElementForBusinessObject(route));
		CreateFigureFeature createFigureFeature = new CreateFigureFeature(fp, "Bean", "",  getEIP(ed, "bean"));
		executeCommandInTransactionDomain(createCtx, createFigureFeature);
		
		AbstractCamelModelElement createdBean = model.findNode("_bean1");
		assertThat(createdBean).isNotNull();

		// now check if the reconnect is possible - it should be
		assertThat(ReconnectNodesFeature.canElementsConnect(inbox, createdBean)).isTrue();
	}

	@Test
	public void reconnectToWiredNodeShouldNotBepossible() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		CamelFile model = ed.getModel();
		
		AbstractCamelModelElement inbox = model.findNode("inbox");
		AbstractCamelModelElement outbox = model.findNode("outbox");
		
		// now check if the reconnect is possible - it should NOT be
		assertThat(ReconnectNodesFeature.canElementsConnect(inbox, outbox)).isFalse();
	}
	
	@Test
	public void reconnectToFirstInFlowShouldNotBePossible() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		CamelFile model = ed.getModel();
		
		AbstractCamelModelElement inbox = model.findNode("inbox");
		AbstractCamelModelElement outbox = model.findNode("outbox");
		
		// now check if the reconnect is possible - it should NOT be
		assertThat(ReconnectNodesFeature.canElementsConnect(outbox, inbox)).isFalse();
	}

	@Test
	public void reconnectToNestedNodeShouldNotBePossible() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		CamelFile model = ed.getModel();
		
		AbstractCamelModelElement inbox = model.findNode("inbox");
		AbstractCamelModelElement to1 = model.findNode("to1");
		
		// now check if the reconnect is possible - it should NOT be
		assertThat(ReconnectNodesFeature.canElementsConnect(inbox, to1)).isFalse();
	}
	
	@Test
	public void reconnectSourceToLastElementOfFlowShouldBePossible() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect2");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();

		AbstractCamelModelElement logger = model.findNode("logger");
		AbstractCamelModelElement bean = model.findNode("bean");
		
		EList<Connection> connections = fp.getDiagramTypeProvider().getDiagram().getConnections();
		Connection con = findConnection(fp, connections, logger, bean);
		// delete the connection
		DeleteContext delcon = new DeleteContext(con);
		delcon.putProperty(DeleteFigureFeature.SKIP_ASKING_DELETE_CONFIRMATION, "true");
		IDeleteFeature deleteFeature = fp.getDeleteFeature(delcon);
		executeCommandInTransactionDomain(delcon, deleteFeature);

		// now check if the reconnect is possible - it should be
		assertThat(ReconnectNodesFeature.canElementsConnect(logger, bean)).isTrue();
	}
	
	@Test
	public void collapsedRoutesStaySmallWhenCollapseExpandChoiceInOtherRoute() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/collapseExpand");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		
		AbstractCamelModelElement route1 = model.findNode("generate-order");
		PictogramElement route1PE = fp.getPictogramElementForBusinessObject(route1);
		
		AbstractCamelModelElement route2 = model.findNode("file-to-jms-route");
		PictogramElement route2PE = fp.getPictogramElementForBusinessObject(route2);
		
		AbstractCamelModelElement route3 = model.findNode("jms-cbr-route");
		
		AbstractCamelModelElement choiceInRoute3 = route3.findNode("countrySelection");
		PictogramElement choiceInRoute3PE = fp.getPictogramElementForBusinessObject(choiceInRoute3);
		
		// first collapse routes 1 & 2
		collapseExpand(fp, route1PE);
		readAndDispatch(0);
		collapseExpand(fp, route2PE);
		readAndDispatch(0);
		
		// then we collapse and expand choice in route 3
		collapseExpand(fp, choiceInRoute3PE);
		readAndDispatch(0);
		collapseExpand(fp, choiceInRoute3PE);
		readAndDispatch(0);
		
		// then we check if the routes 1 & 2 still have minimum height
		assertThat(route1PE.getGraphicsAlgorithm().getHeight()).isEqualTo(FigureUIFactory.IMAGE_DEFAULT_HEIGHT);
		assertThat(route2PE.getGraphicsAlgorithm().getHeight()).isEqualTo(FigureUIFactory.IMAGE_DEFAULT_HEIGHT);

		// then expand the routes again
		collapseExpand(fp, route1PE);
		readAndDispatch(0);
		collapseExpand(fp, route2PE);
		readAndDispatch(0);
		
		// then we check if the routes 1 & 2 have more than minimum height
		int route1OriginalHeight = Integer.parseInt(Graphiti.getPeService().getPropertyValue(route1PE, CollapseFeature.PROP_EXPANDED_HEIGHT));
		assertThat(route1PE.getGraphicsAlgorithm().getHeight()).isEqualTo(route1OriginalHeight);
		int route2OriginalHeight = Integer.parseInt(Graphiti.getPeService().getPropertyValue(route2PE, CollapseFeature.PROP_EXPANDED_HEIGHT));
		assertThat(route2PE.getGraphicsAlgorithm().getHeight()).isEqualTo(route2OriginalHeight);
	}
	
	@Test
	public void collapsedAndExpandedOtherwiseAndChoiceStaysInParentBounds() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/collapseExpand");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		
		AbstractCamelModelElement route3 = model.findNode("jms-cbr-route");
		PictogramElement route3PE = fp.getPictogramElementForBusinessObject(route3);
		
		AbstractCamelModelElement choiceInRoute3 = route3.findNode("countrySelection");
		PictogramElement choiceInRoute3PE = fp.getPictogramElementForBusinessObject(choiceInRoute3);
		
		AbstractCamelModelElement otherwiseInRoute3 = route3.findNode("OtherCustomer");
		PictogramElement otherwiseInRoute3PE = fp.getPictogramElementForBusinessObject(otherwiseInRoute3);
		
		// first collapse otherwise and then choice
		collapseExpand(fp, otherwiseInRoute3PE);
		readAndDispatch(0);
		collapseExpand(fp, choiceInRoute3PE);
		readAndDispatch(0);
		
		// then expand the choice and then the otherwise
		collapseExpand(fp, choiceInRoute3PE);
		readAndDispatch(0);
		collapseExpand(fp, otherwiseInRoute3PE);
		readAndDispatch(0);
		
		// then we check if the otherwise width exceeds the bounds of the choice
		ILocation locChoice = Graphiti.getPeLayoutService().getLocationRelativeToDiagram((Shape)choiceInRoute3PE);
		ILocation locOtherw = Graphiti.getPeLayoutService().getLocationRelativeToDiagram((Shape)otherwiseInRoute3PE);
		int choiceRightBoundary = locChoice.getX() + choiceInRoute3PE.getGraphicsAlgorithm().getWidth();
		int otherwiseRightBoundary = locOtherw.getX() + otherwiseInRoute3PE.getGraphicsAlgorithm().getWidth();
		assertThat(otherwiseRightBoundary).isLessThan(choiceRightBoundary);
		
		// then we check if the choice width exceeds the bounds of the route
		ILocation locRoute = Graphiti.getPeLayoutService().getLocationRelativeToDiagram((Shape)route3PE);
		locChoice = Graphiti.getPeLayoutService().getLocationRelativeToDiagram((Shape)choiceInRoute3PE);
		int routeRightBoundary = locRoute.getX() + route3PE.getGraphicsAlgorithm().getWidth();
		choiceRightBoundary = locChoice.getX() + choiceInRoute3PE.getGraphicsAlgorithm().getWidth();
		assertThat(choiceRightBoundary).isLessThan(routeRightBoundary);
	}		
	
	@Test
	public void collapsedOtherwiseStaysSmallInChoice() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/collapseExpand");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		
		CamelRouteElement route3 = (CamelRouteElement)model.findNode("jms-cbr-route");
		
		AbstractCamelModelElement otherwiseInRoute3 = route3.findNode("OtherCustomer");
		PictogramElement otherwiseInRoute3PE = fp.getPictogramElementForBusinessObject(otherwiseInRoute3);
		
		// first collapse otherwise 
		collapseExpand(fp, otherwiseInRoute3PE);
		readAndDispatch(0);
		
		// then we check if the otherwise has small height
		int otherwiseCollapsedHeight = Integer.parseInt(Graphiti.getPeService().getPropertyValue(otherwiseInRoute3PE, CollapseFeature.PROP_COLLAPSED_HEIGHT));
		assertThat(otherwiseInRoute3PE.getGraphicsAlgorithm().getHeight()).isEqualTo(otherwiseCollapsedHeight);
	}	
	
	@Test
	public void collapsedAndRestoredChoiceWithCollapsedOtherwiseKeepsOtherwiseSmall() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/collapseExpand");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelFile model = ed.getModel();
		
		CamelRouteElement route3 = (CamelRouteElement)model.findNode("jms-cbr-route");
		PictogramElement route3PE = fp.getPictogramElementForBusinessObject(route3);
		
		AbstractCamelModelElement choiceInRoute3 = route3.findNode("countrySelection");
		PictogramElement choiceInRoute3PE = fp.getPictogramElementForBusinessObject(choiceInRoute3);
		
		AbstractCamelModelElement otherwiseInRoute3 = route3.findNode("OtherCustomer");
		PictogramElement otherwiseInRoute3PE = fp.getPictogramElementForBusinessObject(otherwiseInRoute3);
		
		// first collapse otherwise, then choice, then route3 
		collapseExpand(fp, otherwiseInRoute3PE);
		collapseExpand(fp, choiceInRoute3PE);
		collapseExpand(fp, route3PE);
		readAndDispatch(0);
		
		// now expand route
		collapseExpand(fp, route3PE);
		readAndDispatch(0);
		
		// now check if choice has still collapsed height
		int choiceCollapsedHeight = Integer.parseInt(Graphiti.getPeService().getPropertyValue(choiceInRoute3PE, CollapseFeature.PROP_COLLAPSED_HEIGHT));
		assertThat(choiceInRoute3PE.getGraphicsAlgorithm().getHeight()).isEqualTo(choiceCollapsedHeight);		
		
		// now expand choice
		collapseExpand(fp, choiceInRoute3PE);
		readAndDispatch(0);
		
		// then we check if the otherwise has small height
		int otherwiseCollapsedHeight = Integer.parseInt(Graphiti.getPeService().getPropertyValue(otherwiseInRoute3PE, CollapseFeature.PROP_COLLAPSED_HEIGHT));
		assertThat(otherwiseInRoute3PE.getGraphicsAlgorithm().getHeight()).isEqualTo(otherwiseCollapsedHeight);
	}	
	
	private void collapseExpand(IFeatureProvider fp, PictogramElement pe) {
		CustomContext cc = new CustomContext(new PictogramElement[] {pe});
		CollapseFeature cf = getCollapseFeature(fp, cc);
		executeCommandInTransactionDomain(cc, cf);
	}
	
	private CollapseFeature getCollapseFeature(IFeatureProvider fp, ICustomContext cc) {
		ICustomFeature[] features = fp.getCustomFeatures(cc);
		CollapseFeature cf = null;
		for (ICustomFeature custF : features) {
			if (custF instanceof CollapseFeature) {
				cf = (CollapseFeature)custF;
				break;
			}
		}
		return cf;
	}
	
	private Connection findConnection(IFeatureProvider fp, EList<Connection> connections, AbstractCamelModelElement source, AbstractCamelModelElement target) {
		for (Connection con : connections) {
			if (source.equals(getNode(fp, con.getStart())) &&
				target.equals(getNode(fp, con.getEnd()))) {
				return con;
			}
		}
		return null;
	}
	
	private AbstractCamelModelElement getNode(IFeatureProvider fp, Anchor anchor) {
		if (anchor != null) {
			Object obj = fp.getBusinessObjectForPictogramElement(anchor.getParent());
			if (obj instanceof AbstractCamelModelElement) {
				return (AbstractCamelModelElement) obj;
			}
		}
		return null;
	}
	
	private void deleteNode(IFeatureProvider fp, AbstractCamelModelElement deleteNode) throws Exception {
		// delete the endpoint
		PictogramElement deleteNodePE = fp.getPictogramElementForBusinessObject(deleteNode);
		DeleteContext deleteCtx = new DeleteContext(deleteNodePE);
		deleteCtx.putProperty(DeleteFigureFeature.SKIP_ASKING_DELETE_CONFIRMATION, "true");
		IDeleteFeature deleteFeature = fp.getDeleteFeature(deleteCtx);
		executeCommandInTransactionDomain(deleteCtx, deleteFeature);
	}
	
	private void createConnector(IFeatureProvider fp, ContainerShape container, Connection con, Component component) throws Exception {
		// create a new file endpoint -> this one should get appended to the log
		CreateContext createCtx = new CreateContext();
		createCtx.setTargetContainer(container);
		if (con != null) {
			createCtx.setTargetConnection(con);
		}
		createCtx.setX(container.getGraphicsAlgorithm().getX()+5);
		createCtx.setY(container.getGraphicsAlgorithm().getY()+5);
		CreateConnectorFigureFeature ccff = new CreateConnectorFigureFeature(fp, component);
		executeCommandInTransactionDomain(createCtx, ccff);
	}
		
}
