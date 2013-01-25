package org.fusesource.ide.fabric.camel.navigator;

import org.fusesource.fabric.camel.facade.mbean.CamelProcessorMBean;
import org.fusesource.fon.util.messages.INodeStatistics;
import org.fusesource.fon.util.messages.NodeStatisticsContainer;

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
