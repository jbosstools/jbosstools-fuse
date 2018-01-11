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
package org.jboss.tools.fuse.transformation.extensions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;

public class DataTransformationPaletteEntry implements ICustomPaletteEntry {

	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	private static final String CAMEL_DOZER = "camel-dozer";
	private static final String CAMEL_DOZER_STARTER = CAMEL_DOZER + "-starter";
	private static final String PROTOCOL = "dozer";

	@Override
	public ICreateFeature newCreateFeature(IFeatureProvider fp) {
		List<Dependency> requiredDependencies = getRequiredDependencies(CamelUtils.getRuntimeProvider(fp));
		return new DataMapperEndpointFigureFeature(fp,
				Messages.DataTransformationPaletteEntry_paletteName,
				Messages.DataTransformationPaletteEntry_paletteDescription, requiredDependencies);
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	@Override
	public boolean providesProtocol(String protocol) {
		return PROTOCOL.equalsIgnoreCase(protocol);
	}

	@Override
	public List<Dependency> getRequiredDependencies(String runtimeProvider) {
		List<Dependency> deps = new ArrayList<>();
		Dependency dep = new Dependency();
		dep.setGroupId(ORG_APACHE_CAMEL);
		dep.setArtifactId(computeArtifactId(runtimeProvider));
		dep.setVersion(getCurrentProjectCamelVersion());
		deps.add(dep);
		return deps;
	}

	private String computeArtifactId(String runtimeProvider) {
		if(CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT.equals(runtimeProvider)){
			return CAMEL_DOZER_STARTER;
		} else {
			return CAMEL_DOZER;
		}
	}

	@Override
	public boolean isValid(String runtimeProvider) {
    	String camelVersion = getCurrentProjectCamelVersion();
    	return new VersionUtil().isStrictlyLowerThan2200(camelVersion);
	}

	String getCurrentProjectCamelVersion() {
		return CamelUtils.getCurrentProjectCamelVersion();
	}
}