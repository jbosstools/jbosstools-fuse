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
package org.fusesource.ide.camel.editor.integration.globalconfiguration.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.PropertyInputDialog;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.foundation.ui.util.Shells;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * @author brianf
 *
 */
public class PropertyInputDialogIT {

	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private static String TESTNAME = "testName"; //$NON-NLS-1$
	private static String TESTVALUE = "testValue"; //$NON-NLS-1$

	@Test
	public void testValidateReturnErrorWhenBothEntryFieldsAreNull() {
		PropertyInputDialog dialog = new PropertyInputDialog(Shells.getShell());
		assertThat(dialog.validate(null, null)).isNotNull();
	}

	@Test
	public void testValidateReturnErrorWhenOnlyNameFieldIsNull() {
		PropertyInputDialog dialog = new PropertyInputDialog(Shells.getShell());
		assertThat(dialog.validate(TESTNAME, null)).isNotNull();
	}

	@Test
	public void testValidateReturnErrorWhenOnlyValueFieldIsNull() {
		PropertyInputDialog dialog = new PropertyInputDialog(Shells.getShell());
		assertThat(dialog.validate(null, TESTVALUE)).isNotNull();
	}

	@Test
	public void testValidateReturnErrorWhenBothEntryFieldsAreNotNull() {
		PropertyInputDialog dialog = new PropertyInputDialog(Shells.getShell());
		assertThat(dialog.validate(TESTNAME, TESTVALUE)).isNull();
	}

	@Test
	public void testValidateWithPropertyList() {
		PropertyInputDialog dialog = new PropertyInputDialog(Shells.getShell());
		List<AbstractCamelModelElement> propertyList = new ArrayList<>();
		Element propertyNode = beanConfigUtil.createBeanProperty(TESTNAME, TESTVALUE);
		CamelBasicModelElement newProperty = new CamelBasicModelElement(null, propertyNode);
		propertyList.add(newProperty);
		dialog.setPropertyList(propertyList);
		assertThat(dialog.validate(TESTNAME, TESTVALUE)).isNotNull();
	}

}
