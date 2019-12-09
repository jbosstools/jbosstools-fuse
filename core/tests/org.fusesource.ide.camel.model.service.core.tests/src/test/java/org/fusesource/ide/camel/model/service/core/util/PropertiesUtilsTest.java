/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import static org.fusesource.ide.camel.model.service.core.util.PropertiesUtils.replaceParts;
import static org.fusesource.ide.camel.model.service.core.util.PropertiesUtils.updatePathParams;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author apodhrad
 *
 */
public class PropertiesUtilsTest {

	private static final String SCHEMA = "schema-destination";
	private static final String SYNTAX = SCHEMA + ":destination:name:nameExt";

	List<Parameter> pathParams;
	private Map<String, String> modelMap;

	@Before
	public void setup() {
		pathParams = pathParams("destination", "name", "nameExt");

		modelMap = new HashMap<String, String>();
		modelMap.put("destination", "destination");
		modelMap.put("name", "name");
		modelMap.put("nameExt", "nameExt");
	}

	@Test
	public void testUpdatePathParams() {
		assertEquals(SCHEMA + ":myDest:name:nameExt",
				updatePathParams(null, null, SYNTAX, pathParam("destination"), "myDest", pathParams, modelMap));
	}

	@Test
	public void testUpdatePathParamsWithPrefixNames() {
		assertEquals(SCHEMA + ":destination:abc:nameExt",
				updatePathParams(null, null,SYNTAX, pathParam("name"), "abc", pathParams, modelMap));
		assertEquals(SCHEMA + ":destination:name:xyz",
				updatePathParams(null, null,SYNTAX, pathParam("nameExt"), "xyz", pathParams, modelMap));
	}

	@Test
	public void testUpdatePathParamsWithOptions() {
		assertEquals(SCHEMA + ":myDest:name:nameExt?option=123",
				updatePathParams(null, null,SYNTAX + "?option=123", pathParam("destination"), "myDest", pathParams, modelMap));
	}

	@Test
	public void testUpdatePathParamsWithNonexistingParam() {
		assertEquals(SCHEMA + ":destination:name:nameExt",
				updatePathParams(null, null,SYNTAX, pathParam("destinatio"), "myDest", pathParams, modelMap));
	}

	@Test
	public void testUpdatePathParamsWithChangedModelMap() {
		modelMap.put("name", "abc");
		assertEquals(SCHEMA + ":myDest:abc:nameExt",
				updatePathParams(null, null,SYNTAX, pathParam("destination"), "myDest", pathParams, modelMap));
	}

	@Test
	public void testUpdatePathParamsWithSlash() {
		pathParams.add(param("path", "transport"));
		pathParams.add(param("path", "port"));
		pathParams.add(param("path", "path"));

		modelMap.put("transport", "transport");
		modelMap.put("port", "port");
		modelMap.put("path", "path");

		assertEquals(SCHEMA + ":destination:name:nameExt:transport:123/path",
				updatePathParams(null, null,SYNTAX + ":transport:port/path", pathParam("port"), "123", pathParams, modelMap));
		assertEquals(SCHEMA + ":destination:name:nameExt:transport:port/xyz",
				updatePathParams(null, null,SYNTAX + ":transport:port/path", pathParam("path"), "xyz", pathParams, modelMap));
	}

	@Test
	public void testReplaceParts() {
		assertEquals(SCHEMA + ":destination:abc:nameExt", replaceParts(SYNTAX, "name", "abc", ":"));
		assertEquals(SCHEMA + ":destination:name:xyz", replaceParts(SYNTAX, "nameExt", "xyz", ":"));
	}
	
	@Test
	public void testReplacePartsWithRegex() {
		assertEquals(SCHEMA + ":destination:abc:nameExt/path", replaceParts(SYNTAX + "/path", "name", "abc", ":|/"));
		assertEquals(SCHEMA + ":destination:name:xyz/path", replaceParts(SYNTAX+ "/path", "nameExt", "xyz", ":|/"));
		assertEquals(SCHEMA + ":destination:name:nameExt/foo", replaceParts(SYNTAX+ "/path", "path", "foo", ":|/"));
	}

	private static List<Parameter> pathParams(String... names) {
		List<Parameter> pathParams = new ArrayList<Parameter>();
		for (String name : names) {
			pathParams.add(pathParam(name));
		}
		return pathParams;
	}

	public static Parameter pathParam(String name) {
		return param("path", name);
	}

	public static Parameter param(String kind, String name) {
		Parameter pathParam = new Parameter();
		pathParam.setKind(kind);
		pathParam.setName(name);
		return pathParam;
	}

}
