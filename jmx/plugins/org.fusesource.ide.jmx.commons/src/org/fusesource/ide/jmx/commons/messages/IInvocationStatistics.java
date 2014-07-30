package org.fusesource.ide.jmx.commons.messages;

public interface IInvocationStatistics {

	public abstract long getCounter();

	public abstract long getTotalElapsedTime();

	public abstract long getMinElapsedTime();

	public abstract long getMaxElapsedTime();

	public abstract double getMeanElapsedTime();

}