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

import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormatModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.EipModel;
import org.fusesource.ide.camel.model.service.core.catalog.languages.LanguageModel;

/**
 * @author lhein
 */
public class JSONCamelModelLoader extends CamelModelLoader {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader#getComponentModel(java.net.URL)
	 */
	@Override
	protected ComponentModel getComponentModel(URL componentModel) throws IOException {
		throw new IOException("getComponentModel() is not yet implemented for JSON");
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader#getDataFormatModel(java.net.URL)
	 */
	@Override
	protected DataFormatModel getDataFormatModel(URL dataFormatModel) throws IOException {
		throw new IOException("getDataFormatModel() is not yet implemented for JSON");
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader#getEipModel(java.net.URL)
	 */
	@Override
	protected EipModel getEipModel(URL eipModel) throws IOException {
		throw new IOException("getEipModel() is not yet implemented for JSON");
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader#getLanguageModel(java.net.URL)
	 */
	@Override
	protected LanguageModel getLanguageModel(URL languageModel) throws IOException {
		throw new IOException("getLanguageModel() is not yet implemented for JSON");
	}
}
