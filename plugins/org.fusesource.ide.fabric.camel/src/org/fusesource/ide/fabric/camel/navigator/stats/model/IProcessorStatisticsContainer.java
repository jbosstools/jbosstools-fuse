package org.fusesource.ide.fabric.camel.navigator.stats.model;

import java.util.Map;

import org.fusesource.fon.util.messages.NodeStatisticsContainer;

public interface IProcessorStatisticsContainer extends NodeStatisticsContainer {
	public abstract Map<String, IProcessorStatistics> getNodeStatsMap();
}
