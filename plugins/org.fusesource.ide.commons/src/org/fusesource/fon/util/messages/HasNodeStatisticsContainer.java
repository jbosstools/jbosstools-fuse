package org.fusesource.fon.util.messages;

public interface HasNodeStatisticsContainer {

	/**
	 * Returns the container of statistics for each node in a route
	 */
	public abstract NodeStatisticsContainer getNodeStatisticsContainer();

}