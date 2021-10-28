/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor;


import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IGotoMarker;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.validation.diagram.IFuseMarker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Node;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GoToMarkerForCamelEditorTest {

	@Mock
	private CamelEditor camelEditor;
	@Mock
	private CamelDesignEditor designEditor;
	@Mock
	private CamelGlobalConfigEditor configEditor;
	@Mock
	private TextEditor sourceEditor;
	@Mock
	private IGotoMarker sourceGotoMarker;
	@Mock
	private IMarker marker;
	@Mock
	private IResource resource;
	@Mock
	private Node xmlNode;

	private GoToMarkerForCamelEditor goToMarkerForCamelEditor;


	@Before
	public void setup() throws CoreException {
		goToMarkerForCamelEditor = new GoToMarkerForCamelEditor(camelEditor);
		doReturn(sourceEditor).when(camelEditor).getSourceEditor();
		doReturn(sourceGotoMarker).when(sourceEditor).getAdapter(IGotoMarker.class);
		doReturn(designEditor).when(camelEditor).getDesignEditor();
		doReturn(configEditor).when(camelEditor).getGlobalConfigEditor();
		doReturn(true).when(marker).exists();
	}

	@Test
	public void testGotoMarkerSource() throws Exception {
		goToMarkerForCamelEditor.gotoMarker(marker);
		verify(sourceEditor).getAdapter(IGotoMarker.class);
		verify(sourceGotoMarker).gotoMarker(marker);
		verify(camelEditor).setActiveEditor(sourceEditor);
	}

	@Test
	public void testGotoMarkerDesignEditor() throws Exception {
		doReturn("nodeId").when(marker).getAttribute(IFuseMarker.CAMEL_ID);

		CamelFile camelFile = new CamelFile(resource);
		final CamelContextElement camelContext = new CamelContextElement(camelFile, null);
		CamelRouteElement route = new CamelRouteElement(camelContext, null);
		route.setParent(camelContext);
		camelContext.addChildElement(route);
		camelFile.addChildElement(camelContext);
		final CamelEndpoint endPoint = new CamelEndpoint("imap:host:port");
		endPoint.setId("nodeId");
		route.addChildElement(endPoint);
		endPoint.setParent(route);
		doReturn(camelFile).when(designEditor).getModel();

		goToMarkerForCamelEditor.gotoMarker(marker);

		verify(camelEditor).setActiveEditor(designEditor);
		// Ensure Source Editor is no called
		verify(sourceEditor, Mockito.never()).getAdapter(IGotoMarker.class);
		verify(sourceGotoMarker, Mockito.never()).gotoMarker(marker);
	}

	@Test
	public void testGotoMarkerGlobalEditor() throws Exception {
		doReturn("nodeId").when(marker).getAttribute(IFuseMarker.CAMEL_ID);

		CamelFile camelFile = new CamelFile(resource);
		final CamelContextElement camelContext = new CamelContextElement(camelFile, null);
		CamelRouteElement route = new CamelRouteElement(camelContext, null);
		camelFile.addChildElement(route);
		final CamelEndpoint endPoint = new CamelEndpoint("imap:host:port");
		endPoint.setId("nodeId");
		route.addChildElement(endPoint);
		endPoint.setParent(camelContext);
		doReturn(camelFile).when(designEditor).getModel();

		goToMarkerForCamelEditor.gotoMarker(marker);

		verify(camelEditor).setActiveEditor(configEditor);
		// Ensure Source Editor is no called
		verify(sourceEditor, Mockito.never()).getAdapter(IGotoMarker.class);
		verify(sourceGotoMarker, Mockito.never()).gotoMarker(marker);
		// ensure Design editor not called
		verify(designEditor, Mockito.never()).getAdapter(IGotoMarker.class);
	}

	@Test
	public void testGotoMarkerDesignEditorFallBackSource() throws Exception {
		doReturn("nodeId").when(marker).getAttribute(IFuseMarker.CAMEL_ID);

		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		final CamelEndpoint endPoint = new CamelEndpoint("imap:host:port");
		endPoint.setId("nodeIdDifferent");
		route.addChildElement(endPoint);
		endPoint.setParent(route);
		doReturn(camelFile).when(designEditor).getModel();

		goToMarkerForCamelEditor.gotoMarker(marker);
		verify(sourceEditor).getAdapter(IGotoMarker.class);
		verify(sourceGotoMarker).gotoMarker(marker);
		verify(camelEditor).setActiveEditor(sourceEditor);
	}

}
