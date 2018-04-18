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
package org.fusesource.ide.camel.editor.integration.properties;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.camel.editor.properties.DetailsSection;
import org.fusesource.ide.camel.editor.properties.FusePropertySection;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class FusePropertySectionIT {

	@Mock
	private FormToolkit toolkit;
	
	@Mock
	private Composite page;
	
	@Mock
	private Label label;
	
	private FusePropertySection fusePropertySection = new DetailsSection();
	ArgumentCaptor<String> labelCaptor;
	
	@Before
	public void setup() {
		labelCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.doReturn(label).when(toolkit).createLabel(Mockito.eq(page), labelCaptor.capture());
	}
	
	@Test
	public void testNotDeprecatedAndNotMandatory(){
		Parameter parameter = new Parameter();
		parameter.setName("myName");
		parameter.setDeprecated("false");
		parameter.setRequired("false");
		parameter.setKind("text");
		parameter.setJavaType("java.lang.String");

		fusePropertySection.createPropertyLabel(toolkit, page, parameter);
		
		assertEquals("My Name", labelCaptor.getValue());
		Mockito.verify(label, Mockito.never()).setForeground(Mockito.any(Color.class));
	}
	
	@Test
	public void testDeprecated() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setName("myName");
		parameter.setDeprecated("true");
		parameter.setKind("text");
		parameter.setJavaType("java.lang.String");

		fusePropertySection.createPropertyLabel(toolkit, page, parameter);
		
		assertEquals("My Name (deprecated)", labelCaptor.getValue());
		Mockito.verify(label, Mockito.never()).setForeground(Mockito.any(Color.class));
	}
	
	@Test
	public void testMandatory() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setName("myName");
		parameter.setRequired("true");
		parameter.setKind("text");
		parameter.setJavaType("java.lang.String");

		fusePropertySection.createPropertyLabel(toolkit, page, parameter);
		
		assertEquals("My Name *", labelCaptor.getValue());
//		Mockito.verify(label).setForeground(Mockito.any(Color.class));
	}
	
	@Test
	public void testMandatoryAndDeprecated(){
		Parameter parameter = new Parameter();
		parameter.setName("myName");
		parameter.setRequired("true");
		parameter.setDeprecated("true");
		parameter.setKind("text");
		parameter.setJavaType("java.lang.String");
		
		fusePropertySection.createPropertyLabel(toolkit, page, parameter);
		
		assertEquals("My Name * (deprecated)", labelCaptor.getValue());
//		Mockito.verify(label).setForeground(Mockito.any(Color.class));
	}

}
