/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ActiveMQPaletteEntryTest {
	
	@Spy
	ActiveMQPaletteEntry activeMQPaletteEntry = new ActiveMQPaletteEntry();
	
	@Before
	public void setup(){
		doReturn("2.18.1").when(activeMQPaletteEntry).getCurrentProjectCamelVersion();
	}

	@Test
	public void testGetRequiredDependenciesForKarafProvider() throws Exception {
		List<Dependency> requiredDependencies = activeMQPaletteEntry.getRequiredDependencies(CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
		Dependency amqDep = requiredDependencies.get(0);
		assertThat(amqDep.getArtifactId()).isEqualTo(ActiveMQPaletteEntryDependenciesManager.ACTIVEMQ_CAMEL);
		
		Dependency jmsDep = requiredDependencies.get(1);
		assertThat(jmsDep.getArtifactId()).isEqualTo("camel-jms");
	}
	
	@Test
	public void testGetRequiredDependenciesForSpringBootProviderIsUsingGenericOne() throws Exception {
		List<Dependency> requiredDependencies = activeMQPaletteEntry.getRequiredDependencies(CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT);
		
		Dependency amqDep = requiredDependencies.get(0);
		assertThat(amqDep.getArtifactId()).isEqualTo(ActiveMQPaletteEntryDependenciesManager.ACTIVEMQ_CAMEL);
		
		Dependency jmsDep = requiredDependencies.get(1);
		assertThat(jmsDep.getArtifactId()).isEqualTo("camel-jms-starter");
	}
	
	@Test
	public void testGetRequiredDependenciesWhenIssueRetrievingCamelVersionIsUsingGenericOne() throws Exception {
		doReturn(null).when(activeMQPaletteEntry).getCurrentProjectCamelVersion();
		List<Dependency> requiredDependencies = activeMQPaletteEntry.getRequiredDependencies(CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT);
		
		Dependency amqDep = requiredDependencies.get(0);
		assertThat(amqDep.getArtifactId()).isEqualTo(ActiveMQPaletteEntryDependenciesManager.ACTIVEMQ_CAMEL);
		assertThat(amqDep.getVersion()).isEqualTo(ActiveMQPaletteEntryDependenciesManager.LATEST_AMQ_VERSION);
		
		Dependency jmsDep = requiredDependencies.get(1);
		assertThat(jmsDep.getArtifactId()).isEqualTo("camel-jms-starter");
	}

}
