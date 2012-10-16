package org.fusesource.ide.fabric.camel.navigator;

import org.fusesource.fabric.camel.facade.mbean.CamelProcessorMBean;
import org.fusesource.fon.util.messages.NodeStatistics;

public class ProcessorNodeStatistics extends NodeStatistics {
	private final CamelProcessorMBean processorMBean;
	
	public ProcessorNodeStatistics(CamelProcessorMBean processorMBean) {
		this.processorMBean = processorMBean;
	}

	@Override
	public long getCounter() {
		return processorMBean.getExchangesTotal();
	}

	@Override
	public long getTotalElapsedTime() {
		return processorMBean.getTotalProcessingTime();
	}

	@Override
	public long getMinElapsedTime() {
		return processorMBean.getMinProcessingTime();
	}

	@Override
	public long getMaxElapsedTime() {
		return processorMBean.getMaxProcessingTime();
	}

	@Override
	public double getMeanElapsedTime() {
		return processorMBean.getMeanProcessingTime();
	}


}
