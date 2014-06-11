/*******************************************************************************
 * Copyright (c) 2008 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.actions;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.navigator.CommonViewer;
import org.fusesource.ide.commons.ui.actions.HasDoubleClickAction;
import org.fusesource.ide.jmx.core.ConnectJob;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.ui.internal.EditorUtils;
import org.fusesource.ide.jmx.ui.internal.editors.EditorConnectionMapping;


/**
 * The double click action
 */
public class DoubleClickAction extends Action implements
ISelectionChangedListener {
	private ISelection selection;
	private CommonViewer viewer;
	protected EditorConnectionMapping mapping;
	public DoubleClickAction() {
		mapping = new EditorConnectionMapping();
	}
	public EditorConnectionMapping getMapping() {
		return mapping;
	}
	public void selectionChanged(SelectionChangedEvent event) {
		this.selection = event.getSelection();
		viewer = (CommonViewer)event.getSource();
	}

	@Override
	public void run() {
		if( selection == null )
			return;

		StructuredSelection structured = (StructuredSelection) selection;
		final Object element = structured.getFirstElement();

		if (element instanceof IConnectionWrapper) {
			IConnectionWrapper root = (IConnectionWrapper) element;
			IConnectionWrapper[] connection = new IConnectionWrapper[]{root};
			ConnectJob job = new ConnectJob(viewer, connection){

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IStatus answer = super.run(monitor);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							viewer.expandToLevel(element, 1);
						}
					});
					return answer;
				}
			};
			job.schedule();
			viewer.expandToLevel(element, 1);
		} else if (element instanceof HasDoubleClickAction) {
			HasDoubleClickAction hdc = (HasDoubleClickAction) element;
			Action doubleClickAction = hdc.getDoubleClickAction();
			if (doubleClickAction != null) {
				doubleClickAction.run();
			}
		}

		IEditorInput editorInput = EditorUtils.getEditorInput(element);
		if (editorInput != null) {
			IEditorPart editor = EditorUtils
					.openMBeanEditor(editorInput);
			if (editor != null) {
				EditorUtils.revealInEditor(editor, element);
				editor.setFocus();
				mapping.open(findParent(structured), editor);
			}
		}
	}

	protected IConnectionWrapper findParent(IStructuredSelection sel) {
		if( sel instanceof TreeSelection ) {
			TreeSelection sel2 = ((TreeSelection)sel);
			TreePath[] paths = sel2.getPathsFor(sel.getFirstElement());
			if( paths != null && paths.length == 1 ) {
				if( paths[0].getFirstSegment() instanceof IConnectionWrapper )
					return (IConnectionWrapper) paths[0].getFirstSegment();
			}
		}
		return null;
	}
}
