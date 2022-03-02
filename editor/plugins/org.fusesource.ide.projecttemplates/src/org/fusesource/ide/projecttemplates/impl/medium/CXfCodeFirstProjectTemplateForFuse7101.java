/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.impl.medium;

import java.util.Arrays;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.util.FuseBomFilter;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;

public class CXfCodeFirstProjectTemplateForFuse7101 extends AbstractCxfCodeFirstProjectTemplate {

	private static final String MINIMAL_COMPATIBLE_REDUCED_CAMEL_VERSION_PREFIX = "2.23.2.fuse-7_";
	private static final String MINIMAL_COMPATIBLE_CAMEL_VERSION_PREFIX = "2.23.2.fuse-7_10_1";
	private static final String MINIMAL_COMPATIBLE_CAMEL_VERSION = "2.24.0";

	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator(FuseBomFilter.BOM_FUSE_71_KARAF);
	}

	@Override
	public TemplateCreatorSupport getCreator(CommonNewProjectMetaData projectMetaData) {
		return new CXfCodeFirstUnzipTemplateCreator("7.10.1");
	}
	
	@Override
	public boolean isCompatible(EnvironmentData environment) {
		String camelVersion = environment.getCamelVersion();
		return super.isCompatible(environment)
				&& (camelVersion.startsWith(MINIMAL_COMPATIBLE_REDUCED_CAMEL_VERSION_PREFIX)
						&& new VersionUtil().isGreaterThan(camelVersion, MINIMAL_COMPATIBLE_CAMEL_VERSION_PREFIX)
					|| new VersionUtil().isGreaterThan(camelVersion, MINIMAL_COMPATIBLE_CAMEL_VERSION));
	}

	@Override
	public List<String> getJavaExecutionEnvironments() {
		return Arrays.asList("JavaSE-1.8", "JavaSE-11");
	}

}
