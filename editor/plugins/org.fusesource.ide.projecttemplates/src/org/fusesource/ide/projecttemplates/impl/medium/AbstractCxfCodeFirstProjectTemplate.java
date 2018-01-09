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
package org.fusesource.ide.projecttemplates.impl.medium;

import java.io.IOException;
import java.io.InputStream;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.creators.UnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.adopters.util.InvalidProjectMetaDataException;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.util.ICamelDSLTypeSupport;

public abstract class AbstractCxfCodeFirstProjectTemplate extends AbstractProjectTemplate {

	public AbstractCxfCodeFirstProjectTemplate() {
		super();
	}

	@Override
	public boolean supportsDSL(CamelDSLType type) {
		switch (type) {
		case BLUEPRINT:	return false;
		case SPRING:	return true;
		case JAVA:		return true;
		default:		return false;
		}	
	}

	protected class CXfCodeFirstUnzipTemplateCreator extends UnzipStreamCreator {

		private String suffix;

		public CXfCodeFirstUnzipTemplateCreator(String suffix) {
			this.suffix = suffix;
		}

		private static final String TEMPLATE_FOLDER = "templates/";
		private static final String TEMPLATE_BLUEPRINT = "template-medium-cxf-codefirst-blueprint-fuse";
		private static final String TEMPLATE_SPRING = "template-medium-cxf-codefirst-spring-fuse";
		private static final String TEMPLATE_JAVA = "template-medium-cxf-codefirst-java-fuse";

		@Override
		public InputStream getTemplateStream(CommonNewProjectMetaData metadata)
				throws IOException, InvalidProjectMetaDataException {
			String bundleEntry = null;
			if (metadata instanceof ICamelDSLTypeSupport) {
				switch (((ICamelDSLTypeSupport)metadata).getDslType()) {
					case BLUEPRINT:	bundleEntry = getBundleEntry(TEMPLATE_BLUEPRINT);
									break;
					case SPRING:	bundleEntry = getBundleEntry(TEMPLATE_SPRING);
									break;
					case JAVA: bundleEntry = getBundleEntry(TEMPLATE_JAVA);
									break;
					default:
				}
				return getTemplateStream(bundleEntry);
			}
			throw new InvalidProjectMetaDataException("Invalid project metadata not supporting Camel DSL types");
		}

		private String getBundleEntry(String templateDsl) {
			return String.format("%s%s%s.zip", TEMPLATE_FOLDER, templateDsl, suffix);
		}
	}

}