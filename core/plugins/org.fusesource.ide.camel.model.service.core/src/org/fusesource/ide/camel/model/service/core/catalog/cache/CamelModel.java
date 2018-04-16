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
package org.fusesource.ide.camel.model.service.core.catalog.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;

/**
 * @author lhein
 */
public class CamelModel {
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
	 * returns all components of this model
	 * 
	 * @return	a collection containing all supported components
	 */
	public Collection<Component> getComponents() {
		return components.values();
	}
	
	/**
	 * returns the component which supports the given scheme
	 * 
	 * @param scheme
	 * @return	the component or null if no component supports the scheme
	 */
	public Component getComponentForScheme(String scheme) {
		for (Entry<String, Component> e : components.entrySet()) {
			if (e.getKey().equalsIgnoreCase(scheme)) {
				return e.getValue();
			}
		}
		return null;
	}
	
	/**
	 * sets the components
	 * 
	 * @param components
	 */
	public void setComponents(Map<String, Component> components) {
		this.components.putAll(components);
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
	 * adds a dataformat to the cache (overwrites existing dataformat with same name)
	 * 
	 * @param overriddenName
	 * @param dataformat	the dataformat to add
	 */
	public void addDataFormat(String overriddenName, DataFormat dataformat) {
		dataformats.put(overriddenName, dataformat);
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
	 * returns all dataformats of this model
	 * 
	 * @return	a collection containing all supported dataformats
	 */
	public Collection<DataFormat> getDataFormats() {
		return dataformats.values();
	}
	
	/**
	 * retrieves all dataformats for the given model name
	 * 
	 * @param modelName
	 * @return
	 */
	public Collection<DataFormat> getDataFormatsByModelName(String modelName) {
		List<DataFormat> dfs = new ArrayList<>();
		
		for (DataFormat df : dataformats.values()) {
			if (df.getModelName().equalsIgnoreCase(modelName)) {
				dfs.add(df);
			}
		}
		
		return dfs;
	}
	
	/**
	 * sets the dataformats
	 * 
	 * @param dataformats
	 */
	public void setDataFormats(Map<String, DataFormat> dataformats) {
		this.dataformats.putAll(dataformats);
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
	 * returns all eips of this model
	 * 
	 * @return	a collection containing all supported eips
	 */
	public Collection<Eip> getEips() {
		return eips.values();
	}
	
	/**
	 * sets the eips
	 * 
	 * @param eips
	 */
	public void setEips(Map<String, Eip> eips) {
		this.eips.putAll(eips);
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
	
	/**
	 * returns all languages of this model
	 * 
	 * @return	a collection containing all supported languages
	 */
	public Collection<Language> getLanguages() {
		return languages.values();
	}
	
	/**
	 * sets the languages
	 * 
	 * @param languages
	 */
	public void setLanguages(Map<String, Language> languages) {
		this.languages.putAll(languages);
	}

}