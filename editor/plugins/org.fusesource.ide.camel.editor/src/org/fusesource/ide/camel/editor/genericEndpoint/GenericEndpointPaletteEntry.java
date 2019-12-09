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
package org.fusesource.ide.camel.editor.genericEndpoint;

import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;

public class GenericEndpointPaletteEntry implements ICustomPaletteEntry {

	@Override
	public ICreateFeature newCreateFeature(IFeatureProvider fp) {
		return new GenericEndpointFigureFeature(fp);
	}

	@Override
	public List<Dependency> getRequiredDependencies(String runtimeProvider) {
		//computation is delegated to the Wizard
		return null;
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
		return true;
	}

}
