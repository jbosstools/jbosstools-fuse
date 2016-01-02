/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.navigator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.editor.RiderEditor;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.RouteSupport;

public class CamelCtxNavActionProvider extends CommonActionProvider {

	private Action mOpenEditorAction;
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		if(mOpenEditorAction!=null && mOpenEditorAction.isEnabled()){
			menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, mOpenEditorAction);		
		}
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		if(mOpenEditorAction!=null && mOpenEditorAction.isEnabled()){
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, mOpenEditorAction);
		}
		
	}

	@Override
	public void init(ICommonActionExtensionSite aSite) {		
		super.init(aSite);
		if ((aSite.getViewSite() instanceof ICommonViewerWorkbenchSite)
				&& (((ICommonViewerWorkbenchSite)aSite.getViewSite()).getPart() instanceof IViewPart)){	
			final IWorkbenchPage page = ((IViewPart)((ICommonViewerWorkbenchSite)aSite.getViewSite()).getPart()).getViewSite().getPage();
			mOpenEditorAction = new Action("Open"){
				@Override
				public void run() {
					Object o = getActionSite() != null && getActionSite().getStructuredViewer() != null
							&& getActionSite().getStructuredViewer().getStructuredSelection() != null
									? getActionSite().getStructuredViewer().getStructuredSelection().getFirstElement(): null;
					if(o instanceof CamelCtxNavRouteNode){
						CamelCtxNavRouteNode route = (CamelCtxNavRouteNode)o;
						try {
							//IDE.openEditor handles whether the file is already open
							IEditorPart editorPart = IDE.openEditor(page,route.getCamelContextFile(),OpenStrategy.activateOnOpen());
							if(editorPart instanceof RiderEditor){								
								RiderEditor camelEditor = ((RiderEditor)editorPart);
								RouteSupport fromEditor = route.getMatchingRouteFromEditorModel(camelEditor.getModel());
								//switch to the appropriate route
								if(fromEditor!=null && fromEditor!=camelEditor.getSelectedRoute()){
									RiderDesignEditor designEditor = camelEditor.getActiveEditor() instanceof RiderDesignEditor
													? (RiderDesignEditor) camelEditor.getActiveEditor() : null;
									boolean wasDirty = designEditor!=null?designEditor.isDirty():false;
									camelEditor.setSelectedRoute(fromEditor); 
									//selection unnecessarily makes the editor dirty so reset if editor was not dirty already
									if(!wasDirty && designEditor!=null && designEditor.isDirty()){
										designEditor.setDirty(false);//safe?
									}
								}
							}
						} catch (PartInitException e) {
							Activator.getLogger().error("Unable to load the file into camel editor.", e);
						}
					}
				}};
		}
	}
	
}
