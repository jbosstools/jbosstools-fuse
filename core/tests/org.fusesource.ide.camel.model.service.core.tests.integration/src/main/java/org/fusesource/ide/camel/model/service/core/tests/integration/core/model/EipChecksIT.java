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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
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

	public EipChecksIT(String versionToTest) {
		this.versionToTest = versionToTest;
	}

	@Parameters(name = "{0}")
	public static Collection<String> params() {
		return CamelCatalogUtils.getOfficialSupportedCamelCatalogVersions();
	}

	@Test
	public void testChoiceEipModelCanContainWhen() throws IOException, CoreException {
		assertThisEIPCanContainThat(AbstractCamelModelElement.CHOICE_NODE_NAME, AbstractCamelModelElement.WHEN_NODE_NAME);
	}

	@Test
	public void testRouteEipModelCanContainWireTap() throws IOException, CoreException {
		assertThisEIPCanContainThat(AbstractCamelModelElement.ROUTE_NODE_NAME, AbstractCamelModelElement.WIRETAP_NODE_NAME);
	}

	@Test
	public void testWhenEipModelCanContainWireTap() throws IOException, CoreException {
		assertThisEIPCanContainThat(AbstractCamelModelElement.WHEN_NODE_NAME, AbstractCamelModelElement.WIRETAP_NODE_NAME);
	}
	
	@Test
	public void testWireTapCanBeChildOfAllContainers() throws IOException, CoreException {
		Collection<Eip> eips = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(versionToTest).getEips();
		for (Eip eip : eips) {
			if (eip.canHaveChildren() == false || eip.getName().equalsIgnoreCase(AbstractCamelModelElement.CHOICE_NODE_NAME)) continue;
			assertThisEIPCanContainThat(eip.getName(), AbstractCamelModelElement.WIRETAP_NODE_NAME);
		}
	}

	@Test
	public void testExpressionsHaveOneOfValuesAvailable() throws IOException, CoreException {
		Collection<Eip> eips = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(versionToTest).getEips();
		for (Eip eip : eips) {
			for (Parameter p : eip.getParameters() ) {
				if (AbstractCamelModelElement.NODE_KIND_EXPRESSION.equalsIgnoreCase(p.getKind())) {
					// expression parameter -> check for specific oneOf values
					List<String> langs = Arrays.asList(p.getOneOf());
					assertThisParameterHasOneOfValue(langs, eip, p, "vtdxml");
					assertThisParameterHasOneOfValue(langs, eip, p, "xpath");
					assertThisParameterHasOneOfValue(langs, eip, p, "xquery");
					assertThisParameterHasOneOfValue(langs, eip, p, "xtokenize");
				}
			}
		}
	}

	private void assertThisParameterHasOneOfValue(List<String> langs, Eip eip, Parameter p, String value) {
		assertThat(langs.contains(value)).as("OneOf list of parameter " + p.getName() + " of EIP " + eip.getName() + " doesn't contain needed value " + value).isTrue();
	}
	
	private void assertThisEIPCanContainThat(String eipContainer, String eipChild) {
		Eip container = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(versionToTest).getEip(eipContainer);
		assertThat(container.canHaveChildren()).isTrue();
		assertThat(container.getAllowedChildrenNodeTypes()).describedAs("Container " + eipContainer + " is not defined to allow child of type " + eipChild).contains(eipChild);
	}
}
