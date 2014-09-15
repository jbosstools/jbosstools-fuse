/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.progress.UIJob;
import org.fusesource.ide.commons.ui.actions.HasDoubleClickAction;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;

/**
 * @author lhein
 */
public class DoubleClickAction extends Action implements
		ISelectionChangedListener {
	
	private ISelection selection;
	private CommonViewer viewer;

	public DoubleClickAction() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		this.selection = event.getSelection();
		viewer = (CommonViewer) event.getSource();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (selection == null) return;
		
		StructuredSelection structured = (StructuredSelection) selection;
		final Object element = structured.getFirstElement();
		viewer.expandToLevel(element, 1);
		if (element instanceof HasDoubleClickAction) {
			HasDoubleClickAction hdc = (HasDoubleClickAction) element;
			Action doubleClickAction = hdc.getDoubleClickAction();
			if (doubleClickAction != null) {
				doubleClickAction.run();
			}
		} else if (element instanceof Fabric) {
			final Fabric fabric = (Fabric) element;
			UIJob job = new UIJob("Connect to Fabric: " + fabric.toString()) {
				
				/* (non-Javadoc)
				 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
				 */
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					IStatus answer = super.run(monitor);
					FabricConnectAction action = new FabricConnectAction(fabric);
					action.run();
					viewer.expandToLevel(element, 1);
					return answer;
				}
			};
			job.schedule();
			viewer.expandToLevel(element, 1);
		}
	}
}
