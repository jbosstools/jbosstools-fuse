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

public class ExportFileTypeComboSelection2ExportFileTypeConverter extends Converter {

	public ExportFileTypeComboSelection2ExportFileTypeConverter() {
		super(Integer.class, ExportFileType.class);
	}

	@Override
	public Object convert(Object fromObject) {
		int selection = (Integer) fromObject;
		switch (selection) {
		case 0:
			return ExportFileType.BLUEPRINT;
		case 1:
			return ExportFileType.SPRING;
		}
		return null;
	}

}
