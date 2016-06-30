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
package org.fusesource.ide.camel.editor.preferences;

import static org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement.USER_LABEL_REGEX;

import org.eclipse.jface.dialogs.IInputValidator;
import org.fusesource.ide.camel.editor.internal.UIMessages;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class UserLabelsListValidator implements IInputValidator {

	private String[] labels;

	public UserLabelsListValidator(String... labels) {
		this.labels = labels;
	}

	@Override
	public String isValid(String newText) {
		if (isDuplicate(newText)) {
			return UIMessages.userLabels_errorMessageDuplicate;
		}
		if (newText.matches(USER_LABEL_REGEX)) {
			return null;
		}
		if (newText.isEmpty()) {
			return UIMessages.userLabels_errorMessageEmpty;
		}
		if (newText.matches(".*[\\W&&[^-.]].*")) {
			return UIMessages.userLabels_errorMessageCharacter;
		}
		if (newText.matches(".*\\..*\\..*")) {
			return UIMessages.userLabels_errorMessageMoreCommas;
		}
		if (newText.matches("[\\w-]+[\\.]?")) {
			return UIMessages.userLabels_errorMessageAttribute;
		}
		return UIMessages.userLabels_errorMessage;
	}

	private boolean isDuplicate(String newLabel) {
		String component = newLabel.split("\\.")[0];
		for (String label : labels) {
			if (component.equals(label.split("\\.")[0])) {
				return true;
			}
		}
		return false;
	}

}
