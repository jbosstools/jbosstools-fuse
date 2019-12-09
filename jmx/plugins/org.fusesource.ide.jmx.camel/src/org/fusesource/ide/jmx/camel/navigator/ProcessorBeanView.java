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

import org.fusesource.ide.jmx.camel.navigator.stats.model.IProcessorStatistics;
import org.fusesource.ide.jmx.commons.messages.INodeStatistics;
import org.fusesource.ide.jmx.commons.messages.NodeStatisticsContainer;



/**
 * A little facade to abstract away the CamelProcessorMBean so that we can use the underlying caching
 * mechanism rather than talking chattily to JMX
 *
 */
public class ProcessorBeanView {
	private final CamelContextNode camelContextNode;
	private String routeId;
	private String processorId;
	private String state;

	public ProcessorBeanView(CamelContextNode camelContextNode, String routeId, String processorId) {
		this.camelContextNode = camelContextNode;
		this.routeId = routeId;
		this.processorId = processorId;
	}

	public String getCamelId() {
		return camelContextNode.getContextId();
	}

	public String getRouteId() {
		return routeId;
	}

	public String getProcessorId() {
		return processorId;
	}

	protected INodeStatistics nodeStatistics() {
		NodeStatisticsContainer container = camelContextNode.getNodeStatisticsContainer(routeId);
		if (container != null){
			return container.getNodeStats(processorId);
		}
		return null;
	}

	protected IProcessorStatistics stats() {
		INodeStatistics answer = nodeStatistics();
		if (answer instanceof IProcessorStatistics) {
			return (IProcessorStatistics) answer;
		}
		return null;
	}

	public Long getExchangesCompleted() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getExchangesCompleted();
	}

	public Long getExchangesFailed() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getExchangesFailed();
	}

	public Long getFailuresHandled() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getFailuresHandled();
	}

	public Long getRedeliveries() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getRedeliveries();
	}

	public Long getExternalRedeliveries() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getExternalRedeliveries();
	}

	public Long getMinProcessingTime() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getMinProcessingTime();
	}

	public Long getMaxProcessingTime() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getMaxProcessingTime();
	}

	public Long getTotalProcessingTime() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getTotalProcessingTime();
	}

	public Long getLastProcessingTime() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getLastProcessingTime();
	}

	public Long getMeanProcessingTime() {
		IProcessorStatistics stats = stats();
		return (stats == null) ? null : stats.getMeanProcessingTime();
	}


	/**
	  // TODO stuff from the MBean ommitted for now

	public String getState() {
		// TODO needs MBean...
	}
	 */

}
