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

public abstract class AbstractEAPSpringTemplate extends AbstractProjectTemplate {

	public AbstractEAPSpringTemplate() {
		super();
	}

	@Override
	public boolean supportsDSL(CamelDSLType type) {
		switch (type) {
			case BLUEPRINT:	return false;
			case SPRING:	return true;
			case JAVA:		return false;
			default:		return false;
		}	
	}

	/**
	 * creator class for the CBR simple template 
	 */
	protected class EAPSpringUnzipTemplateCreator extends UnzipStreamCreator {
		
		private String suffix;

		public EAPSpringUnzipTemplateCreator(String suffix) {
			this.suffix = suffix;
		}

		private static final String TEMPLATE_FOLDER = "templates/";
		private static final String TEMPLATE_SPRING = "template-medium-eap-wildfly-spring-fuse";
		
		@Override
		public InputStream getTemplateStream(NewProjectMetaData metadata) throws IOException {
			if(CamelDSLType.SPRING.equals(metadata.getDslType())) {
				return getTemplateStream(String.format("%s%s%s.zip", TEMPLATE_FOLDER, TEMPLATE_SPRING, suffix));
			}
			return null;
		}
	}

}