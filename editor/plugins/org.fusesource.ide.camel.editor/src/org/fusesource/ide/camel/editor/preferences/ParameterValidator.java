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

import org.eclipse.jface.dialogs.IInputValidator;
import org.fusesource.ide.camel.editor.internal.UIMessages;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class ParameterValidator implements IInputValidator {

	@Override
	public String isValid(String newText) {
		if (newText.isEmpty()) {
			return UIMessages.preferredLabelsErrorMessageEmptyParameter;
		}
		if (newText.matches(".*[\\W&&[^-]].*")) {
			return UIMessages.preferredLabelsErrorMessageWrongCharacter;
		}
		return null;
	}

}
