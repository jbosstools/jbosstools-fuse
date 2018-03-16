/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author lhein
 */
public class SyndesisExtensionSqlConnectorJSONLoadingTest {

	private SyndesisExtension extension;

	@Before
	public void onStart() throws IOException {
		File jsonFile = new File("testdata/sql-connector.json");
		try (FileInputStream fis = new FileInputStream(jsonFile)) {
			this.extension = SyndesisExtension.getJSONFactoryInstance(fis);
		}
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getId()}.
	 */
	@Test
	public void testGetId() {
		assertEquals("Id value is not matching", "sql", extension.getId());
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getIcon()}.
	 */
	@Test
	public void testGetIcon() {
		assertEquals("icon value is not matching", "fa-database", extension.getIcon());
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertEquals("description value is not matching", "Invoke SQL or stored procedures.", extension.getDescription());
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getConnectorCustomizers()}.
	 */
	@Test
	public void testGetConnectorCustomizers() {
		assertThat(extension.getConnectorCustomizers()).contains("io.syndesis.connector.sql.customizer.DataSourceCustomizer");
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getDependencies()}.
	 */
	@Test
	public void testGetDependencies() {
		assertThat(extension.getDependencies()).filteredOn("id", "io.syndesis.connector:connector-sql:1.3-SNAPSHOT").isNotNull(); 
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getActions()}.
	 */
	@Test
	public void testGetActions() {
		assertThat(extension.getActions()).hasSize(4);
	}
	
	@Test
	public void testGetName() {
		assertThat(extension.getName()).isEqualTo("Database");
	}
	
	@Test
	public void testGetTags() {
		assertThat(extension.getTags()).contains("verifier");
	}

	@Test
	public void testSyndesisAction() {
		SyndesisAction action = extension.getActions().get(0);
		
		assertEquals("Action ID not matching", "sql-connector", action.getId());

		assertEquals("Action Name not matching", "Invoke SQL", action.getName());

		assertEquals("Action Description not matching", "Invoke SQL to obtain, store, update, or delete data.", action.getDescription());
		
		assertEquals("Action Pattern not matching", "To", action.getPattern());
		
		assertEquals("Action Type not matching", "connector", action.getActionType());

		assertThat(action.getTags()).contains("dynamic");
	}

	@Test
	public void testSyndesisActionDescriptor() {
		SyndesisActionDescriptor descriptor = extension.getActions().get(0).getDescriptor();
		
		ActionDataShape inputShape = descriptor.getInputDataShape();
		assertThat(inputShape.getKind()).isEqualTo("json-schema");

		ActionDataShape outputShape = descriptor.getOutputDataShape();
		assertThat(outputShape.getKind()).isEqualTo("json-schema");
		
		assertThat(descriptor.getComponentScheme()).isEqualTo("sql");
		assertThat(descriptor.getConnectorCustomizers()).contains("io.syndesis.connector.sql.customizer.SqlConnectorCustomizer");
	}

	@Test
	public void testPropertyDefinitionSteps() {
		List<PropertyDefinitionStep> steps = extension.getActions().get(0).getDescriptor().getPropertyDefinitionSteps();
		assertThat(steps).hasSize(1);
		
		PropertyDefinitionStep step = steps.get(0);
		assertThat(step.getDescription()).isEqualTo("Enter a SQL statement that starts with INSERT, SELECT, UPDATE or DELETE.");
		assertThat(step.getName()).isEqualTo("SQL statement");
		assertThat(step.getProperties()).hasSize(1);
	}

	@Test
	public void testStepProperties() {
		Map<String, SyndesisExtensionProperty> properties = extension.getActions().get(0).getDescriptor().getPropertyDefinitionSteps().get(0).getProperties();
		assertThat(properties).containsKey("query");
		
		SyndesisExtensionProperty prop = properties.get("query");
		assertThat(prop.getDeprecated()).isFalse();
		assertThat(prop.getDescription()).isEqualTo("SQL statement to be executed. Can contain input parameters prefixed by ':#' (for example ':#MYPARAMNAME').");
		assertThat(prop.getDisplayName()).isEqualTo("SQL statement");
		assertThat(prop.getGroup()).isEqualTo("common");
		assertThat(prop.getJavaType()).isEqualTo("java.lang.String");
		assertThat(prop.getKind()).isEqualTo("path");
		assertThat(prop.getRequired()).isTrue();
		assertThat(prop.getSecret()).isFalse();
		assertThat(prop.getType()).isEqualTo("string");
	}
	
	@Test
	public void testExtensionProperties() {
		assertThat(extension.getExtensionProperties()).containsOnlyKeys(
				"url",
				"user",
				"password",
				"schema"
		);
	}
}
