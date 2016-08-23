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

package org.fusesource.ide.camel.model.service.core.adopters;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author lhein
 */
public class JSONParserTest {
	private static final String PREFIX_PATH				= "";
	private static final String COMPONENT_TEST_FILE 	= PREFIX_PATH + "logComponent.json";
	private static final String DATAFORMAT_TEST_FILE 	= PREFIX_PATH + "stringDataFormat.json";
	private static final String EIP_TEST_FILE 			= PREFIX_PATH + "beanEIP.json";
	private static final String LANGUAGE_TEST_FILE 		= PREFIX_PATH + "constantLanguage.json";

	private CamelModelLoader loader;
	private CamelModel model;
	
	@Before
	public void setup() throws Exception {
		this.loader = new JSONCamelModelLoader();
		URL componentURL 	= this.getClass().getResource(COMPONENT_TEST_FILE);
		URL dataformatURL 	= this.getClass().getResource(DATAFORMAT_TEST_FILE);
		URL eipURL 			= this.getClass().getResource(EIP_TEST_FILE);
		URL languageURL 	= this.getClass().getResource(LANGUAGE_TEST_FILE);
		this.model = loader.getCamelModel(	componentURL, 
											eipURL, 
											languageURL, 
											dataformatURL);
	}
	
	@After
	public void tearDown() throws Exception {
		this.model = null;
		this.loader = null;
	}
	
	@Test
	@Ignore
	public void testModelLoading() throws Exception {
		assertThat(this.model).isNotNull();
	}
	
	@Test
	@Ignore
	public void testComponentJSONModel() throws Exception {
		Component log = this.model.getComponentModel().getComponentForScheme("log");
		assertThat(log).isNotNull();
		assertThat(log.getKind()).isEqualToIgnoringCase("component");
		assertThat(log.getScheme()).isEqualToIgnoringCase("log");
		assertThat(log.getSyntax()).isEqualToIgnoringCase("log:loggerName");
		assertThat(log.getTitle()).isEqualToIgnoringCase("Log");
		assertThat(log.getDescription()).startsWith("The log component logs ");
		assertThat(log.getTags()).contains("core", "monitoring");
		assertThat(log.getConsumerOnly()).isEqualToIgnoringCase("false");
		assertThat(log.getProducerOnly()).isEqualToIgnoringCase("true");
		assertThat(log.getClazz()).isEqualToIgnoringCase("org.apache.camel.component.log.LogComponent");
		assertThat(log.getDependencies().size()==1).isTrue();
		assertThat(log.getDependencies().get(0).getGroupId()).isEqualToIgnoringCase("org.apache.camel");
		assertThat(log.getDependencies().get(0).getArtifactId()).isEqualToIgnoringCase("camel-core");
		assertThat(log.getDependencies().get(0).getVersion()).isNotEmpty();
	}
	
	@Test
	@Ignore
	public void testDataFormatJSONModel() throws Exception {
		DataFormat df = this.model.getDataformatModel().getDataFormatByName("string");
		assertThat(df).isNotNull();
		assertThat(df.getName()).isEqualToIgnoringCase("string");
		assertThat(df.getKind()).isEqualToIgnoringCase("dataformat");
		assertThat(df.getModelName()).isEqualToIgnoringCase("string");
		assertThat(df.getTitle()).contains("String");
		assertThat(df.getDescription()).startsWith("The Core Camel ");
		assertThat(df.getTags()).contains("dataformat", "transformation", "core");
		assertThat(df.getClazz()).isEqualToIgnoringCase("org.apache.camel.impl.StringDataFormat");
		assertThat(df.getModelJavaType()).isEqualToIgnoringCase("org.apache.camel.model.dataformat.StringDataFormat");
		assertThat(df.getDependencies().size()==1).isTrue();
		assertThat(df.getDependencies().get(0).getGroupId()).isEqualToIgnoringCase("org.apache.camel");
		assertThat(df.getDependencies().get(0).getArtifactId()).isEqualToIgnoringCase("camel-core");
		assertThat(df.getDependencies().get(0).getVersion()).isNotEmpty();
		assertThat(df.getParameters().size()>0).isTrue();
		Parameter p = df.getParameter("id");
		assertThat(p).isNotNull();
		assertThat(p.getKind()).isEqualToIgnoringCase("attribute");
		assertThat(p.getRequired()).isEqualToIgnoringCase("false");
		assertThat(p.getType()).isEqualToIgnoringCase("string");
		assertThat(p.getJavaType()).isEqualToIgnoringCase("java.lang.String");
		assertThat(p.getDeprecated()).isEqualToIgnoringCase("false");
		assertThat(p.getDescription()).startsWith("Sets the value of ");
	}
	
	@Test
	@Ignore
	public void testEIPJSONModel() throws Exception {
		Eip eip = this.model.getEipModel().getEIPByName("bean");
		assertThat(eip).isNotNull();
		assertThat(eip.getName()).isEqualToIgnoringCase("bean");
		assertThat(eip.getKind()).isEqualToIgnoringCase("model");
		assertThat(eip.getTitle()).contains("Bean");
		assertThat(eip.getDescription()).startsWith("Calls a java ");
		assertThat(eip.getTags()).contains("eip", "endpoint");
		assertThat(eip.getClazz()).isEqualToIgnoringCase("org.apache.camel.model.BeanDefinition");
		assertThat(eip.getInput()).isEqualToIgnoringCase("true");
		assertThat(eip.getOutput()).isEqualToIgnoringCase("false");
		assertThat(eip.getParameters().size()>0).isTrue();
		Parameter p = eip.getParameter("id");
		assertThat(p).isNotNull();
		assertThat(p.getKind()).isEqualToIgnoringCase("attribute");
		assertThat(p.getRequired()).isEqualToIgnoringCase("false");
		assertThat(p.getType()).isEqualToIgnoringCase("string");
		assertThat(p.getJavaType()).isEqualToIgnoringCase("java.lang.String");
		assertThat(p.getDeprecated()).isEqualToIgnoringCase("false");
		assertThat(p.getDescription()).startsWith("Sets the id of ");
	}
	
	@Test
	@Ignore
	public void testLanguageJSONModel() throws Exception {
		Language lang = this.model.getLanguageModel().getLanguageByName("constant");
		assertThat(lang).isNotNull();
		assertThat(lang.getName()).isEqualToIgnoringCase("constant");
		assertThat(lang.getKind()).isEqualToIgnoringCase("language");
		assertThat(lang.getTitle()).isEqualToIgnoringCase("Constant");
		assertThat(lang.getDescription()).startsWith("For expressions and predicates ");
		assertThat(lang.getTags()).contains("language", "core");
		assertThat(lang.getClazz()).isEqualToIgnoringCase("org.apache.camel.language.constant.ConstantLanguage");
		assertThat(lang.getModelJavaType()).isEqualToIgnoringCase("org.apache.camel.model.language.ConstantExpression");
		assertThat(lang.getDependencies().size()==1).isTrue();
		assertThat(lang.getDependencies().get(0).getGroupId()).isEqualToIgnoringCase("org.apache.camel");
		assertThat(lang.getDependencies().get(0).getArtifactId()).isEqualToIgnoringCase("camel-core");
		assertThat(lang.getDependencies().get(0).getVersion()).isNotEmpty();
		assertThat(lang.getParameters().size()>0).isTrue();
		Parameter p = lang.getParameter("id");
		assertThat(p).isNotNull();
		
		assertThat(p.getKind()).isEqualToIgnoringCase("attribute");
		assertThat(p.getRequired()).isEqualToIgnoringCase("false");
		assertThat(p.getType()).isEqualToIgnoringCase("string");
		assertThat(p.getJavaType()).isEqualToIgnoringCase("java.lang.String");
		assertThat(p.getDeprecated()).isEqualToIgnoringCase("false");
		assertThat(p.getDescription()).startsWith("Sets the id of ");
	}
}
