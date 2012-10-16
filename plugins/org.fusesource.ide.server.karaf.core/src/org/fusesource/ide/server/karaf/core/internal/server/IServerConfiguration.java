package org.fusesource.ide.server.karaf.core.internal.server;

/**
 * @author lhein
 */
public interface IServerConfiguration {
	static final String HOST_NAME = "sshHost";
	static final String PORT_NUMBER = "sshPort";
	static final String USER_ID = "userId";
	static final String PASSWORD = "password";
	
	static final String SERVER_TYPE_PREFIX_KARAF   = "org.fusesource.ide.server.karaf.";
	static final String SERVER_TYPE_PREFIX_SMX     = "org.fusesource.ide.server.smx.";
	static final String SERVER_TYPE_PREFIX_FUSEESB = "org.fusesource.ide.server.fuseesb.";
	
	/**
	 * put in here all server type id's to be supported by this karaf adapter
	 * otherwise those servers launch configurations will not be displayed correctly
	 */
	static final String[] SERVER_IDS_SUPPORTED = new String[] {
		"org.fusesource.ide.server.karaf.2x"
	   ,"org.fusesource.ide.server.smx.4x"
	   ,"org.fusesource.ide.server.fuseesb.7x"
		// more server type id's to be added here!
	};
	
	/**
	 * returns the host name
	 * 
	 * @return
	 */
	String getHostName();

	/**
	 * returns the password
	 * 
	 * @return
	 */
	String getPassword();

	/**
	 * returns the port number
	 * 
	 * @return
	 */
	int getPortNumber();

	/**
	 * returns the user name
	 * 
	 * @return
	 */
	String getUserName();
}
