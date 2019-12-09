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

package org.fusesource.ide.camel.model.service.core.tests.integration.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElementIDUtil;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.CamelIOHandlerIT;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.CamelFilesFinder;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class GlobalNewIdIT {
	
	@Rule
	public FuseProject fuseProject = new FuseProject("External Files");
	
	private static final String CAMEL_FILE = "empty-CamelFile.xml";
	
	@Before
	public void setup() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		store.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_ALWAYS);
	}

	@Test
	public void testRouteCreationUniqueId() throws Exception {
		IFile f = prepareProject(CAMEL_FILE);
		openFileInEditor(f);
		checkCorrectEditorOpened(f);
		assertThat(getCurrentActiveEditor()).isNotNull();
		CamelFile model = CamelFilesFinder.getFileFromEditor(f);
		assertThat(model).isNotNull();
		CamelRouteContainerElement ctx = model.getRouteContainer();
		CamelRouteElement route1 = createRoute(model, ctx);
		ctx.addChildElement(route1);
		new CamelModelElementIDUtil().ensureUniqueID(route1);
		assertThat(route1.getId()).isNotEmpty();
		CamelRouteElement route2 = createRoute(model, ctx);
		ctx.addChildElement(route2);
		new CamelModelElementIDUtil().ensureUniqueID(route2);
		assertThat(route2.getId()).isNotEmpty();
		assertThat(route1.getId()).isNotEqualTo(route2.getId());
	}
	
	private CamelRouteElement createRoute(CamelFile cf, CamelRouteContainerElement parent) {
		Node newNode = cf.createElement("route", null);
		return new CamelRouteElement(parent, newNode);
	}
	
	private IFile prepareProject(String name) throws Exception {
		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);

		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());
		return fileInProject;
	}
	
	private void openFileInEditor(IFile file) {
		if (file != null) {
		    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		    try {
		        IDE.openEditor(page, file, CamelUtils.CAMEL_EDITOR_ID, true);
		    } catch ( PartInitException e ) {
		    	e.printStackTrace();
		    }
		} else {
		    //Do something if the file does not exist
		}
	}

	/**
	 * @param camelResource
	 * @throws InterruptedException
	 */
	private void checkCorrectEditorOpened(IFile camelResource) throws InterruptedException {
		readAndDispatch(0);
		int currentAwaitedTime = 0;
		while (getCurrentActiveEditor() == null && currentAwaitedTime < 30000) {
			Thread.sleep(100);
			currentAwaitedTime += 100;
			System.out.println("awaited activation of editor " + currentAwaitedTime);
		}
		// @formatter:off
		IEditorPart editor = getCurrentActiveEditor();
		// @formatter:on
		assertThat(editor).isNotNull();
		IEditorInput editorInput = editor.getEditorInput();
		assertThat(editorInput.getAdapter(IFile.class)).isEqualTo(camelResource);
	}
	
	private void readAndDispatch(int currentNumberOfTry) {
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

	/**
	 * @return
	 */
	private IEditorPart getCurrentActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}
}
