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
package org.fusesource.ide.camel.editor.preference;

import static org.fusesource.ide.camel.editor.internal.UIMessages.preferredLabels_errorMessageDuplicateComponent;
import static org.fusesource.ide.camel.editor.internal.UIMessages.preferredLabels_errorMessageEmptyComponent;
import static org.fusesource.ide.camel.editor.internal.UIMessages.preferredLabels_errorMessageWrongCharacter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.fusesource.ide.camel.editor.preferences.ComponentValidator;
import org.junit.Test;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class ComponentValidatorTest {

	@Test
	public void testValidInputs() {
		ComponentValidator validator = new ComponentValidator();
		assertNull(validator.isValid("abc"));
		assertNull(validator.isValid("abc-2xyz"));
		assertNull(validator.isValid("abcxyz-2"));
	}

	@Test
	public void testErrorMessageForEmptyInput() {
		assertEquals(preferredLabels_errorMessageEmptyComponent, new ComponentValidator().isValid(""));
	}

	@Test
	public void testErrorMessageForInputWithSpecialCharacter() {
		assertEquals(preferredLabels_errorMessageWrongCharacter, new ComponentValidator().isValid("abc;"));
		assertEquals(preferredLabels_errorMessageWrongCharacter, new ComponentValidator().isValid("abc/xyz"));
	}

	@Test
	public void testErrorMessageForDuplicateInput() {
		assertEquals(preferredLabels_errorMessageDuplicateComponent, new ComponentValidator("abc").isValid("abc"));
		assertEquals(preferredLabels_errorMessageDuplicateComponent,
				new ComponentValidator("abc", "xyz").isValid("xyz"));
	}

}
