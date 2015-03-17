/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.connectors;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.fusesource.ide.camel.model.Activator;

/**
 * @author lhein
 */
public class ComponentModelFactory {

	private static HashMap<String, ComponentModel> supportedCamelModels;
	
	/**
	 * initializes all available models for the connectors group of the camel editor palette
	 */
	public static void initializeModels() {
		supportedCamelModels = new HashMap<String, ComponentModel>();
		Enumeration<URL> models = Activator.getDefault().getBundle().findEntries("components", "components-*.xml", false);
		while (models.hasMoreElements()) {
			URL model = models.nextElement();
			String fileName = model.getFile();
			String version = fileName.substring(fileName.indexOf("-")+1, fileName.indexOf(".xml"));
			try {
				ComponentModel m = ComponentModel.getComponentFactoryInstance(model.openStream(), version);
				supportedCamelModels.put(version, m);				
			} catch (IOException ex) {
				Activator.getLogger().error(ex);
				continue;
			}
		}	
	}
	
	/**
	 * returns the list of supported camel versions
	 * 
	 * @return
	 */
	public static List<String> getSupportedCamelVersions() {
		if (supportedCamelModels == null || supportedCamelModels.isEmpty()) {
			initializeModels();
		}
		return Arrays.asList(supportedCamelModels.keySet().toArray(new String[supportedCamelModels.size()]));
	}
	
	/**
	 * returns the model for a given camel version or null if not supported
	 * 
	 * @param camelVersion
	 * @return
	 */
	public static ComponentModel getModelForVersion(String camelVersion) {
		return supportedCamelModels.get(camelVersion);
	}
}
