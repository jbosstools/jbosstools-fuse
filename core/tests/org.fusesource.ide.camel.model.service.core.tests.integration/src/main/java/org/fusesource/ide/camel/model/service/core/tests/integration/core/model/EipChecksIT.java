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

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lhein
 */
@RunWith(Parameterized.class)
public class EipChecksIT {

	private String versionToTest;

	public EipChecksIT(final String versionToTest) {
		this.versionToTest = versionToTest;
	}
	
	@Parameters(name = "{0}")
	public static Collection<String> params() {
		return CamelModelFactory.getSupportedCamelVersions();
	}
	
	@Test
	public void testChoiceEipModel() throws IOException, CoreException {
		assertChoiceEipModelCorrect(versionToTest);
	}
	
	@Test
	public void testRouteEipModel() throws IOException, CoreException {
		assertRouteEipModelCorrect(versionToTest);
	}
	
	private void assertChoiceEipModelCorrect(String camelVersion) {
		Eip choiceEip = CamelModelFactory.getModelForVersion(camelVersion).getEipModel().getEIPByName(AbstractCamelModelElement.CHOICE_NODE_NAME);
		assertThat(choiceEip).isNotNull();
		assertThat(choiceEip.canHaveChildren()).isTrue();
		assertThat(choiceEip.getAllowedChildrenNodeTypes()).contains(AbstractCamelModelElement.WHEN_NODE_NAME);
	}
	
	private void assertRouteEipModelCorrect(String camelVersion) {
		Eip routeEip = CamelModelFactory.getModelForVersion(camelVersion).getEipModel().getEIPByName(AbstractCamelModelElement.ROUTE_NODE_NAME);
		assertThat(routeEip).isNotNull();
		assertThat(routeEip.canHaveChildren()).isTrue();
		assertThat(routeEip.getAllowedChildrenNodeTypes()).contains(AbstractCamelModelElement.WIRETAP_NODE_NAME);
	}
}
