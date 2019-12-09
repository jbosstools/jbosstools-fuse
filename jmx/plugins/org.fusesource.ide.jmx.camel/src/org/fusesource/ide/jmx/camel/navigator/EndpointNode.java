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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelBrowsableEndpointMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelContextMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelEndpointMBean;
import org.fusesource.ide.foundation.core.util.URIs;
import org.fusesource.ide.foundation.ui.drop.DropHandler;
import org.fusesource.ide.foundation.ui.drop.DropHandlerFactory;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.foundation.ui.util.Shells;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.Messages;
import org.fusesource.ide.jmx.commons.messages.Exchange;
import org.fusesource.ide.jmx.commons.messages.Exchanges;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IExchangeBrowser;
import org.fusesource.ide.jmx.commons.messages.IMessage;
import org.fusesource.ide.jmx.commons.tree.MessageDropHandler;
import org.fusesource.ide.jmx.commons.tree.MessageDropTarget;
import org.jboss.tools.jmx.ui.ImageProvider;


public class EndpointNode extends NodeSupport implements IExchangeBrowser, MessageDropTarget, DropHandlerFactory, ImageProvider, ContextMenuProvider{

	private final EndpointSchemeNode schemeNode;
	private final CamelEndpointMBean endpointMBean;
	private String remaining;

	public EndpointNode(EndpointSchemeNode schemeNode, CamelEndpointMBean endpointMBean) {
		super(schemeNode);
		this.schemeNode = schemeNode;
		this.endpointMBean = endpointMBean;
		setPropertyBean(endpointMBean);
	}

	@Override
	public String toString() {
		return getRemaining();
	}

	public CamelEndpointMBean getEndpointMBean() {
		return endpointMBean;
	}

	public EndpointSchemeNode getSchemeNode() {
		return schemeNode;
	}

	public String getRemaining() {
		if (remaining == null) {
			remaining = URIs.getRemaining(getEndpointUri());
		}
		return remaining;
	}

	@Override
	public List<IExchange> browseExchanges() {
		List<IExchange> answer = new ArrayList<>();
		if (endpointMBean instanceof CamelBrowsableEndpointMBean) {
			CamelBrowsableEndpointMBean browsable = (CamelBrowsableEndpointMBean) endpointMBean;
			long size = browsable.queueSize();
			try {
				for (int i = 0; i < size; i++) {
					String xml = browsable.browseMessageAsXml(i, true);
					if (xml != null) {
						Exchange exchange = Exchanges.unmarshalNoNamespaceXmlString(xml);
						if (exchange != null) {
							IMessage in = exchange.getIn();
							if (in != null) {
								in.setEndpointUri(getEndpointUri());
							}
							answer.add(exchange);
						}
					}
				}
			} catch (Exception e) {
				CamelJMXPlugin.getLogger().warning("Failed to browse messages for " + this + ". " + e, e);
			}
		}
		return answer;
	}


	@Override
	public Image getImage() {
		return CamelJMXPlugin.getDefault().getImage("queue.png");
	}

	@Override
	public DropHandler createDropHandler(DropTargetEvent event) {
		return new MessageDropHandler(this);
	}

	@Override
	public void dropMessage(IMessage message) {
		try {
			String uri = getEndpointUri();
			getCamelContextNode().send(uri, message);
		} catch (Exception e) {
			CamelJMXPlugin.showUserError("Failed to send message to " + this, "Could not send message to " + this, e);
		}
	}

	public String getEndpointUri() {
		return getEndpointMBean().getEndpointUri();
	}

	public CamelContextNode getCamelContextNode() {
		return schemeNode.getEndpointsNode().getCamelContextNode();
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		Action deleteEndpointAction = new Action(Messages.DeleteEndpointAction, SWT.CHECK) {
			@Override
			public void run() {
				showDeleteEndpointDialog();
			}

		};
		deleteEndpointAction.setToolTipText(Messages.DeleteEndpointActionToolTip);
		deleteEndpointAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("delete.gif"));
		menu.add(deleteEndpointAction);

	}

	protected void showDeleteEndpointDialog() {
		String message = Messages.bind(Messages.DeleteEndpointDialogMessage, endpointMBean.getEndpointUri());
		boolean confirm = MessageDialog.openConfirm(Shells.getShell(), Messages.DeleteEndpointDialogTitle,
				message);
		if (confirm) {
			deleteEndpoint();
		}
	}

	protected void deleteEndpoint() {
		try {
			CamelContextMBean mbean = getCamelContextNode().getCamelContextMBean();
			mbean.removeEndpoints(getEndpointUri());
			schemeNode.refresh();
		} catch (Exception e) {
			CamelJMXPlugin.showUserError("Failed to delete Endpoint", "Failed to delete endpoint: " + getEndpointUri(), e);
		}
	}

	protected void doDelete() {
		String name = getEndpointUri();
		// TODO
		//schemeNode.getEndpointsNode().getCamelContextNode().getFacade().removeEndpoint(name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof EndpointNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return java.util.Objects.hash(getConnection(), endpointMBean, schemeNode);
	}
}
