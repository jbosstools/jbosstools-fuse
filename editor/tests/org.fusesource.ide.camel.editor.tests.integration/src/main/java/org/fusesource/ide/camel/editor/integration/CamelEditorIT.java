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

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CamelEditorIT {
	
	@Rule
	public FuseProject fuseProject = new FuseProject(CamelEditorIT.class.getName());

	@Test
	public void openFileWithoutContext() throws Exception {
		InputStream inputStream = CamelEditorIT.class.getClassLoader().getResourceAsStream("/beans.xml");
		final IFile fileWithoutContext = fuseProject.getProject().getFile("beans.xml");
		fileWithoutContext.create(inputStream, true, new NullProgressMonitor());

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart openEditorOnFileStore = IDE.openEditor(page, fileWithoutContext);
		
		assertThat(((CamelEditor)openEditorOnFileStore).getDesignEditor()).isNotNull();
	}
	
}
