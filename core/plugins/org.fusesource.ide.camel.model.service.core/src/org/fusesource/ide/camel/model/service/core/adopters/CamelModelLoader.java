/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core.adopters;

import java.io.IOException;
import java.net.URL;

import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormatModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.EipModel;
import org.fusesource.ide.camel.model.service.core.catalog.languages.LanguageModel;

/**
 * @author lhein
 */
public abstract class CamelModelLoader {
	
	protected ComponentModel compModel;
	protected EipModel eipModel;
	protected LanguageModel langModel;
	protected DataFormatModel dfModel;
	
	private CamelModel cachedModel;
	
	/**
	 * creates the camel model using 4 xml model definitions
	 * 
	 * @param componentModel
	 * @param eipModel
	 * @param languageModel
	 * @param dataformatModel
	 * @return
	 * @throws IOException
	 */
	public CamelModel getCamelModel( 	URL componentModel, 
										URL eipModel,
										URL languageModel,
										URL dataformatModel) throws IOException {
		if (cachedModel == null) {
			cachedModel = new CamelModel();
		}
		
		try {
			if (this.compModel == null) {
				this.compModel 	= getComponentModel(componentModel);
				cachedModel.setComponentModel(this.compModel);
			}
			if (this.eipModel  == null) {
				this.eipModel 	= getEipModel(eipModel);
				cachedModel.setEipModel(this.eipModel);
			}
			if (this.langModel == null) {
				this.langModel 	= getLanguageModel(languageModel);
				cachedModel.setLanguageModel(this.langModel);
			}
			if (this.dfModel   == null) {
				this.dfModel 	= getDataFormatModel(dataformatModel);
				cachedModel.setDataformatModel(this.dfModel);	
			}
		} catch (Exception ex) {
			throw new IOException("Unable to generate the Camel Model.", ex);
		} 

		return cachedModel;
	}

	/**
	 * generates a component model object from a given url
	 * 
	 * @param componentModel	the url pointing to the model data
	 * @return	the component model 
	 * @throws IOException	on any loading / creation errors
	 */
	protected abstract ComponentModel getComponentModel(URL componentModel) throws IOException;
	
	/**
	 * generates a eip model object from a given url
	 * 
	 * @param eipModel	the url pointing to the model data
	 * @return	the eip model 
	 * @throws IOException	on any loading / creation errors
	 */
	protected abstract EipModel getEipModel(URL eipModel) throws IOException;
	
	/**
	 * generates a language model object from a given url
	 * 
	 * @param languageModel	the url pointing to the model data
	 * @return	the language model 
	 * @throws IOException	on any loading / creation errors
	 */
	protected abstract LanguageModel getLanguageModel(URL languageModel) throws IOException;
	
	/**
	 * generates a dataformat model object from a given url
	 * 
	 * @param dataFormatModel	the url pointing to the model data
	 * @return	the dataformat model 
	 * @throws IOException	on any loading / creation errors
	 */
	protected abstract DataFormatModel getDataFormatModel(URL dataFormatModel) throws IOException;
}
