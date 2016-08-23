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

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.internal.command.CommandExec;
import org.eclipse.graphiti.internal.command.GenericFeatureCommandWithContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateConnectorFigureFeature;
import org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature;
import org.fusesource.ide.camel.editor.features.delete.DeleteFigureFeature;
import org.fusesource.ide.camel.editor.features.misc.ReconnectNodesFeature;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.FigureUIFactory;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CamelEditorIT {
	
	@Rule
	public FuseProject fuseProject = new FuseProject(CamelEditorIT.class.getName());
	
	private boolean safeRunnableIgnoreErrorStateBeforeTests;
	boolean statusHandlerCalled = false;
	private IViewPart contentOutlineView = null;

	private StatusHandler statusHandlerBeforetest;

	@Before
	public void setup() throws Exception {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart welcomePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		welcomePage.dispose();
		page.closeAllPerspectives(false, false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
		safeRunnableIgnoreErrorStateBeforeTests = SafeRunnable.getIgnoreErrors();
		SafeRunnable.setIgnoreErrors(false);
		statusHandlerBeforetest = Policy.getStatusHandler();
		statusHandlerCalled = false;
		Policy.setStatusHandler(new StatusHandler() {
			
			@Override
			public void show(IStatus status, String title) {
				statusHandlerCalled = true;
			}
		});
	}
	
	@After
	public void tearDown() throws Exception {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllEditors(false);
		SafeRunnable.setIgnoreErrors(safeRunnableIgnoreErrorStateBeforeTests);
		Policy.setStatusHandler(statusHandlerBeforetest);
	}
	
	@Test
	public void openFileWithoutContext() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/beans.xml");
		
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
	}
	
	@Test
	public void openFileWithoutContextWhenOutlinePageOpened() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditorWithOutlineViewOpened("/beans.xml");
		
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		assertThat(statusHandlerCalled).isFalse();
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(contentOutlineView);
	}
	
	@Test
	public void addWiredElementsAfterThenBeforeLog() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/basic.xml");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelModel metaModel = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
		CamelFile model = ed.getModel();
		AbstractCamelModelElement logEP = model.findNode("log1");
		PictogramElement logPE = fp.getPictogramElementForBusinessObject(logEP);
		
		// first we try to append a file endpoint to a log endpoint by droping
		// it on the log endpoint instead of the diagram
		createConnector(fp, (ContainerShape)logPE, null, metaModel.getComponentModel().getComponentForScheme("file"));
		AbstractCamelModelElement fileEP = model.findNode("_to1");
		assertThat(fileEP).isNotNull();
		assertThat(fileEP.isToEndpoint()).isTrue();
		assertThat(logEP.getOutputElement()).isEqualTo(fileEP);
		assertThat(fileEP.getInputElement()).isEqualTo(logEP);

		// then we try to prepend a file endpoint to a log endpoint by droping
		// it on the log endpoint instead of the diagram (as the output is already
		// occupied by the earlier file endpoint we now prepend it as input)
		createConnector(fp, (ContainerShape)logPE, null, metaModel.getComponentModel().getComponentForScheme("file"));
		AbstractCamelModelElement fromFileEP = model.findNode("_to2");
		assertThat(fromFileEP).isNotNull();
		assertThat(fromFileEP.isFromEndpoint()).isTrue();
		assertThat(logEP.getInputElement()).isEqualTo(fromFileEP);
		assertThat(fromFileEP.getOutputElement()).isEqualTo(logEP);
	}
	
	@Test
	public void insertElementIntoFlow() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/insert.xml");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		CamelModel metaModel = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
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
						metaModel.getComponentModel().getComponentForScheme("file"));
		
		readAndDispatch(20);
		
		AbstractCamelModelElement insertedEP = model.findNode("_to1");
		assertThat(insertedEP).isNotNull();
		assertThat(insertedEP.isToEndpoint()).isTrue();
		assertThat(insertedEP.getInputElement()).isEqualTo(inbox);
		assertThat(insertedEP.getOutputElement()).isEqualTo(outbox);
	}
	
	@Test
	public void deleteElementFromFlow() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/delete.xml");
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
		IEditorPart openEditorOnFileStore = openFileInEditor("/delete.xml");
		
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
		CamelModel metaModel = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
		// now drop another file endpoint onto the connection
		createConnector(fp, 
						(ContainerShape)fp.getPictogramElementForBusinessObject(inbox.getParent()), 
						con,
						metaModel.getComponentModel().getComponentForScheme("file"));
		
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
				metaModel.getComponentModel().getComponentForScheme("file"));

		readAndDispatch(20);

		AbstractCamelModelElement insertedEP2 = model.findNode("_to2");
		assertThat(insertedEP2.isToEndpoint()).isTrue();
		assertThat(insertedEP2.getInputElement()).isEqualTo(model.findNode("_to1"));
		assertThat(insertedEP2.getOutputElement()).isEqualTo(outbox);
		assertThat(connections.size()).isEqualTo(3);

	}
	
	@Test
	public void deleteElement_respectComponentHeightWhenThereAreHighHeightChoiceContainer() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/deleteKeepHeight.xml");
		
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
		IEditorPart openEditorOnFileStore = openFileInEditor("/route.xml");
		
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
		CreateFigureFeature createRouteFigureFeature = new CreateFigureFeature(fp, "Route", "", CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion()).getEipModel().getEIPByName("route"));
		if(createRouteFigureFeature.canExecute(createCtx)){
			TransactionalEditingDomain editingDomain = CamelUtils.getDiagramEditor().getEditingDomain();
			CommandExec.getSingleton().executeCommand(new GenericFeatureCommandWithContext(createRouteFigureFeature, createCtx), editingDomain);
		}
		
		AbstractCamelModelElement createdRoute = model.findNode("_route1");
		assertThat(createdRoute).isNotNull();
	}

	@Test
	public void dropRouteOnRoute() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route.xml");
		
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
		CreateFigureFeature createRouteFigureFeature = new CreateFigureFeature(fp, "Route", "", CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion()).getEipModel().getEIPByName("route"));
		assertThat(createRouteFigureFeature.canExecute(createCtx)).isFalse();
	}
	
	@Test	
	public void dropWhenOnRoute() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route.xml");
		
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
		CreateFigureFeature createWhenFigureFeature = new CreateFigureFeature(fp, "When", "", CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion()).getEipModel().getEIPByName("when"));
		assertThat(createWhenFigureFeature.canExecute(createCtx)).isTrue();
		if(createWhenFigureFeature.canExecute(createCtx)){
			TransactionalEditingDomain editingDomain = CamelUtils.getDiagramEditor().getEditingDomain();
			CommandExec.getSingleton().executeCommand(new GenericFeatureCommandWithContext(createWhenFigureFeature, createCtx), editingDomain);
		}
		AbstractCamelModelElement when = model.findNode("_when1");
		assertThat(when).isNotNull();
		PictogramElement pe = fp.getPictogramElementForBusinessObject(when);
		assertThat(pe).isNotNull();
	}
	
	@Test
	public void dropRouteOnLog() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/basic.xml");
		
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
		CreateFigureFeature createRouteFigureFeature = new CreateFigureFeature(fp, "Route", "", CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion()).getEipModel().getEIPByName("route"));
		assertThat(createRouteFigureFeature.canExecute(createCtx)).isFalse();
	}
	
	@Test
	public void dropRouteOnCamelContext() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route.xml");
		
		readAndDispatch(20);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		IFeatureProvider fp = ed.getFeatureProvider();
		Diagram dia = fp.getDiagramTypeProvider().getDiagram();

		CreateContext createCtx = new CreateContext();
		createCtx.setX(1);
		createCtx.setY(1);
		createCtx.setTargetContainer(dia);
		CreateFigureFeature createRouteFigureFeature = new CreateFigureFeature(fp, "Route", "", CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion()).getEipModel().getEIPByName("route"));
		assertThat(createRouteFigureFeature.canExecute(createCtx)).isTrue();
	}
	
	@Test
	public void reconnectToUnwiredNodeShouldBePossible() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect.xml");
		
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
		CreateFigureFeature createFigureFeature = new CreateFigureFeature(fp, "Bean", "", CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion()).getEipModel().getEIPByName("bean"));
		if(createFigureFeature.canExecute(createCtx)){
			TransactionalEditingDomain editingDomain = CamelUtils.getDiagramEditor().getEditingDomain();
			CommandExec.getSingleton().executeCommand(new GenericFeatureCommandWithContext(createFigureFeature, createCtx), editingDomain);
		}
		
		AbstractCamelModelElement createdBean = model.findNode("_bean1");
		assertThat(createdBean).isNotNull();

		// now check if the reconnect is possible - it should be
		assertThat(ReconnectNodesFeature.canElementsConnect(inbox, createdBean)).isTrue();
	}

	@Test
	public void reconnectToWiredNodeShouldNotBepossible() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect.xml");
		
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
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect.xml");
		
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
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect.xml");
		
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
		IEditorPart openEditorOnFileStore = openFileInEditor("/reconnect2.xml");
		
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
		if (deleteFeature.canExecute(delcon)) {
			TransactionalEditingDomain editingDomain = CamelUtils.getDiagramEditor().getEditingDomain();
			CommandExec.getSingleton().executeCommand(new GenericFeatureCommandWithContext(deleteFeature, delcon), editingDomain);
		}

		// now check if the reconnect is possible - it should be
		assertThat(ReconnectNodesFeature.canElementsConnect(logger, bean)).isTrue();
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
		if (deleteFeature.canExecute(deleteCtx)) {
			TransactionalEditingDomain editingDomain = CamelUtils.getDiagramEditor().getEditingDomain();
			CommandExec.getSingleton().executeCommand(new GenericFeatureCommandWithContext(deleteFeature, deleteCtx), editingDomain);
		}
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
		if (ccff.canExecute(createCtx)) {
			TransactionalEditingDomain editingDomain = CamelUtils.getDiagramEditor().getEditingDomain();
			CommandExec.getSingleton().executeCommand(new GenericFeatureCommandWithContext(ccff, createCtx), editingDomain);
		}
	}
	
	private IEditorPart openFileInEditor(String filePath) throws Exception {
		InputStream inputStream = CamelEditorIT.class.getClassLoader().getResourceAsStream(filePath);
		final IFile fileWithoutContext = fuseProject.getProject().getFile(filePath.startsWith("/") ? filePath.substring(1) : filePath);
		fileWithoutContext.create(inputStream, true, new NullProgressMonitor());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllPerspectives(false, false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
		readAndDispatch(20);
		IEditorPart editor = IDE.openEditor(page, fileWithoutContext, true);
		page.activate(editor);
		editor.setFocus();
		readAndDispatch(20);
		return editor;
	}
	
	private IEditorPart openFileInEditorWithOutlineViewOpened(String filePath) throws Exception {
		InputStream inputStream = CamelEditorIT.class.getClassLoader().getResourceAsStream(filePath);
		final IFile fileWithoutContext = fuseProject.getProject().getFile(filePath.startsWith("/") ? filePath.substring(1) : filePath);
		fileWithoutContext.create(inputStream, true, new NullProgressMonitor());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllPerspectives(false, false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
		contentOutlineView  = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.ContentOutline");
		readAndDispatch(20);
		IEditorPart editor = IDE.openEditor(page, fileWithoutContext, true);
		page.activate(editor);
		editor.setFocus();
		readAndDispatch(20);
		return editor;
	}
	
	protected void readAndDispatch(int currentNumberOfTry) {
		try{
			while (Display.getDefault().readAndDispatch()) {
				
			}
		} catch(SWTException swtException){
			//TODO: remove try catch when https://issues.jboss.org/browse/FUSETOOLS-1913 is done (CI with valid GUI)
			swtException.printStackTrace();
			if(currentNumberOfTry < 100){
				readAndDispatch(currentNumberOfTry ++);
			} else {
				System.out.println("Tried 100 times to wait for UI... Continue and see what happens.");
			}
		}
	}
}
