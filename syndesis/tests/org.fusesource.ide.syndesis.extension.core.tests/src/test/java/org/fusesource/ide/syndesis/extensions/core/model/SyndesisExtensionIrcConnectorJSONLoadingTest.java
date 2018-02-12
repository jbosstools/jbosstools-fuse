/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.syndesis.extensions.core.model.ActionDataShape;
import org.fusesource.ide.syndesis.extensions.core.model.PropertyDefinitionStep;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisAction;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisActionDescriptor;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtensionProperty;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lhein
 */
public class SyndesisExtensionIrcConnectorJSONLoadingTest {

	private SyndesisExtension extension;

	@Before
	public void onStart() throws IOException {
		File jsonFile = new File("testdata/irc-connector.json");
		this.extension = SyndesisExtension.getJSONFactoryInstance(new FileInputStream(jsonFile));
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getExtensionId()}.
	 */
	@Test
	public void testGetExtensionId() {
		assertEquals("extensionId value is not matching", "io.syndesis.extensions:syndesis-connector-irc", extension.getExtensionId());
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getVersion()}.
	 */
	@Test
	public void testGetVersion() {
		assertEquals("version value is not matching", "1.0.0", extension.getVersion());
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("name value is not matching", "IRC", extension.getName());
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertEquals("description value is not matching", "IRC Extension Connector for Syndesis", extension.getDescription());
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getDependencies()}.
	 */
	@Test
	public void testGetDependencies() {
		assertThat(extension.getDependencies()).filteredOn("id", "io.syndesis.integration:integration-runtime:jar:1.3-SNAPSHOT").isNotNull(); 
		assertThat(extension.getDependencies()).filteredOn("id", "org.apache.camel:camel-core:jar:2.20.1").isNotNull();
		assertThat(extension.getDependencies()).filteredOn("id", "org.apache.camel:camel-irc:jar:2.20.1").isNotNull();
		assertThat(extension.getDependencies()).filteredOn("id", "org.apache.camel:camel-spring-boot:jar:2.20.1").isNotNull();
		assertThat(extension.getDependencies()).filteredOn("id", "org.springframework.boot:spring-boot-autoconfigure:jar:1.5.8.RELEASE").isNotNull();
		assertThat(extension.getDependencies()).filteredOn("id", "org.springframework.boot:spring-boot-loader:jar:1.5.8.RELEASE").isNotNull();
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getExtensionType()}.
	 */
	@Test
	public void testGetExtensionType() {
		assertEquals("extension type value is not matching", "Connectors", extension.getExtensionType());
	}
		
	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getActions()}.
	 */
	@Test
	public void testGetActions() {
		assertTrue("actions are not correct", extension.getActions() != null && !extension.getActions().isEmpty() && extension.getActions().size() == 1);
	}

	@Test
	public void testSyndesisAction() {
		SyndesisAction action = extension.getActions().get(0);
		
		assertEquals("Action ID not matching", "io.syndesis:irc-privmsg", action.getId());

		assertEquals("Action Name not matching", "IRC PRIVMSG", action.getName());

		assertEquals("Action Pattern not matching", "From", action.getPattern());
		
		assertEquals("Action Type not matching", "connector", action.getActionType());

		assertEquals("Action Description not matching", "React to privmsg", action.getDescription());
	}

	@Test
	public void testSyndesisActionDescriptor() {
		SyndesisActionDescriptor descriptor = extension.getActions().get(0).getDescriptor();
		
		ActionDataShape inputShape = descriptor.getInputDataShape();
		assertThat(inputShape.getKind()).isEqualTo("any");

		ActionDataShape outputShape = descriptor.getOutputDataShape();
		assertThat(outputShape.getKind()).isEqualTo("any");
	}

	@Test
	public void testPropertyDefinitionSteps() {
		List<PropertyDefinitionStep> steps = extension.getActions().get(0).getDescriptor().getPropertyDefinitionSteps();
		assertThat(steps).hasSize(1);
		
		PropertyDefinitionStep step = steps.get(0);
		assertThat(step.getDescription()).isEqualTo("Properties");
		assertThat(step.getName()).isEqualTo("properties");
		assertThat(step.getProperties()).hasSize(4);
	}

	@Test
	public void testStepProperties() {
		Map<String, SyndesisExtensionProperty> properties = extension.getActions().get(0).getDescriptor().getPropertyDefinitionSteps().get(0).getProperties();
		assertThat(properties.keySet()).contains(
				"nickname",
				"hostname",
				"port",
				"channels"
		);
		
		SyndesisExtensionProperty prop = properties.get("port");
		assertThat(prop.getComponentProperty()).isFalse();
		assertThat(prop.getDeprecated()).isFalse();
		assertThat(prop.getDescription()).isEqualTo("port");
		assertThat(prop.getDisplayName()).isEqualTo("port_");
		assertThat(prop.getJavaType()).isEqualTo("int");
		assertThat(prop.getKind()).isEqualTo("parameter");
		assertThat(prop.getRequired()).isTrue();
		assertThat(prop.getSecret()).isFalse();
		assertThat(prop.getType()).isEqualTo("int");
	}
	
	@Test
	public void testConfiguredProperties() {
		SyndesisActionDescriptor descriptor = extension.getActions().get(0).getDescriptor();
		assertThat(descriptor.getConfiguredProperties().keySet()).contains(
				"autoRejoin",
				"onNick",
				"onQuit",
				"onJoin",
				"onKick",
				"onMode",
				"onPart",
				"onTopic",
				"onPrivmsg"
		);
	}
}
