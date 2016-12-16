/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.tests.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.adopters.XmlCamelModelLoader;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.junit.rules.ExternalResource;

/**
 * @author Aurelien Pupier
 *
 */
public class CamelModelRegistrationRule extends ExternalResource {

	@Override
	protected void before() throws Throwable {
		super.before();
		HashMap<String, Map<String, CamelModel>> mockedSupportedCamelModels = new HashMap<>();

		URL componentModel = this.getClass().getResource("components.xml");
		URL eipModel = this.getClass().getResource("eips.xml");
		URL languageModel = this.getClass().getResource("languages.xml");
		URL dataformatModel = this.getClass().getResource("dataformats.xml");
		final CamelModel camelModel = new XmlCamelModelLoader().getCamelModel(componentModel, eipModel, languageModel, dataformatModel);
		camelModel.setCamelVersion("2.16");
		camelModel.setRuntimeProvider(CamelModelFactory.RUNTIME_PROVIDER_KARAF);
		Map<String, CamelModel> modelMap = new HashMap<>();
		modelMap.put(CamelModelFactory.RUNTIME_PROVIDER_KARAF, camelModel);
		mockedSupportedCamelModels.put("2.16", modelMap);
		CamelModelFactory.initializeModels(mockedSupportedCamelModels);
	}

	public CamelModel getCamelModel() {
		return CamelModelFactory.getModelForVersion("2.16");
	}

}
