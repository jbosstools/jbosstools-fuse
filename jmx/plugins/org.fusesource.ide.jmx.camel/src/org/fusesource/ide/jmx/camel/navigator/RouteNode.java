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

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelRouteMBean;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.Messages;
import org.jboss.tools.jmx.ui.ImageProvider;


public class RouteNode extends ProcessorNodeSupport implements ImageProvider {
	private final RoutesNode routesNode;
	private final CamelRouteElement route;
	private CamelRouteMBean routeMBean;

	public RouteNode(RoutesNode routesNode, CamelRouteElement route) {
		super(routesNode, route);
		this.routesNode = routesNode;
		this.route = route;
	}

	public RoutesNode getRoutesNode() {
		return routesNode;
	}

	@Override
	public CamelContextNode getCamelContextNode() {
		return getRoutesNode().getCamelContextNode();
	}

	@Override
	public String toString() {
		return Strings.getOrElse(getRouteId(), "Route");
	}

	@Override
	public Image getImage() {
		return CamelJMXPlugin.getDefault().getImage(route.getIconName().replaceAll(".png", "16.png"));
	}

	@Override
	protected void loadChildren() {
		List<AbstractCamelModelElement> children = route.getChildElements();
		for (AbstractCamelModelElement node : children) {
			if (node.getInputElement() == null) {
				addChild(new ProcessorNode(this, this, node));
			}
		}
	}

	public CamelRouteMBean getRouteMBean() {
		return routeMBean;
	}

	public void setRouteMBean(CamelRouteMBean mbean) {
		this.routeMBean = mbean;
		setPropertyBean(this.routeMBean);
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		Function1<IMenuManager, Void> fn = new Function1<IMenuManager, Void>() {
			@Override
			public Void apply(IMenuManager mm) {
				if (isMBeanStarted()) {
					Action stopRouteAction = new Action(Messages.StopRouteAction, SWT.CHECK) {
						@Override
						public void run() {
							stopMBean();
						}

					};
					stopRouteAction.setToolTipText(Messages.StopRouteActionToolTip);
					stopRouteAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("stop_task.gif"));
					mm.add(stopRouteAction);
				} else {
					Action startRouteAction = new Action(Messages.StartRouteAction, SWT.CHECK) {
						@Override
						public void run() {
							startMBean();
						}

					};
					startRouteAction.setToolTipText(Messages.StartRouteActionToolTip);
					startRouteAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("start_task.gif"));
					mm.add(startRouteAction);
				}
				return null;
			}
		};
		getCamelContextNode().provideContextMenu(menu, fn);
	}

	protected void startMBean() {
		routeMBean.start();

		// TODO how to force a refresh of the dependent views???
	}

	protected void stopMBean() {
		routeMBean.stop();
		// TODO how to force a refresh of the dependent views???
	}

	public boolean isMBeanStarted() {
		String state = routeMBean.getState();
		return Objects.equal("Started", state);
	}

	@Override
	public String getNodeId() {
		if(route != null){
			return route.getId();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof RouteNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return java.util.Objects.hash(getConnection(), routeMBean, route.getId());
	}
}
