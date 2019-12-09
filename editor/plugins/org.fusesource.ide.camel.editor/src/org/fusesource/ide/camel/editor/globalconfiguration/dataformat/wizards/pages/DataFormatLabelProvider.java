/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.pages;

import org.eclipse.jface.viewers.LabelProvider;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;

/**
 * @author Aurelien Pupier
 *
 */
public class DataFormatLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof DataFormat) {
			final DataFormat dataFormat = (DataFormat) element;
			String descriptionSuffix = computeDescriptionSuffix(dataFormat);
			return dataFormat.getName() + descriptionSuffix;
		}
		return super.getText(element);
	}

	/**
	 * @param dataFormat
	 * @return
	 */
	private String computeDescriptionSuffix(final DataFormat dataFormat) {
		final String description = dataFormat.getDescription();
		String descriptionSuffix = "";
		if (description != null && !description.isEmpty()) {
			descriptionSuffix = " - " + description;
		}
		return descriptionSuffix;
	}

}
