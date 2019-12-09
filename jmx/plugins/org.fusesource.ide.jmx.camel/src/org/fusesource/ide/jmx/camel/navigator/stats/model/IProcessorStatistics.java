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

package org.fusesource.ide.jmx.camel.navigator.stats.model;

import org.fusesource.ide.jmx.commons.messages.INodeStatistics;

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