/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.tests.integration.core.util;

import static org.fusesource.ide.camel.model.service.core.util.PropertiesUtils.updateURIParams;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.resources.IResource;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 
 * @author apodhrad
 *
 */
@RunWith(Parameterized.class)
public class PropertiesUtilsTestIT {

	@Mock
	private IResource resource;

	private Component component;

	@Parameters
	public static Collection<Component> components() {
		CamelModel camelModel = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
		return camelModel.getComponentModel().getSupportedComponents();
	}

	public PropertiesUtilsTestIT(Component component) {
		this.component = component;
	}

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testUpdateURIParamsWithPathParams() throws Exception {
		for (Parameter p : component.getUriParameters()) {
			if (p.getKind().equalsIgnoreCase("path")) {
				// Sometimes it returns a path param which is not in the syntax (then it causes NPE), e.g openshift
				if (!component.getSyntax().contains(p.getName())) {
					continue;
				}
				// This is really weird because, see FUSETOOLS-1803 
				if (component.getScheme().equals("http4s")) {
					continue;
				}
				CamelEndpoint endpoint = createCamelEndpoint(component.getSyntax());
				updateURIParams(endpoint, p, "abc", component, modelMap(component.getUriParameters()));
				assertUri(endpoint.getUri(), component, p, "abc");
			}
		}
	}

	private CamelEndpoint createCamelEndpoint(String uri) {
		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		CamelEndpoint endpoint = new CamelEndpoint(uri);
		route.addChildElement(endpoint);
		endpoint.setParent(route);
		return endpoint;
	}

	private IObservableMap modelMap(List<Parameter> params) {
		Map<String, String> map = new HashMap<String, String>();
		for (Parameter param : params) {
			map.put(param.getName(), param.getName());
		}
		return new ObservableMap(map);
	}

	private static void assertUri(String uri, Component component, Parameter param, String value) {
		assertTrue("Uri '" + uri + "' contains wrong scheme. The correct scheme is '" + component.getScheme() + "'",
				uri.startsWith(component.getScheme() + ":"));
		assertTrue("Uri '" + uri + "' doesn't contain the change '" + param.getName() + "' -> '" + value
				+ "'. The syntax is '" + component.getSyntax() + "'", uri.contains(value));
		assertTrue(
				"Uri '" + uri + "' doesn't properly include the change '" + param.getName() + "' -> '" + value
						+ "'. The syntax is '" + component.getSyntax() + "'",
				correctUri(uri, param.getName(), value).equals(component.getSyntax()));
	}

	private static String correctUri(String uri, String paramName, String paramValue) {
		int index = uri.indexOf(paramValue);
		return uri.substring(0, index) + uri.substring(index).replaceFirst(paramValue, paramName);
	}

}
