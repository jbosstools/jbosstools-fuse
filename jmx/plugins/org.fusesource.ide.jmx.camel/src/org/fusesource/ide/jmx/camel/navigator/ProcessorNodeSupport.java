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

import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.camel.model.service.core.model.*;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IInvocationStatistics;
import org.fusesource.ide.jmx.commons.messages.INodeStatistics;
import org.fusesource.ide.jmx.commons.messages.ITraceExchangeBrowser;
import org.fusesource.ide.jmx.commons.messages.InvocationStatistics;
import org.fusesource.ide.jmx.commons.messages.NodeStatisticsContainer;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;


public abstract class ProcessorNodeSupport extends RefreshableCollectionNode implements ContextMenuProvider, ITraceExchangeBrowser, ImageProvider {

	private final CamelRouteElement route;

	public ProcessorNodeSupport(Node parent, CamelRouteElement route) {
		super(parent);
		this.route = route;
	}

	public CamelRouteElement getRoute() {
		return route;
	}

	public String getRouteId() {
		return route.getId();
	}

	public abstract CamelContextNode getCamelContextNode();

	public abstract String getNodeId();


	@Override
	public List<IExchange> browseExchanges() {
		return getCamelContextNode().getTraceExchanges(getRouteId());
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
		    // no generic contents properties
			return null;
		}
		return super.getAdapter(adapter);
	}

	@Override
    public boolean requiresContentsPropertyPage() {
        return false;
    }

    public List<IPropertySource> getAllProcessorsPropertySourceList() {
		List<IPropertySource> answer = new ArrayList<IPropertySource>();
		appendAllProcessorSourceList(answer);
		return answer;
	}

	protected void appendAllProcessorSourceList(List<IPropertySource> list) {
		Node[] children = getChildren();
		for (Node node : children) {
			if (node instanceof ProcessorNode) {
				ProcessorNodeSupport processor = (ProcessorNodeSupport) node;
				IPropertySource source = processor.getPropertySource();
				if (source != null) {
					list.add(source);
				}
				processor.appendAllProcessorSourceList(list);
			}
		}
	}


	@Override
	public NodeStatisticsContainer getNodeStatisticsContainer() {
		return getCamelContextNode().getNodeStatisticsContainer(getRouteId());
	}

	public INodeStatistics getNodeStatistics() {
		String id = getNodeId();
		if (id != null) {
			return getNodeStatisticsContainer().getNodeStats(id);
		}
		return null;
	}

	public IInvocationStatistics getTotalStatistics() {
		InvocationStatistics stats = new InvocationStatistics();
		appendStatistics(stats);
		return stats;
	}

	protected void appendStatistics(InvocationStatistics stats) {
		INodeStatistics s = getNodeStatistics();
		if (s != null) {
			stats.combineChild(s);
		}
		Node[] children = getChildren();
		for (Node node : children) {
			if (node instanceof ProcessorNodeSupport) {
				ProcessorNodeSupport child = (ProcessorNodeSupport) node;
				child.appendStatistics(stats);
			}
		}
	}

}