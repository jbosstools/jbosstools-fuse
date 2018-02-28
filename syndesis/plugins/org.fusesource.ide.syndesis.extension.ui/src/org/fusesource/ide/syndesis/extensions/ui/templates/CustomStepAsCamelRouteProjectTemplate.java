/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.syndesis.extensions.ui.templates;

import java.io.IOException;
import java.io.InputStream;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.UnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.InvalidProjectMetaDataException;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;

/**
 * @author lheinema
 *
 */
public class CustomStepAsCamelRouteProjectTemplate extends AbstractProjectTemplate {
	
	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator(null);
	}
	
	@Override
	public TemplateCreatorSupport getCreator(CommonNewProjectMetaData projectMetaData) {
		return new SyndesisProjectCreator();
	}
	
	private class SyndesisProjectCreator extends UnzipStreamCreator {

		private static final String TEMPLATE_FOLDER = "templates/";
		private static final String TEMPLATE_XML = "template-syndesis-ext-xml.zip";
		
		@Override
		public InputStream getTemplateStream(CommonNewProjectMetaData metadata)
				throws IOException, InvalidProjectMetaDataException {
			String bundleEntry = String.format("%s%s", TEMPLATE_FOLDER, TEMPLATE_XML);
			return getTemplateStream(SyndesisExtensionsUIActivator.getBundleContext().getBundle(), bundleEntry);
		}
	}
}
