package org.fusesource.ide.server.karaf.core.internal.runtime;


/**
 * @author lhein
 */
public interface IKarafRuntimeWorkingCopy extends IKarafRuntime {
	
	/**
	 * sets the karaf installation folder
	 * 
	 * @param installDir
	 */
	void setKarafInstallDir(String installDir);
	
	/**
	 * sets the karaf properties file location
	 * 
	 * @param propFile
	 */
	void setKarafPropertiesFileLocation(String propFile);
	
	/**
	 * sets the version of the karaf installation
	 * 
	 * @param version	the version
	 */
	void setKarafVersion(String version);
}
