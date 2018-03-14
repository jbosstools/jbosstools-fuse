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
package org.fusesource.ide.syndesis.extensions.ui.templates;

import java.io.IOException;
import java.io.InputStream;

import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.projecttemplates.adopters.creators.UnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.InvalidProjectMetaDataException;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;

/**
 * @author lheinema
 */
public class SyndesisProjectCreator extends UnzipStreamCreator {

	private static final String DEFAULT_TEMPLATE_FOLDER = "templates/";
	
	private String templateFolder;
	private String templateFileName;
	
	public SyndesisProjectCreator(String templateFileName) {
		this(null, templateFileName);
	}
	
	public SyndesisProjectCreator(String templateFolder, String templateFileName) {
		this.templateFolder = Strings.isBlank(templateFolder) ? DEFAULT_TEMPLATE_FOLDER : templateFolder;
		if (Strings.isBlank(templateFileName)) {
			throw new IllegalArgumentException("The template file name must not be empty.");
		}
		this.templateFileName = templateFileName;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.adopters.creators.InputStreamCreator#getTemplateStream(org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData)
	 */
	@Override
	public InputStream getTemplateStream(CommonNewProjectMetaData metadata)
			throws IOException, InvalidProjectMetaDataException {
		String bundleEntry = String.format("%s%s", this.templateFolder, this.templateFileName);
		return getTemplateStream(SyndesisExtensionsUIActivator.getBundleContext().getBundle(), bundleEntry);
	}
}
