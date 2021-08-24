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
public class BaseConfigPropertyProvider {

	private Properties configProps = new Properties();
	private File propertyFile = null;
	
	public BaseConfigPropertyProvider(File propertyFile) {
		this.propertyFile = propertyFile;
		loadPropertiesFromFile();
	}
	
	/**
	 * /!\ it doesn't support several ${env:} in the same value, it doesn't support ${prop:}
	 * These cases are not used by default in the context used by Tooling.(and it wasn't supported before neither)
	 * 
	 * It was inspired from https://github.com/apache/karaf/blob/5144a1eba5687dab0f016a9bf95e58e6687a26ad/client/src/main/java/org/apache/karaf/client/ClientConfig.java#L276
	 * 
	 * @param propertyName
	 * @return the property value with environment variable resolved as best as possible. 
	 */
	public String getEnvironmentResolvedConfigurationProperty(String propertyName) {
		String brutValue = configProps.getProperty(propertyName, null);
		if (brutValue != null && brutValue.startsWith("${env:")) {
			String envKey = extractEnvKey(brutValue);
			String envValue = System.getenv(envKey);
			if (envValue != null) {
				return envValue;
			} else {
				int indexOfDelimiterOfDefaultValue = brutValue.lastIndexOf(":-");
				if (indexOfDelimiterOfDefaultValue != -1) {
					return brutValue.substring(indexOfDelimiterOfDefaultValue + 2, brutValue.length() - 1);
				} else {
					return brutValue;
				}
			}
		} else {
			return brutValue;
		}
	}

	private String extractEnvKey(String brutValue) {
		String env = brutValue.substring("${env:".length() + 1);
		 if (env.lastIndexOf(":") != -1) {
		     env = brutValue.substring(0, env.lastIndexOf(":"));
		 }
		 if (env.lastIndexOf("}") != -1) {
		     env = brutValue.substring(0, env.lastIndexOf("}"));
		 }
		return env;
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
