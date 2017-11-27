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
package org.fusesource.ide.projecttemplates.impl.simple;

import java.io.IOException;
import java.io.InputStream;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.UnzipStreamCreator;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

public abstract class AbstractEmptyProjectTemplate extends AbstractProjectTemplate {

	public AbstractEmptyProjectTemplate() {
		super();
	}

	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator();
	}

	protected class BlankProjectCreator extends UnzipStreamCreator {

		private String suffix;

		public BlankProjectCreator(String suffix) {
			this.suffix = suffix;
		}

		private static final String TEMPLATE_FOLDER = "templates/";
		private static final String TEMPLATE_BLUEPRINT = "template-blank-blueprint-fuse";
		private static final String TEMPLATE_SPRING = "template-blank-spring-fuse";
		private static final String TEMPLATE_JAVA = "template-blank-java-fuse";

		@Override
		public InputStream getTemplateStream(NewProjectMetaData metadata) throws IOException {
			String bundleEntry = null;
			switch (metadata.getDslType()) {
			case BLUEPRINT:	bundleEntry = getBundleEntry(TEMPLATE_BLUEPRINT);
							break;
			case SPRING:	bundleEntry = getBundleEntry(TEMPLATE_SPRING);
							break;
			case JAVA:		bundleEntry = getBundleEntry(TEMPLATE_JAVA);
							break;
			default:
			}
			return getTemplateStream(bundleEntry);
		}

		private String getBundleEntry(String templateDsl) {
			return String.format("%s%s%s.zip", TEMPLATE_FOLDER, templateDsl, suffix);
		}
	}

}