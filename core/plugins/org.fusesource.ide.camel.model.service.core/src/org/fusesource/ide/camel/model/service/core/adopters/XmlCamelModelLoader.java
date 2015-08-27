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
 *
 */
public class XmlCamelModelLoader extends CamelModelLoader {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader#getComponentModel(java.net.URL)
	 */
	@Override
	protected ComponentModel getComponentModel(URL componentModel) throws IOException {
		return ComponentModel.getXMLFactoryInstance(componentModel.openStream());
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader#getDataFormatModel(java.net.URL)
	 */
	@Override
	protected DataFormatModel getDataFormatModel(URL dataFormatModel) throws IOException {
		return DataFormatModel.getXMLFactoryInstance(dataFormatModel.openStream());
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader#getEipModel(java.net.URL)
	 */
	@Override
	protected EipModel getEipModel(URL eipModel) throws IOException {
		return EipModel.getXMLFactoryInstance(eipModel.openStream());
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader#getLanguageModel(java.net.URL)
	 */
	@Override
	protected LanguageModel getLanguageModel(URL languageModel) throws IOException {
		return LanguageModel.getXMLFactoryInstance(languageModel.openStream());
	}
}
