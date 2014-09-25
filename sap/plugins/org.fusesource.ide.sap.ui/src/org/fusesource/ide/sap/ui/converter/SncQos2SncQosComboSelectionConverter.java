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

public class SncQos2SncQosComboSelectionConverter extends Converter {

	public SncQos2SncQosComboSelectionConverter() {
		super(String.class, Integer.class);
	}

	@Override
	public Object convert(Object fromObject) {
		String selection = (String) fromObject;
		switch (selection) {
		case "1":
			return 1;
		case "2":
			return 2;
		case "3":
			return 3;
		case "8":
			return 4;
		case "9":
			return 5;
		}
		return null;
	}

}
