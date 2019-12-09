/*******************************************************************************
 * Copyright (c) 2007 - 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.ui.view.server.editor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.view.servers.AbstractServerAction;
import org.fusesource.ide.server.karaf.ui.KarafUIPlugin;
import org.fusesource.ide.server.karaf.ui.SshConnector;

public class ShowInShellActionProvider extends CommonActionProvider {
	private ICommonActionExtensionSite actionSite;
	private ShowInShellAction showInShellAction;
	
	public ShowInShellActionProvider() {
		super();
	}

	@Override
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		this.actionSite = aSite;
		createActions(aSite);
	}

	protected void createActions(ICommonActionExtensionSite aSite) {
		ICommonViewerWorkbenchSite commonViewerWorkbenchSite =
				CommonActionProviderUtils.getCommonViewerWorkbenchSite(aSite);
		if (commonViewerWorkbenchSite != null) {
			showInShellAction = new ShowInShellAction(commonViewerWorkbenchSite.getSelectionProvider());
		}
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		CommonActionProviderUtils.addToShowInQuickSubMenu(showInShellAction, menu, actionSite);
	}

	/**
	 * @author lhein
	 */
	public class ShowInShellAction extends AbstractServerAction {

		/**
		 * 
		 * @param sp
		 */
		public ShowInShellAction(ISelectionProvider sp) {
			super(sp, null);
			IViewRegistry reg = PlatformUI.getWorkbench().getViewRegistry();
			IViewDescriptor desc = reg.find(KarafUIPlugin.TERMINAL_VIEW_ID);
			setText(desc.getLabel());
			setImageDescriptor(desc.getImageDescriptor());
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.wst.server.ui.internal.view.servers.AbstractServerAction#accept(org.eclipse.wst.server.core.IServer)
		 */
		@Override
		public boolean accept(IServer server) {
			return 	server != null && 
					server.getServerState() == IServer.STATE_STARTED && 
					KarafUIPlugin.getDefault().isKarafServer(server);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.wst.server.ui.internal.view.servers.AbstractServerAction#perform(org.eclipse.wst.server.core.IServer)
		 */
		@Override
		public void perform(IServer server) {
			SshConnector c = SshConnector.getConnectorForServer(server);
			if (c == null) {
				c = new SshConnector(server);
			} else {
				c.onDisconnect();
			}
			c.start();
		}
	}
}
