/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog_json.cache;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.catalog_json.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog_json.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog_json.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog_json.languages.Language;

/**
 * @author lhein
 */
public class CamelCatalogCache {
	private Map<String, Component> components = new HashMap<>();
	private Map<String, DataFormat> dataformats = new HashMap<>();
	private Map<String, Eip> eips = new HashMap<>();
	private Map<String, Language> languages = new HashMap<>();
	
	/**
	 * adds a component to the cache (overwrites existing component with same id)
	 * 
	 * @param component	the component to add
	 */
	public void addComponent(Component component) {
		components.put(component.getScheme(), component);
	}
	
	/**
	 * returns the component with the given scheme if existing
	 * 
	 * @param scheme	the scheme of the component (for instance "file")
	 * @return	the cached component or null if not existing
	 */
	public Component getComponent(String scheme) {
		return components.get(scheme);
	}
	
	/**
	 * adds a dataformat to the cache (overwrites existing dataformat with same name)
	 * 
	 * @param dataformat	the dataformat to add
	 */
	public void addDataFormat(DataFormat dataformat) {
		dataformats.put(dataformat.getName(), dataformat);
	}
	
	/**
	 * returns the dataformat with the given name if existing
	 * 
	 * @param name	the name of the dataformat (for instance "base64")
	 * @return	the cached dataformat or null if not existing
	 */
	public DataFormat getDataFormat(String name) {
		return dataformats.get(name);
	}
	
	/**
	 * adds an eip to the cache (overwrites existing eip with same name)
	 * 
	 * @param eip	the eip to add
	 */
	public void addEip(Eip eip) {
		eips.put(eip.getName(), eip);
	}
	
	/**
	 * returns the eip with the given name if existing
	 * 
	 * @param name	the name of the eip (for instance "choice")
	 * @return	the cached eip or null if not existing
	 */
	public Eip getEip(String name) {
		return eips.get(name);
	}
	
	/**
	 * adds a language to the cache (overwrites existing language with same name)
	 * 
	 * @param language	the language to add
	 */
	public void addLanguage(Language language) {
		languages.put(language.getName(), language);
	}
	
	/**
	 * returns the language with the given name if existing
	 * 
	 * @param name	the name of the language (for instance "constant")
	 * @return	the cached language or null if not existing
	 */
	public Language getLanguage(String name) {
		return languages.get(name);
	}		
}