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

/**
 * @author lhein
 */
public interface IConfigurationPropertyProvider {
	
	/**
	 * retrieves a property from a config file and returns it
	 * 
	 * @param propertyName			the name of the property
	 * @return	the value of that property from the file or null if not found
	 */
	public String getConfigurationProperty(String propertyName);
	
	/**
	 * retrieves a property from a config file and returns it if found, otherwise
	 * the defaultValue will be returned
	 * 
	 * @param propertyName			the name of the property
	 * @param defaultValue			the default value to return if not found
	 * @return	the value of that property if found, otherwise the defaultValue
	 */
	public String getConfigurationProperty(String propertyName, String defaultValue);
}
