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

public class CpicTraceComboSelection2TraceLevelConverter extends Converter {

	public CpicTraceComboSelection2TraceLevelConverter() {
		super(Integer.class, String.class);
	}

	@Override
	public Object convert(Object fromObject) {
		int selection = (Integer) fromObject;
		switch (selection) {
		case 1:
			return "0";
		case 2:
			return "1";
		case 3:
			return "2";
		case 4:
			return "3";
		}
		return null;
	}

}
