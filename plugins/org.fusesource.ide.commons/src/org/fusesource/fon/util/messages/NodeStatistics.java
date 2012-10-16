package org.fusesource.fon.util.messages;

public class NodeStatistics extends InvocationStatistics implements INodeStatistics {
	/* (non-Javadoc)
	 * @see org.fusesource.fon.util.messages.INodeStatistics#addExchange(org.fusesource.fon.util.messages.IExchange)
	 */
	@Override
	public void addExchange(IExchange exchange) {
		Long delta = null;
		IMessage in = exchange.getIn();
		if (in != null) {
			delta = in.getElapsedTime();
		}
		increment(delta);
	}
}
