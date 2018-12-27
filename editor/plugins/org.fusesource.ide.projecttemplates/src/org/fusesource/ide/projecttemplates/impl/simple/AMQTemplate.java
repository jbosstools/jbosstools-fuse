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
package org.fusesource.ide.projecttemplates.impl.simple;

import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuse6ToBomMapper;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.DSLDependentUnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;

/**
 * @author lhein
 */
public class AMQTemplate extends AbstractProjectTemplate {
	

	@Override
	public boolean supportsDSL(CamelDSLType type) {
		switch (type) {
		case BLUEPRINT:	return true;
		case SPRING:	return true;
		case JAVA:		return false;
		default:		return false;
		}	
	}
	
	@Override
	public boolean isCompatible(EnvironmentData environment) {
		return super.isCompatible(environment)
				&& new VersionUtil().isStrictlyLowerThan2200(environment.getCamelVersion())
				&& FuseDeploymentPlatform.STANDALONE.equals(environment.getDeploymentPlatform())
				&& FuseRuntimeKind.KARAF.equals(environment.getFuseRuntime())
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

	@Override
	public TemplateCreatorSupport getCreator(CommonNewProjectMetaData projectMetaData) {
		return new AMQUnzipTemplateCreator();
	}

	/**
	 * creator class for the CBR simple template 
	 */
	private class AMQUnzipTemplateCreator extends DSLDependentUnzipStreamCreator {

		private static final String TEMPLATE_BLUEPRINT = "template-simple-amq-blueprint";
		private static final String TEMPLATE_SPRING = "template-simple-amq-spring";
		
		public AMQUnzipTemplateCreator() {
			super(TEMPLATE_BLUEPRINT, TEMPLATE_SPRING, null, "");
		}
		
	}
}
