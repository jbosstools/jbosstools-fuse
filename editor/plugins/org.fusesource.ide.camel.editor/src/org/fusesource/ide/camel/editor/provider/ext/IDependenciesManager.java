/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.provider.ext;

import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.Dependency;

/**
 * Allows to manage dependencies depending on Camel Version.
 * 
 * @since 8.0
 */
public interface IDependenciesManager {
	
	public static final String EXT_POINT_NAME = "dependenciesManager";
	
	/**
	 * Allows to manage Maven plugin dependencies.
	 * By default, org.apache.camel:camel-* are already handled
	 * 
	 * @param currentPlugins
	 * @param camelVersion
	 */
	public void updatePluginDependencies(List<Plugin> currentPlugins, String camelVersion);
	
	/**
	 * Allows to manage Maven dependencies
	 * By default, org.apache.camel:camel-* are already handled
	 * 
	 * @param currentDependencies
	 * @param camelVersion
	 */
	public void updateDependencies(List<Dependency> currentDependencies, String camelVersion);
	

}
