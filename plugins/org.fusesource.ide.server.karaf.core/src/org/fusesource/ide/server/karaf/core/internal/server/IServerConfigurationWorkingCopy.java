package org.fusesource.ide.server.karaf.core.internal.server;

/**
 * @author lhein
 */
public interface IServerConfigurationWorkingCopy extends IServerConfiguration {
	
	/**
	 * sets the host name
	 * 
	 * @param hostName
	 */
	void setHostName(String hostName);

	/**
	 * sets the password
	 * 
	 * @param password
	 */
	void setPassword(String password);

	/**
	 * sets the port number
	 * 
	 * @param portNo
	 */
	void setPortNumber(int portNo);

	/**
	 * sets the user name
	 * 
	 * @param userName
	 */
	void setUserName(String userName);
}
