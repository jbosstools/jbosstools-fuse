/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.fusesource.ide.sap.ui.export.SapConnectionConfigurationExportSettings.ExportFileType;

public class ExportFileType2ExportFileTypeComboSelectionConverter extends Converter {

	public ExportFileType2ExportFileTypeComboSelectionConverter() {
		super(ExportFileType.class, Integer.class);
	}

	@Override
	public Object convert(Object fromObject) {
		ExportFileType selection = (ExportFileType) fromObject;
		switch (selection) {
		case BLUEPRINT:
			return 0;
		case SPRING:
			return 1;
		}
		return null;
	}

}
