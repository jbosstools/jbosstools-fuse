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
import org.fusesource.ide.projecttemplates.adopters.creators.UnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

public abstract class AbstractCBRTemplate extends AbstractProjectTemplate {

	public AbstractCBRTemplate() {
		super();
	}

	@Override
	public boolean supportsDSL(CamelDSLType type) {
		switch (type) {
			case BLUEPRINT:	return true;
			case SPRING:	return true;
			case JAVA:		return true;
			default:		return false;
		}	
	}

	/**
	 * creator class for the CBR simple template 
	 */
	protected class CBRUnzipTemplateCreator extends UnzipStreamCreator {
		
		private String suffix;

		public CBRUnzipTemplateCreator(String suffix) {
			this.suffix = suffix;
		}

		private static final String TEMPLATE_FOLDER = "templates/";
		private static final String TEMPLATE_BLUEPRINT = "template-simple-cbr-blueprint-fuse";
		private static final String TEMPLATE_SPRING = "template-simple-cbr-spring-fuse";
		private static final String TEMPLATE_JAVA = "template-simple-cbr-java-fuse";
		
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