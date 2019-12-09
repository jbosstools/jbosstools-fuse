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
package org.fusesource.ide.camel.editor.integration.globalconfiguration.wizards.pages;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.CamelFeatureProvider;
import org.fusesource.ide.camel.editor.provider.DiagramTypeProvider;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;

public class CustomPaletteEntry1 implements ICustomPaletteEntry {

	public static final String INTEGRATION_TEST_KARAF_ONLY = "integration test - karaf only";

	public CustomPaletteEntry1() {
		//keep for reflection instantiation
	}

	@Override
	public ICreateFeature newCreateFeature(IFeatureProvider fp) {
		return new CreateEndpointFigureFeature(new CamelFeatureProvider(new DiagramTypeProvider()), INTEGRATION_TEST_KARAF_ONLY, "used by integration test", "",
				Collections.<Dependency> emptyList());
	}

	@Override
	public List<Dependency> getRequiredDependencies(String runtimeProvider) {
		return Collections.emptyList();
	}

	@Override
	public boolean providesProtocol(String protocol) {
		return false;
	}

	@Override
	public String getProtocol() {
		return null;
	}

	@Override
	public boolean isValid(String runtimeProvider) {
		return CamelCatalogUtils.RUNTIME_PROVIDER_KARAF.equals(runtimeProvider);
	}

}
