package org.fusesource.ide.jmx.commons.messages;

public interface INodeStatistics extends IInvocationStatistics {

	// TODO ideally this interface would be moved to ITraceStatistics or something
	public abstract void addExchange(IExchange exchange);

}