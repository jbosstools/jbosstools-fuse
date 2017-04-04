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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.resources.IResource;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
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

	@Parameterized.Parameter
	public String componentName;

	@Parameterized.Parameter(value = 1)
	public Component component;
	
	@Parameterized.Parameter(value = 2)
	public String runtimeProvider;

	@Parameters(name = "{0} {2}")
	public static Collection<Object[]> components() {
		Set<Object[]> res = new HashSet<>();
		res.addAll(retrieveComponents(CamelCatalogUtils.RUNTIME_PROVIDER_KARAF));
		res.addAll(retrieveComponents(CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT));
		return res;
	}

	private static HashSet<Object[]> retrieveComponents(String runtimeProvider) {
		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForVersion(CamelCatalogUtils.getLatestCamelVersion(), runtimeProvider);
		final Collection<Component> supportedComponents = camelModel.getComponents();
		Stream<Object[]> stream = supportedComponents.stream().map(component -> new Object[] { component.getName(), component, runtimeProvider });
		return stream.collect(Collectors.toCollection(HashSet::new));
	}

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testUpdateURIParamsWithPathParams() throws Exception {
		for (Parameter p : component.getParameters()) {
			if ("path".equalsIgnoreCase(p.getKind())) {
				CamelEndpoint endpoint = createCamelEndpoint(component.getSyntax());
				updateURIParams(endpoint, p, "abc", component, modelMap(component.getParameters()));
				assertUri(endpoint.getUri(), component, p, "abc");
			}
		}
	}
	
	private CamelEndpoint createCamelEndpoint(String uri) {
		CamelFile camelFile = spy(new CamelFile(resource));
		doReturn(CamelCatalogCacheManager.getInstance().getCamelModelForVersion(CamelCatalogUtils.getLatestCamelVersion(), runtimeProvider)).when(camelFile).getCamelModel();
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		CamelEndpoint endpoint = new CamelEndpoint(uri);
		route.addChildElement(endpoint);
		endpoint.setParent(route);
		return endpoint;
	}

	private IObservableMap<String, String> modelMap(List<Parameter> params) {
		Map<String, String> map = new HashMap<String, String>();
		for (Parameter param : params) {
			map.put(param.getName(), param.getName());
		}
		return new ObservableMap<String, String>(map);
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
