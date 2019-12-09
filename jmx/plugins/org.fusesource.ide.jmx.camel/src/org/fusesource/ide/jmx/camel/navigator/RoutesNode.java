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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelRouteMBean;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.navigator.stats.model.HasTotalStatistics;
import org.fusesource.ide.jmx.camel.navigator.stats.model.IProcessorStatistics;
import org.fusesource.ide.jmx.camel.navigator.stats.model.IProcessorStatisticsContainer;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IInvocationStatistics;
import org.fusesource.ide.jmx.commons.messages.ITraceExchangeBrowser;
import org.fusesource.ide.jmx.commons.messages.InvocationStatistics;
import org.fusesource.ide.jmx.commons.messages.NodeStatisticsContainer;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;


public class RoutesNode extends RefreshableCollectionNode implements ContextMenuProvider, ITraceExchangeBrowser, ImageProvider, HasTotalStatistics {
	private final CamelContextNode camelContextNode;

	public RoutesNode(CamelContextNode camelContextNode) {
		super(camelContextNode);
		this.camelContextNode = camelContextNode;
	}

	@Override
	public String toString() {
		return "Routes";
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
		    // no default "Contents" page for routes
			return null;
		}
		return super.getAdapter(adapter);
	}

    @Override
    public boolean requiresContentsPropertyPage() {
        return false;
    }

	public CamelJMXFacade getFacade() {
		return camelContextNode.getFacade();
	}

	public CamelContextNode getCamelContextNode() {
		return camelContextNode;
	}

	@Override
	protected void loadChildren() {
		Map<String, RouteNode> routeMap = new HashMap<>();
		CamelRouteContainerElement camelContext = getCamelContextNode().getRouteContainer();
		if (camelContext != null) {
			List<AbstractCamelModelElement> children = camelContext.getChildElements();
			for (AbstractCamelModelElement node : children) {
				if (node instanceof CamelRouteElement) {
					CamelRouteElement route = (CamelRouteElement) node;
					RouteNode routeNode = new RouteNode(this, route);
					String id = route.getId();
					if (id != null) {
						routeMap .put(id, routeNode);
					}
					addChild(routeNode);
				}
			}
		}
		try {
			List<CamelRouteMBean> routeMBeans = getFacade().getRoutes(getManagementName());
			for (CamelRouteMBean mbean : routeMBeans) {
				String routeId = mbean.getRouteId();
				if (routeId != null) {
					RouteNode node = routeMap.get(routeId);
					if (node != null) {
						node.setRouteMBean(mbean);
					}
				}
			}
		} catch (Exception e) {
			CamelJMXPlugin.getLogger().warning(e);
		}
	}

	public String getCamelContextId() {
		return camelContextNode.getContextId();
	}

	public String getManagementName() {
		return camelContextNode.getManagementName();
	}

	@Override
	public List<IExchange> browseExchanges() {
		return getCamelContextNode().browseExchanges();
	}

	@Override
	public NodeStatisticsContainer getNodeStatisticsContainer() {
		return getCamelContextNode().getNodeStatisticsContainer();
	}



	@Override
	public IInvocationStatistics getTotalStatistics() {
		NodeStatisticsContainer container = getNodeStatisticsContainer();
		if (container instanceof IProcessorStatisticsContainer) {
			IProcessorStatisticsContainer processorContainer = (IProcessorStatisticsContainer) container;
			Map<String, IProcessorStatistics> map = processorContainer.getNodeStatsMap();
			Collection<IProcessorStatistics> values = map.values();
			InvocationStatistics stats = new InvocationStatistics();
			for (IProcessorStatistics statistics : values) {
				stats.combineChild(statistics);
			}
			return stats;
		}
		return null;
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		getCamelContextNode().provideContextMenu(menu);
	}

	public List<IPropertySource> getAllProcessorsPropertySourceList() {
		List<IPropertySource> answer = new ArrayList<>();
		Node[] children = getChildren();
		for (Node node : children) {
			if (node instanceof ProcessorNodeSupport) {
				ProcessorNodeSupport processor = (ProcessorNodeSupport) node;
				processor.appendAllProcessorSourceList(answer);
			}
		}
		return answer;
	}

	@Override
	public Image getImage() {
		return CamelJMXPlugin.getDefault().getImage("camel_route_folder.png");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof RoutesNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), camelContextNode);
	}
}
