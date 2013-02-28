/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
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

package org.fusesource.ide.jmx.ui.internal.views.navigator;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.navigator.NavigatorPlugin;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.LinkHelperService;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.progress.UIJob;

public class UpdateSelectionJob extends UIJob {
	
	public static void launchJob(String viewId) {
        IWorkbench work = PlatformUI.getWorkbench();
        IWorkbenchWindow window = work.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        IViewReference ref = window.getActivePage().findViewReference(viewId);
        if( ref != null ) {
            IWorkbenchPart part = ref.getPart(false);
            if ( part != null && page.isPartVisible(part)) {
            	if( part instanceof CommonNavigator)
            		new UpdateSelectionJob((CommonNavigator)part).schedule();
            }
        }
	}
	
	
	private CommonNavigator commonNavigator;
	private LinkHelperService linkService;
	public UpdateSelectionJob(CommonNavigator commonNavigator) {
		super("Updating Selection Job"); // TODO 
		this.commonNavigator = commonNavigator;
		//linkService = new LinkHelperService((NavigatorContentService)commonNavigator.getCommonViewer().getNavigatorContentService());
	}

	public IStatus runInUIThread(IProgressMonitor monitor) {

		if (!commonNavigator.getCommonViewer().getControl().isDisposed()) {
			SafeRunner.run(new ISafeRunnable() {

				public void run() throws Exception {
					IWorkbenchPage page = commonNavigator.getSite()
							.getPage();
					if (page != null) {
						IEditorPart editor = page.getActiveEditor();
						if (editor != null) {
							IEditorInput input = editor.getEditorInput();
							// TODO is this equivalent to the now removed org.eclipse.ui.internal.navigator.LinkHelperService code?
							commonNavigator.show(new ShowInContext(input, commonNavigator.getCommonViewer().getSelection()));
							/*
							IStructuredSelection newSelection = linkService.getSelectionFor(input);
							if (!newSelection.isEmpty() && 
									!allShown((IStructuredSelection)commonNavigator.getCommonViewer().getSelection(), 
											newSelection)) {
								commonNavigator.selectReveal(newSelection);
							}
							*/
						}
					}
				}
				protected boolean allShown(IStructuredSelection navigatorSel, IStructuredSelection editorSel) {
					List navList = navigatorSel.toList();
					Iterator i = editorSel.iterator();
					while(i.hasNext()) {
						if( !navList.contains(i.next()))
							return false;
					}
					return true;
				}
				public void handleException(Throwable e) {
					String msg = e.getMessage() != null ? e.getMessage()
							: e.toString();
					NavigatorPlugin.logError(0, msg, e);
				}
			});

		}

		return Status.OK_STATUS;
	}
}
