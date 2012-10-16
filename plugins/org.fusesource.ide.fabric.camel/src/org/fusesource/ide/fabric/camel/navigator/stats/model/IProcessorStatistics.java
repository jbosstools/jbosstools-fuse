package org.fusesource.ide.fabric.camel.navigator.stats.model;

import org.fusesource.fon.util.messages.INodeStatistics;

public interface IProcessorStatistics extends INodeStatistics {

	public String getId();
	public long getExchangesCompleted();
	public long getExchangesFailed();
	public long getFailuresHandled();
	public long getRedeliveries();
	public long getExternalRedeliveries();
	public long getMinProcessingTime();
	public long getMaxProcessingTime();
	public long getTotalProcessingTime();
	public long getLastProcessingTime();
	public long getMeanProcessingTime();


}