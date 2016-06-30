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
package org.fusesource.ide.camel.model.service.core.tests.integration.core.model;

import static org.fusesource.ide.preferences.PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL;
import static org.fusesource.ide.preferences.PreferencesConstants.EDITOR_USER_LABELS;
import static org.junit.Assert.assertEquals;

import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.preferences.PreferenceManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class CamelBasicModelElementTestIT {

	@Before
	@After
	public void cleanUserLabelsPreference() {
		PreferenceManager.getInstance().savePreference(EDITOR_USER_LABELS, "");
		PreferenceManager.getInstance().savePreference(EDITOR_PREFER_ID_AS_LABEL, false);
	}

	@Test
	public void testDisplayingDefaultText() {
		assertEquals("Log coolName", simpleLog().getDisplayText());
	}

	@Test
	public void testDisplayingIdText() {
		PreferenceManager.getInstance().savePreference(EDITOR_PREFER_ID_AS_LABEL, true);
		assertEquals("Log _log1", simpleLog().getDisplayText());
	}

	@Test
	public void testDisplayingUserText() {
		PreferenceManager.getInstance().savePreference(EDITOR_USER_LABELS, "log.message");
		assertEquals("Log ${body}", simpleLog().getDisplayText());
	}

	@Test
	public void testDisplayingUserTextIfIdIsPrefered() {
		PreferenceManager.getInstance().savePreference(EDITOR_PREFER_ID_AS_LABEL, true);
		PreferenceManager.getInstance().savePreference(EDITOR_USER_LABELS, "log.message");
		assertEquals("Log _log1", simpleLog().getDisplayText());
	}

	private static AbstractCamelModelElement simpleLog() {
		Eip eip = new Eip();
		eip.setName("log");

		AbstractCamelModelElement element = new CamelBasicModelElement(null, null);
		element.setId("_log1");
		element.setParameter("logName", "coolName");
		element.setParameter("message", "${body}");
		element.setUnderlyingMetaModelObject(eip);

		return element;
	}

}
