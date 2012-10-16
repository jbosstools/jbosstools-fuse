package org.fusesource.ide.commons.ui.actions;

public interface IConnectable {

	public boolean isConnected();

	/**
	 * We can try to connect but it could keep trying for a while and fail, so even though we might not have managed to connect
	 * yet, we should maybe disconnect before trying to connect again.
	 * 
	 * Returns true if we should try and connect as we have not yet - or false if we've connected or are currently trying to connect.
	 */
	public boolean shouldConnect();

	public void connect() throws Exception;

	public void disconnect() throws Exception;
}
