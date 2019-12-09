/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.impl.simple;

import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;

public class OSESpringBootXMLTemplateForFuse6 extends AbstractOSESpringBootXMLTemplate {
	
	private static final String MINIMAL_COMPATIBLE_CAMEL_VERSION = "2.18.0";
	private static final String MAXIMAL_COMPATIBLE_CAMEL_VERSION = "2.20.0";
	
	@Override
	public boolean isCompatible(EnvironmentData environment) {
		VersionUtil versionUtil = new VersionUtil();
		String camelVersion = environment.getCamelVersion();
		return super.isCompatible(environment)
				&& versionUtil.isGreaterThan(camelVersion, MINIMAL_COMPATIBLE_CAMEL_VERSION)
				&& versionUtil.isStrictlyGreaterThan(MAXIMAL_COMPATIBLE_CAMEL_VERSION, camelVersion);
	}
	
	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator(null);
	}

	@Override
	public TemplateCreatorSupport getCreator(CommonNewProjectMetaData projectMetaData) {
		return new OSEUnzipTemplateCreator("6");
	}
	
}
