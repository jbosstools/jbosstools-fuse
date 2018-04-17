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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.eclipse.ui.internal.views.markers.ProblemsView;
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.test.util.editor.AbstractCamelEditorIT;
import org.fusesource.ide.camel.validation.ValidationFactory;
import org.fusesource.ide.camel.validation.ValidationResult;
import org.fusesource.ide.foundation.core.util.JobWaiterUtil;
import org.junit.Test;

/**
 * 
 * @author vcornejo
 *
 */
public class MarkersRemoverValidatorIT extends AbstractCamelEditorIT {

	private ProblemsView problemsView = null;

	@Test
	public void checkMarkers() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditorWithProblemsViewOpened("routeWithValidationErrorOnGlobalElements.xml");
		readAndDispatch(20);
		CamelEditor camelEditor = (CamelEditor) openEditorOnFileStore;
		CamelGlobalConfigEditor globalEditor = camelEditor.getGlobalConfigEditor();
		camelEditor.setActiveEditor(globalEditor);
		globalEditor.setFocus();
		readAndDispatch(20);
		globalEditor.reload();
		readAndDispatch(20);

		Map<String, ArrayList<Object>> model = camelEditor.getGlobalConfigEditor().getModel();
		List<Object> elements = model.get(CamelGlobalConfigEditor.FUSE_CAT_ID);
		assertTrue(elements != null && !elements.isEmpty());
		
		for (int i = 0; i < elements.size(); i++) {
			ValidationResult validationResult = ValidationFactory.getInstance().validate((AbstractCamelModelElement) elements.get(i));
			System.out.println("Validation result for "+ elements.get(i) + " :\n"+ validationResult.toString());
		}
		
		waitProblemViewJob();
		
		Method getAllMarkersMethod = ExtendedMarkersView.class.getDeclaredMethod("getAllMarkers", new Class[] {}); //$NON-NLS-1$
		getAllMarkersMethod.setAccessible(true);
		IMarker[] markers = (IMarker[]) getAllMarkersMethod.invoke(problemsView, new Object[] {});
		// there are 2 expected errors or warnings
		assertThat(markers).hasSize(2);

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

		waitProblemViewJob();
		
		markers = (IMarker[]) getAllMarkersMethod.invoke(problemsView, new Object[] {});
		assertThat(markers).isEmpty();

	}

	private void waitProblemViewJob() {
		new JobWaiterUtil(Arrays.asList(problemsView.MARKERSVIEW_UPDATE_JOB_FAMILY)).waitJob(new NullProgressMonitor());
	}

	private IEditorPart openFileInEditorWithProblemsViewOpened(String filePath) throws Exception {
		InputStream inputStream = MarkersRemoverValidatorIT.class.getClassLoader().getResourceAsStream(filePath);
		final IFile fileWithoutContext = fuseProject.getProject().getFile(filePath.startsWith("/") ? filePath.substring(1) : filePath);
		fileWithoutContext.create(inputStream, true, new NullProgressMonitor());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllPerspectives(false, false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());

		readAndDispatch(20);
		this.problemsView = (ProblemsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.ProblemView");
		readAndDispatch(20);
		IEditorPart editor = IDE.openEditor(page, fileWithoutContext, true);
		page.activate(editor);
		readAndDispatch(20);
		return editor;
	}
	
	protected String computeFilePathoUse(String filePath) {
		return filePath;
	}

}
