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
package org.fusesource.ide.camel.validator.marker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.validation.ValidationFactory;
import org.fusesource.ide.camel.test.util.editor.AbstractCamelEditorIT;
import org.junit.Rule;
import org.junit.Test;

/**
 * 
 * @author vcornejo
 *
 */
public class MarkersRemoverValidatorIT extends AbstractCamelEditorIT {

	@Rule
	public FuseProject fuseProject = new FuseProject(MarkersRemoverValidatorIT.class.getName());

	private boolean safeRunnableIgnoreErrorStateBeforeTests;
	private boolean statusHandlerCalled = false;
	private IViewPart problemView = null;
	private StatusHandler statusHandlerBeforetest;
	private IEditorPart openEditorOnFileStore;

	@Test
	public void checkMarkers() throws Exception {
		openEditorOnFileStore = openFileInEditorWithProblemsViewOpened("routeWithValidationErrorOnGlobalElements.xml");
		readAndDispatch(20);
		CamelEditor camelEditor = (CamelEditor) openEditorOnFileStore;
		CamelDesignEditor camelDesignEditor = camelEditor.getDesignEditor();
		CamelGlobalConfigEditor globalEditor = camelEditor.getGlobalConfigEditor();
		camelEditor.setActiveEditor(globalEditor);
		globalEditor.setFocus();
		readAndDispatch(20);
		globalEditor.reload();
		readAndDispatch(20);

		Map<String, ArrayList<Object>> model = camelEditor.getGlobalConfigEditor().getModel();
		List<Object> elements = model.get(CamelGlobalConfigEditor.FUSE_CAT_ID);
		assertTrue(elements != null && elements.size() > 0);

		for (int i = 0; i < elements.size(); i++) {
			ValidationFactory.getInstance().validate((AbstractCamelModelElement) elements.get(i));
		}

		Thread.currentThread().sleep(1000);
		readAndDispatch(20);

		Method getAllMarkersMethod = ExtendedMarkersView.class.getDeclaredMethod("getAllMarkers", new Class[] {}); //$NON-NLS-1$
		getAllMarkersMethod.setAccessible(true);
		IMarker[] markers = (IMarker[]) getAllMarkersMethod.invoke(problemView, new Object[] {});

		int initial = markers.length;
		// there are errors or warnings
		assertThat(initial == 2).isTrue();

		Field deleteButtonField = CamelGlobalConfigEditor.class.getDeclaredField("btnDelete");
		deleteButtonField.setAccessible(true);
		Button deleteButton = (Button) deleteButtonField.get(globalEditor);

		for (int i = 0; i < elements.size(); i++) {
			// select element
			globalEditor.setSelection((AbstractCamelModelElement) elements.get(i));
			// delete selected element from the tree
			deleteButton.notifyListeners(SWT.Selection, null);
			readAndDispatch(20);
		}

		globalEditor.doSave(null);

		Thread.currentThread().sleep(3000);
		readAndDispatch(20);
		readAndDispatch(20);

		markers = (IMarker[]) getAllMarkersMethod.invoke(problemView, new Object[] {});
		assertThat(markers.length == 0).isTrue();

	}

	private IEditorPart openFileInEditorWithProblemsViewOpened(String filePath) throws Exception {
		InputStream inputStream = MarkersRemoverValidatorIT.class.getClassLoader().getResourceAsStream(filePath);
		final IFile fileWithoutContext = fuseProject.getProject().getFile(filePath.startsWith("/") ? filePath.substring(1) : filePath);
		fileWithoutContext.create(inputStream, true, new NullProgressMonitor());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllPerspectives(false, false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());

		readAndDispatch(20);
		this.problemView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.ProblemView");
		readAndDispatch(20);
		IEditorPart editor = IDE.openEditor(page, fileWithoutContext, true);
		page.activate(editor);
		readAndDispatch(20);
		return editor;
	}

}
