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

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.UnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

public class OSESpringBootXMLTemplate extends AbstractProjectTemplate {
	
	private static ComparableVersion minimalCompatibleCamelVersion = new ComparableVersion("2.18.0");
	
	@Override
	public boolean isCompatible(String camelVersion) {
		return new ComparableVersion(camelVersion).compareTo(minimalCompatibleCamelVersion) >= 0;
	}
	
	@Override
	public boolean supportsDSL(CamelDSLType type) {
		return type == CamelDSLType.SPRING;
	}
	
	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator();
	}

	@Override
	public TemplateCreatorSupport getCreator(NewProjectMetaData projectMetaData) {
		return new OSEUnzipTemplateCreator();
	}
	
	/**
	 * creator class for the CBR simple template 
	 */
	private class OSEUnzipTemplateCreator extends UnzipStreamCreator {

		private static final String TEMPLATE_FOLDER = "templates/";
		private static final String TEMPLATE_SPRING = "template-simple-ose-log-springboot.zip";
		
		@Override
		public InputStream getTemplateStream(NewProjectMetaData metadata) throws IOException {
			String bundleEntry = null;
			CamelDSLType dslType = metadata.getDslType();
			if (dslType == CamelDSLType.SPRING) {
				bundleEntry = String.format("%s%s", TEMPLATE_FOLDER, TEMPLATE_SPRING);
			}
			return getTemplateStream(bundleEntry);
		}
	}

}
