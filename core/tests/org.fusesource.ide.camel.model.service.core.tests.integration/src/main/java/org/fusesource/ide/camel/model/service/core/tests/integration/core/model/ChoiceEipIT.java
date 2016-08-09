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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lhein
 */
@RunWith(Parameterized.class)
public class ChoiceEipIT {

	private String versionToTest;

	public ChoiceEipIT(final String versionToTest) {
		this.versionToTest = versionToTest;
	}
	
	@Parameters(name = "{0}")
	public static Collection<String> params() {
		//@formatter:off
		return CamelModelFactory.getSupportedCamelVersions();
		//@formatter:on
	}
	
	@Test
	public void testChoiceEipModel() throws IOException, CoreException {
		assertChoiceEipModelCorrect(versionToTest);
	}
	
	private void assertChoiceEipModelCorrect(String camelVersion) {
		Eip choiceEip = CamelModelFactory.getModelForVersion(camelVersion).getEipModel().getEIPByName("choice");
		assertThat(choiceEip).isNotNull();
		assertThat(choiceEip.canHaveChildren()).isTrue();
		assertThat(choiceEip.getAllowedChildrenNodeTypes()).contains("when");
	}
}
