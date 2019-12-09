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
import org.fusesource.ide.jmx.commons.messages.NodeStatistics;

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
