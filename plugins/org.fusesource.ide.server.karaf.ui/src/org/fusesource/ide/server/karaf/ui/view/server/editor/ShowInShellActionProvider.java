/*******************************************************************************
 * Copyright (c) 2007 - 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.ui.view.server.editor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class ShowInShellActionProvider extends CommonActionProvider {
	private ICommonActionExtensionSite actionSite;

	public ShowInShellActionProvider() {
		super();
	}

	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		this.actionSite = aSite;
		createActions(aSite);
	}

	protected void createActions(ICommonActionExtensionSite aSite) {
		ICommonViewerWorkbenchSite commonViewerWorkbenchSite =
				CommonActionProviderUtils.getCommonViewerWorkbenchSite(aSite);
		if (commonViewerWorkbenchSite != null) {
			// TODO add a show-in shell action
			//showInServerLogAction = new ShowInServerLogAction(commonViewerWorkbenchSite.getSelectionProvider());
		}
	}

	public void fillContextMenu(IMenuManager menu) {
		//CommonActionProviderUtils.addToShowInQuickSubMenu(showInServerLogAction, menu, actionSite);
	}

//	public class ShowInServerLogAction extends AbstractServerAction {
//		public ShowInServerLogAction(ISelectionProvider sp) {
//			super(sp, null);
//
//			IViewRegistry reg = PlatformUI.getWorkbench().getViewRegistry();
//			IViewDescriptor desc = reg.find(ServerLogView.VIEW_ID);
//			setText(desc.getLabel());
//			setImageDescriptor(desc.getImageDescriptor());
//		}
//
//		public boolean accept(IServer server) {
//			return (server.getServerType() != null && server.loadAdapter(IDeployableServer.class,
//					new NullProgressMonitor()) != null);
//		}
//
//		public void perform(IServer server) {
//			try {
//				IWorkbenchPart part = CommonActionProviderUtils.showView(ServerLogView.VIEW_ID);
//				if (part != null) {
//					ServerLogView view = (ServerLogView) part.getAdapter(ServerLogView.class);
//					if (view != null) {
//						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(view);
//						view.setFocus();
//						view.setServer(server);
//					}
//				}
//			} catch (PartInitException e) {
//				JBossServerUIPlugin.log("could not show view " + ServerLogView.VIEW_ID, e);
//			}
//		}
//	}
}
