/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.impl.simple;

import org.fusesource.ide.camel.model.service.core.util.FuseBomFilter;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;

public class CBRTemplateForFuse76 extends AbstractCBRTemplate {

	private static final String FUSE_7_7_INCOMPATIBLE_CAMEL_VERSION = "2.23.2.fuse-77";
	private static final String MINIMAL_COMPATIBLE_CAMEL_VERSION = "2.21.0.fuse-760";

	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator(FuseBomFilter.BOM_FUSE_71_KARAF);
	}

	@Override
	public TemplateCreatorSupport getCreator(CommonNewProjectMetaData projectMetaData) {
		return new CBRUnzipTemplateCreator("7.6");
	}
	
	@Override
	public boolean isCompatible(EnvironmentData environment) {
		return super.isCompatible(environment)
				&& new VersionUtil().isStrictlyGreaterThan(environment.getCamelVersion(), MINIMAL_COMPATIBLE_CAMEL_VERSION)
				&& !environment.getCamelVersion().startsWith(FUSE_7_7_INCOMPATIBLE_CAMEL_VERSION);
	}

}
