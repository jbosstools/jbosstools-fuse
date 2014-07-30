package org.fusesource.ide.jmx.commons.messages;

public interface HasNodeStatisticsContainer {

	/**
	 * Returns the container of statistics for each node in a route
	 */
	public abstract NodeStatisticsContainer getNodeStatisticsContainer();

}