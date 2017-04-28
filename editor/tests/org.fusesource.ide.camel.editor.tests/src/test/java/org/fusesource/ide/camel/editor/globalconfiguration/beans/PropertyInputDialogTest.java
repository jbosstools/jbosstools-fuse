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
package org.fusesource.ide.camel.editor.globalconfiguration.beans;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * @author brianf
 *
 */
public class PropertyInputDialogTest {

	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private static String TESTNAME = "testName"; //$NON-NLS-1$
	private static String TESTVALUE = "testValue"; //$NON-NLS-1$

	@Test
	public void testValidate() {
		PropertyInputDialog dialog = new PropertyInputDialog(Display.getDefault().getActiveShell());
		Assertions.assertThat(dialog.validate(null, null)).isNotNull();
		Assertions.assertThat(dialog.validate(TESTNAME, null)).isNotNull();
		Assertions.assertThat(dialog.validate(null, TESTVALUE)).isNotNull();
		Assertions.assertThat(dialog.validate(TESTNAME, TESTVALUE)).isNull();
	}

	@Test
	public void testValidateWithPropertyList() {
		PropertyInputDialog dialog = new PropertyInputDialog(Display.getDefault().getActiveShell());
		List<AbstractCamelModelElement> propertyList = new ArrayList<>();
		Element propertyNode = beanConfigUtil.createBeanProperty(TESTNAME, TESTVALUE);
		CamelBasicModelElement newProperty = new CamelBasicModelElement(null, propertyNode);
		propertyList.add(newProperty);
		dialog.setPropertyList(propertyList);
		Assertions.assertThat(dialog.validate(TESTNAME, TESTVALUE)).isNotNull();
	}

}
