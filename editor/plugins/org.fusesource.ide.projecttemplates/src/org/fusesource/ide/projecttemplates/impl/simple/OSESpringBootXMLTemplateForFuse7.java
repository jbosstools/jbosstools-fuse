/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.impl.simple;

import org.fusesource.ide.camel.model.service.core.util.FuseBomFilter;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;

public class OSESpringBootXMLTemplateForFuse7 extends AbstractOSESpringBootXMLTemplate {
	
	protected static final String PLACEHOLDER_FABRIC8MAVENPLUGIN_VERSION = "%%%PLACEHOLDER_FABRIC8MAVENPLUGIN_VERSION%%%";
	private static final String MINIMAL_COMPATIBLE_CAMEL_VERSION = "2.20.0";
	private static final String MAXIMAL_COMPATIBLE_CAMEL_VERSION = "2.21.0.fuse-710";

	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenConfiguratorForOSESpringBootTemplate(FuseBomFilter.BOM_FUSE_FIS);
	}

	@Override
	public TemplateCreatorSupport getCreator(CommonNewProjectMetaData projectMetaData) {
		return new OSEUnzipTemplateCreator("7");
	}

	@Override
	public boolean isCompatible(EnvironmentData environment) {
		return super.isCompatible(environment)
				&& new VersionUtil().isGreaterThan(environment.getCamelVersion(), MINIMAL_COMPATIBLE_CAMEL_VERSION)
				&& new VersionUtil().isGreaterThan(MAXIMAL_COMPATIBLE_CAMEL_VERSION, environment.getCamelVersion());
	}
}
