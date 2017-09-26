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
package org.fusesource.ide.camel.validation.l10n;

import org.eclipse.osgi.util.NLS;

/**
 * @author Aurelien Pupier
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.fusesource.ide.camel.validation.l10n.messages"; //$NON-NLS-1$
	public static String NumberValidator_messageError;
	public static String RequiredPropertyValidator_messageMissingParameter;
	public static String eipWithoutChild;
	public static String validationSameComponentIdAndComponentDefinitionId;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
