/******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.fuse.transformation.editor.TransformationEditor;

public class CloseEditorWithoutValidTransformationEditorInput implements Runnable {

	private final TransformationEditor transformationEditor;

	public CloseEditorWithoutValidTransformationEditorInput(TransformationEditor transformationEditor) {
		this.transformationEditor = transformationEditor;
	}

	@Override
	public void run() {
		if (this.transformationEditor.getSite() != null && this.transformationEditor.getSite().getPage() != null) {
			IEditorReference[] refs = this.transformationEditor.getSite().getPage().getEditorReferences();
			if (refs != null) {
				closeAllEditorsWithoutValidInput(refs);
			}
		}
	}

	protected void closeAllEditorsWithoutValidInput(IEditorReference[] refs) {
		for (IEditorReference ref : refs) {
			IEditorPart editor = ref.getEditor(false);
			if (editor != null && isValidEditorInput(editor)) {
				this.transformationEditor.getSite().getPage().closeEditor(editor, false);
				editor.dispose();
			}
		}
	}

	protected boolean isValidEditorInput(IEditorPart editor) {
		IEditorInput editorInput = editor.getEditorInput();
		return editorInput instanceof FileEditorInput
				&& !((FileEditorInput)editorInput).getFile().exists();
	}
}