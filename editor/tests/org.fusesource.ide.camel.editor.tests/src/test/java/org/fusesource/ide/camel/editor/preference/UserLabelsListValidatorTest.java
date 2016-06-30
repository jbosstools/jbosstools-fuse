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

import static org.fusesource.ide.camel.editor.internal.UIMessages.userLabels_errorMessageAttribute;
import static org.fusesource.ide.camel.editor.internal.UIMessages.userLabels_errorMessageCharacter;
import static org.fusesource.ide.camel.editor.internal.UIMessages.userLabels_errorMessageDuplicate;
import static org.fusesource.ide.camel.editor.internal.UIMessages.userLabels_errorMessageEmpty;
import static org.fusesource.ide.camel.editor.internal.UIMessages.userLabels_errorMessageMoreCommas;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.fusesource.ide.camel.editor.preferences.UserLabelsListValidator;
import org.junit.Test;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class UserLabelsListValidatorTest {

	@Test
	public void testValidInputs() {
		UserLabelsListValidator validator = new UserLabelsListValidator();
		assertNull(validator.isValid("abc.xyz"));
		assertNull(validator.isValid("123.321"));
		assertNull(validator.isValid("abc-2.xyz"));
		assertNull(validator.isValid("abc.xyz-2"));
		assertNull(validator.isValid("abc-2.xyz-2"));
	}

	@Test
	public void testErrorMessageForEmptyInput() {
		assertEquals(userLabels_errorMessageEmpty, new UserLabelsListValidator().isValid(""));
	}

	@Test
	public void testErrorMessageForInputWithSpecialCharacter() {
		assertEquals(userLabels_errorMessageCharacter, new UserLabelsListValidator().isValid("abc;"));
		assertEquals(userLabels_errorMessageCharacter, new UserLabelsListValidator().isValid("abc/xyz"));
	}

	@Test
	public void testErrorMessageForInputWithoutAttribute() {
		assertEquals(userLabels_errorMessageAttribute, new UserLabelsListValidator().isValid("abc"));
		assertEquals(userLabels_errorMessageAttribute, new UserLabelsListValidator().isValid("abc-2"));
		assertEquals(userLabels_errorMessageAttribute, new UserLabelsListValidator().isValid("abc."));
		assertEquals(userLabels_errorMessageAttribute, new UserLabelsListValidator().isValid("abc-2."));
	}

	@Test
	public void testErrorMessageForInputWithMoreCommas() {
		assertEquals(userLabels_errorMessageMoreCommas, new UserLabelsListValidator().isValid("abc.."));
		assertEquals(userLabels_errorMessageMoreCommas, new UserLabelsListValidator().isValid("abc.xyz."));
	}

	@Test
	public void testErrorMessageForDuplicateInput() {
		assertEquals(userLabels_errorMessageDuplicate, new UserLabelsListValidator("abc").isValid("abc"));
		assertEquals(userLabels_errorMessageDuplicate, new UserLabelsListValidator("abc").isValid("abc."));
		assertEquals(userLabels_errorMessageDuplicate, new UserLabelsListValidator("abc").isValid("abc.xyz"));
		assertEquals(userLabels_errorMessageDuplicate, new UserLabelsListValidator("abc").isValid("abc.xyz;"));
		assertEquals(userLabels_errorMessageDuplicate, new UserLabelsListValidator("abc", "xyz").isValid("xyz"));
	}

}
