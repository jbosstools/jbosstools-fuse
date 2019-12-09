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

import javax.xml.bind.annotation.XmlAttribute;

import org.fusesource.ide.jmx.commons.messages.IExchange;


/**
 * @author lhein
 */
public class ProcessorStatistics implements IProcessorStatistics  {
	private String id;
	private long exchangesCompleted;
	private long exchangesFailed;
	private long failuresHandled;
	private long redeliveries;
	private long externalRedeliveries;
	private long minProcessingTime;
	private long maxProcessingTime;
	private long totalProcessingTime;
	private long lastProcessingTime;
	private long meanProcessingTime;


	@Override
	public String toString() {
		return "ProcessorStatistics(" + getId() + " completed: " + exchangesCompleted + ")";
	}

	/**
	 * @return the id
	 */
	@Override
	@XmlAttribute (name = "id")
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the exchangesCompleted
	 */
	@Override
	@XmlAttribute (name = "exchangesCompleted")
	public long getExchangesCompleted() {
		return this.exchangesCompleted;
	}

	/**
	 * @param exchangesCompleted the exchangesCompleted to set
	 */
	public void setExchangesCompleted(long exchangesCompleted) {
		this.exchangesCompleted = exchangesCompleted;
	}

	/**
	 * @return the exchangesFailed
	 */
	@Override
	@XmlAttribute (name = "exchangesFailed")
	public long getExchangesFailed() {
		return this.exchangesFailed;
	}

	/**
	 * @param exchangesFailed the exchangesFailed to set
	 */
	public void setExchangesFailed(long exchangesFailed) {
		this.exchangesFailed = exchangesFailed;
	}

	/**
	 * @return the failuresHandled
	 */
	@Override
	@XmlAttribute (name = "failuresHandled")
	public long getFailuresHandled() {
		return this.failuresHandled;
	}

	/**
	 * @param failuresHandled the failuresHandled to set
	 */
	public void setFailuresHandled(long failuresHandled) {
		this.failuresHandled = failuresHandled;
	}

	/**
	 * @return the redeliveries
	 */
	@Override
	@XmlAttribute (name = "redeliveries")
	public long getRedeliveries() {
		return this.redeliveries;
	}

	/**
	 * @param redeliveries the redeliveries to set
	 */
	public void setRedeliveries(long redeliveries) {
		this.redeliveries = redeliveries;
	}

	/**
	 * @return the externalRedeliveries
	 */
	@Override
	@XmlAttribute (name = "externalRedeliveries")
	public long getExternalRedeliveries() {
		return this.externalRedeliveries;
	}

	/**
	 * @param externalRedeliveries the externalRedeliveries to set
	 */
	public void setExternalRedeliveries(long externalRedeliveries) {
		this.externalRedeliveries = externalRedeliveries;
	}

	/**
	 * @return the minProcessingTime
	 */
	@Override
	@XmlAttribute (name = "minProcessingTime")
	public long getMinProcessingTime() {
		return this.minProcessingTime;
	}

	/**
	 * @param minProcessingTime the minProcessingTime to set
	 */
	public void setMinProcessingTime(long minProcessingTime) {
		this.minProcessingTime = minProcessingTime;
	}

	/**
	 * @return the maxProcessingTime
	 */
	@Override
	@XmlAttribute (name = "maxProcessingTime")
	public long getMaxProcessingTime() {
		return this.maxProcessingTime;
	}

	/**
	 * @param maxProcessingTime the maxProcessingTime to set
	 */
	public void setMaxProcessingTime(long maxProcessingTime) {
		this.maxProcessingTime = maxProcessingTime;
	}

	/**
	 * @return the totalProcessingTime
	 */
	@Override
	@XmlAttribute (name = "totalProcessingTime")
	public long getTotalProcessingTime() {
		return this.totalProcessingTime;
	}

	/**
	 * @param totalProcessingTime the totalProcessingTime to set
	 */
	public void setTotalProcessingTime(long totalProcessingTime) {
		this.totalProcessingTime = totalProcessingTime;
	}

	/**
	 * @return the lastProcessingTime
	 */
	@Override
	@XmlAttribute (name = "lastProcessingTime")
	public long getLastProcessingTime() {
		return this.lastProcessingTime;
	}

	/**
	 * @param lastProcessingTime the lastProcessingTime to set
	 */
	public void setLastProcessingTime(long lastProcessingTime) {
		this.lastProcessingTime = lastProcessingTime;
	}

	/**
	 * @return the meanProcessingTime
	 */
	@Override
	@XmlAttribute (name = "meanProcessingTime")
	public long getMeanProcessingTime() {
		return this.meanProcessingTime;
	}

	/**
	 * @param meanProcessingTime the meanProcessingTime to set
	 */
	public void setMeanProcessingTime(long meanProcessingTime) {
		this.meanProcessingTime = meanProcessingTime;
	}


	// INodeStatistics interface

	@Override
	public long getCounter() {
		return exchangesCompleted;
	}

	@Override
	public long getTotalElapsedTime() {
		return totalProcessingTime;
	}

	@Override
	public long getMinElapsedTime() {
		return minProcessingTime;
	}

	@Override
	public long getMaxElapsedTime() {
		return maxProcessingTime;
	}

	@Override
	public double getMeanElapsedTime() {
		return meanProcessingTime;
	}

	@Override
	public void addExchange(IExchange exchange) {
		// TODO ideally this interface would be moved to ITraceStatistics or something
	}
}
