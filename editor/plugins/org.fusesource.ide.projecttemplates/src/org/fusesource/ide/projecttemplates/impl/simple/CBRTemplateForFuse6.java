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
package org.fusesource.ide.projecttemplates.impl.simple;

import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuse6ToBomMapper;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;

/**
 * @author lhein
 */
public class CBRTemplateForFuse6 extends AbstractCBRTemplate {

	@Override
	public TemplateCreatorSupport getCreator(CommonNewProjectMetaData projectMetaData) {
		return new CBRUnzipTemplateCreator("6");
	}
	
	@Override
	public boolean isCompatible(EnvironmentData environment) {
		return super.isCompatible(environment)
				&& new VersionUtil().isStrictlyLowerThan2200(environment.getCamelVersion())
				&& is62OrNewerThan63R9(environment.getCamelVersion());
	}
	
	private boolean is62OrNewerThan63R9(String camelVersion) {
		return new VersionUtil().isStrictlyGreaterThan("2.17.0", camelVersion)
			|| new VersionUtil().isGreaterThan(camelVersion, CamelForFuse6ToBomMapper.FUSE_63_R9_CAMEL_VERSION);
	}

	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator(null);
	}
	
}
