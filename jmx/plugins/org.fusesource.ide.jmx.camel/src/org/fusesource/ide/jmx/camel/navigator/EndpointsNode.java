/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.camel.navigator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelContextMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelEndpointMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.util.URIs;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.foundation.ui.tree.RefreshableUI;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.foundation.ui.util.Shells;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.Messages;
import org.jboss.tools.jmx.ui.ImageProvider;


public class EndpointsNode extends RefreshableCollectionNode implements ImageProvider, ContextMenuProvider {
	private final CamelContextNode camelContextNode;
	private Map<String,EndpointSchemeNode> schemeNodes = new HashMap<>();

	public EndpointsNode(CamelContextNode camelContextNode) {
		super(camelContextNode);
		this.camelContextNode = camelContextNode;
	}

	public CamelContextNode getCamelContextNode() {
		return camelContextNode;
	}

	public CamelJMXFacade getFacade() {
		return getCamelContextNode().getFacade();
	}
	
	@Override
	public String toString() {
		return "Endpoints";
	}

	@Override
	public Image getImage() {
		return CamelJMXPlugin.getDefault().getImage("endpoint_folder.png");
	}
	
	@Override
	public void clearChildren() {
		// lets not clear the children as it makes refreshing auto-close nodes etc
		Collection<EndpointSchemeNode> nodes = schemeNodes.values();
		for (EndpointSchemeNode schemeNode : nodes) {
			schemeNode.clearChildren();
		}
	}

	@Override
	protected void refreshUI() {
		super.refreshUI();
		Collection<EndpointSchemeNode> values = schemeNodes.values();
		RefreshableUI ui = getRefreshableUI();
		if (ui != null) {
			for (EndpointSchemeNode schemeNode : values) {
				ui.fireRefresh(schemeNode, false);
			}
		}
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		addCreateEndpointAction(menu, "");
	}

	protected void addCreateEndpointAction(IMenuManager menu, final String defaultEndpointName) {
		Action createEndpointAction = new Action(Messages.CreateEndpointAction, SWT.CHECK) {
			@Override
			public void run() {
				showCreateEndpointDialog(defaultEndpointName);
			}

		};
		createEndpointAction.setToolTipText(Messages.CreateEndpointActionToolTip);
		createEndpointAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("new_queue.png"));
		menu.add(createEndpointAction);
	}

	protected void showCreateEndpointDialog(String defaultEndpointName) {
		InputDialog dialog = new InputDialog(Shells.getShell(), Messages.CreateEndpointDialogTitle,
				Messages.CreateEndpointDialogMessage, defaultEndpointName, null);
		int result = dialog.open();
		if (result == Window.OK) {
			String endpoint = dialog.getValue();
			createEndpoint(endpoint);
		}
	}

	protected void createEndpoint(String uri) {
		if (!Strings.isBlank(uri)) {
			try {
				// TODO when there is a method on the facade do it...
				CamelContextMBean mbean = getCamelContextNode().getCamelContextMBean();
				mbean.createEndpoint(uri);
				
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						refresh();
					}
				});
			} catch (Exception e) {
				CamelJMXPlugin.showUserError("Failed to create Endpoint", "Failed to create endpoint: " + uri, e);
			}
		}
	}


	@Override
	protected void loadChildren() {
		try {
			List<CamelEndpointMBean> endpoints = camelContextNode.getFacade()
					.getEndpoints(camelContextNode.getManagementName());
			for (CamelEndpointMBean endpointMBean : endpoints) {
				String uri = endpointMBean.getEndpointUri();
				String scheme = URIs.getScheme(uri);
				EndpointSchemeNode schemeEndpoint = getEndpointScheme(scheme);
				EndpointNode endpoint = new EndpointNode(schemeEndpoint, endpointMBean);
				schemeEndpoint.addChild(endpoint);
			}
		} catch (Exception e) {
			CamelJMXPlugin.getLogger().warning("Failed to load endpoints for "
					+ camelContextNode + ". " + e, e);
		}
	}

	

	protected EndpointSchemeNode getEndpointScheme(String scheme) {
		EndpointSchemeNode answer = schemeNodes.get(scheme);
		if (answer == null) {
			answer = new EndpointSchemeNode(this, scheme);
			addChild(answer);
			schemeNodes.put(scheme, answer);
		}
		return answer;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof EndpointsNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), schemeNodes);
	}
}
