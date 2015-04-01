/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.model.catalog;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.catalog.components.ComponentModel;

/**
 * @author lhein
 */
public class CamelModel {
	
	private String camelVersion;
	
	private ComponentModel componentModel;
	
	
	/**
	 * creates a model skeleton for the given camel version
	 * the initializing of the model will happen lazy on access
	 * 
	 * @param camelVersion	the camel version of the model
	 */
	public CamelModel(String camelVersion) {
		this.camelVersion = camelVersion;
	}
	
	/**
	 * @return the camelVersion
	 */
	public String getCamelVersion() {
		return this.camelVersion;
	}
	
	/**
	 * @return the componentModel
	 */
	public ComponentModel getComponentModel() {
		if (this.componentModel == null) initialize();
		return this.componentModel;
	}
	
	/**
	 * initializes the model
	 */
	private synchronized void initialize() {
		Enumeration<URL> models = Activator.getDefault().getBundle().findEntries("catalogs/" + camelVersion, "*.xml", false);
		while (models.hasMoreElements()) {
			URL model = models.nextElement();
			String fileName = model.getFile();
			try {
				if (fileName.endsWith("components.xml")) {
					this.componentModel = ComponentModel.getFactoryInstance(model.openStream(), this);
				} else if (fileName.endsWith("dataformats.xml")) {
					System.err.println("No model for dataformats yet...");
//					this.dataformatModel = DataFormatModel.getFactoryInstance(model.openStream(), this);
				} else if (fileName.endsWith("languages.xml")) {
					System.err.println("No model for languages yet...");
//					this.languageModel = LanguagesModel.getFactoryInstance(model.openStream(), this);
				} else if (fileName.endsWith("eips.xml")) {
					System.err.println("No model for eips yet...");
//					this.eipModel = EIPModel.getFactoryInstance(model.openStream(), this);
				} 
			} catch (IOException ex) {
				Activator.getLogger().error(ex);
				continue;
			}
		}		
	}
}
