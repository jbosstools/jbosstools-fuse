/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.fusesource.ide.server.karaf.core.Activator;

/**
 * @author lhein
 */
public class BaseConfigPropertyProvider implements
		IConfigurationPropertyProvider {

	private Properties configProps = new Properties();
	private File propertyFile = null;
	
	/**
	 * 
	 */
	public BaseConfigPropertyProvider(File propertyFile) {
		this.propertyFile = propertyFile;
		loadPropertiesFromFile();
	}
	
	@Override
	public String getConfigurationProperty(String propertyName) {
		return getConfigurationProperty(propertyName, null);
	}
	
	@Override
	public String getConfigurationProperty(String propertyName,
			String defaultValue) {
		return configProps.getProperty(propertyName, defaultValue);
	}

	/**
	 * retrieves the keys of all available properties
	 * 
	 * @return
	 */
	public Enumeration<Object> getPropertyKeys() {
		return this.configProps.keys();
	}
	
	/**
	 * loads the properties from the given file
	 */
	private void loadPropertiesFromFile() {
		this.configProps.clear();
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(this.propertyFile))) {
			this.configProps.load(bis);	
		} catch (IOException ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/**
	 * reloads the contents of the properties file
	 */
	public void reload() {
		loadPropertiesFromFile();
	}
}
