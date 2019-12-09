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

import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelProcessorMBean;
import org.fusesource.ide.jmx.commons.messages.INodeStatistics;
import org.fusesource.ide.jmx.commons.messages.NodeStatisticsContainer;

public class RuntimeNodeStatisticsContainer implements NodeStatisticsContainer {
	private final CamelContextNode camelContextNode;

	public RuntimeNodeStatisticsContainer(CamelContextNode camelContextNode) {
		this.camelContextNode = camelContextNode;
	}

	@Override
	public INodeStatistics getNodeStats(String nodeId) {
		if (nodeId != null) {
			CamelProcessorMBean processorMBean = camelContextNode.getProcessorMBean(nodeId);
			if (processorMBean != null) {
				return new ProcessorNodeStatistics(processorMBean);
			}
		}
		return null;
	}
}
