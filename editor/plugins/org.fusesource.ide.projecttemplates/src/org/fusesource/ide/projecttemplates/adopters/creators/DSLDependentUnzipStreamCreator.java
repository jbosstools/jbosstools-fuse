/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.adopters.creators;

import java.io.IOException;
import java.io.InputStream;

import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.adopters.util.InvalidProjectMetaDataException;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.util.ICamelDSLTypeSupport;

public class DSLDependentUnzipStreamCreator extends UnzipStreamCreator {
	
	private static final String TEMPLATE_FOLDER = "templates/";
	
	private String blueprintTemplateName;
	private String springTemplateName;
	private String javaTemplateName;
	private String suffix;

	/**
	 * @param blueprintName: the name of the blueprint template or null if not existing
	 * @param springName: the name of the spring template or null if not existing
	 * @param javaName: the name of the java template or null if not existing
	 * @param suffix
	 */
	public DSLDependentUnzipStreamCreator(String blueprintName, String springName, String javaName, String suffix) {
		this.blueprintTemplateName = blueprintName;
		this.springTemplateName = springName;
		this.javaTemplateName = javaName;
		this.suffix = suffix;
	}

	@Override
	public InputStream getTemplateStream(CommonNewProjectMetaData metadata) throws IOException, InvalidProjectMetaDataException {
		if (metadata instanceof ICamelDSLTypeSupport) {
			String templateName = getTemplateName(((ICamelDSLTypeSupport)metadata).getDslType());
			if(templateName != null) {
				return getTemplateStream(getBundleEntry(templateName));
			}
		}
		throw new InvalidProjectMetaDataException("Invalid project metadata not supporting Camel DSL types");
	}

	private String getTemplateName(CamelDSLType dslType) {
		switch (dslType) {
		case BLUEPRINT:	return blueprintTemplateName;
		case SPRING:	return springTemplateName;
		case JAVA:		return javaTemplateName;
		default: return null;
		}
	}

	private String getBundleEntry(String templateDsl) {
		return String.format("%s%s%s.zip", TEMPLATE_FOLDER, templateDsl, suffix);
	}

}
