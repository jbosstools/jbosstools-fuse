/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.impl.simple;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;

public class OSESpringBootXMLTemplateForFuse6 extends AbstractOSESpringBootXMLTemplate {
	
	private static ComparableVersion minimalCompatibleCamelVersion = new ComparableVersion("2.18.0");
	private static ComparableVersion maximalCompatibleCamelVersion = new ComparableVersion("2.20.0");
	
	@Override
	public boolean isCompatible(String camelVersion) {
		return new ComparableVersion(camelVersion).compareTo(minimalCompatibleCamelVersion) >= 0
				&& new ComparableVersion(camelVersion).compareTo(maximalCompatibleCamelVersion) < 0;
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
