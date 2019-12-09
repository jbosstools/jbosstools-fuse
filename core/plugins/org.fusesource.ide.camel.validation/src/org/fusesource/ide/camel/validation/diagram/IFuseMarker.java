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
package org.fusesource.ide.camel.validation.diagram;

import org.fusesource.ide.camel.validation.CamelValidationActivator;

/**
 * @author Aurelien Pupier
 *
 */
public interface IFuseMarker {

	public static final String PATH = "IFuseMarker_PATH";
	public static final String CAMEL_ID = "IFuseMarker_CAMEL_ID";
	String MARKER_TYPE = CamelValidationActivator.PLUGIN_ID + ".JBossFuseToolingValidationProblem";

}
