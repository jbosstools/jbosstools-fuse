/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.wsdl2rest.ui.wizard.pages;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;

/**
 * @author brianf
 *
 */
public class WsdlValidator implements IValidator {
	private int isURLAccessible(String urlText) {
		int code = 200;
		try {
			final URL url = new URL(urlText);
			InputStream testStream = url.openStream();
			testStream.close();
		} catch (Exception ex) {
			code = -1;
		}
		return code;
	}

	@Override
	public IStatus validate(Object value) {
		if (!((value instanceof String) && ((String) value).length() > 0)) {
			return ValidationStatus.error(UIMessages.wsdl2RestWizardFirstPageValidatorWSDLUrlRequired);
		}
		int responseCode = isURLAccessible((String) value);
		if (responseCode != 200) {
			return ValidationStatus.error(UIMessages.wsdl2RestWizardFirstPageValidatorWSDLInaccessible);
		}
		return ValidationStatus.ok();   		}
}