/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
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

import java.io.InputStream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.test.util.editor.AbstractCamelEditorIT;
import org.junit.Test;

public class CamelEditorCheckOpeningInSpecialCasesIT extends AbstractCamelEditorIT {
	
	private IViewPart contentOutlineView = null;

	@Test
	public void openRoutesFileAndCheckFor2Tabs() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/routes");
		assertThat(getAvailableEditorTabCount(openEditorOnFileStore)).isEqualTo(2);
	}
	
	@Test
	public void openRouteContextFileAndCheckFor2Tabs() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/routeContext");
		assertThat(getAvailableEditorTabCount(openEditorOnFileStore)).isEqualTo(2);
	}
	
	@Test
	public void openContextFileAndCheckFor4Tabs() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route");
		assertThat(getAvailableEditorTabCount(openEditorOnFileStore)).isEqualTo(4);
	}
	
	private int getAvailableEditorTabCount(IEditorPart editorPart) {
		CamelEditor ed = (CamelEditor)editorPart;
		return ed.getTabbedFolder().getItemCount();
	}
	
	@Test
	public void openFileWithoutContext() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/beans");
		
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
	}
	
	@Test
	public void openFileWithoutContextWhenOutlinePageOpened() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditorWithOutlineViewOpened("/beans");
		
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
		assertThat(statusHandlerCalled).isFalse();
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(contentOutlineView);
	}
	
	private IEditorPart openFileInEditorWithOutlineViewOpened(String filePathPattern) throws Exception {
		String filePath = computeFilePathoUse(filePathPattern);
		InputStream inputStream = CamelEditorIT.class.getClassLoader().getResourceAsStream(filePath);
		camelFileUsedInTest = fuseProject.getProject().getFile(filePath.startsWith("/") ? filePath.substring(1) : filePath);
		camelFileUsedInTest.create(inputStream, true, new NullProgressMonitor());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllPerspectives(false, false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
		
		readAndDispatch(20);
		contentOutlineView  = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.ContentOutline");
		readAndDispatch(20);
		//Workaround to ignore Widget is disposed for JMX Navigator, issue fixed in JBoss Tools 10.0 (by side effect of a larger modification)
		statusHandlerCalled = false;
		IEditorPart editor = IDE.openEditor(page, camelFileUsedInTest, true);
		page.activate(editor);
		editor.setFocus();
		readAndDispatch(20);
		return editor;
	}
	
	protected String computeFilePathoUse(String filePath) {
		return filePath+".xml";
	}
}
