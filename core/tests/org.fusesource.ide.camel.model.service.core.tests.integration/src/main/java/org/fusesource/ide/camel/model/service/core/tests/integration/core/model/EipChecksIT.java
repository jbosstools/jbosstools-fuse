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
package org.fusesource.ide.camel.model.service.core.tests.integration.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author lhein
 */
@RunWith(Parameterized.class)
public class EipChecksIT {

	private String versionToTest;
	private String runtimeProvider;

	public EipChecksIT(String versionToTest, String runtimeProvider) {
		this.versionToTest = versionToTest;
		this.runtimeProvider = runtimeProvider;
	}

	@Parameters(name = "{0} {1}")
	public static Collection<String[]> params() {
		Collection<String[]> params = new HashSet<>();
		List<String> supportedCamelVersions = CamelModelFactory.getSupportedCamelVersions();
		for (String supportedCamelVersion : supportedCamelVersions) {
			params.add(new String[]{supportedCamelVersion, CamelModelFactory.RUNTIME_PROVIDER_KARAF});
			if (supportedCamelVersion.compareTo("2.18") > 1) {
				params.add(new String[]{supportedCamelVersion, CamelModelFactory.RUNTIME_PROVIDER_SPRINGBOOT});
			}
		}
		return params;
	}

	@Test
	public void testChoiceEipModelCanContainWhen() throws IOException, CoreException {
		assertThisEIPCanContainThat(versionToTest, AbstractCamelModelElement.CHOICE_NODE_NAME, AbstractCamelModelElement.WHEN_NODE_NAME);
	}

	@Test
	public void testRouteEipModelCanContainWireTap() throws IOException, CoreException {
		assertThisEIPCanContainThat(versionToTest, AbstractCamelModelElement.ROUTE_NODE_NAME, AbstractCamelModelElement.WIRETAP_NODE_NAME);
	}

	@Test
	public void testWhenEipModelCanContainWireTap() throws IOException, CoreException {
		assertThisEIPCanContainThat(versionToTest, AbstractCamelModelElement.WHEN_NODE_NAME, AbstractCamelModelElement.WIRETAP_NODE_NAME);
	}
	
	@Test
	public void testWireTapCanBeChildOfAllContainers() throws IOException, CoreException {
		ArrayList<Eip> eips = CamelModelFactory.getModelForVersion(versionToTest, runtimeProvider).getEipModel().getSupportedEIPs();
		for (Eip eip : eips) {
			if (eip.canHaveChildren() == false || eip.getName().equalsIgnoreCase(AbstractCamelModelElement.CHOICE_NODE_NAME)) continue;
			assertThisEIPCanContainThat(versionToTest, eip.getName(), AbstractCamelModelElement.WIRETAP_NODE_NAME);
		}
	}

	private void assertThisEIPCanContainThat(String camelVersion, String eipContainer, String eipChild){
		Eip container = CamelModelFactory.getModelForVersion(camelVersion, runtimeProvider).getEipModel().getEIPByName(eipContainer);
		assertThat(container.canHaveChildren()).isTrue();
		assertThat(container.getAllowedChildrenNodeTypes()).describedAs("Container " + eipContainer + " is not defined to allow child of type " + eipChild).contains(eipChild);
	}
}
