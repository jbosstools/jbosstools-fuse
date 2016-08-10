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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateConnectorFigureFeature;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class CamelEditorIT {
	
	@Rule
	public FuseProject fuseProject = new FuseProject(CamelEditorIT.class.getName());

	@Before
	public void setup() throws Exception {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart welcomePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		welcomePage.dispose();
		page.closeAllPerspectives(false, false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
	}
	
	@After
	public void tearDown() throws Exception {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllEditors(false);
	}
	
	@Test
	public void openFileWithoutContext() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/beans.xml");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
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
	@Ignore("doesn't work because of EMF transaction errors")
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
	@Ignore("doesn't work because of EMF transaction errors")
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
	
	private void deleteNode(IFeatureProvider fp, AbstractCamelModelElement deleteNode) throws Exception {
		// delete the endpoint
		PictogramElement deleteNodePE = fp.getPictogramElementForBusinessObject(deleteNode);
		DeleteContext deleteCtx = new DeleteContext(deleteNodePE);
		if (fp.getDeleteFeature(deleteCtx).canExecute(deleteCtx)) {
			fp.getDeleteFeature(deleteCtx).execute(deleteCtx);
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
			ccff.execute(createCtx);
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
