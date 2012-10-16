package org.fusesource.ide.fabric.camel.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Image;
import org.fusesource.fon.util.messages.Exchange;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IExchange;
import org.fusesource.fon.util.messages.IExchangeBrowser;
import org.fusesource.fon.util.messages.IMessage;

import org.fusesource.fabric.camel.facade.mbean.CamelBrowsableEndpointMBean;
import org.fusesource.fabric.camel.facade.mbean.CamelContextMBean;
import org.fusesource.fabric.camel.facade.mbean.CamelEndpointMBean;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.commons.ui.drop.DropHandler;
import org.fusesource.ide.commons.ui.drop.DropHandlerFactory;
import org.fusesource.ide.commons.util.URIs;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.camel.FabricCamelPlugin;
import org.fusesource.ide.fabric.camel.Messages;
import org.fusesource.ide.fabric.navigator.MessageDropHandler;
import org.fusesource.ide.fabric.navigator.MessageDropTarget;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;


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
		List<IExchange> answer = new ArrayList<IExchange>();
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
				FabricCamelPlugin.getLogger().warning("Failed to browse messages for " + this + ". " + e, e);
			}
		}
		return answer;
	}


	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("queue.png");
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
			FabricPlugin.showUserError("Failed to send message to " + this, "Could not send message to " + this, e);
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
		deleteEndpointAction.setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("delete.gif"));
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
			FabricPlugin.showUserError("Failed to delete Endpoint", "Failed to delete endpoint: " + getEndpointUri(), e);
		}
	}

	protected void doDelete() {
		String name = getEndpointUri();
		// TODO
		//schemeNode.getEndpointsNode().getCamelContextNode().getFacade().removeEndpoint(name);
	}
}
