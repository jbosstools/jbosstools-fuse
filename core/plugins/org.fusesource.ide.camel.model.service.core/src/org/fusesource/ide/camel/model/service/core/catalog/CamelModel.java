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

package org.fusesource.ide.camel.model.service.core.catalog;

import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormatModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.EipModel;
import org.fusesource.ide.camel.model.service.core.catalog.languages.LanguageModel;

/**
 * @author lhein
 */
public class CamelModel {
	
	private String camelVersion;
	
	private ComponentModel componentModel;
	private DataFormatModel dataformatModel;
	private EipModel eipModel;
	private LanguageModel languageModel;
	
	/**
	 * 
	 */
	public CamelModel() {
	}
	
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
	 * @param camelVersion the camelVersion to set
	 */
	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}
	
	/**
	 * @param componentModel the componentModel to set
	 */
	public void setComponentModel(ComponentModel componentModel) {
		this.componentModel = componentModel;
		this.componentModel.setModel(this);
	}
	
	/**
	 * @param dataformatModel the dataformatModel to set
	 */
	public void setDataformatModel(DataFormatModel dataformatModel) {
		this.dataformatModel = dataformatModel;
		this.dataformatModel.setModel(this);
	}
	
	/**
	 * @param eipModel the eipModel to set
	 */
	public void setEipModel(EipModel eipModel) {
		this.eipModel = eipModel;
		this.eipModel.setModel(this);
	}
	
	/**
	 * @param languageModel the languageModel to set
	 */
	public void setLanguageModel(LanguageModel languageModel) {
		this.languageModel = languageModel;
		this.languageModel.setModel(this);
	}
	
	/**
	 * @return the componentModel
	 */
	public synchronized ComponentModel getComponentModel() {
		return this.componentModel;
	}
	
	/**
	 * @return the dataformatModel
	 */
	public synchronized DataFormatModel getDataformatModel() {
		return this.dataformatModel;
	}
	
	/**
	 * @return the eipModel
	 */
	public synchronized EipModel getEipModel() {
		return this.eipModel;
	}
	
	/**
	 * @return the languageModel
	 */
	public synchronized LanguageModel getLanguageModel() {
		return this.languageModel;
	}
}
