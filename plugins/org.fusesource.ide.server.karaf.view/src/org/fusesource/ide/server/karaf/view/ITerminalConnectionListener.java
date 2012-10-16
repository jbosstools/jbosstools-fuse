package org.fusesource.ide.server.karaf.view;

/**
 * @author lhein
 */
public interface ITerminalConnectionListener {
	
	/**
	 * called upon establishing connection
	 */
	void onConnect();
	
	/**
	 * called upon disconnecting from host
	 */
	void onDisconnect();
}
