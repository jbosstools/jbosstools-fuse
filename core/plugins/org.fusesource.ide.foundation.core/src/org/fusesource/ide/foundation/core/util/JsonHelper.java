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
package org.fusesource.ide.foundation.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.dmr.Property;

/**
 * @author lhein
 *
 */
public class JsonHelper {
	
	private JsonHelper() {
		// static access only
	}
    
    /**
     * returns the model node of the json content
     * 
     * @param content   the json as string
     * @return          the model node
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
     * @param node          the json model node
     * @param propertyName  the name of the property to get
     * @return  the property or null if not defined
     */
    public static String getAsString(final ModelNode node, String propertyName) {
        final ModelNode propertyNode = node.get(propertyName);
        return propertyNode.isDefined() ? propertyNode.asString() : null;
    }
    
    /**
     * returns the property as long
     * 
     * @param node          the json model node
     * @param propertyName  the name of the property to get
     * @return  the property or null if not defined
     */
    public static Long getAsLong(final ModelNode node, String propertyName) {
        final ModelNode propertyNode = node.get(propertyName);
        return propertyNode.isDefined() ? propertyNode.asLong() : null;
    }
    
    /**
     * returns the property as list
     * 
     * @param node          the json model node
     * @param propertyName  the name of the property to get
     * @return  the property or null if not defined
     */
    public static List<ModelNode> getAsList(final ModelNode node, String propertyName) {
        final ModelNode propertyNode = node.get(propertyName);
        return propertyNode.isDefined() ? propertyNode.asList() : new ArrayList<>();
    }
    
    /**
     * returns the property as map<string, string>
     * 
     * @param node          the json model node
     * @param propertyName  the name of the property to get
     * @return  the property or null if not defined
     */
    public static Map<String, String> getAsPropertiesMap(final ModelNode node, String propertyName) {
        final ModelNode propertyNode = node.get(propertyName);
        List<Property> propertyList = propertyNode.isDefined() ? propertyNode.asPropertyList() : new ArrayList<>();
        Map<String, String> properties = new HashMap<>();
        for (Property prop : propertyList) {
            properties.put(prop.getName(), prop.getValue().asString());
        }
        return properties;
    }
    
    /**
     * returns the property as map<string, string>
     * 
     * @param node          the json model node
     * @param propertyName  the name of the property to get
     * @return  the property or null if not defined
     */
    public static Map<String, Object> getAsMap(final ModelNode node, String propertyName) {
        final ModelNode propertyNode = node.get(propertyName);
        List<Property> propertyList = propertyNode.isDefined() ? propertyNode.asPropertyList() : new ArrayList<>();
        Map<String, Object> properties = new HashMap<>();
        for (Property prop : propertyList) {
            properties.put(prop.getName(), prop.getValue());
        }
        return properties;
    }
    

    /**
     * returns the property as map<string, string>
     * 
     * @param node          the json model node
     * @return  the property or null if not defined
     */
    public static Map<String, Object> getAsMap(final ModelNode node) {
        Map<String, Object> properties = new HashMap<>();
        for (String key : node.keys()) {
            
            ModelNode value = node.get(key);
            ModelType t = value.getType();
            
            switch (t) {
            
            case BIG_DECIMAL:   properties.put(key, value.asBigDecimal());
                                break;
            case BIG_INTEGER:   properties.put(key, value.asBigInteger());
                                break;
            case BOOLEAN:       properties.put(key, value.asBoolean(false));
                                break;
            case BYTES:         properties.put(key, value.asBytes());
                                break;
            case DOUBLE:        properties.put(key, value.asDouble(0));
                                break;
            case INT:           properties.put(key, value.asInt(0));
                                break;
            case LIST:          List<ModelNode> l = value.asList();
                                if (!l.isEmpty()) {
                                    ModelNode lo = l.get(0);
                                    switch (lo.getType()) {
                                        case STRING:    List<String> listBigDecValue = getStringList(value);
                                                        properties.put(key, listBigDecValue);
                                                        break;
                                        default:        properties.put(key, l);
                                    }
                                }
                                break;
            case LONG:          properties.put(key, value.asLong(0));
                                break;
            case OBJECT:        properties.put(key, value.asObject());
                                break;
            case PROPERTY:      properties.put(key, value.asProperty());
                                break;
            case STRING:        properties.put(key, value.asString());
                                break;
            case UNDEFINED:     properties.put(key, null);
                                break;
            default:            properties.put(key, value);
            }           
        }
        return properties;
    }
    
    public static List<String> getStringList(ModelNode node) {
        List<String> result = new ArrayList<>();
        List<ModelNode> l = node.asList();
        for (ModelNode n : l) {
            result.add(n.asString());
        }
        return result;
    }
    
    /**
     * returns the property as string array
     * 
     * @param node          the json model node
     * @param propertyName  the name of the property to get
     * @return  the property or null if not defined
     */
    public static String[] getAsStringArray(final ModelNode node, String propertyName) {
        List<String> result = getAsStringList(node, propertyName);
        return result.toArray(new String[result.size()]);
    }
    
    /**
     * returns the property as string list
     * 
     * @param node          the json model node
     * @param propertyName  the name of the property to get
     * @return  the property or null if not defined
     */
    public static List<String> getAsStringList(final ModelNode node, String propertyName) {
        final ModelNode propertyNode = node.get(propertyName);
        List<Property> propertyList = propertyNode.isDefined() ? propertyNode.asPropertyList() : new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Property p : propertyList) {
            result.add(p.getValue().asString());
        }
        return result;
    }    
}
