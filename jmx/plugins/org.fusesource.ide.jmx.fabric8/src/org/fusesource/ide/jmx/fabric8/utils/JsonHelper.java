/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.fabric8.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;

/**
 * @author lhein
 */
public class JsonHelper {
	
	/**
	 * returns the model node of the json content
	 * 
	 * @param content	the json as string
	 * @return			the model node
	 * @throws Exception if json is unparsable
	 */
	public static ModelNode getModelNode(final String content) throws Exception {
		if (content == null) {
			throw new Exception("Could not unmarshall response: no content.");
		}
		final ModelNode node = ModelNode.fromJSONString(content);
		if (!node.isDefined()) {
			throw new Exception("Could not unmarshall response: erroneous content.");
		}
		return node;
	}

	/**
	 * returns the property as string
	 * 
	 * @param node			the json model node
	 * @param propertyName	the name of the property to get
	 * @return	the property or null if not defined
	 */
	public static String getAsString(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asString() : null;
	}
	
	/**
	 * returns the property as long
	 * 
	 * @param node			the json model node
	 * @param propertyName	the name of the property to get
	 * @return	the property or null if not defined
	 */
	public static Long getAsLong(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asLong() : null;
	}
	
	/**
	 * returns the property as list
	 * 
	 * @param node			the json model node
	 * @param propertyName	the name of the property to get
	 * @return	the property or null if not defined
	 */
	public static List<ModelNode> getAsList(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asList() : new ArrayList<ModelNode>();
	}
	
	/**
	 * returns the property as map<string, string>
	 * 
	 * @param node			the json model node
	 * @param propertyName	the name of the property to get
	 * @return	the property or null if not defined
	 */
	public static Map<String, String> getAsPropertiesMap(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		List<Property> propertyList = propertyNode.isDefined() ? propertyNode.asPropertyList() : new ArrayList<Property>();
		Map<String, String> properties = new HashMap<String, String>();
		for (Property prop : propertyList) {
			properties.put(prop.getName(), prop.getValue().asString());
		}
		return properties;
	}
	
	/**
	 * returns the property as map<string, string>
	 * 
	 * @param node			the json model node
	 * @param propertyName	the name of the property to get
	 * @return	the property or null if not defined
	 */
	public static Map<String, Object> getAsMap(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		List<Property> propertyList = propertyNode.isDefined() ? propertyNode.asPropertyList() : new ArrayList<Property>();
		Map<String, Object> properties = new HashMap<String, Object>();
		for (Property prop : propertyList) {
			properties.put(prop.getName(), prop.getValue());
		}
		return properties;
	}
	
	/**
	 * returns the property as string array
	 * 
	 * @param node			the json model node
	 * @param propertyName	the name of the property to get
	 * @return	the property or null if not defined
	 */
	public static String[] getAsStringArray(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		List<Property> propertyList = propertyNode.isDefined() ? propertyNode.asPropertyList() : new ArrayList<Property>();
		List<String> result = new ArrayList<String>();
		for (Property p : propertyList) {
			result.add(p.getValue().asString());
		}
		return result.toArray(new String[result.size()]);
	}
}
