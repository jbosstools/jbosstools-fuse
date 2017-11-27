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

import java.io.IOException;
import java.io.InputStream;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.UnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

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
	public boolean isCompatible(String camelVersion) {
		return isStrictlyLowerThan2200(camelVersion);
	}

	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator();
	}

	@Override
	public TemplateCreatorSupport getCreator(NewProjectMetaData projectMetaData) {
		return new AMQUnzipTemplateCreator();
	}

	/**
	 * creator class for the CBR simple template 
	 */
	private class AMQUnzipTemplateCreator extends UnzipStreamCreator {

		private static final String TEMPLATE_FOLDER = "templates/";
		private static final String TEMPLATE_BLUEPRINT = "template-simple-amq-blueprint.zip";
		private static final String TEMPLATE_SPRING = "template-simple-amq-spring.zip";
		
		@Override
		public InputStream getTemplateStream(NewProjectMetaData metadata) throws IOException {
			String bundleEntry = null;
			switch (metadata.getDslType()) {
				case BLUEPRINT:	bundleEntry = String.format("%s%s", TEMPLATE_FOLDER, TEMPLATE_BLUEPRINT);
								break;
				case SPRING:	bundleEntry = String.format("%s%s", TEMPLATE_FOLDER, TEMPLATE_SPRING);
								break;
				default:
			}
			return getTemplateStream(bundleEntry);
		}

	}
}
