/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DetailsSectionTest {
	
	@Mock
	private DetailsSection detailsSection;
	
	@Before
	public void setup(){
		doCallRealMethod().when(detailsSection).shouldHidePropertyFromGroup(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyString());
	}

	@Test
	public void test_displayDataFormatOnGeneralTab() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setJavaType("org.apache.camel.model.DataFormatDefinition");
		parameter.setName("dataFormatType");
		parameter.setKind("element");
		parameter.setType("object");
		assertThat(detailsSection.shouldHidePropertyFromGroup(FusePropertySection.DEFAULT_GROUP, parameter , null)).isFalse();
	}
	
	@Test
	public void test_displayDescriptionOnGeneralTab() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setJavaType("org.apache.camel.model.DescriptionDefinition");
		parameter.setName("description");
		parameter.setKind("element");
		parameter.setType("object");
		assertThat(detailsSection.shouldHidePropertyFromGroup(FusePropertySection.DEFAULT_GROUP, parameter , null)).isFalse();
	}
	
	@Test
	public void test_displayIdOnGeneralTab() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setJavaType("java.lang.String");
		parameter.setName("id");
		parameter.setKind("attribute");
		parameter.setType("string");
		assertThat(detailsSection.shouldHidePropertyFromGroup(FusePropertySection.DEFAULT_GROUP, parameter , null)).isFalse();
	}

	@Test
	public void test_displayRefOnGeneralTab() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setJavaType("java.lang.String");
		parameter.setName("ref");
		parameter.setKind("attribute");
		parameter.setType("string");
		assertThat(detailsSection.shouldHidePropertyFromGroup(FusePropertySection.DEFAULT_GROUP, parameter , null)).isFalse();
	}
	
	@Test
	public void test_notDisplayParameterWithGroupOnGeneralTab() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setJavaType("java.lang.String");
		parameter.setName("paramWithgroup");
		parameter.setKind("attribute");
		parameter.setType("string");
		parameter.setGroup("myGroup");
		assertThat(detailsSection.shouldHidePropertyFromGroup(FusePropertySection.DEFAULT_GROUP, parameter , "myGroup")).isTrue();
		assertThat(detailsSection.shouldHidePropertyFromGroup("myGroup", parameter , "myGroup")).isFalse();
	}
	
}
