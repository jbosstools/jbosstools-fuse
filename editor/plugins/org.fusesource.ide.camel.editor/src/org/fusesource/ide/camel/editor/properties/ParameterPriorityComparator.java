/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.properties;

import java.util.Comparator;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;

/**
 * @author Aurelien Pupier
 *
 */
public class ParameterPriorityComparator implements Comparator<Parameter> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Parameter o1, Parameter o2) {
		if(isRequired(o1) && isRequired(o2) || !isRequired(o1) && !isRequired(o2)){
			return o1.getName().compareTo(o2.getName());
		}
		if(isRequired(o1) && !isRequired(o2) ){
			return -1;
		} else {
			return 1;
		}
	}

	protected boolean isRequired(Parameter parameter) {
		return isParameterValueTrue(parameter.getRequired());
	}

	private boolean isParameterValueTrue(String parameterValue) {
		return parameterValue != null && parameterValue.equalsIgnoreCase("true");
	}

}
